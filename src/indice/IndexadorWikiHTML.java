

import indice.estrutura.Indice;
import indice.estrutura.IndiceLight;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.clapper.util.html.HTMLUtil;

import ptstemmer.Stemmer;
import ptstemmer.exceptions.PTStemmerException;
import ptstemmer.implementations.OrengoStemmer;


public class IndexadorWikiHTML {
	
	private Stemmer stemmer ;
	private File dirWikiSample;
	private Indice indiceTexto;
	public IndexadorWikiHTML(File dirWikiSample)
	{
		this.dirWikiSample = dirWikiSample;
		try {
			stemmer = new OrengoStemmer();
			indiceTexto = new IndiceLight(15000);
			
		} catch (PTStemmerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String preprocessTxt(String txtLimpo) {
		txtLimpo = StringUtil.replaceAcento(txtLimpo);
		txtLimpo = txtLimpo.toLowerCase();
		return txtLimpo;
	}
	public Map<String,Integer> getFreqTerm(String[] arrTermos ,Map<String,Integer> mapFreqTerm) {

		for(String termo : arrTermos)
		{
			if(termo.length()>0)
			{
				if(!StringUtil.isStopWord(termo))
				{
							
					//adiciona termo
					//procura termo, se nao existir inser
					if(!mapFreqTerm.containsKey(stemmer.getWordStem( termo)))
					{
						mapFreqTerm.put( stemmer.getWordStem( termo), 1);
						
					}
					else
					{
						
						Integer count = mapFreqTerm.get(stemmer.getWordStem( termo))+1;
						mapFreqTerm.put(stemmer.getWordStem( termo), count);
					}
					/*
					if(stemmer.getWordStem( termo).equals("bel"))
					{
						System.out.println(stemmer.getWordStem( termo)+" => "+termo);
					}
					*/
					
					
					
				}
			}
		}
		//System.out.println("Termos: "+allTerms+" Sem stopword:"+allTermsWithoutStop);
		return mapFreqTerm;
	}
	public void indexa(Integer idDoc, String txtLimpo) throws Exception {
		String[] arrTermos = txtLimpo.split("[^a-zç0-9]");
		
		Map<String,Integer> mapFreqTermDoc = new HashMap<String,Integer>(); 
		mapFreqTermDoc = getFreqTerm(arrTermos,mapFreqTermDoc);
		index(indiceTexto,idDoc,mapFreqTermDoc);
	}
	public void index(Indice indice, int intDocId,Map<String,Integer> freqTerm) throws Exception
	{
		for(String termo : freqTerm.keySet())
		{
			
			int freqTermo = freqTerm.get(termo);
			
			if(freqTermo > Short.MAX_VALUE)
			{
				throw new Exception("O Short deu overflow :(");
			}
			
			indice.index(termo, intDocId, freqTermo);
			
		}
	}
	public void indexaFiles() throws NumberFormatException, IOException, Exception
	{
		//navega em todos os diretorios
		int n = 0;
		for(File dirWiki : dirWikiSample.listFiles())
		{
			//para cada diretorio, le os arquivos
			if(dirWiki.isDirectory())
			{
				for(File arqWiki : dirWiki.listFiles())
				{
					System.out.println("Indexando artigo numero: "+n+" "+arqWiki.getAbsolutePath());
					String txt = ArquivoUtil.leTexto(arqWiki);
					txt = HTMLUtil.textFromHTML(txt);
					String txtLimpo = this.preprocessTxt(txt);
					this.indexa(Integer.valueOf(arqWiki.getName().replaceAll("\\.html", "")),txtLimpo);
					n++;
				}	
			}
			
			
		}
	}
	public void gravaIndice(File f) throws FileNotFoundException, IOException
	{
		ObjectOutputStream arqOutput = new ObjectOutputStream(	new FileOutputStream(f));

		arqOutput.writeObject(this.indiceTexto);
		arqOutput.close();
		
		

	}
    public static long usedMem() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
	public static void main(String[] args) throws Exception
	{
		long time = System.currentTimeMillis();
		IndexadorWikiHTML idx = new IndexadorWikiHTML(new File("/home/hasan/aulas/wikiSample"));
		long usedMemBefore = usedMem();
		idx.indexaFiles();
		long usedMemAfter = usedMem();
		idx.gravaIndice(new File("/tmp/indice_gravado.obj"));
		System.out.println("Indice gravado em: "+(System.currentTimeMillis()-time)/(1000.0*60));
		System.out.println("Memoria usada: "+(usedMemAfter-usedMemBefore)/(1024.0*1024.0));
	}
}
