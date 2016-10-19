package utils;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.DependencyTree;
import models.LanguageResponse;
import models.Node;
import models.Token;
import questions.siblings.SiblingTraverser;

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

    public static void analyzeDependenciesBetweenSiblings(String directory, Gson gson) {
        List<LanguageResponse> responses = FileUtils.parseLanguageResponsesFromFiles(directory, gson);
        List<DependencyTree> trees = LanguageUtils.toDependencyTrees(responses);

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
                    findSiblingsInTree(tree.getRoot(), siblings.get(i), siblings.get(j), siblingFound);
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

    private static void findSiblingsInTree(Node node, String siblingA, String siblingB, boolean[] siblingFound) {
        for (int i = 0; i < node.children.size() - 1; i++) {
            String nodeSibling = node.children.get(i).token.dependencyEdge.label
                    + ","
                    + node.children.get(i + 1).token.dependencyEdge.label;

            if (nodeSibling.equals(siblingA)) {
                siblingFound[0] = true;
            } else if (nodeSibling.equals(siblingB)) {
                siblingFound[1] = true;
            }

            findSiblingsInTree(node.children.get(i), siblingA, siblingB, siblingFound);
        }
    }
}
