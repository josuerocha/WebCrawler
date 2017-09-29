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
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import util.PrintColor;

/**
 *
 * @author jr
 */
public class PageFetcher extends Thread {

    private Escalonador escalonador;
    private RobotExclusion robotExclusion = new RobotExclusion();

    public PageFetcher(Escalonador escalonador) {
        this.escalonador = escalonador;
    }

    @Override
    public void run() {
        URLAddress currentUrl;
        Record record;
        
        while (true) {
            currentUrl = escalonador.getURL(); //Requesting page from page scheduler

            try {
                if ((record = escalonador.getRecordAllowRobots(currentUrl)) == null) { //Getting robots.txt record from domain
                    record = robotExclusion.get(currentUrl.getUrlRobotsTxt(), "BrutusBot"); //requesting robots.txt from URL
                    escalonador.putRecorded(currentUrl.getDomain(), record); //saving requested robots.tx
                }

                if (record == null || record.allows(currentUrl.getPath())) {  //Checking if collection is allowed
                    InputStream stream = ColetorUtil.getUrlStream("BrutusBot", currentUrl.getUrlObj());
                    String pageContent = ColetorUtil.consumeStream(stream);

                    List<String> linkList = HtmlProcessor.getInstance().extractLinks(pageContent);
                    for (String link : linkList) {                        
                        if(link.length() > 1 && link.substring(0, 2).equals("//")){
                            
                            link = Constants.HTTP + ":" + link;
                            
                        }else if(!ColetorUtil.isAbsoluteURL(link)){
                            link = currentUrl.getDomain() + link;
                        }
                        try{
                            escalonador.adicionaNovaPagina(new URLAddress(link,1));
                        }catch(Exception ex){
                            System.out.println(PrintColor.RED + link + PrintColor.RESET);
                        }
                    }
                    
                    System.out.println("COLLECTED: " + currentUrl.getAddress());
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
