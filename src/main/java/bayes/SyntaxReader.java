package bayes;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import models.DependencyEdge;
import models.DependencyTree;
import models.LanguageResponse;
import models.Node;
import models.TextSpan;
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
                            LanguageResponse response = mGson.fromJson(content, LanguageResponse.class);
                            if (response.tokens.size() == 0) {
                                System.err.println(path + " did not produce tokens");
                            }
                            return response;
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

    public LanguageResponse convertParsedSentence(String parsedSentence) {
        return mGson.fromJson(parsedSentence, LanguageResponse.class);
    }

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
                .map(SyntaxReader::toDependencyTree)
                .collect(Collectors.toList());
    }
}
