package bayes;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import models.DependencyTree;
import models.LanguageResponse;
import models.Token;

public class SyntaxReader {

    private Gson mGson;

    public SyntaxReader(Gson gson) {
        mGson = gson;
    }

    public List<LanguageResponse> readParsedDataFromFiles(String directory) {
        try {
            return Files.list(new File(directory).toPath())
                    .collect(Collectors.mapping((Function<Path, LanguageResponse>) path -> {
                        try {
                            String content = new String(Files.readAllBytes(path));
                            return mGson.fromJson(content, LanguageResponse.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }, Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Assumes that each language response contains one sentence.
     */
    public static List<DependencyTree> toDependencyTrees(List<LanguageResponse> responses) {
        return responses.stream()
                .map(languageResponse -> {
                    Token[] sentenceTokens = languageResponse.tokens.toArray(new Token[languageResponse.tokens.size()]);
                    return new DependencyTree(sentenceTokens, 0);
                })
                .collect(Collectors.toList());
    }
}
