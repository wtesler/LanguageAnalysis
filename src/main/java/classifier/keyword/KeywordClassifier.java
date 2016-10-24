package classifier.keyword;

import classifier.Classifier;
import models.language.LanguageResponse;

/**
 * Scores a {@link LanguageResponse} based on whether it contains certain lemma keys. If no keys match, it returns a
 * negative classification.
 */
public abstract class KeywordClassifier extends Classifier<LanguageResponse, String> {

    @Override
    public final double classify(LanguageResponse response, boolean interactive) {
        double scoreSum = response.tokens.stream()
                .mapToDouble(token -> {
                    Double score = getScore(token.lemma.toUpperCase());
                    return score != null ? score : 0;
                })
                .sum();
        return scoreSum > 0 ? 1 : -1;
    }
}
