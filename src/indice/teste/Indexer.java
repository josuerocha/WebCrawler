/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indice.teste;
import util.ArquivoUtil;
import util.*;
import java.io.BufferedReader;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jr
 */
public class Indexer {
    
    private Pattern docIdPattern = Pattern.compile("([0-9])*");
    private File rootdir;
    private String dirpath;
    
    public Indexer(String dirpath){
        this.dirpath = dirpath;
        rootdir = new File(dirpath);
        
    }
    
    public void getFiles(){
        
        BufferedReader bufferedReader;
        
        for(File subdir : rootdir.listFiles(File::isDirectory)){
            for(File htmlFile : subdir.listFiles()){
                
                try{
                    String content = ArquivoUtil.leTexto(htmlFile);
                    
                    System.out.println(htmlFile.getName());
                    Matcher matcher = docIdPattern.matcher(htmlFile.getName());
                    matcher.find();
                    int docId = Integer.parseInt(matcher.group());
                    indexDocument(content,docId);
                    
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            
            }
            
        }
    }
    
    
    public void indexDocument(String content,int docId){
        //content = cleanFile();
    }
    
    public static void main(String[] args) {
        String wikipath = "wikiSample";
        Indexer indexer = new Indexer(wikipath);
        indexer.getFiles();
        
    }
   
    
}

