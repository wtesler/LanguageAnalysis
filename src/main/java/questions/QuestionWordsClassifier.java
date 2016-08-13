package questions;

import java.util.List;

import classifier.Classification;
import classifier.Classifier;
import models.LanguageResponse;

public class QuestionWordsClassifier extends Classifier<LanguageResponse> {

    private final SyntaxReader mSyntaxReader;

    public QuestionWordsClassifier(SyntaxReader syntaxReader) {
        mSyntaxReader = syntaxReader;
    }

    @Override
    public void train(String positiveDir, String negativeDir) {
        setScore("WHO", 1.0);
        setScore("WHAT", 1.0);
        setScore("WHEN", 1.0);
        setScore("WHERE", 1.0);
        setScore("WHY", 1.0);
        setScore("HOW", 1.0);
    }

    @Override
    public Classification classifyDirectory(String dir) {
        List<LanguageResponse> responses = mSyntaxReader.readParsedDataFromFiles(dir);
        return classifyObjects(responses);
    }

    @Override
    public boolean classify(LanguageResponse response) {
        return getScore(response.tokens.get(0).lemma.toUpperCase()) != null;
//
//        int keywordCount = response.tokens
//                .stream()
//                .mapToInt(token -> getScore(token.lemma.toUpperCase()) != null ? 1 : 0)
//                .sum();
//        return keywordCount != 0;
    }
}
