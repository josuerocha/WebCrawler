
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * Created on 05/07/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
/**
 * @author Daniel Hasan Dalip
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class StringUtil {
	private static String END_STOP_WORDS = "/home/hasan/data/dumps/stopWords.csv";
	// private static String END_STOP_WORDS =
	// "/home/curupira/hasan/stopWords.csv";
	public static final String APENAS_ACENTOS = ((char) 225) + "-"
			+ ((char) 250);
	public static Pattern objPatternStopWord;
	private static Pattern objPonctuation;
	private static HashMap<String, Boolean> mapWords = new HashMap<String, Boolean>();

	public static String[] REPLACES = { "a", "e", "i", "o", "u" };

	public static Pattern[] PATTERNS_ACENTO = null;

	public static void compilePatterns() {
		PATTERNS_ACENTO = new Pattern[REPLACES.length];
		PATTERNS_ACENTO[0] = Pattern.compile("[âãáàä]",
				Pattern.CASE_INSENSITIVE);
		PATTERNS_ACENTO[1] = Pattern
				.compile("[éèêë]", Pattern.CASE_INSENSITIVE);
		PATTERNS_ACENTO[2] = Pattern
				.compile("[íìîï]", Pattern.CASE_INSENSITIVE);
		PATTERNS_ACENTO[3] = Pattern.compile("[óòôõö]",
				Pattern.CASE_INSENSITIVE);
		PATTERNS_ACENTO[4] = Pattern
				.compile("[úùûü]", Pattern.CASE_INSENSITIVE);

	}

	public static String replaceAcento(String text) {
		if (PATTERNS_ACENTO == null) {
			compilePatterns();
		}

		String result = text;
		for (int i = 0; i < PATTERNS_ACENTO.length; i++) {
			Matcher matcher = PATTERNS_ACENTO[i].matcher(result);
			result = matcher.replaceAll(REPLACES[i]);
		}
		return result;
	}
	public static String[] carregaStopWords()
	{
		try
		{
			BufferedReader r = new BufferedReader(new FileReader(new File(END_STOP_WORDS)));
			String listaPalavras = "";
			String linha;
			while((linha = r.readLine())!=null)
			{
				listaPalavras += linha.replaceAll(" ","");
			}
			r.close();
			
			
			
			//faz uma expressao regular com todas as stopwords
			String[] arrPalavras = listaPalavras.split(",");
			StringBuilder strRegExp = new StringBuilder();
			mapWords = new HashMap<String, Boolean>();
			for(int i =0 ;i<arrPalavras.length; i++)
			{
				strRegExp.append("("+arrPalavras[i]+")");
				if(i<arrPalavras.length-1)
				{
					strRegExp.append("|");	
				}
				mapWords.put(arrPalavras[i].toUpperCase(),true);
				
			}
			objPatternStopWord = Pattern.compile("[^a-z]("+strRegExp.toString()+")[^a-z]",Pattern.CASE_INSENSITIVE);
			
			
			return listaPalavras.split(",");
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return null;
	}
	public static boolean isStopWord(String palavra)
	{
		if(mapWords.size() == 0)
		{
			carregaStopWords();
		}
		return mapWords.containsKey(palavra.toUpperCase()); 
	}
	public static String encodeXML(String txt) {
		return txt.replaceAll("&", "&amp;");
	}

	public static void main(String[] args) {
		/*
		 * System.out.println(retiraStopWords(
		 * "What is the fastest, yet secure way to encrypt passwords in (PHP Prefered), and for."
		 * )); System.exit(0); List<String> lst = retiraBlocoTagInicial(
		 * "<h1> oi </h1> lalala oi oi oi lalal <h1> xpto </h1> lalal xpto lalal xpto"
		 * ,"<h1>","</h1>"); System.out.println(lst);
		 */

	}
}
