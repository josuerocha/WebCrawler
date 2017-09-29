/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.util.ArrayList;
import java.util.List;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 *
 * @author jr
 */
public class HtmlProcessor {
    
        private static HtmlProcessor htmlProcessor = new HtmlProcessor();
        private static HtmlCleaner htmlCleaner = new HtmlCleaner();
        
        public static HtmlProcessor getInstance(){
            if(htmlProcessor == null){
                htmlProcessor = new HtmlProcessor();
            }
            
            return htmlProcessor;
        }
        
        public HtmlProcessor(){
            CleanerProperties props = htmlCleaner.getProperties();
        }
        
        public List<String> extractLinks(String pageContent){
            TagNode rootNode = htmlCleaner.clean(pageContent);
            
            List<String> links = new ArrayList<String> ();
            TagNode linkElements[] = rootNode.getElementsByName("href",true);
            
            for ( TagNode node : linkElements){
                String link = node.getAttributeByName ("href");
                if(link != null && link.length() > 0){
                    links.add(link);
                }
            }
            
            return links;
        }
}
