/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import com.trigonic.jrobotx.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import crawler.PrintColor;

/**
 *
 * @author jr e Tulio Fonseca
 */
public class HtmlProcessor {

    private static HtmlProcessor htmlProcessor;
    private static HtmlCleaner htmlCleaner;
    private static String jsPattern = new String("(.)*javascript(.)*");
    Pattern nofollowPattern = Pattern.compile(".*" + Constants.NOFOLLOW + ".*", Pattern.CASE_INSENSITIVE);
    Pattern noindexPattern = Pattern.compile(".*" + Constants.NOINDEX + ".*", Pattern.CASE_INSENSITIVE);

    /**
     * Usou o padrão Singleton para não permitir o retorno da mesma instâcia de
     * um objeto no código.
     *
     * @param
     * @return htmlProcessor
     */
    public static HtmlProcessor getInstance() {
        if (htmlProcessor == null) {
            htmlProcessor = new HtmlProcessor();
        }

        return htmlProcessor;
    }

    /**
     * Contrutor da classe, inicializa a API e seta alguns parâmetros da
     * biblioteca.
     *
     * @param
     * @return
     */
    private HtmlProcessor() {
        htmlCleaner = new HtmlCleaner();
        CleanerProperties props = htmlCleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(true);
    }

    /**
     * Processa todas as Tags de uma página, encontrando todas as Tags iniciando
     * com 'a' seguido de 'href'
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

    /**
     * Processa todas as Tags de página, encontrando todas as Tags iniciando com
     * 'meta', seguido de 'name' logo após é processado de modo a buscar se
     * nessa Tag possui um atributo do tipo 'content' e caso tiver descobrir se
     * há ou não o NOINDEX e/ou NOFOLLOW para assim decidir as permissões de
     * acesso a página
     *
     * @param pageContent
     * @return permission
     */

    public boolean[] allowsIndexing(String pageContent, StringBuffer buff) {

        boolean permission[] = {true, true};

        TagNode rootNode = htmlCleaner.clean(pageContent);

        TagNode metaElements[] = rootNode.getElementsByName("meta", true);

        for (TagNode node : metaElements) {
            String name = node.getAttributeByName("name");
            if (name != null && name.equalsIgnoreCase("robots")) {

                String content = node.getAttributeByName("content");

                if (noindexPattern.matcher(content).matches()) {
                    permission[0] = false;
                }

                if (nofollowPattern.matcher(content).matches()) {
                    permission[1] = false;
                }
                    
                buff.append(PrintColor.GREEN + content + PrintColor.RESET + PrintColor.RESET);
            }
        }

        return permission;

    }
}
