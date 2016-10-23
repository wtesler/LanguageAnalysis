package price.keyword;

import java.util.List;

import classifier.Classifier;
import classifier.KeywordClassifier;
import models.LanguageResponse;

public class PriceKeywordClassifier extends KeywordClassifier {

    @Override
    public void train(
            List<LanguageResponse> positiveExamples,
            List<LanguageResponse> negativeExamples,
            boolean interactive) {
        setScore("COST", 1.0);
        setScore("COSTLY", 1.0);
        setScore("COSTLIEST", 1.0);
        setScore("EXPENSIVE", 1.0);
        setScore("CHEAP", 1.0);
        setScore("CHEAPER", 1.0);
        setScore("CHEAPEST", 1.0);
        setScore("FREE", 1.0);
        setScore("DOLLARS", 1.0);
        setScore("PRICE", 1.0);
        setScore("MONEY", 1.0);
        setScore("CASH", 1.0);
    }
}
