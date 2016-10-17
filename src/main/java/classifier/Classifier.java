package classifier;

import java.util.HashMap;
import java.util.List;

public abstract class Classifier<T> {

    private final HashMap<String, Double> mScoreMap = new HashMap<>();
    private double mScoreSum;

    private final int MAX_CONFIDENCE = 4;

    private Double mPositiveConfidence = 1.0;
    private Double mNegativeConfidence = 1.0;

    // Tested Values
    int mFalsePositives = 0;
    int mFalseNegatives = 0;

    int mTruePositives = 0;
    int mTrueNegatives = 0;

    public Classifier() { }

    /**
     * Use the data to set a score.
     */
    public abstract void train(List<T> positiveExamples, List<T> negativeExamples);

    public abstract boolean classify(T object);

    /**
     * Use the data to set a confidence level.
     */
    public void test(List<T> positiveExamples, List<T> negativeExamples) {
        //normalize();

        Score positiveScore = scoreObjects(positiveExamples);
        mTruePositives += positiveScore.correct;
        mFalseNegatives += positiveScore.total - positiveScore.correct;

        Score negativeScore = scoreObjects(negativeExamples);
        mFalsePositives += negativeScore.correct;
        mTrueNegatives += negativeScore.total - negativeScore.correct;

        double positiveConfidence = (double) mTruePositives / (mTruePositives + mFalsePositives);
        positiveConfidence = -Math.log(1 - positiveConfidence);
        if (positiveConfidence > MAX_CONFIDENCE) {
            positiveConfidence = MAX_CONFIDENCE;
        }

        double negativeConfidence = (double) mTrueNegatives / (mTrueNegatives + mFalseNegatives);
        negativeConfidence = -Math.log(1 - negativeConfidence);
        if (negativeConfidence > MAX_CONFIDENCE) {
            negativeConfidence = MAX_CONFIDENCE;
        }

        setPositiveConfidence(positiveConfidence);
        setNegativeConfidence(negativeConfidence);

        System.out.println(getClass().getSimpleName());
        System.out.println("\tPositive Confidence: " + getPositiveConfidence());
        System.out.println("\tNegative Confidence: " + getNegativeConfidence());
    }

    public final Score scoreObjects(List<T> presumedPositives) {
        Integer correctlyClassifed = presumedPositives.stream()
                .mapToInt(response -> classify(response) ? 1 : 0)
                .sum();

        Score score = new Score();
        score.correct = correctlyClassifed;
        score.total = presumedPositives.size();
        return score;
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

    protected final Double getScore(String key) {
        return mScoreMap.get(key);
    }

    protected final void setScore(String key, Double value) {
        if (mScoreMap.containsKey(key)) {
            mScoreSum -= Math.abs(mScoreMap.get(key));
        }
        mScoreMap.put(key, value);
        mScoreSum += Math.abs(value);
    }

    protected final HashMap<String, Double> getScores() {
        return mScoreMap;
    }

    private void normalize() {
        mScoreMap.entrySet().parallelStream()
                .forEach(entry -> mScoreMap.put(entry.getKey(), entry.getValue() / mScoreSum));
    }
}
