package com.main.ecommerceprototype.CMS;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String name;
    private List<Node> children;

    private String parameters;

    public Node(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public Node(String name, List<Node> children) {
        this.name = name;
        this.children = children;
    }

    public Node(String name, String parameters, List<Node> children) {
        this.name = name;
        this.parameters = parameters;
        this.children = children;
    }
    public Node(String name, String parameters) {
        this.name = name;
        this.parameters = parameters;
        this.children = new ArrayList<>();
    }
    public String getName() {
        return name;
    }
    public List<Node> getChildren() {
        return children;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}
