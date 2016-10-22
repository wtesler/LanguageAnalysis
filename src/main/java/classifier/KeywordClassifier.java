package classifier;

import models.LanguageResponse;

/**
 * Scores a {@link LanguageResponse} based on whether it contains certain lemma keys. If no keys match, it returns a
 * negative classification.
 */
public abstract class KeywordClassifier extends Classifier<LanguageResponse> {

    @Override
    public final double classify(LanguageResponse response) {
        double scoreSum = response.tokens.stream()
                .mapToDouble(token -> {
                    Double score = getScore(token.lemma.toUpperCase());
                    return score != null ? score : 0;
                })
                .sum();
        return scoreSum > 0 ? scoreSum / response.tokens.size() : -1;
    }
}
