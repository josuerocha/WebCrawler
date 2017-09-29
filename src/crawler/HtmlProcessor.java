/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;

/**
 *
 * @author jr
 */
public class HtmlProcessor {
        private static HtmlCleaner htmlCleaner = new HtmlCleaner();
        
        public HtmlProcessor(){
            CleanerProperties props = htmlCleaner.getProperties();
        }
}
