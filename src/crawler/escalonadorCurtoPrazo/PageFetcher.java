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
import java.io.InputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.log4j.Logger;
import crawler.PrintColor;
import java.net.SocketTimeoutException;

/**
 *
 * @author jr e Tulio F
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
    
    
    /**
     *  Cria um robot e um buff para dispensar a necessidade de criar outro toda hora.
     *  O contrutor tambem coloca um objeto para receber o escalonar para poder utiliza-lo na Classe.
     *
     * @param escalonador
     * @return
     */
    public PageFetcher(Escalonador escalonador) {
        this.escalonador = escalonador;
        robotExclusion = new RobotExclusion();
        htmlProcessor = HtmlProcessor.getInstance();
        buff = new StringBuffer();
        
    }

    
    /**
     * Sera o ponto de partida da thread, ele atua como o pipeline geral da coleta, coletando a 
     * pagina logo apos coletando as metaTags, e por fim olhando as permissoes de 
     * coletar/extrair os links das páginas.
     *
     * @param 
     * @return
     */
    @Override
    public void run() {

        while (!escalonador.finalizouColeta()) {
            this.currentUrl = escalonador.getURL(); //Requesting page from page scheduler
            buff.setLength(0);// limpar o buff

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
                escalonador.adicionaNovaPaginaSemChecar(currentUrl);
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
    
     /**
     * Verifica as permissões dos Robots.txt. Caso ja tenha as permissoes ja salva no escalonador, 
     * ele ja acessa e verifica as permissoes disponiveis. Caso não tenha, ele vai mandar 
     * a requisição pedindo pelo Robots.txt do servidor.
     *
     * @param 
     * @return boolean
     */
    public boolean retrieveRobotsPermission() throws Exception {
        if ((record = escalonador.getRecordAllowRobots(currentUrl)) == null) { //Getting robots.txt record from domain
            record = robotExclusion.get(currentUrl.getUrlRobotsTxt(), Constants.USER_AGENT); //requesting robots.txt from URL
            escalonador.putRecorded(currentUrl.getDomain(), record); //saving requested robots.tx
            System.out.println(PrintColor.PURPLE + "REGISTRO ENCONTRADO" + PrintColor.RESET);
        }

        return record == null || record.allows(currentUrl.getPath());   //Checking if collection is allowed
    }
    
    /**
     * Coletar a página.
     *
     * @param 
     * @return
     */
    public void collectPage() throws Exception {
        this.stream = ColetorUtil.getUrlStream(Constants.USER_AGENT, currentUrl.getUrlObj());
        this.pageContent = ColetorUtil.consumeStream(stream);
    }

    /**
     * Verifica as permissoes de coleta e de extraçoes de links, 
     * caso tenha ele realiza a extração/coleta.
     *
     * @param pageContent, permission
     * @return
     */
    public void processPage(String pageContent, boolean[] permission) {
        if (permission[0]) // permissão de coleta
        { 
            escalonador.countFetchedPage();
            escalonador.addCollectedURL(currentUrl);
            buff.insert(0, PrintColor.BLUE + "COLLECTED: " + PrintColor.RESET + currentUrl.getAddress() + " ");
        } else {
            buff.append(PrintColor.RED + " NOTINDEXING" + PrintColor.RESET);
        }
        buff.append(permission[0] + " " + permission[1] + " ");
        if (permission[1]) //permissãA claso de extração de links
        {
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
