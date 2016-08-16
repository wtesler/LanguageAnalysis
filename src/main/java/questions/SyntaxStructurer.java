package questions;

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

public class SyntaxStructurer {

    private Gson mGson;

    public SyntaxStructurer(Gson gson) {
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
}
