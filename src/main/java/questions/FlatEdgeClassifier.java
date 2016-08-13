package questions;

import classifier.Classifier;
import models.LanguageResponse;

public class FlatEdgeClassifier extends Classifier<LanguageResponse> {

    @Override
    public void train(String positiveDir, String negativeDir) {

    }

    @Override
    public boolean classify(LanguageResponse response) {
        return false;
    }
}
