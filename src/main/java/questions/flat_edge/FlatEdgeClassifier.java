package questions.flat_edge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import classifier.Classifier;
import cloud.CloudParser;
import models.LanguageResponse;

public class FlatEdgeClassifier extends Classifier<LanguageResponse> {

    private final CloudParser mCloudParser;

    public FlatEdgeClassifier(CloudParser cloudParser) {
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

                    double score = positiveFrequency - negativeFrequency;

                    setScore(entry.getKey(), score);
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

        //System.out.println("FlatEdgeClassifier classifies: " + score);

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

        // Normalize value
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
