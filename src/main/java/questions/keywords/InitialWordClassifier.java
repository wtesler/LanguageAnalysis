package questions.keywords;

import java.util.List;

import classifier.Classifier;
import models.LanguageResponse;

public class InitialWordClassifier extends Classifier<LanguageResponse> {

    @Override
    public void train(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples) {
        setScore("CAN", 1.0);
        setScore("BE", 1.0);
        setScore("HAVE", 1.0);
        setScore("SHOULD", 1.0);
        setScore("WOULD", 1.0);
        setScore("COULD", 1.0);
        setScore("DO", 1.0);
        setScore("WHICH", 1.0);
        setScore("HOW", 1.0);
    }

    @Override
    public boolean classify(LanguageResponse response) {
        //System.out.println("InitialWordClassifier classifies: " + getScore(response.tokens.get(0).lemma.toUpperCase()));

        return getScore(response.tokens.get(0).lemma.toUpperCase()) != null;
    }
}
