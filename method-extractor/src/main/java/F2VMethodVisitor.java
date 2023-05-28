package src.main.java;

import                   com.github.javaparser.ast.Node;
import                 com.github.javaparser.ast.stmt.*;
import                 com.github.javaparser.ast.expr.*;
import com.github.javaparser.printer.configuration.*;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;

import      java.io.BufferedWriter;
import java.util.stream.Collectors;
import java.util.*;

import org.apache.commons.lang3.tuple.MutablePair;
import        org.apache.commons.lang3.tuple.Pair;

import src.main.java.StringNormalizer;
import src.main.java.MethodVisitor;

import org.apache.commons.text.StringEscapeUtils;
import com.github.javaparser.ast.visitor.*;


class Feature {

    public List<String>    terminals;
    public List<String> nonTerminals;
    public      String          code;
    public      String          path;

    public Feature() {
        this.terminals    = new ArrayList<String>();
        this.nonTerminals = new ArrayList<String>();
        this.code         = "";
    }

    public Feature addPath(String path) {
        this.path = path;
        return this;
    }

    public String toString() {
        return "{\"path\":\"" + StringEscapeUtils.escapeJson(this.path) + "\"," +
               "\"raw\":\"" + StringEscapeUtils.escapeJson(code) + "\"," + 
               "\"f2v\":[" + terminals.stream().filter(x->!x.equals("")).collect(Collectors.toList())+ "," + nonTerminals + "]}";
    }

}

class Features {
    public List<Feature> features;
    public String path;
    public Features(List<Feature> features, String path) {
        this.features = features;
        this.path = path;
    }
    
    public String toString() {
        return this.features.stream().map(x->x.addPath(path).toString()).collect(Collectors.joining("\n"));
    }
}


public class F2VMethodVisitor implements MethodVisitor{

	private StringNormalizer stringNormalizer;
	private Integer level;

	public F2VMethodVisitor(StringNormalizer stringNormalizer) {
		this.stringNormalizer = stringNormalizer;
		this.level = 0;
	}

	public String visit(Node node, String path) {
		return new Features(visitBody(((MethodDeclaration) node).getBody().get()), path).toString();
	}

	private boolean isTerminal(Node node) {
		return node.getChildNodes().isEmpty() ? true : false;
	}

	private boolean isStatement(Node node) {
		return ((node instanceof Statement) && !(node instanceof BlockStmt)) ? true : false;
	}

	private boolean isBlockStatement(Node node) {
		return node instanceof BlockStmt;
	}

	private boolean isMethodDeclaration(Node node) {
		return node instanceof MethodDeclaration;
	}

	private String getOperatorIfAny(Node node) {
		if      (node instanceof BinaryExpr) {return ((BinaryExpr) node).getOperator().toString();}
		else if (node instanceof AssignExpr) {return ((AssignExpr) node).getOperator().toString();}
		else if (node instanceof  UnaryExpr) {return (( UnaryExpr) node).getOperator().toString();}
		else {return "";}
	}

	public void visitStatement(Node node, Feature feature) {
		if (isTerminal(node)) {
			feature.nonTerminals.add(stringNormalizer.normalize_nonterminals(node.getMetaModel().toString()+getOperatorIfAny(node)));
			feature.   terminals.add(stringNormalizer.normalize_terminals(node.toString())+getOperatorIfAny(node));
		} else { 
			feature.nonTerminals.add(stringNormalizer.normalize_nonterminals(node.getMetaModel().toString()+getOperatorIfAny(node)));
			node.getChildNodes()
                .stream()
                .filter(x -> !isStatement(x))
                .forEach(x -> visitStatement(x,feature));
		} 
	}

	public List<Feature> visitBody(Node node) {
        return node.findAll(Statement.class)
                   .stream()
                   .filter(statement -> isStatement(statement))
                   .map(statement -> {
                       Feature feature = new Feature();
                       visitStatement(statement, feature);
                       VoidVisitor<Void> visitor = new StatementPrettyPrinter();
                       statement.accept(visitor, null);
                       feature.code = visitor.toString();
                       return feature;
                   })
                   .collect(Collectors.toList());
	}    
}
