package price_discovery.ensemble;

import classifier.ensemble.EnsembleClassifier;
import models.language.LanguageResponse;
import price_discovery.keyword.PriceDiscoveryKeywordClassifier;

public class PriceDiscoveryEnsembleClassifier extends EnsembleClassifier<LanguageResponse> {

    public PriceDiscoveryEnsembleClassifier() {
        addClassifier(new PriceDiscoveryKeywordClassifier());
    }

    @Override
    public double classify(LanguageResponse model, boolean interactive) {
        double decision = super.classify(model, false);
        if (interactive) {
            if (decision > 0) {
                System.out.println("Client wants to discover a price.");
            }
        }
        return decision;
    }
}
