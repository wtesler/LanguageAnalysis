package classifier.ensemble;

import java.util.ArrayList;
import java.util.List;

import classifier.Classifier;
import utils.VisUtils;

public abstract class EnsembleClassifier<T> extends Classifier<T, Void> {
    private List<Classifier<T, ?>> mClassifiers = new ArrayList<>();

    @Override
    public void train(List<T> positiveExamples, List<T> negativeExamples, boolean interactive) {
        mClassifiers.forEach(classifier -> classifier.train(positiveExamples, negativeExamples, interactive));
    }

    @Override
    public void test(List<T> positiveExamples, List<T> negativeExamples, boolean interactive) {
        mClassifiers.forEach(classifier -> classifier.test(positiveExamples, negativeExamples, interactive));
        super.test(positiveExamples, negativeExamples, interactive);
    }

    @Override
    public double classify(T model, boolean interactive) {
        double totalClassification = mClassifiers.stream().mapToDouble(classifier -> {
            double decision = classifier.classify(model);
            double confidence = decision > 0 ? classifier.getPositiveConfidence() : classifier.getNegativeConfidence();
            double classification = decision * confidence;
            if (interactive) {
                System.out.println(
                        VisUtils.toGauge(classification)
                                + " " + classifier.getClass().getSimpleName() + ": " + classification);
            }
            return classification;
        })
        .sum();

        totalClassification /= mClassifiers.size();

        if (interactive) {
            System.out.println(
                    VisUtils.toGauge(totalClassification)
                            + " " + getClass().getSimpleName() + ": " + totalClassification);
        }
        return totalClassification;
    }

    public void addClassifier(Classifier<T, ?> classifier) {
        mClassifiers.add(classifier);
    }
}
