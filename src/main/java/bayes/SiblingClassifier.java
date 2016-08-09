package bayes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.DependencyTree;
import models.LanguageResponse;

public class SiblingClassifier extends Classifier {

    private final SyntaxReader mSyntaxReader;

    public SiblingClassifier(SyntaxReader syntaxReader) {
        mSyntaxReader = syntaxReader;
    }

    @Override
    public void train(String dirClassA, String dirClassB) {
        HashMap<String, Double> valueMapA = analyzeTotalSiblingOccurrences(dirClassA);
        HashMap<String, Double> valueMapB = analyzeTotalSiblingOccurrences(dirClassB);

        valueMapA.entrySet()
                .stream()
                .forEach(entry -> {
                    Double statementValue = valueMapB.get(entry.getKey());
                    if (statementValue == null) {
                        statementValue = .00005;
                    }
                    double questionValue = entry.getValue();
                    double a = questionValue / statementValue;
                    double b = statementValue / questionValue;

                    double value = a - b;

                    setValue(entry.getKey(), value);

                    String result = entry.getKey() + " -> " + entry.getValue();
                    System.out.println(result);
                });
    }

    public HashMap<String, Double> analyzeTotalSiblingOccurrences(String directory) {
        List<LanguageResponse> responses = mSyntaxReader.readParsedDataFromFiles(directory);
        List<DependencyTree> trees = SyntaxReader.toDependencyTrees(responses);

        final HashMap<String, Double> siblingCountMap = new HashMap<>();
        trees.stream().forEach(tree -> SiblingTraverser.collectSiblingCountInNode(tree.getRoot(), siblingCountMap));

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
                .sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()));
        //.forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));

        //System.out.println("Total pairs: " + numSiblings.toString());

        return siblingCountMap;
    }

    public void analyzeDependenciesBetweenSiblings(String directory) {
        List<LanguageResponse> responses = mSyntaxReader.readParsedDataFromFiles(directory);
        List<DependencyTree> trees = SyntaxReader.toDependencyTrees(responses);

        final HashMap<String, Double> siblingCountMap = new HashMap<>();
        trees.stream().forEach(tree -> SiblingTraverser.collectSiblingCountInNode(tree.getRoot(),
                siblingCountMap));

        List<String> siblings = siblingCountMap
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (int i = 0; i < siblings.size() - 1; i++) {
            for (int j = i + 1; j < siblings.size(); j++) {
                boolean isAIndependentFromB = false;
                boolean isBIndependentFromA = false;

                for (DependencyTree tree : trees) {
                    if (isAIndependentFromB && isBIndependentFromA) {
                        break;
                    }

                    boolean[] siblingFound = new boolean[2];
                    SiblingTraverser.findSiblingsInTree(
                            tree.getRoot(), siblings.get(i), siblings.get(j), siblingFound);
                    if (siblingFound[0] && !siblingFound[1]) {
                        isAIndependentFromB = true;
                    } else if (!siblingFound[0] && siblingFound[1]) {
                        isBIndependentFromA = true;
                    }
                }

                if (!isAIndependentFromB && !isBIndependentFromA) {
                    System.out.println(siblings.get(i) + " is co-dependent with " + siblings.get(j));
                } else if (!isAIndependentFromB) {
                    System.out.println(siblings.get(i) + " depends on " + siblings.get(j));
                } else if (!isBIndependentFromA) {
                    System.out.println(siblings.get(j) + " depends on " + siblings.get(i));
                }
            }
        }
    }

    @Override
    public void classifyDirectory(String testDirectory) {
        List<LanguageResponse> responses = mSyntaxReader.readParsedDataFromFiles(testDirectory);
        List<DependencyTree> trees = SyntaxReader.toDependencyTrees(responses);

        Integer correctlyClassifed = trees.stream()
                .mapToInt(tree -> {
                    double score = classifyTree(tree, getValues());
                    return score <= 0 ? 0 : 1;
                })
                .sum();

        System.out.println("Accuracy: " + (double) correctlyClassifed / trees.size());
    }

    public double classifySentence(String parsedSentence, final HashMap<String, Double> siblingScoreMap) {
        LanguageResponse response = mSyntaxReader.convertParsedSentence(parsedSentence);
        DependencyTree tree = SyntaxReader.toDependencyTree(response);

        return classifyTree(tree, siblingScoreMap);
    }

    public double classifyTree(DependencyTree tree, final HashMap<String, Double> siblingScoreMap) {
        HashSet<String> siblingSet = new HashSet<>();
        SiblingTraverser.collectSiblingSet(tree.getRoot(), siblingSet);

        double score = siblingSet
                .stream()
                .mapToDouble(siblingPair -> {
                    Double value = siblingScoreMap.get(siblingPair);
                    return value != null ? value: 0;
                })
                .sum();
//
//        if (score >= 0) {
//            System.out.println("----------------");
//
//            siblingSet
//                    .stream()
//                    .forEach(siblingPair -> {
//                        Double value = siblingScoreMap.get(siblingPair);
//                        System.out.println(siblingPair + " -> " + value);
//                    });
//
//            System.out.println("----");
//
//            System.out.println(tree.toString() + "score: " + score);
//        }

        return score;
    }
}
