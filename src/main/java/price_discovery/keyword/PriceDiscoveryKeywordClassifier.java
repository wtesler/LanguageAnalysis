package price_discovery.keyword;

import java.util.List;

import classifier.keyword.KeywordClassifier;
import models.language.LanguageResponse;

public class PriceDiscoveryKeywordClassifier extends KeywordClassifier {

    @Override
    public void train(
            List<LanguageResponse> positiveExamples,
            List<LanguageResponse> negativeExamples,
            boolean interactive) {
        setScore("SEE", 1.0);
        setScore("WHERE", 0.8);
        setScore("SAY", 0.5);
        setScore("FIND", 0.5);
        setScore("SHOW", 0.2);
    }
}
