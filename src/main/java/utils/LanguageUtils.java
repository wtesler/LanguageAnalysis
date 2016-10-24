package utils;

import java.util.List;
import java.util.stream.Collectors;

import models.language.DependencyTree;
import models.language.LanguageResponse;
import models.language.Token;

public class LanguageUtils {

    private LanguageUtils() { }

    /**
     * Assumes that each language response contains one sentence.
     */
    public static DependencyTree toDependencyTree(LanguageResponse response) {
        Token[] sentenceTokens = response.tokens.toArray(new Token[response.tokens.size()]);
        return new DependencyTree(sentenceTokens, 0);
    }

    /**
     * Assumes that each language response contains one sentence.
     */
    public static List<DependencyTree> toDependencyTrees(List<LanguageResponse> responses) {
        return responses.stream()
                .map(LanguageUtils::toDependencyTree)
                .collect(Collectors.toList());
    }
}
