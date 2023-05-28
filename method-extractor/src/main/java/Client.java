package src.main.java;

import java.util.logging.Logger;

import         java.nio.file.Paths;
import         java.nio.file.Files;
import      java.io.BufferedWriter;
import          java.io.FileWriter;

import org.apache.commons.io.FilenameUtils;

import org.apache.commons.cli.CommandLineParser;
import     org.apache.commons.cli.DefaultParser;
import       org.apache.commons.cli.CommandLine;
import           org.apache.commons.cli.Options;
import            org.apache.commons.cli.Option;


public class Client {

	private static Logger logger = Logger.getLogger("[INFO]");

	public static void main(String[] args) throws Exception {

		System.setProperty("java.util.logging.SimpleFormatter.format","%1$tm/%1$td/%1$tY-%1$tH:%1$tM:%1$tS|%5$s%6$s%n");
		
		final Options options = new Options(); 
		options.addOption(new Option(  "output_path",  true, "where to store the output."));
		options.addOption(new Option(   "input_path",  true,    "folder with java files."));
		options.addOption(new Option(      "verbose", false,   "extra info in std output"));

		CommandLineParser parser = new DefaultParser();
		CommandLine  commandLine = parser.parse(options, args);
		
        BufferedWriter writer0 = new BufferedWriter(new FileWriter(commandLine.getOptionValue("output_path")));
		Extractor extractor = new Extractor(writer0, commandLine);

        Files.walk(Paths.get(commandLine.getOptionValue("input_path")))
        	.map(p -> p.toAbsolutePath())
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(".java"))
        	.forEach(extractor::accept);

		writer0.flush();

	}
}
