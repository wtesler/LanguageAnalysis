package questions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import classifier.Classifier;
import models.DependencyTree;
import models.LanguageResponse;
import models.Token;
import utils.LanguageUtils;

public class FlatEdgeClassifier extends Classifier<LanguageResponse> {

    private final SyntaxStructurer mSyntaxStructurer;

    public FlatEdgeClassifier(SyntaxStructurer syntaxStructurer) {
        mSyntaxStructurer = syntaxStructurer;
    }


    @Override
    public void train(String positiveDir, String negativeDir) {
        List<LanguageResponse> positiveResponses = mSyntaxStructurer.readParsedDataFromFiles(positiveDir);
        List<LanguageResponse> negativeResponses = mSyntaxStructurer.readParsedDataFromFiles(negativeDir);

        HashMap<String, Double> positiveFrequencies = getSiblingFrequencyMap(positiveResponses);
        HashMap<String, Double> negativeFrequencies = getSiblingFrequencyMap(negativeResponses);

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

                    String result = entry.getKey() + " -> " + entry.getValue();
                    //System.out.println(result);
                });
    }

    @Override
    public boolean classify(LanguageResponse response) {
        double score = 0;

        for (int i = 0; i < response.tokens.size() - 1; i++) {
            String siblingLabel = response.tokens.get(0).dependencyEdge.label
                    + ","
                    + response.tokens.get(0).dependencyEdge.label;

            score += getScore(siblingLabel) != null ? getScore(siblingLabel) : 0;
        }

        return score > 0;
    }

    public HashMap<String, Double> getSiblingFrequencyMap(List<LanguageResponse> responses) {
        final HashMap<String, Double> siblingCountMap = new HashMap<>();
        responses.
                forEach(response -> {
                    for (int i = 0; i < response.tokens.size() - 1; i++) {
                        String siblingLabel = response.tokens.get(0).dependencyEdge.label
                                + ","
                                + response.tokens.get(0).dependencyEdge.label;

                        siblingCountMap.put(siblingLabel, siblingCountMap.containsKey(siblingLabel) ?
                                siblingCountMap.get(siblingLabel) + 1 : 1);
                    }
                });

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
