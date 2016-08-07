package bayes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import models.DependencyTree;
import models.LanguageResponse;

public class Classifier {

    private final SyntaxReader mSyntaxReader;

    public Classifier(SyntaxReader syntaxReader) {
        mSyntaxReader = syntaxReader;
    }

    public void classify() {
        HashMap<String, Double> questionsMap = analyzeSiblings("parses/questions_training");

        HashMap<String, Double> statementsMap = analyzeSiblings("parses/statements_training");

        questionsMap.entrySet()
                .stream()
                .forEach(entry -> {
                    Double statementValue = statementsMap.get(entry.getKey());
                    if (statementValue == null) {
                        statementValue = 0.0;
                    }
                    Double questionValue = questionsMap.get(entry.getKey());
                    questionsMap.put(entry.getKey(), questionValue - statementValue);
                });

        questionsMap.entrySet()
                .stream()
                .sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()))
                .forEach(entry -> {
                    String result = entry.getKey() + " -> " + entry.getValue();
                    System.out.println(result);
                });
    }

    private HashMap<String, Double> analyzeSiblings(String directory) {
        List<LanguageResponse> responses = mSyntaxReader.readParsedDataFromFiles(directory);
        List<DependencyTree> trees = SyntaxReader.toDependencyTrees(responses);

        final HashMap<String, Double> siblingCountMap = new HashMap<>();
        trees.stream().forEach(tree -> {
            TreeTraverser.collectSiblingsInNode(tree.getRoot(), siblingCountMap);
        });

        Double numSiblings = siblingCountMap.entrySet()
                .stream()
                .mapToDouble(Map.Entry::getValue)
                .sum();

        siblingCountMap.entrySet()
                .stream()
                .forEach(entry -> {
                    double fraction = entry.getValue() / numSiblings;
                    siblingCountMap.put(entry.getKey(), fraction);
                });

        siblingCountMap.entrySet()
                .stream()
                .sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()))
                .forEach(entry -> {
                    String result = entry.getKey() + " -> " + entry.getValue();
                    //System.out.println(result);
                });

        //System.out.println("Total pairs: " + numSiblings.toString());

        return siblingCountMap;
    }

    private List<String> readListFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
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
}
