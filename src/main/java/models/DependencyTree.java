package models;

import java.util.ArrayList;

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

    public Node getRoot() {
        return mRoot;
    }

    public class Node {

        public Token token;
        public ArrayList<Node> children = new ArrayList<>();

        public Node(Token token) {
            this.token = token;
        }
    }
}
