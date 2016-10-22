package classifier;

import java.util.HashMap;

/**
 * A classifier which computes scores based on the frequency of a key's appearance.
 * @param <T>
 */
public abstract class FrequencyClassifer<T> extends Classifier<T> {

    public void scoreWithFrequencyAnalysis(
            HashMap<String, Double> positiveFrequencies,
            HashMap<String, Double> negativeFrequencies) {

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

        negativeFrequencies.entrySet()
                .stream()
                .forEach(entry -> {
                    if (getScore(entry.getKey()) != null) {
                        return;
                    }
                    Double positiveFrequency = positiveFrequencies.get(entry.getKey());
                    if (positiveFrequency == null) {
                        positiveFrequency = 0.0;
                    }
                    double negativeFrequency = entry.getValue();

                    double score = positiveFrequency - negativeFrequency;

                    setScore(entry.getKey(), score);
                });
    }
}
