/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indice.teste;

import indice.estrutura.Indice;
import indice.estrutura.IndiceLight;
import util.StringUtil;
import util.ArquivoUtil;
import ptstemmer.Stemmer;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.clapper.util.html.HTMLUtil;
import ptstemmer.exceptions.PTStemmerException;
import ptstemmer.implementations.OrengoStemmer;

public class Indexer {

    private Pattern docIdPattern = Pattern.compile("([0-9])*"); // Nome dos arquivos que sejam apenas numeros.
    private File rootdir; // Diretorio raiz
    private String dirpath; // String que recebe o caminho do diretorio dos arquivos
    private Stemmer ptStemmer;
    private Indice indice;

    /**
     * Construtor da classe Indexer, ele recebe o caminho do diretorio dos
     * arquivos e inicia o diretorio raiz dos arquivos.
     *
     * @param
     * @return
     */
    public Indexer(String dirpath) {
        this.dirpath = dirpath;        
        rootdir = new File(dirpath);
        
        try {
            ptStemmer = new OrengoStemmer();
            indice = new IndiceLight(15000);
        } catch (PTStemmerException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Função responsavel por ler todos os arquivos das pastas no diretorio e
     * indexa-los.
     *
     * @param
     * @return
     */
    public void getFiles() {

        for (File subdir : rootdir.listFiles(File::isDirectory)) {
            for (File htmlFile : subdir.listFiles()) {

                try {
                    // Lê conteudo do arquivo HTML
                    String content = ArquivoUtil.leTexto(htmlFile);                    
                    System.out.println(htmlFile.getName());
                    
                    // Obtem o id do documento, a partir dos numeros do nome do arquivo
                    Matcher matcher = docIdPattern.matcher(htmlFile.getName());
                    matcher.find();
                    int docId = Integer.parseInt(matcher.group());
                    
                    // Indexa o texto do documento
                    indexDocument(HTMLUtil.textFromHTML(content), docId);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

    /**
     * Função que recebe um texto (content) e executa as funções de retirar os
     * acentos e deixa-las no diminutivo, ignorar as stopwords e stemmer, alem
     * de calcular a frequencia de cada termo ja tratado no documento.
     *
     * @param content,docId
     * @return
     */
    public void indexDocument(String content, int docId) {
        // Pre-processamento do conteudo das paginas
        content = StringUtil.replaceAcento(content);
        content = content.toLowerCase();
        
        // Obtendo Map de ocorrencias de termos no documento
        Map<String, Integer> termFrequency = getTermFrequency(content);
        
        //Indexa cada termo do documento com sua respectiva frequencia
        for (String term : termFrequency.keySet()) {           
            indice.index(term, docId, termFrequency.get(term));
        }

    }
    /**
     *  Função que dado um texto (content), faz seu processamento, separando as palavras por caracter 
     *  especial, em seguida percorre o vetor dos termos verificando se uma palavra é uma stopword, caso 
     *  seja a mesma é ignorada se não for, é aplicado a função do ptStemmer para extrair o prefixo e 
     *  deixar apenas o radical da palavra, logo apos é verificado se a palavra ja foi adicionada no Map de
     *  terfrequencias, caso já esteja adicionado a frequencia do termo é adicionado mais um , caso não 
     *  esteja o termo é adicionado no Map com frequencia igual a um .
     *
     * @param content
     * @return Map<String, Integer>
     */
    public Map<String, Integer> getTermFrequency(String content) {
        Map<String, Integer> termFrequency = new HashMap<>();

        String[] terms = content.split("[\\D\\W]");

        for (String term : terms) {
            //Verifica se o termo esta vazio
            if(termIsEmpty(term)) continue;
            
            //Verifica se o termo é uma stopword(Se for, é ignorado)
            if (!StringUtil.isStopWord(term)) {
                term = ptStemmer.getWordStem(term);
                //Verifica se o termo ja esta no Map
                if (termFrequency.containsKey(term)) {
                    //Atualiza a frequencia
                    Integer frequency = termFrequency.get(term) + 1;
                    termFrequency.put(term, frequency);
                } else {
                    // Inicia a frequencia com o valor 1
                    termFrequency.put(term, 1);
                }
            }
        }
        return termFrequency;
    }
    /**
     * Verificar se o termo não é uma palavra sem caracteres
     *
     * @param term
     * @return boolean
     */    
    
    public boolean termIsEmpty(String term){
        return term.length()==0;        
    }

    public static void main(String[] args) {
        String wikipath = "wikiSample";
        Indexer indexer = new Indexer(wikipath);
        indexer.getFiles();

    }

}
