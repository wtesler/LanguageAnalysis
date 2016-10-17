package questions.siblings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import classifier.Classifier;
import cloud.CloudParser;
import models.DependencyTree;
import models.LanguageResponse;
import utils.LanguageUtils;

public class SiblingClassifier extends Classifier<LanguageResponse> {

    private final CloudParser mCloudParser;

    public SiblingClassifier(CloudParser cloudParser) {
        mCloudParser = cloudParser;
    }

    @Override
    public void train(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples) {
        HashMap<String, Double> positiveFrequencies = getSiblingFrequencyMap(positiveExamples);
        HashMap<String, Double> negativeFrequencies = getSiblingFrequencyMap(negativeExamples);

        positiveFrequencies.entrySet()
                .stream()
                .forEach(entry -> {
                    Double negativeFrequency = negativeFrequencies.get(entry.getKey());
                    if (negativeFrequency == null) {
                        negativeFrequency = 0.0;
                    }
                    double positiveFrequency = entry.getValue();
//                    double a = positiveValue / negativeValue;
//                    double b = negativeValue / positiveValue;

                    double score = positiveFrequency - negativeFrequency;

                    setScore(entry.getKey(), score);

                    //String result = entry.getKey() + " -> " + entry.getValue();
                    //System.out.println(result);
                });
    }

    @Override
    public boolean classify(LanguageResponse response) {
        DependencyTree tree = LanguageUtils.toDependencyTree(response);
        HashSet<String> siblingSet = new HashSet<>();
        SiblingTraverser.collectSiblingSet(tree.getRoot(), siblingSet);

        double score = siblingSet
                .stream()
                .mapToDouble(siblingPair -> {
                    Double value = getScores().get(siblingPair);
                    return value != null ? value: 0;
                })
                .sum();

        return score > 0;
//
//        if (score >= 0) {
//            System.out.println("----------------");
//
//            siblingSet
//                    .stream()
//                    .forEach(siblingPair -> {
//                        Double value = siblingScoreMap.get(siblingPair);
//                        System.out.println(siblingPair + " -> " + value);
//                    });
//
//            System.out.println("----");
//
//            System.out.println(tree.toString() + "score: " + score);
//        }
    }

    public HashMap<String, Double> getSiblingFrequencyMap(List<LanguageResponse> responses) {
        List<DependencyTree> trees = LanguageUtils.toDependencyTrees(responses);

        final HashMap<String, Double> siblingCountMap = new HashMap<>();
        trees.stream().forEach(tree -> SiblingTraverser.collectSiblingCountInNode(tree.getRoot(), siblingCountMap));

        Double numSiblings = siblingCountMap.entrySet()
                .stream()
                .mapToDouble(Map.Entry::getValue)
                .sum();

        siblingCountMap.entrySet()
                .stream()
                .forEach(entry -> {
                    double fraction = entry.getValue() / numSiblings;
                    siblingCountMap.put(entry.getKey(), fraction);
                });

//        siblingCountMap.entrySet()
//                .stream()
//                .sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()));
//                .forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getScore()));

        //System.out.println("Total pairs: " + numSiblings.toString());

        return siblingCountMap;
    }

    public void analyzeDependenciesBetweenSiblings(String directory) {
        List<LanguageResponse> responses = mCloudParser.parseDataFromFiles(directory);
        List<DependencyTree> trees = LanguageUtils.toDependencyTrees(responses);

        final HashMap<String, Double> siblingCountMap = new HashMap<>();
        trees.stream().forEach(tree -> SiblingTraverser.collectSiblingCountInNode(tree.getRoot(),
                siblingCountMap));

        List<String> siblings = siblingCountMap
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (int i = 0; i < siblings.size() - 1; i++) {
            for (int j = i + 1; j < siblings.size(); j++) {
                boolean isAIndependentFromB = false;
                boolean isBIndependentFromA = false;

                for (DependencyTree tree : trees) {
                    if (isAIndependentFromB && isBIndependentFromA) {
                        break;
                    }

                    boolean[] siblingFound = new boolean[2];
                    SiblingTraverser.findSiblingsInTree(
                            tree.getRoot(), siblings.get(i), siblings.get(j), siblingFound);
                    if (siblingFound[0] && !siblingFound[1]) {
                        isAIndependentFromB = true;
                    } else if (!siblingFound[0] && siblingFound[1]) {
                        isBIndependentFromA = true;
                    }
                }

                if (!isAIndependentFromB && !isBIndependentFromA) {
                    System.out.println(siblings.get(i) + " is co-dependent with " + siblings.get(j));
                } else if (!isAIndependentFromB) {
                    System.out.println(siblings.get(i) + " depends on " + siblings.get(j));
                } else if (!isBIndependentFromA) {
                    System.out.println(siblings.get(j) + " depends on " + siblings.get(i));
                }
            }
        }
    }

    public double classifyTree(DependencyTree tree) {
        HashSet<String> siblingSet = new HashSet<>();
        SiblingTraverser.collectSiblingSet(tree.getRoot(), siblingSet);

        double score = siblingSet
                .stream()
                .mapToDouble(siblingPair -> {
                    Double value = getScores().get(siblingPair);
                    return value != null ? value: 0;
                })
                .sum();
//
//        if (score >= 0) {
//            System.out.println("----------------");
//
//            siblingSet
//                    .stream()
//                    .forEach(siblingPair -> {
//                        Double value = siblingScoreMap.get(siblingPair);
//                        System.out.println(siblingPair + " -> " + value);
//                    });
//
//            System.out.println("----");
//
//            System.out.println(tree.toString() + "score: " + score);
//        }

        return score;
    }
}
