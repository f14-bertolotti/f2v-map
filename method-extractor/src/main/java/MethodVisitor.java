package src.main.java;

import com.github.javaparser.ast.Node;

public interface MethodVisitor {
	public String visit(Node node, String path); 
}

