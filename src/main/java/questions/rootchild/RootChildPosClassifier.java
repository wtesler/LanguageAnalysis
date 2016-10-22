package questions.rootchild;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import classifier.FrequencyClassifer;
import models.DependencyTree;
import models.LanguageResponse;
import utils.LanguageUtils;

public class RootChildPosClassifier extends FrequencyClassifer<LanguageResponse> {

    @Override
    public void train(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples) {
        HashMap<String, Double> positiveFrequencies = getRootChildFrequencyMap(positiveExamples);
        HashMap<String, Double> negativeFrequencies = getRootChildFrequencyMap(negativeExamples);

        scoreWithFrequencyAnalysis(positiveFrequencies, negativeFrequencies);
    }

    @Override
    public double classify(LanguageResponse response) {
        DependencyTree tree = LanguageUtils.toDependencyTree(response);
        HashSet<String> rootChildSet = new HashSet<>();
        RootChildPosTraverser.collectRootChildSet(tree.getRoot(), rootChildSet);

        double score = rootChildSet
                .stream()
                .mapToDouble(rootChildPair -> {
                    Double value = getScores().get(rootChildPair);
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

    public HashMap<String, Double> getRootChildFrequencyMap(List<LanguageResponse> responses) {
        List<DependencyTree> trees = LanguageUtils.toDependencyTrees(responses);

        final HashMap<String, Double> rootChildCountMap = new HashMap<>();
        trees.stream()
                .forEach(tree -> RootChildPosTraverser.collectRootChildCountInNode(tree.getRoot(), rootChildCountMap));

        Double numRootChildPairs = rootChildCountMap.entrySet()
                .stream()
                .mapToDouble(Map.Entry::getValue)
                .sum();

        rootChildCountMap.entrySet()
                .stream()
                .forEach(entry -> {
                    double fraction = entry.getValue() / numRootChildPairs;
                    rootChildCountMap.put(entry.getKey(), fraction);
                });

//        siblingCountMap.entrySet()
//                .stream()
//                .sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()));
//                .forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getScore()));

        //System.out.println("Total pairs: " + numSiblings.toString());

        return rootChildCountMap;
    }
}
