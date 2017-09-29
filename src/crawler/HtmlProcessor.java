/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 *
 * @author jr
 */
public class HtmlProcessor {
    
        private static HtmlProcessor htmlProcessor;
        private static HtmlCleaner htmlCleaner;
        
        public static HtmlProcessor getInstance(){
            if(htmlProcessor == null){
                htmlProcessor = new HtmlProcessor();
            }
            
            return htmlProcessor;
        }
        
        private HtmlProcessor(){
            htmlCleaner = new HtmlCleaner();
            CleanerProperties props = htmlCleaner.getProperties();
            props.setAllowHtmlInsideAttributes(true);
            props.setAllowMultiWordAttributes(true);
            props.setRecognizeUnicodeChars(true);
            props.setOmitComments(true);
        }
        
        public List<String> extractLinks(String pageContent) {
            
            TagNode rootNode = htmlCleaner.clean(pageContent);
            
            List<String> links = new ArrayList<> ();
            TagNode linkElements[] = rootNode.getElementsByName("a",true);
            
            for ( TagNode node : linkElements){

                String link = node.getAttributeByName("href");
                if(link != null && link.length() > 0){
                    links.add(link);
                }
            }
            
            return links;
           
        }
}
