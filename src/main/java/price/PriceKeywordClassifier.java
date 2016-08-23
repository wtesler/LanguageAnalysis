package price;

import java.util.List;

import classifier.Classifier;
import models.LanguageResponse;

public class PriceKeywordClassifier extends Classifier<LanguageResponse> {

    @Override
    public void train(
            List<LanguageResponse> positiveExamples,
            List<LanguageResponse> negativeExamples) {
        setScore("COST", 1.0);
        setScore("COSTLY", 1.0);
        setScore("EXPENSIVE", 1.0);
        setScore("CHEAP", 1.0);
        setScore("FREE", 1.0);
        setScore("CREDIT", 1.0);
        setScore("DEBIT", 1.0);
        setScore("DOLLARS", 1.0);
        setScore("PRICE", 1.0);
        setScore("MONEY", 1.0);
        setScore("CASH", 1.0);
    }

    @Override
    public boolean classify(LanguageResponse response) {
        int keywordCount = response.tokens.stream()
                .mapToInt(token -> getScore(token.lemma.toUpperCase()) != null ? 1 : 0)
                .sum();
        return keywordCount != 0;
    }
}
