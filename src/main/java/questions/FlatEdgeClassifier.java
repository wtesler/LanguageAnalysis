package questions;

import classifier.Classification;
import classifier.Classifier;
import models.LanguageResponse;

public class FlatEdgeClassifier extends Classifier<LanguageResponse> {

    @Override
    public void train(String positiveDir, String negativeDir) {

    }

    @Override
    public Classification classifyDirectory(String dir) {
        return null;
    }

    @Override
    public boolean classify(LanguageResponse response) {
        return false;
    }
}
