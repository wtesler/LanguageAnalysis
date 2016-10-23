package price.bigram;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import classifier.Classifier;
import models.LanguageResponse;

public class BigramKeywordClassifier extends Classifier<LanguageResponse, String> {

    private final List<String> ILLEGAL_LEMMAS = new ArrayList<>();

    public BigramKeywordClassifier() {
        ILLEGAL_LEMMAS.add(".");
        ILLEGAL_LEMMAS.add(",");
        ILLEGAL_LEMMAS.add(";");
    }

    @Override
    public void train(List<LanguageResponse> positiveExamples, List<LanguageResponse> negativeExamples,
                      boolean interactive) {
        setScore("HOW MUCH", 1.0);
    }

    @Override
    public double classify(LanguageResponse response, boolean interactive) {
        double classification = getModelStream(response).mapToDouble(bigram -> {
            Double score = getScore(bigram);
            return score != null ? score : 0;
        }).sum();
        return classification > 0 ? 1 : -1;
    }

    private Stream<String> getModelStream(LanguageResponse response) {
        return IntStream.range(0, response.tokens.size())
                .mapToObj(i -> {
                    if (i == response.tokens.size() - 1) {
                        return null;
                    }

                    String label1 = response.tokens.get(i).lemma.toUpperCase();
                    String label2 = response.tokens.get(i + 1).lemma.toUpperCase();

                    if (ILLEGAL_LEMMAS.contains(label1) || ILLEGAL_LEMMAS.contains(label2)) {
                        return null;
                    }

                    return label1 + " " + label2;
                })
                .filter(s -> s != null);
    }
}
