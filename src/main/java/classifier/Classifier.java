package classifier;

import java.util.HashMap;
import java.util.List;

public abstract class Classifier<T> {

    private final HashMap<String, Double> mScoreMap = new HashMap<>();

    private final int MAX_CONFIDENCE = 10;

    private Double mPositiveConfidence = 1.0;
    private Double mNegativeConfidence = 1.0;

    public Classifier() { }

    /**
     * Use the data to set a score.
     *
     * @param positiveDir
     * @param negativeDir
     */
    public abstract void train(String positiveDir, String negativeDir);

    public abstract boolean classify(T object);

    /**
     * Use the data to set a confidence level.
     */
    public void test(List<T> positiveObjects, List<T> negativeObjects) {
        int truePositives = 0;
        int falseNegatives = 0;
        int falsePositives = 0;
        int trueNegatives = 0;

        Score positiveScore = scoreObjects(positiveObjects);
        truePositives += positiveScore.correct;
        falseNegatives += positiveScore.total - positiveScore.correct;

        Score negativeScore = scoreObjects(negativeObjects);
        falsePositives += negativeScore.correct;
        trueNegatives += negativeScore.total - negativeScore.correct;

        double positiveConfidence = (double) truePositives / (truePositives + falsePositives);
        positiveConfidence = -Math.log(1 - positiveConfidence);
        if (positiveConfidence == Double.POSITIVE_INFINITY) {
            positiveConfidence = MAX_CONFIDENCE;
        }

        double negativeConfidence = (double) trueNegatives / (trueNegatives + falseNegatives);
        negativeConfidence = -Math.log(1 - negativeConfidence);
        if (negativeConfidence == Double.POSITIVE_INFINITY) {
            negativeConfidence = MAX_CONFIDENCE;
        }

        setPositiveConfidence(positiveConfidence);
        setNegativeConfidence(negativeConfidence);
        System.out.println("Positive Confidence: " + getPositiveConfidence());
        System.out.println("Negative Confidence: " + getNegativeConfidence());
    }

    public final Score scoreObjects(List<T> objects) {
        Integer correctlyClassifed = objects.stream()
                .mapToInt(response -> classify(response) ? 1 : 0)
                .sum();

        Score score = new Score();
        score.correct = correctlyClassifed;
        score.total = objects.size();
        return score;
    }

    protected final Double getScore(String key) {
        return mScoreMap.get(key);
    }

    protected final HashMap<String, Double> getScores() {
        return mScoreMap;
    }

    protected final void setScore(String key, Double value) {
        mScoreMap.put(key, value);
    }

    protected final void addScores(HashMap<String, Double> values) {
        mScoreMap.putAll(values);
    }

    public final Double getPositiveConfidence() {
        return mPositiveConfidence;
    }

    public final void setPositiveConfidence(double confidence) {
        mPositiveConfidence = confidence;
    }

    public final Double getNegativeConfidence() {
        return mNegativeConfidence;
    }

    public final void setNegativeConfidence(double confidence) {
        mNegativeConfidence = confidence;
    }
}
