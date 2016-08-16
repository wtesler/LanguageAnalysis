package questions;

import java.util.ArrayList;
import java.util.List;

import classifier.Classifier;
import models.LanguageResponse;

public class EnsembleQuestionClassifier extends Classifier<LanguageResponse> {

    private final SyntaxStructurer mSyntaxStructurer;

    private List<Classifier<LanguageResponse>> mClassifiers = new ArrayList<>();

    public EnsembleQuestionClassifier(SyntaxStructurer syntaxStructurer) {
        mSyntaxStructurer = syntaxStructurer;

        //mClassifiers.add(new SiblingClassifier(mSyntaxStructurer));
        mClassifiers.add(new FlatEdgeClassifier(mSyntaxStructurer));
        mClassifiers.add(new QuestionWordsClassifier());
        mClassifiers.add(new InitialWordClassifier());
    }

    @Override
    public void train(String positiveDir, String negativeDir) {
        mClassifiers.forEach(classifier -> classifier.train(positiveDir, negativeDir));
    }

    @Override
    public void test(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples) {
        mClassifiers.forEach(classifier -> classifier.test(positiveExamples, negativeExamples));
        super.test(positiveExamples, negativeExamples);
    }

    @Override
    public boolean classify(LanguageResponse response) {
        return classify(response, false);
    }

    public boolean classify(LanguageResponse response, boolean interactive) {
        double consensus = mClassifiers.stream().mapToDouble(classifier -> {
            boolean decision = classifier.classify(response);
            double confidence = decision ? classifier.getPositiveConfidence() : classifier.getNegativeConfidence();
            return (decision ? 1 : -1) * confidence;
        }).sum();

        if (interactive) {
            System.out.println("confidence: " + consensus);
        }
        return consensus > 0;
    }
}
