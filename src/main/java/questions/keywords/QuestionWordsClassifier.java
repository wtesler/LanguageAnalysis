package questions.keywords;

import java.util.List;

import classifier.Classifier;
import models.LanguageResponse;

public class QuestionWordsClassifier extends Classifier<LanguageResponse> {

    @Override
    public void train(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples) {
        setScore("WHO", 1.0);
        setScore("WHAT", 1.0);
        setScore("WHEN", 1.0);
        setScore("WHERE", 1.0);
        setScore("WHY", 1.0);
        setScore("HOW", 1.0);
    }

    @Override
    public boolean classify(LanguageResponse response) {
        //return getScore(response.tokens.get(0).lemma.toUpperCase()) != null;
//
        int keywordCount = response.tokens
                .stream()
                .mapToInt(token -> getScore(token.lemma.toUpperCase()) != null ? 1 : 0)
                .sum();
        return keywordCount != 0;
    }
}
