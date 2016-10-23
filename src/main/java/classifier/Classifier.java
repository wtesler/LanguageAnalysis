package classifier;

import java.util.HashMap;
import java.util.List;

public abstract class Classifier<T, S> {

    private final HashMap<S, Double> mScoreMap = new HashMap<>();
    private double mHighestScore = Double.MIN_VALUE;
    private double mLowestScore = Double.MAX_VALUE;

    private final int MAX_CONFIDENCE = 5;

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
    public abstract void train(List<T> positiveExamples, List<T> negativeExamples, boolean interactive);

    public final double classify(T model) {
        return classify(model, false);
    }

    public abstract double classify(T object, boolean interactive);

    /**
     * Use the data to set a confidence level.
     */
    public void test(List<T> positiveExamples, List<T> negativeExamples, boolean interactive) {
        Score positiveScore = score(positiveExamples, interactive);
        mTruePositives += positiveScore.correct;
        mFalseNegatives += positiveScore.total - positiveScore.correct;

        Score negativeScore = score(negativeExamples, interactive);
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

    public final Score score(List<T> presumedPositives, boolean interactive) {
        Integer correctlyClassifed = presumedPositives.stream()
                .mapToInt(response -> classify(response) > 0 ? 1 : 0)
                .sum();

        return new Score(correctlyClassifed, presumedPositives.size());
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

    protected final Double getScore(S key) {
        return mScoreMap.get(key);
    }

    protected final void setScore(S key, Double value) {
        mScoreMap.put(key, value);
        if (value > mHighestScore) {
            mHighestScore = value;
        }
        if (value < mLowestScore) {
            mLowestScore = value;
        }
    }

    protected final HashMap<S, Double> getScores() {
        return mScoreMap;
    }

    protected final double getRange() {
        return mHighestScore - mLowestScore;
    }
}
