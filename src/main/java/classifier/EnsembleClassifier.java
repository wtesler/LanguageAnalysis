package classifier;

import java.util.ArrayList;
import java.util.List;

import utils.VisUtils;

public abstract class EnsembleClassifier<T> extends Classifier<T> {
    private List<Classifier<T>> mClassifiers = new ArrayList<>();

    @Override
    public void train(List<T> positiveExamples, List<T> negativeExamples) {
        mClassifiers.forEach(classifier -> classifier.train(positiveExamples, negativeExamples));
    }

    @Override
    public void test(List<T> positiveExamples, List<T> negativeExamples) {
        mClassifiers.forEach(classifier -> classifier.test(positiveExamples, negativeExamples));
        super.test(positiveExamples, negativeExamples);
    }

    @Override
    public double classify(T model) {
        return classify(model, false);
    }

    public double classify(T model, boolean interactive) {
        double totalClassification = mClassifiers.stream().mapToDouble(classifier -> {
            double decision = classifier.classify(model);
            double confidence = decision > 0 ? classifier.getPositiveConfidence() : classifier.getNegativeConfidence();
            double classification = decision * confidence;
            if (interactive) {
                System.out.println(
                        VisUtils.toGauge(classification)
                                + " " + classifier.getClass().getSimpleName()
                                + ": " + classification);
            }
            return classification;
        })
                .sum();

        totalClassification /= mClassifiers.size();

        if (interactive) {
            System.out.println(
                    VisUtils.toGauge(totalClassification)
                            + " " + getClass().getSimpleName()
                            + ": " + totalClassification);
        }
        return totalClassification;
    }

    public void addClassifier(Classifier<T> classifier) {
        mClassifiers.add(classifier);
    }
}
