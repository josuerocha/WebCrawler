/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package query_eval;

/**
 *
 * @author jr
 */
public class TermData implements Comparable<TermData>{
    
    private static int orderBy = 1;
    
    
    private String term;
    private double idf;
    private int globalFreq;
    private double tf;

    public TermData(String term, double idf, int globalFreq,double tf){
        this.term = term;
        this.idf = idf;
        this.globalFreq = globalFreq;
        this.tf = tf;
    }
    
    public String getTerm() {
        return term;
    }

    public double getIdf() {
        return idf;
    }

    public int getGlobalFreq() {
        return globalFreq;
    }
    
    public double getTf(){
        return tf;
    }
    
    public static void orderByIdf(){
        orderBy = 1;
    }
    
    public static void orderByFrequency(){
        orderBy = 2;
    }
    
    public static void orderByTf(){
        orderBy = 3;
    }
    
    public int compareTo(TermData o) {
        //ordem ascendente
        switch(orderBy){
            case 1:
                return Double.compare(this.idf,o.getIdf());
            case 2:
                return  o.getGlobalFreq() - this.globalFreq;
            case 3:
                return Double.compare(this.tf,o.getTf());
            default:
                return 0;
        }
    }
    
    
}
