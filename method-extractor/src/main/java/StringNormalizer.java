package src.main.java; 

import               java.util.Map;
import           java.util.HashMap;
import     java.util.stream.Stream;
import    java.util.logging.Logger;
import java.util.stream.Collectors;

import jep.SharedInterpreter;

import        org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.MutablePair;

public class StringNormalizer {

	private SharedInterpreter      interpreter;
	private Map<String,  String>   memoization;
	private Map<String, Integer> targetCounter;
	private Map<String, Integer>  tokenCounter;
	private static Logger logger = Logger.getLogger("[INFO]");
    public boolean toCount;
	
	public StringNormalizer() {
        	try { 
			this.targetCounter = new HashMap<String, Integer>();
			this. tokenCounter = new HashMap<String, Integer>();
            
            this.toCount = true;
			this.memoization = new HashMap<String, String>();
			this.interpreter =       new SharedInterpreter();
			
			this.interpreter.exec(                          "from spiral.ronin import split");
			this.interpreter.exec("lowerize = lambda x: [y.lower() for y in x if y != \"\"]");
			this.interpreter.exec(        "tokenize = lambda x:','.join(lowerize(split(x)))");

		} catch (Exception exception) {
			exception.printStackTrace();
			System.exit(-1);
		}
	}

    public void doCount   () { this.toCount = true; }
    public void doNotCount() { this.toCount = false;}

	private String spiralInvoke(String input) {
		try {
			memoization.putIfAbsent(input,(String) interpreter.invoke("tokenize", input));
		} catch (Exception exception) {
			logger.warning("SOMETHING WENT BAD WITH JEP");
			logger.warning("INPUT :"+input);
			exception.printStackTrace();
			System.exit(-1);
		}
		return memoization.get(input);
	}

	public String normalize_target(String input) {
		String result = Stream.of(spiralInvoke(input).split(",")) 
				      .map(z->z.replaceAll("[^A-Za-z]",""))
				      .filter(z->!z.equals(""))
				      .collect(Collectors.joining(","));
		targetCounter.putIfAbsent(result, 1);
		targetCounter.replace(result, targetCounter.get(result)+(toCount?1:0));
		return result;
	}

	public String normalize_terminals(String input) {
		String result =  Stream.of(input.replaceAll("[^A-Za-z0-9\\[\\]<>\\?]",",").split(","))
				       .map(x -> Stream.of(spiralInvoke(x.substring(0,Math.min(x.length(), 128))).split(","))
						       .map(z->z.replaceAll("[^A-Za-z]",""))
						       .filter(z->!z.equals(""))
                               .map(z->"\""+z+"\"")
						       .map(z -> {tokenCounter.putIfAbsent(z,0); tokenCounter.replace(z,tokenCounter.get(z)+(toCount?1:0)); return z;})
						       .collect(Collectors.joining(",")))
			               .filter(x -> !x.equals(""))
			               .collect(Collectors.joining(","));
		return result;
	}

	public String normalize_nonterminals(String input) {
		String result = input.toLowerCase();
		tokenCounter.putIfAbsent(result, 1);
		tokenCounter.replace(result,tokenCounter.get(result)+(toCount?1:0));
		return "\"" + input.toLowerCase() + "\"";
	}

    public void increment_terminals(String token) {
        tokenCounter.putIfAbsent(token,1);
        tokenCounter.replace(token, tokenCounter.get(token)+(toCount?1:0));
    }

    public void increment_nonterminals(String token) {
        tokenCounter.putIfAbsent(token,1);
        tokenCounter.replace(token, tokenCounter.get(token)+(toCount?1:0));
    }

	public Map<String,Integer> getTargetCounter () {return this.targetCounter;}
	public Map<String,Integer>  getTokenCounter () {return this. tokenCounter;}
 } 

