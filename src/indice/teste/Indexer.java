/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indice.teste;

import util.StringUtil;
import util.ArquivoUtil;
import java.io.BufferedReader;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jr e Tulio Fonseca
 */
public class Indexer {

    private Pattern docIdPattern = Pattern.compile("([0-9])*");
    private File rootdir;
    private String dirpath;

    /**
     *
     *
     * @param @return
     */
    public Indexer(String dirpath) {
        this.dirpath = dirpath;
        rootdir = new File(dirpath);

    }

    /**
     *
     *
     * @param @return
     */
    public void getFiles() {

        for (File subdir : rootdir.listFiles(File::isDirectory)) {
            for (File htmlFile : subdir.listFiles()) {

                try {
                    String content = ArquivoUtil.leTexto(htmlFile);

                    System.out.println(htmlFile.getName());
                    Matcher matcher = docIdPattern.matcher(htmlFile.getName());
                    matcher.find();
                    int docId = Integer.parseInt(matcher.group());
                    indexDocument(content, docId);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

        }
    }

    /**
     *
     *
     * @param @return
     */
    public void indexDocument(String content, int docId) {
        content = StringUtil.replaceAcento(content);
        content = content.toLowerCase();
        
        Map<String, Integer> termFrequency = getTermFrequency(content);
        
    }

    public Map<String, Integer> getTermFrequency(String content) {
        Map<String, Integer> termFrequency = new HashMap<>();

        String[] terms = content.split("[\\D\\W]");

        for (String term : terms) {
            if (!StringUtil.isStopWord(term)) {
                if (termFrequency.containsKey(term)) {
                    Integer frequency = termFrequency.get(term) + 1;
                    termFrequency.put(term, frequency);
                } else {
                    termFrequency.put(term, 1);
                }
            }
        }
        
        return termFrequency;
    }

    public static void main(String[] args) {
        String wikipath = "wikiSample";
        Indexer indexer = new Indexer(wikipath);
        indexer.getFiles();

    }

}
