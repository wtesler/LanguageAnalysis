package question_price.ensemble;

import classifier.ensemble.EnsembleClassifier;
import models.LanguageResponse;
import question_price.bigram.BigramKeywordClassifier;
import question_price.keyword.PriceKeywordClassifier;

public class PriceEnsembleClassifier extends EnsembleClassifier<LanguageResponse> {

    public PriceEnsembleClassifier() {
        addClassifier(new PriceKeywordClassifier());
        addClassifier(new BigramKeywordClassifier());
    }

    @Override
    public double classify(LanguageResponse model, boolean interactive) {
        double decision = super.classify(model, false);
        if (interactive) {
            if (decision > 0) {
                System.out.println("This relates to price.");
            }
        }
        return decision;
    }
}
