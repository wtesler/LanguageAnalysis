package classifier.frequency;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import classifier.Classifier;

/**
 * A classifier which computes scores based on the frequency of a key's appearance.
 *
 * @param <T> The learning input type.
 * @param <S> Key type (For example: {@link String} for a collection of "A,B" pairs.
 */
public class FrequencyClassifer<T, S> extends Classifier<T, S> {

    private final FrequencyCollector<T, S> mCollector;

    public FrequencyClassifer(FrequencyCollector<T, S> collector) {
        mCollector = collector;
    }

    @Override
    public void train(List<T> positiveExamples, List<T> negativeExamples, boolean interactive) {
        ConcurrentHashMap<S, Double> positiveFrequencies = getFrequencyMap(positiveExamples);
        ConcurrentHashMap<S, Double> negativeFrequencies = getFrequencyMap(negativeExamples);

        scoreWithFrequencyAnalysis(positiveFrequencies, negativeFrequencies);
    }

    @Override
    public double classify(T input, boolean interactive) {
        HashSet<S> modelSet = new HashSet<>();
        mCollector.collectModelSet(input, modelSet);

        double score = modelSet
                .stream()
                .mapToDouble(rootChildPair -> {
                    Double value = getScores().get(rootChildPair);
                    return value != null ? value: 0;
                })
                .sum();

        return score / getRange();
    }

    public void scoreWithFrequencyAnalysis(
            ConcurrentHashMap<S, Double> positiveFrequencies,
            ConcurrentHashMap<S, Double> negativeFrequencies) {

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

    public ConcurrentHashMap<S, Double> getFrequencyMap(List<T> inputs) {
        final ConcurrentHashMap<S, Double> mModelCountMap = new ConcurrentHashMap<>();
        inputs.stream().forEach(input -> mCollector.collectModelCountMap(input, mModelCountMap));

        Double totalModels = mModelCountMap.entrySet()
                .stream()
                .mapToDouble(Map.Entry::getValue)
                .sum();

        mModelCountMap.entrySet()
                .stream()
                .forEach(entry -> mModelCountMap.put(entry.getKey(), entry.getValue() / totalModels));

        return mModelCountMap;
    }
}
