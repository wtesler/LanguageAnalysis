package price.ensemble;

import classifier.EnsembleClassifier;
import models.LanguageResponse;
import price.keyword.PriceKeywordClassifier;

public class EnsemblePriceClassifier extends EnsembleClassifier<LanguageResponse> {

    public EnsemblePriceClassifier() {
        addClassifier(new PriceKeywordClassifier());
    }
}
