package questions;

import java.util.List;

import classifier.Classification;
import classifier.Classifier;
import models.LanguageResponse;

public class InitialWordClassifier extends Classifier<LanguageResponse> {

    private final SyntaxReader mSyntaxReader;

    public InitialWordClassifier(SyntaxReader syntaxReader) {
        mSyntaxReader = syntaxReader;
    }

    @Override
    public void train(String positiveDir, String negativeDir) {
        setScore("CAN", 1.0);
        setScore("IS", 1.0);
        setScore("ARE", 1.0);
        setScore("SHOULD", 1.0);
        setScore("WOULD", 1.0);
        setScore("COULD", 1.0);
        setScore("DO", 1.0);
        setScore("DOES", 1.0);
    }

    @Override
    public Classification classifyDirectory(String dir) {
        List<LanguageResponse> responses = mSyntaxReader.readParsedDataFromFiles(dir);
        return classifyObjects(responses);
    }

    @Override
    public boolean classify(LanguageResponse response) {
        return getScore(response.tokens.get(0).lemma.toUpperCase()) != null;
    }
}
