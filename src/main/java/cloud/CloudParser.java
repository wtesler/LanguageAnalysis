package cloud;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import models.Features;
import models.LanguageDocument;
import models.LanguageRequest;
import models.LanguageResponse;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import utils.FileUtils;

public class CloudParser {

    private final Gson mGson;
    private final LanguageClient mLanguageClient;

    private final PublishSubject<String> mParsedSentenceSubject = PublishSubject.create();

    public CloudParser(Gson gson, LanguageClient languageClient) {
        mGson = gson;
        mLanguageClient = languageClient;
    }

    public void parseSentencesResource(String resourceName, String outputDirectory) {
        List<String> list = FileUtils.readListFromResources(resourceName, getClass());
        if (list == null) {
            return;
        }

        final Integer[] count = new Integer[]{ 0 };
        list.stream().forEach(sentence -> {

            LanguageRequest languageRequest = constructLanguageRequest(sentence);
            mLanguageClient.rawParse(languageRequest)
                    .observeOn(Schedulers.newThread())
                    .toBlocking()
                    .subscribe(responseBody -> {
                        try {
                            FileUtils.writeTextToFile(
                                    outputDirectory,
                                    count[0].toString() + ".txt",
                                    responseBody.string());
                            count[0]++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }, throwable -> System.err.println(throwable.toString()));
        });
    }

    public void parseSentence(String sentence) {
        LanguageRequest languageRequest = constructLanguageRequest(sentence);
        mLanguageClient.rawParse(languageRequest)
                .observeOn(Schedulers.newThread())
                .toBlocking()
                .subscribe(responseBody -> {
                    try {
                        mParsedSentenceSubject.onNext(responseBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, throwable -> System.err.println(throwable.toString()));
    }

    public Observable<String> getParsedSentenceObservable() {
        return mParsedSentenceSubject.asObservable();
    }

    public LanguageResponse convertParsedSentence(String parsedSentence) {
        return mGson.fromJson(parsedSentence, LanguageResponse.class);
    }

    public List<LanguageResponse> parseDataFromFiles(String directory) {
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

    private static LanguageRequest constructLanguageRequest(String content) {
        LanguageDocument document = new LanguageDocument();
        document.content = content;
        document.language = LanguageDocument.ENGLISH;
        document.type = LanguageDocument.PLAIN_TEXT;

        Features features = new Features();
        features.extractSyntax = true;

        LanguageRequest request = new LanguageRequest();
        request.document = document;
        request.features = features;
        request.encodingType = LanguageRequest.UTF8;

        return request;
    }
}