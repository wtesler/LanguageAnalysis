package models;

public class DependencyTree {

    private final Node[] mNodes;

    private Node mRoot;

    public DependencyTree(Token[] tokens, int startOffset) {
        mNodes = new Node[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            mNodes[i] = new Node(tokens[i]);
        }

        for (int i = 0; i < mNodes.length; i++) {
            Node node = mNodes[i];
            int headTokenIndex = node.token.dependencyEdge.headTokenIndex - startOffset;
            if (headTokenIndex == i) {
                mRoot = node;
            } else {
                mNodes[headTokenIndex].children.add(node);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        printRecursively(getRoot(), builder, "");
        return builder.toString();
    }

    public Node getRoot() {
        return mRoot;
    }

    private static void printRecursively(
            Node node, final StringBuilder builder, final String prefixedSpace) {
        builder.append(prefixedSpace)
                .append(node.token.dependencyEdge.label)
                .append(" (")
                .append(node.token.text.content)
                .append(")\n");
        node.children.stream().forEach(child -> printRecursively(child, builder, prefixedSpace + "\t"));
    }
}
