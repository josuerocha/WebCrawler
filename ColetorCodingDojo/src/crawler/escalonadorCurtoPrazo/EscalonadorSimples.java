package crawler.escalonadorCurtoPrazo;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.trigonic.jrobotx.Record;
import com.trigonic.jrobotx.RobotExclusion;

import crawler.Servidor;
import crawler.URLAddress;

public class EscalonadorSimples implements Escalonador{
	private int contadorPaginas;
	public Set<URLAddress> pagVisitada = new HashSet<URLAddress>();
	public Map<Servidor,List<URLAddress>> fila = new LinkedHashMap<Servidor,List<URLAddress>>();
	public Map<String,Record> mapRobots = new HashMap<String,Record>(); 
	static final int MAXPROFUNDIDADE = 4;
	static final int LIMITE_PAGINAS = 500;
	
	@Override
	public synchronized URLAddress getURL() {
		URLAddress url = null;
		while(!this.finalizouColeta()){
			try{
				for(Servidor s : fila.keySet()){
					List<URLAddress> urlList = fila.get(s);
						if(!urlList.isEmpty()){
							url = urlList.remove(0);
							s.acessadoAgora();
							return url;							
					    }						
				}
				
				wait(1000);
				
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		return url;
	}

	@Override
	public synchronized boolean adicionaNovaPagina(URLAddress urlAdd) {
		// TODO Auto-generated method stub
		if(!pagVisitada.contains(urlAdd) && urlAdd.getDepth() < MAXPROFUNDIDADE ){
			Servidor servidor = new Servidor(urlAdd.getDomain());
			if (!fila.containsKey(servidor)){
				List<URLAddress> lista = new ArrayList<URLAddress>();
				lista.add(urlAdd);
				fila.put(servidor,lista);
			}else{
				 fila.get(servidor).add(urlAdd);
			}
			notifyAll();
			return true;
		}else {
			return false;
		}
	}


	@Override
	public synchronized Record getRecordAllowRobots(URLAddress url) {
		// TODO Auto-generated method stub		
		return mapRobots.get(url.getDomain());		
	}

	@Override
	public synchronized void putRecorded(String domain, Record domainRec) {
		// TODO Auto-generated method stub
		mapRobots.put(domain, domainRec);
	}
	@Override
	public boolean finalizouColeta() {
		return contadorPaginas >= LIMITE_PAGINAS;
	}

	@Override
	public synchronized void countFetchedPage() {
		contadorPaginas++;
		
	}
	

}
