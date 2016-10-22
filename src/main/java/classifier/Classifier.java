package classifier;

import java.util.HashMap;
import java.util.List;

public abstract class Classifier<T> {

    private final HashMap<String, Double> mScoreMap = new HashMap<>();
    private double mHighestScore = Double.MIN_VALUE;
    private double mLowestScore = Double.MAX_VALUE;

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
     * Use the data to set scores.
     */
    public abstract void train(List<T> positiveExamples, List<T> negativeExamples);

    public abstract double classify(T object);

    /**
     * Use the data to set a confidence level.
     */
    public void test(List<T> positiveExamples, List<T> negativeExamples) {
        Score positiveScore = score(positiveExamples);
        mTruePositives += positiveScore.correct;
        mFalseNegatives += positiveScore.total - positiveScore.correct;

        Score negativeScore = score(negativeExamples);
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

        setPositiveConfidence(positiveConfidence / MAX_CONFIDENCE);
        setNegativeConfidence(negativeConfidence / MAX_CONFIDENCE);

        System.out.println(getClass().getSimpleName());
        System.out.println("\tPositive Confidence: " + getPositiveConfidence());
        System.out.println("\tNegative Confidence: " + getNegativeConfidence());
    }

    public final Score score(List<T> presumedPositives) {
        Integer correctlyClassifed = presumedPositives.stream()
                .mapToInt(response -> classify(response) > 0 ? 1 : 0)
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
        mScoreMap.put(key, value);
        if (value > mHighestScore) {
            mHighestScore = value;
        }
        if (value < mLowestScore) {
            mLowestScore = value;
        }
    }

    protected final HashMap<String, Double> getScores() {
        return mScoreMap;
    }

    protected final double getRange() {
        return mHighestScore - mLowestScore;
    }
}
