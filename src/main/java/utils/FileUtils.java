package utils;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import models.language.LanguageResponse;

public class FileUtils {

    private FileUtils() { }

    /**
     *
     * @param directory
     * @param fileName i.e. "words.txt"
     * @param content
     */
    public static void writeTextToFile(String directory, String fileName, String content) {
        String qualifiedName = directory + "/" + fileName;
        if (!Files.isDirectory(Paths.get(directory))) {
            try {
                Files.createDirectories(Paths.get(directory));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(qualifiedName))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readListFromResources(String fileName, Class context) {
        ClassLoader classLoader = context.getClassLoader();
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

    public static void suffixFilenames(String directory, String suffix) {
        try {
            Files.list(new File(directory).toPath())
                    .forEach(path -> {
                        path.toFile().renameTo(new File(path.toString() + suffix));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<LanguageResponse> parseLanguageResponsesFromFiles(String directory, Gson gson) {
        try {
            return Files.list(new File(directory).toPath())
                    .collect(Collectors.mapping((Function<Path, LanguageResponse>) path -> {
                        try {
                            String content = new String(Files.readAllBytes(path));
                            LanguageResponse response = gson.fromJson(content, LanguageResponse.class);
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
}
