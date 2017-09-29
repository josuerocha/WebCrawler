/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler.escalonadorCurtoPrazo;

import com.trigonic.jrobotx.Record;
import crawler.URLAddress;

/**
 *
 * @author jr
 */
public class PageFetcher extends Thread{
    
    private Escalonador escalonador;
    
    public PageFetcher(Escalonador escalonador){
        this.escalonador = escalonador;
    }
    
    @Override
    public void run(){
        URLAddress currentUrl;
        Record record;
        while(true){
            currentUrl = escalonador.getURL(); //Requesting page from page scheduler
            
            if((record = escalonador.getRecordAllowRobots(currentUrl)) == null ){ //Getting robots.txt record from domain
                
            }
            
            if(record.allows(currentUrl.getPath())){
                    
            }
        }
    }
    
    
}
