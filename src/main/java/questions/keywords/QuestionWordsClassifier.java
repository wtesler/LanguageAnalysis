package questions.keywords;

import java.util.List;

import classifier.Classifier;
import models.LanguageResponse;

public class QuestionWordsClassifier extends Classifier<LanguageResponse, String> {

    @Override
    public void train(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples,
                      boolean interactive) {
        setScore("WHO", 1.0);
        setScore("WHAT", 1.0);
        setScore("WHEN", 1.0);
        setScore("WHERE", 1.0);
        setScore("WHY", 1.0);
        setScore("HOW", 1.0);
        setScore("WHICH", 1.0);
    }

    @Override
    public double classify(LanguageResponse response, boolean interactive) {
        if (getScore(response.tokens.get(0).lemma.toUpperCase()) != null) {
            return 1;
        } else if (response.tokens.size() > 1
                && getScore(response.tokens.get(response.tokens.size() - 1).lemma.toUpperCase()) != null) {
            return 1;
        } else if (response.tokens.size() > 2
                && getScore(response.tokens.get(response.tokens.size() - 2).lemma.toUpperCase()) != null) {
            return .5;
        } else {
            return -1;
        }

//        List<Token> tokens = new ArrayList<>(response.tokens);
////        if (tokens.size() > 0) {
////            tokens.remove(0);
////        }
//
//        int keywordCount = tokens
//                .stream()
//                .mapToInt(token -> getScore(token.lemma.toUpperCase()) != null ? 1 : 0)
//                .sum();
//        return keywordCount != 0 ? 1 : -1;
    }
}
