package questions;

import java.util.ArrayList;
import java.util.List;

import classifier.Classifier;
import models.LanguageResponse;
import questions.root_child_pos.RootChildPosClassifier;
import questions.flat_edge.FlatEdgeClassifier;
import questions.keywords.InitialWordClassifier;
import questions.keywords.QuestionWordsClassifier;
import questions.partofspeech.PosBigramClassifier;
import questions.siblings.SiblingClassifier;
import utils.VisUtils;

public class EnsembleQuestionClassifier extends Classifier<LanguageResponse> {

    private List<Classifier<LanguageResponse>> mClassifiers = new ArrayList<>();

    public EnsembleQuestionClassifier() {
        mClassifiers.add(new SiblingClassifier());
        mClassifiers.add(new FlatEdgeClassifier());
        mClassifiers.add(new QuestionWordsClassifier());
        mClassifiers.add(new InitialWordClassifier());
        mClassifiers.add(new PosBigramClassifier());
    }

    @Override
    public void train(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples) {
        mClassifiers.forEach(classifier -> classifier.train(positiveExamples, negativeExamples));
    }

    @Override
    public void test(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples) {
        mClassifiers.forEach(classifier -> classifier.test(positiveExamples, negativeExamples));
        super.test(positiveExamples, negativeExamples);
    }

    @Override
    public double classify(LanguageResponse response) {
        return classify(response, false);
    }

    public double classify(LanguageResponse response, boolean interactive) {
        double totalClassification = mClassifiers.stream().mapToDouble(classifier -> {
            double decision = classifier.classify(response);
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
}
