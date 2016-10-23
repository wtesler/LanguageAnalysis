package questions.flatedge;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import classifier.frequency.FlatFrequencyCollector;
import models.LanguageResponse;

public class FlatEdgeCollector extends FlatFrequencyCollector<LanguageResponse, String> {

    public Stream<String> getModelStream(LanguageResponse response) {
        return IntStream.range(0, response.tokens.size())
                .mapToObj(i -> {
                    if (i == response.tokens.size() - 1) {
                        return null;
                    }

                    String label1 = response.tokens.get(i).lemma;
                    String label2 = response.tokens.get(i + 1).lemma;
                    return label1 + "," + label2;
                })
                .filter(s -> s != null);
    }
}
