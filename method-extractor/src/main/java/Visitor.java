package src.main.java;

import                        com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import      com.github.javaparser.ast.body.MethodDeclaration;
import         com.github.javaparser.ast.visitor.TreeVisitor;
import            com.github.javaparser.ast.comments.Comment;

import                    java.util.Map;
import                java.util.HashMap;
import           java.io.BufferedWriter;
import         java.util.logging.Logger;
import      java.util.stream.Collectors;
import java.util.NoSuchElementException;

import org.apache.commons.cli.CommandLine;
import src.main.java.MethodVisitor;

public class Visitor {

	private StringNormalizer normalizer;
	private   BufferedWriter     writer;
	private        Extractor  extractor;
	private    MethodVisitor    visitor;
	private static Logger logger = Logger.getLogger("[INFO]");

	public Visitor(BufferedWriter writer,Extractor extractor,CommandLine commandLine) {
		this.normalizer  = new StringNormalizer();
        this.visitor     = new F2VMethodVisitor(this.normalizer);
		this.writer      =    writer;
		this.extractor   = extractor;
	}

	public Map<String,Integer> getTargetCounter() {return normalizer.getTargetCounter();}
	public Map<String,Integer>  getTokenCounter() {return normalizer. getTokenCounter();}

	public TreeVisitor getTreeVisitor() throws Exception {
 	       return new TreeVisitor() {
 		       	@Override
 		       	public void process(Node node) {
 	       			if (node instanceof MethodDeclaration && // node must be a method
 	       			  !(node instanceof ConstructorDeclaration) && // node must not be a constructor
	 	       	   	   ((MethodDeclaration) node).getBody().isPresent() &&  // node mush have a body
				  !((MethodDeclaration) node).getBody().get().getChildNodes().isEmpty() && // body must not be empy
				  !((MethodDeclaration) node).getBody().get().getChildNodes().stream().filter(n -> !(n instanceof Comment)).collect(Collectors.toList()).isEmpty()) // body must not contains only comments
				    {
				    	try {
				    		writer.write(visitor.visit(node, extractor.getCurrentPath()) + "\n");
				    	} catch (Throwable throwable) {
				    		logger.warning("COULD NOT WRITE. SOMETHING BAD HAPPENED.");
				    		logger.warning(    "PATH :" + extractor.getCurrentPath());
				    		logger.warning("POSITION :" + node.getBegin().toString() + ";" + node.getEnd().toString());
				    		logger.warning(    "NODE :"+node.toString());
				    		throwable.printStackTrace();
				    		System.exit(-1);
				    	}
 	        		}
 	       		}
 		};
	}
}
