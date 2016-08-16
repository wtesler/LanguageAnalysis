package questions;

import java.util.HashMap;
import java.util.List;

import classifier.Classifier;
import models.LanguageResponse;

public class PosClassifier extends Classifier<LanguageResponse> {

    private final SyntaxStructurer mSyntaxStructurer;

    public PosClassifier(SyntaxStructurer syntaxStructurer) {
        mSyntaxStructurer = syntaxStructurer;
    }

    @Override
    public void train(String positiveDir, String negativeDir) {
        List<LanguageResponse> positiveResponses = mSyntaxStructurer.readParsedDataFromFiles(positiveDir);
        List<LanguageResponse> negativeResponses = mSyntaxStructurer.readParsedDataFromFiles(negativeDir);

        HashMap<String, Double> positiveFrequencies = getSiblingFrequencyMap(positiveResponses);
        HashMap<String, Double> negativeFrequencies = getSiblingFrequencyMap(negativeResponses);
    }

    @Override
    public boolean classify(LanguageResponse response) {
        //return getScore(response.tokens.get(0).lemma.toUpperCase()) != null;
//
        int keywordCount = response.tokens
                .stream()
                .mapToInt(token -> getScore(token.lemma.toUpperCase()) != null ? 1 : 0)
                .sum();
        return keywordCount != 0;
    }
}
