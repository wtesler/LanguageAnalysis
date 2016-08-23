package questions.keywords;

import java.util.List;

import classifier.Classifier;
import models.LanguageResponse;

public class InitialWordClassifier extends Classifier<LanguageResponse> {

    @Override
    public void train(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples) {
        setScore("CAN", 1.0);
        setScore("IS", 1.0);
        setScore("HAS", 1.0);
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
