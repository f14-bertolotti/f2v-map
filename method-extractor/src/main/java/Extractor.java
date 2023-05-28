package src.main.java;

import            com.github.javaparser.JavaParser;
import   com.github.javaparser.ast.CompilationUnit;
import           com.github.javaparser.ParseResult;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.body.MethodDeclaration;

import               java.util.Map;
import    java.util.logging.Logger;
import java.util.function.Consumer;
import      java.io.BufferedWriter;
import          java.nio.file.Path;
import java.util.NoSuchElementException;
import org.apache.commons.cli.CommandLine;

import java.util.Map;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class Extractor implements Consumer<Path> {

	private static Logger logger = Logger.getLogger("[INFO]");

	private JavaParser   javaParser;
	private String      currentPath;
	private Visitor         visitor;
	private BufferedWriter   writer;
	private CommandLine commandLine;
    
	public Extractor(BufferedWriter writer, CommandLine commandLine) throws Exception{
		this.currentPath = "";
		this.writer      = writer;
		this.commandLine = commandLine;
		this.javaParser  = new JavaParser();
		this.visitor     = new Visitor(writer,this,commandLine);
    
    }

	public Map<String,Integer>  getTokenCounter() {return visitor. getTokenCounter();}
	public Map<String,Integer> getTargetCounter() {return visitor.getTargetCounter();}

	@Override
	public void accept(Path path) {

    	this.currentPath = path.toString();
	    if (this.commandLine.hasOption("verbose")) logger.info(this.currentPath.toString());

        try{
            ParseResult<CompilationUnit> parseResult1    = javaParser.parse(path.toFile());
            ParseResult<MethodDeclaration>  parseResult2 = javaParser.parseMethodDeclaration(Files.readAllLines(path).stream().collect(Collectors.joining("")));

            if (parseResult1.isSuccessful()) { 
                visitor.getTreeVisitor().visitPreOrder(parseResult1.getResult().get());
            } else if(parseResult2.isSuccessful()) {
                visitor.getTreeVisitor().visitPreOrder(parseResult2.getResult().get());
            }
         
        } catch (Throwable throwable) {
            logger.info(throwable.toString());
        }

	}
    
    public String getCurrentPath() {
        return currentPath;
    }
}


