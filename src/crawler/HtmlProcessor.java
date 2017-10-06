/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import com.trigonic.jrobotx.Constants;
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
    private static String jsPattern = new String("(.)*javascript(.)*");

    public static HtmlProcessor getInstance() {
        if (htmlProcessor == null) {
            htmlProcessor = new HtmlProcessor();
        }

        return htmlProcessor;
    }

    private HtmlProcessor() {
        htmlCleaner = new HtmlCleaner();
        CleanerProperties props = htmlCleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(true);
    }
    /**
     * Processa todas as Tags de uma página, achando todas as Tags iniciando com 'a' seguido de 'href' 
     *
     * @param pageContent
     * @return links
     */
    public List<String> extractLinks(String pageContent) {

        TagNode rootNode = htmlCleaner.clean(pageContent);

        List<String> links = new ArrayList<>();
        TagNode linkElements[] = rootNode.getElementsByName("a", true);

        for (TagNode node : linkElements) {

            String link = node.getAttributeByName("href");
            if (link != null && link.length() > 0 && !link.matches(jsPattern)) {
                links.add(link);
            }
        }

        return links;

    }

    public boolean[] allowsIndexing(String pageContent) {

        boolean permission[] = {true, true};

        TagNode rootNode = htmlCleaner.clean(pageContent);

        TagNode metaElements[] = rootNode.getElementsByName("meta", true);

        for (TagNode node : metaElements) {
            String name = node.getAttributeByName("name");
            if (name != null && name.equalsIgnoreCase("robots")) {

                String content = node.getAttributeByName("content");
                String attributes[] = content.split(",");

                switch (attributes.length) {
                    case 1:
                        if (attributes[0].equalsIgnoreCase(Constants.NOINDEX)) {
                            permission[0] = false;
                        }
                        System.out.print("ATTRIBUTES: " + attributes[0]);

                        break;

                    case 2:
                        if (attributes[0].equalsIgnoreCase(Constants.NOINDEX)) {
                            permission[0] = false;
                        }

                        if (attributes[1].equalsIgnoreCase(Constants.NOFOLLOW)) {
                            permission[1] = false;
                        }
                        System.out.print("ATTRIBUTES: " + attributes[0] + " " + attributes[1]);
                        System.out.print(" PERMISSIONS " + permission[0] + " " + permission[1]);
                        break;
                }

                /* if(permission[0] == false || permission[1] == false){
                    System.exit(1);
                }
                 */
            }
        }

        return permission;

    }
}
