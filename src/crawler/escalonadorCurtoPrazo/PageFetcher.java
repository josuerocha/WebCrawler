/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler.escalonadorCurtoPrazo;

import com.trigonic.jrobotx.Record;
import com.trigonic.jrobotx.RobotExclusion;
import crawler.URLAddress;

/**
 *
 * @author jr
 */
public class PageFetcher extends Thread{
    
    private Escalonador escalonador;
    private RobotExclusion robotExclusion;
    
    public PageFetcher(Escalonador escalonador){
        this.escalonador = escalonador;
    }
    
    @Override
    public void run(){
        URLAddress currentUrl;
        Record record;
        robotExclusion = new RobotExclusion();
        
        while(true){
            currentUrl = escalonador.getURL(); //Requesting page from page scheduler
            
            if((record = escalonador.getRecordAllowRobots(currentUrl)) == null ){ //Getting robots.txt record from domain
                try{
                    record = robotExclusion.get(currentUrl.getUrlRobotsTxt(), "BrutusBot"); //requesting robots.txt from URL
                    escalonador.putRecorded(currentUrl.getDomain(), record); //saving requested robots.tx
                }catch(Exception ex){
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
                
            }
            
            if(record.allows(currentUrl.getPath())){ //Checking if collection is allowed
                   System.out.println("PAGE ALLOWS COLLECTING " + currentUrl.getAddress());
            }
            
        }
    }
    
    
}
