package models;

import java.util.ArrayList;

public class Node {

    public Token token;
    public ArrayList<Node> children = new ArrayList<>();

    public Node(Token token) {
        this.token = token;
    }
}
