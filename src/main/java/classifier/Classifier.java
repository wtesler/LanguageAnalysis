package classifier;

import java.util.HashMap;
import java.util.List;

public abstract class Classifier<T> {

    private final HashMap<String, Double> mScoreMap = new HashMap<>();

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

    public abstract Classification classifyDirectory(String dir);

    public abstract boolean classify(T object);

    protected final Classification classifyObjects(List<T> objects) {
        Integer correctlyClassifed = objects.stream()
                .mapToInt(response -> classify(response) ? 1 : 0)
                .sum();

        Classification classification = new Classification();
        classification.numPositive = correctlyClassifed;
        classification.total = objects.size();

        return classification;
    }

    /**
     * Use the data to set a confidence level.
     */
    public void test(String positiveDir, String negativeDir) {
        int truePositives = 0;
        int falseNegatives = 0;
        int falsePositives = 0;
        int trueNegatives = 0;

        Classification positiveClassification = classifyDirectory("parses/general_questions_testing");
        truePositives += positiveClassification.numPositive;
        falseNegatives += positiveClassification.total - positiveClassification.numPositive;

        Classification negativeClassification = classifyDirectory("parses/general_responses_testing");
        falsePositives += negativeClassification.numPositive;
        trueNegatives += negativeClassification.total - negativeClassification.numPositive;

        double positiveConfidence = (double) truePositives / (truePositives + falsePositives);
        positiveConfidence = -Math.log(1 - positiveConfidence);
        if (positiveConfidence == Double.POSITIVE_INFINITY) {
            positiveConfidence = 10;
        }

        double negativeConfidence = (double) trueNegatives / (trueNegatives + falseNegatives);
        negativeConfidence = -Math.log(1 - negativeConfidence);
        if (negativeConfidence == Double.POSITIVE_INFINITY) {
            negativeConfidence = 10;
        }

        setPositiveConfidence(positiveConfidence);
        setNegativeConfidence(negativeConfidence);
        System.out.println("Positive Confidence: " + getPositiveConfidence());
        System.out.println("Negative Confidence: " + getNegativeConfidence());
    }

    protected Double getScore(String key) {
        return mScoreMap.get(key);
    }

    protected HashMap<String, Double> getScores() {
        return mScoreMap;
    }

    protected void setScore(String key, Double value) {
        mScoreMap.put(key, value);
    }

    protected void addScores(HashMap<String, Double> values) {
        mScoreMap.putAll(values);
    }

    public Double getPositiveConfidence() {
        return mPositiveConfidence;
    }

    public void setPositiveConfidence(double confidence) {
        mPositiveConfidence = confidence;
    }

    public Double getNegativeConfidence() {
        return mNegativeConfidence;
    }

    public void setNegativeConfidence(double confidence) {
        mNegativeConfidence = confidence;
    }
}
