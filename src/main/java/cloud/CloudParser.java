package cloud;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import models.language.Features;
import models.language.LanguageDocument;
import models.language.LanguageRequest;
import models.language.LanguageResponse;
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
