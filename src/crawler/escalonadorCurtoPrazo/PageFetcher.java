/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler.escalonadorCurtoPrazo;

import com.trigonic.jrobotx.Record;
import com.trigonic.jrobotx.RobotExclusion;
import crawler.ColetorUtil;
import crawler.HtmlProcessor;
import crawler.URLAddress;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.log4j.Logger;
import util.PrintColor;

/**
 *
 * @author jr
 */
public class PageFetcher extends Thread {

    private Escalonador escalonador;
    private RobotExclusion robotExclusion = new RobotExclusion();
    Logger logger = Logger.getLogger(PageFetcher.class);
    HtmlProcessor htmlProcessor = HtmlProcessor.getInstance();

    public PageFetcher(Escalonador escalonador) {
        this.escalonador = escalonador;
    }

    @Override
    public void run() {
        URLAddress currentUrl;
        Record record;

        while (!escalonador.finalizouColeta()) {
            currentUrl = escalonador.getURL(); //Requesting page from page scheduler

            try {
                if (currentUrl != null) {
                    if ((record = escalonador.getRecordAllowRobots(currentUrl)) == null) { //Getting robots.txt record from domain
                        record = robotExclusion.get(currentUrl.getUrlRobotsTxt(), "BrutusBot"); //requesting robots.txt from URL
                        escalonador.putRecorded(currentUrl.getDomain(), record); //saving requested robots.tx
                    }

                    StringBuffer buff = new StringBuffer();

                    if (record == null || record.allows(currentUrl.getPath())) {  //Checking if collection is allowed
                        InputStream stream = ColetorUtil.getUrlStream("BrutusBot", currentUrl.getUrlObj());
                        String pageContent = ColetorUtil.consumeStream(stream);
                        boolean[] permission = htmlProcessor.allowsIndexing(pageContent,buff);

                        if (permission[0]) {
                            escalonador.countFetchedPage();
                            escalonador.addCollectedURL(currentUrl);
                            buff.insert(0,"COLLECTED: " + currentUrl.getAddress() + " ");
                        } else {
                            buff.append(PrintColor.RED + " NOTINDEXING" + PrintColor.RESET);
                        }

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

                        System.out.println(buff.toString());
                    }
                }
            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
                System.out.println(PrintColor.RED + "DESCARTANDO PAGINA NAO EXISTENTE: " + currentUrl.getAddress() + PrintColor.RESET);
            } catch (UnknownHostException ex) {
                System.out.println(ex.getMessage());
                System.out.println(PrintColor.RED + "NOME DE DOMINIO NAO RESOLVIDO: " + currentUrl.getAddress() + PrintColor.RESET);
            } catch (ConnectException ex){
                System.out.println(PrintColor.RED + "FALHA DE CONNEXAO. TENTANDO NOVAMENTE  " + currentUrl.getAddress() + PrintColor.RESET);
                
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
