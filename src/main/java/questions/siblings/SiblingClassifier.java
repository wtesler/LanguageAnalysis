package questions.siblings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import classifier.FrequencyClassifer;
import models.DependencyTree;
import models.LanguageResponse;
import utils.LanguageUtils;

public class SiblingClassifier extends FrequencyClassifer<LanguageResponse> {

    @Override
    public void train(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples,
                      boolean interactive) {
        HashMap<String, Double> positiveFrequencies = getSiblingFrequencyMap(positiveExamples);
        HashMap<String, Double> negativeFrequencies = getSiblingFrequencyMap(negativeExamples);

        scoreWithFrequencyAnalysis(positiveFrequencies, negativeFrequencies);
    }

    @Override
    public double classify(LanguageResponse response, boolean interactive) {
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

        return score / getRange();
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
}
