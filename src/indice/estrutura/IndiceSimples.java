package indice.estrutura;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;




public class IndiceSimples extends Indice
{
	
	
	/**
	 * Versao - para gravação do arquivo binário
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String,List<Ocorrencia>> mapIndice = new HashMap<String,List<Ocorrencia>>();

	
	public IndiceSimples()
	{

	}
	


	@Override
	public void index(String termo,int docId,int freqTermo) {
		if(!mapIndice.containsKey(termo)){
			List<Ocorrencia> list = new ArrayList<Ocorrencia>();
			list.add(new Ocorrencia(docId,freqTermo));
			mapIndice.put(termo, list);
		}else{
			mapIndice.get(termo).add(new Ocorrencia(docId,freqTermo));
		}
	}

	
	

	@Override
	public Map<String,Integer> getNumDocPerTerm()
	{
		Map<String,Integer> map = new HashMap<String,Integer>();
		for(String termo : mapIndice.keySet()){
			map.put(termo,mapIndice.get(termo).size());
		}
		return map;
	
	}
	
	@Override
	public int getNumDocumentos()
	{		
		
		Set<Integer> setDocumento = new HashSet<Integer>();
		
		for(String termo : mapIndice.keySet()){
			
			List<Ocorrencia> listOcorrencia = mapIndice.get(termo);
			for(Ocorrencia ocorrencia : listOcorrencia){
				
				if(!setDocumento.contains(ocorrencia.getDocId())){
					setDocumento.add(ocorrencia.getDocId());
				}
			
			}
					
		}
		
		return setDocumento.size();
	}
	
	@Override
	public Set<String> getListTermos()
	{
		Set<String> set = new HashSet<String>(); 
		for(String termo : mapIndice.keySet()){
			set.add(termo);
		}
		return set;
	}	
	
	@Override
	public List<Ocorrencia> getListOccur(String termo)
	{
		return mapIndice.get(termo);
	}	
	

	public static void main(String args[]){
		
		
		
	}

}
