package questions.flatedge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import classifier.FrequencyClassifer;
import models.LanguageResponse;

public class FlatEdgeClassifier extends FrequencyClassifer<LanguageResponse> {

    @Override
    public void train(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples) {
        HashMap<String, Double> positiveFrequencies = getTokenSiblingFrequencyMap(positiveExamples);
        HashMap<String, Double> negativeFrequencies = getTokenSiblingFrequencyMap(negativeExamples);

        scoreWithFrequencyAnalysis(positiveFrequencies, negativeFrequencies);
    }

    @Override
    public double classify(LanguageResponse response) {
        double score = 0;

        for (int i = 0; i < response.tokens.size() - 1; i++) {
            String siblingLabel = response.tokens.get(i).dependencyEdge.label
                    + ","
                    + response.tokens.get(i + 1).dependencyEdge.label;
            score += getScore(siblingLabel) != null ? getScore(siblingLabel) : 0;
        }

        return score / getRange();
    }

    public HashMap<String, Double> getTokenSiblingFrequencyMap(List<LanguageResponse> responses) {
        final HashMap<String, Double> siblingCountMap = new HashMap<>();
        responses.
                forEach(response -> {
                    for (int i = 0; i < response.tokens.size() - 1; i++) {
                        String siblingLabel = response.tokens.get(i).dependencyEdge.label
                                + ","
                                + response.tokens.get(i + 1).dependencyEdge.label;

                        siblingCountMap.put(siblingLabel, siblingCountMap.containsKey(siblingLabel) ?
                                siblingCountMap.get(siblingLabel) + 1 : 1);
                    }
                });

        Double numSiblings = siblingCountMap.entrySet()
                .stream()
                .mapToDouble(Map.Entry::getValue)
                .sum();

        // Normalize value
        siblingCountMap.entrySet()
                .stream()
                .forEach(entry -> {
                    double fraction = entry.getValue() / numSiblings;
                    siblingCountMap.put(entry.getKey(), fraction);
                });

        return siblingCountMap;
    }
}
