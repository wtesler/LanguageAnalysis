package questions;

import java.util.ArrayList;
import java.util.List;

import classifier.Classification;
import classifier.Classifier;
import models.LanguageResponse;

public class EnsembleQuestionClassifier extends Classifier<LanguageResponse> {

    private final SyntaxReader mSyntaxReader;

    private List<Classifier<LanguageResponse>> mClassifiers = new ArrayList<>();

    public EnsembleQuestionClassifier(SyntaxReader syntaxReader) {
        mSyntaxReader = syntaxReader;

        mClassifiers.add(new SiblingClassifier(mSyntaxReader));
        mClassifiers.add(new QuestionWordsClassifier(mSyntaxReader));
        mClassifiers.add(new InitialWordClassifier(mSyntaxReader));
    }

    @Override
    public void train(String positiveDir, String negativeDir) {
        mClassifiers.forEach(classifier -> classifier.train(positiveDir, negativeDir));
    }

    @Override
    public void test(String positiveDir, String negativeDir) {
        mClassifiers.forEach(classifier -> classifier.test(positiveDir, negativeDir));
        super.test(positiveDir, negativeDir);
    }

    @Override
    public boolean classify(LanguageResponse response) {
        double consensus = mClassifiers.stream().mapToDouble(classifier -> {
            boolean decision = classifier.classify(response);
            double confidence = decision ? classifier.getPositiveConfidence() : classifier.getNegativeConfidence();
            return (decision ? 1 : -1) * confidence;
        }).sum();

        System.out.println("consensus: " + consensus);
        return consensus > 0;
    }

    @Override
    public Classification classifyDirectory(String dir) {
        List<LanguageResponse> responses = mSyntaxReader.readParsedDataFromFiles(dir);
        return classifyObjects(responses);
    }
}
