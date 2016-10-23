package questions.partofspeech;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import classifier.FrequencyClassifer;
import models.DependencyTree;
import models.LanguageResponse;
import utils.LanguageUtils;

public class PosBigramClassifier extends FrequencyClassifer<LanguageResponse> {

    @Override
    public void train(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples,
                      boolean interactive) {
        HashMap<String, Double> positiveFrequencies = getPosBigramFrequencyMap(positiveExamples);
        HashMap<String, Double> negativeFrequencies = getPosBigramFrequencyMap(negativeExamples);

        scoreWithFrequencyAnalysis(positiveFrequencies, negativeFrequencies);
    }

    @Override
    public double classify(LanguageResponse response, boolean interactive) {
        DependencyTree tree = LanguageUtils.toDependencyTree(response);
        HashSet<String> posBigramSet = new HashSet<>();
        PosTraverser.collectPosBigrams(tree.getRoot(), posBigramSet);

        double score = posBigramSet
                .stream()
                .mapToDouble(siblingPair -> {
                    Double value = getScores().get(siblingPair);
                    return value != null ? value: 0;
                })
                .sum();

        return score / getRange();
    }

    public HashMap<String, Double> getPosBigramFrequencyMap(List<LanguageResponse> responses) {
        List<DependencyTree> trees = LanguageUtils.toDependencyTrees(responses);

        final HashMap<String, Double> posBigramCountMap = new HashMap<>();
        trees.stream().forEach(tree -> PosTraverser.collectPosBigramCount(tree.getRoot(), posBigramCountMap));

        Double numBigrams = posBigramCountMap.entrySet()
                .stream()
                .mapToDouble(Map.Entry::getValue)
                .sum();

        posBigramCountMap.entrySet()
                .stream()
                .forEach(entry -> {
                    double fraction = entry.getValue() / numBigrams;
                    posBigramCountMap.put(entry.getKey(), fraction);
                });

//        posBigramCountMap.entrySet()
//                .stream()
//                .sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()));
//                .forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getScore()));

        //System.out.println("Total bigrams: " + numBigrams.toString());

        return posBigramCountMap;
    }
}
