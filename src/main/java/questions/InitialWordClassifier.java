package questions;

import classifier.Classifier;
import models.LanguageResponse;

public class InitialWordClassifier extends Classifier<LanguageResponse> {

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
        setScore("WHICH", 1.0);
    }

    @Override
    public boolean classify(LanguageResponse response) {
        return getScore(response.tokens.get(0).lemma.toUpperCase()) != null;
    }
}
