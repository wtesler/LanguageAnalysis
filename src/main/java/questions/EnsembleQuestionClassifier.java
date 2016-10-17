package questions;

import java.util.ArrayList;
import java.util.List;

import classifier.Classifier;
import cloud.CloudParser;
import models.LanguageResponse;
import questions.flat_edge.FlatEdgeClassifier;
import questions.keywords.InitialWordClassifier;
import questions.keywords.QuestionWordsClassifier;
import questions.partofspeech.PosBigramClassifier;
import questions.siblings.SiblingClassifier;

public class EnsembleQuestionClassifier extends Classifier<LanguageResponse> {

    private final CloudParser mCloudParser;

    private List<Classifier<LanguageResponse>> mClassifiers = new ArrayList<>();

    public EnsembleQuestionClassifier(CloudParser cloudParser) {
        mCloudParser = cloudParser;

        mClassifiers.add(new SiblingClassifier(cloudParser));
        mClassifiers.add(new FlatEdgeClassifier(cloudParser));
        mClassifiers.add(new QuestionWordsClassifier());
        mClassifiers.add(new InitialWordClassifier());
        mClassifiers.add(new PosBigramClassifier(cloudParser));
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
    public boolean classify(LanguageResponse response) {
        return classify(response, false);
    }

    public boolean classify(LanguageResponse response, boolean interactive) {
        double totalClassification = mClassifiers.stream().mapToDouble(classifier -> {
            boolean decision = classifier.classify(response);
            double confidence = decision ? classifier.getPositiveConfidence() : classifier.getNegativeConfidence();
            double classification = (decision ? 1 : -1) * confidence;
            if (interactive) {
                System.out.println(classifier.getClass().getSimpleName() + ": " + classification);
            }
            return classification;
        })
        .sum();

        totalClassification /= mClassifiers.size();

        if (interactive) {
            System.out.println("Total Classification: " + totalClassification);
        }
        return totalClassification > 0;
    }
}
