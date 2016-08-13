package questions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import classifier.Classifier;
import models.DependencyTree;
import models.LanguageResponse;

public class SiblingClassifier extends Classifier<LanguageResponse> {

    private final SyntaxReader mSyntaxReader;

    public SiblingClassifier(SyntaxReader syntaxReader) {
        mSyntaxReader = syntaxReader;
    }

    @Override
    public void train(String positiveDir, String negativeDir) {
        HashMap<String, Double> positiveFreqMap = getSiblingFrequencyMap(positiveDir);
        HashMap<String, Double> negativeFreqMap = getSiblingFrequencyMap(negativeDir);

        positiveFreqMap.entrySet()
                .stream()
                .forEach(entry -> {
                    Double negativeValue = negativeFreqMap.get(entry.getKey());
                    if (negativeValue == null) {
                        negativeValue = 0.0;
                    }
                    double positiveValue = entry.getValue();
//                    double a = positiveValue / negativeValue;
//                    double b = negativeValue / positiveValue;

                    double value = positiveValue - negativeValue;

                    setScore(entry.getKey(), value);

                    String result = entry.getKey() + " -> " + entry.getValue();
                    //System.out.println(result);
                });
    }

    @Override
    public boolean classify(LanguageResponse response) {
        DependencyTree tree = SyntaxReader.toDependencyTree(response);
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

    public HashMap<String, Double> getSiblingFrequencyMap(String directory) {
        List<LanguageResponse> responses = mSyntaxReader.readParsedDataFromFiles(directory);
        List<DependencyTree> trees = SyntaxReader.toDependencyTrees(responses);

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
        List<LanguageResponse> responses = mSyntaxReader.readParsedDataFromFiles(directory);
        List<DependencyTree> trees = SyntaxReader.toDependencyTrees(responses);

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
