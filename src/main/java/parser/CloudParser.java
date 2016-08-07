package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import app.App;
import cloud.LanguageClient;
import models.Features;
import models.LanguageDocument;
import models.LanguageRequest;
import rx.schedulers.Schedulers;

public class CloudParser {

    private final LanguageClient mLanguageClient;

    public CloudParser(App app, LanguageClient languageClient) {
        app.getAppComponent().inject(this);
        mLanguageClient = languageClient;
    }

    public void parseSentencesResource(String resourceName, String outputDirectory) {
        List<String> list = readListFromResources(resourceName);
        if (list == null) {
            return;
        }

        list.stream().forEach(sentence -> {
            LanguageRequest languageRequest = constructLanguageRequest(sentence);
            mLanguageClient.rawParse(languageRequest)
                    .observeOn(Schedulers.newThread())
                    .toBlocking()
                    .subscribe(responseBody -> {
                        try {
                            writeToDirectory(
                                    outputDirectory,
                                    UUID.randomUUID().toString(),
                                    responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }, throwable -> System.err.println(throwable.toString()));
        });
    }

    private List<String> readListFromResources(String fileName) {
        ClassLoader classLoader = CloudParser.class.getClassLoader();
        URL fileUrl = classLoader.getResource(fileName);
        if (fileUrl == null) {
            System.err.println("Resource URL not found.");
            return null;
        }
        String qualifiedName = fileUrl.getFile();
        if (qualifiedName == null) {
            System.err.println("Could not get file from URL.");
            return null;
        }

        File file = new File(qualifiedName);
        try (Stream<String> stream = Files.lines(file.toPath())) {
            return stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void writeToDirectory(String directory, String file, String content) {
        String qualifiedName = directory + "/" + file + ".txt";
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(qualifiedName))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
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
