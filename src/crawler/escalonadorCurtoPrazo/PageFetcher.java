/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler.escalonadorCurtoPrazo;

import com.trigonic.jrobotx.Constants;
import com.trigonic.jrobotx.Record;
import com.trigonic.jrobotx.RobotExclusion;
import crawler.ColetorUtil;
import crawler.HtmlProcessor;
import crawler.URLAddress;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.log4j.Logger;
import crawler.PrintColor;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

/**
 *
 * @author jr
 */
public class PageFetcher extends Thread {

    Logger logger = Logger.getLogger(PageFetcher.class);
    private Escalonador escalonador;
    private RobotExclusion robotExclusion;
    HtmlProcessor htmlProcessor;
    private URLAddress currentUrl;
    private Record record;
    private StringBuffer buff;
    private InputStream stream;
    private String pageContent;
    boolean[] metaTagsPermission;

    public PageFetcher(Escalonador escalonador) {
        this.escalonador = escalonador;

        robotExclusion = new RobotExclusion();
        htmlProcessor = HtmlProcessor.getInstance();
        buff = new StringBuffer();
        
    }

    @Override
    public void run() {

        while (!escalonador.finalizouColeta()) {
            this.currentUrl = escalonador.getURL(); //Requesting page from page scheduler
            buff.setLength(0);

            try {
                if (this.currentUrl != null) {

                    boolean robotPermission = retrieveRobotsPermission();

                    if (robotPermission) {

                        collectPage();

                        metaTagsPermission = htmlProcessor.allowsIndexing(pageContent, buff);

                        processPage(pageContent, metaTagsPermission);
                        
                        buff.append(" FINALIZOU:" + escalonador.finalizouColeta());
                        System.out.println(buff.toString());
                    }
                }
            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
                System.out.println(PrintColor.RED + "DESCARTANDO PAGINA NAO EXISTENTE: " + currentUrl.getAddress() + PrintColor.RESET);
            } catch (UnknownHostException ex) {
                System.out.println(ex.getMessage());
                System.out.println(PrintColor.RED + "NOME DE DOMINIO NAO RESOLVIDO: " + currentUrl.getAddress() + PrintColor.RESET);
            } catch (ConnectException ex) {
                System.out.println(PrintColor.RED + "FALHA DE CONNEXAO. TENTANDO NOVAMENTE  " + currentUrl.getAddress() + "TENTATIVAS:" + currentUrl.getAttempts() + PrintColor.RESET);
                currentUrl.incrementAttempts();
                System.out.println(ex.getMessage());
                ex.printStackTrace();
                //escalonador.adicionaNovaPaginaSemChecar(currentUrl);
            } catch(SocketTimeoutException ex){
                System.out.println(this.currentUrl);
                ex.printStackTrace();
            } catch (Exception ex) {
                System.out.println(this.currentUrl);
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }

        }
    }

    public boolean retrieveRobotsPermission() throws Exception {

        if ((record = escalonador.getRecordAllowRobots(currentUrl)) == null) { //Getting robots.txt record from domain
            record = robotExclusion.get(currentUrl.getUrlRobotsTxt(), Constants.USER_AGENT); //requesting robots.txt from URL
            escalonador.putRecorded(currentUrl.getDomain(), record); //saving requested robots.tx
        }

        return record == null || record.allows(currentUrl.getPath());   //Checking if collection is allowed
    }

    public void collectPage() throws Exception {
        this.stream = ColetorUtil.getUrlStream(Constants.USER_AGENT, currentUrl.getUrlObj());
        this.pageContent = ColetorUtil.consumeStream(stream);
    }

    public void processPage(String pageContent, boolean[] permission) {
        if (permission[0]) {
            escalonador.countFetchedPage();
            escalonador.addCollectedURL(currentUrl);
            buff.insert(0, PrintColor.BLUE + "COLLECTED: " + PrintColor.RESET + currentUrl.getAddress() + " ");
        } else {
            buff.append(PrintColor.RED + " NOTINDEXING" + PrintColor.RESET);
        }
        buff.append(permission[0] + " " + permission[1] + " ");
        if (permission[1]) {
            List<String> linkList = htmlProcessor.extractLinks(pageContent);
            for (String link : linkList) {
                try {
                    escalonador.adicionaNovaPagina(new URLAddress(link, currentUrl.getDomain()));
                } catch (Exception ex) {
                    logger.error(PrintColor.RED + "INVALID LINK: " + link + PrintColor.RESET);
                    buff.append(PrintColor.RED + " INVALID LINK:" + link + PrintColor.RESET);
                }
            }
        } else {
            buff.append(PrintColor.RED + " NOTFOLLOWED" + PrintColor.RESET);

        }
    }

}
