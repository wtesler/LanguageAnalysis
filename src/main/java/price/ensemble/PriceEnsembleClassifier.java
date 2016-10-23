package price.ensemble;

import classifier.ensemble.EnsembleClassifier;
import models.LanguageResponse;
import price.bigram.BigramKeywordClassifier;
import price.keyword.PriceKeywordClassifier;

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
            } else {
                System.out.println("This does not relate to price.");
            }
        }
        return decision;
    }
}
