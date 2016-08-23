package questions.partofspeech;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import classifier.Classifier;
import cloud.CloudParser;
import models.DependencyTree;
import models.LanguageResponse;
import utils.LanguageUtils;

public class PosBigramClassifier extends Classifier<LanguageResponse> {

    private final CloudParser mCloudParser;

    public PosBigramClassifier(CloudParser cloudParser) {
        mCloudParser = cloudParser;
    }

    @Override
    public void train(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples) {
        HashMap<String, Double> positiveFrequencies = getPosBigramFrequencyMap(positiveExamples);
        HashMap<String, Double> negativeFrequencies = getPosBigramFrequencyMap(negativeExamples);

        positiveFrequencies.entrySet()
                .stream()
                .forEach(entry -> {
                    Double negativeFrequency = negativeFrequencies.get(entry.getKey());
                    if (negativeFrequency == null) {
                        negativeFrequency = 0.0;
                    }
                    double positiveFrequency = entry.getValue();

                    double score = positiveFrequency - negativeFrequency;

                    setScore(entry.getKey(), score);

                    String result = entry.getKey() + " -> " + entry.getValue();
                    System.out.println(result);
                });
    }

    @Override
    public boolean classify(LanguageResponse response) {
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

        return score > 0;
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
