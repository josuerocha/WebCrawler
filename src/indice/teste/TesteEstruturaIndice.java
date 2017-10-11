package indice.teste;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import indice.estrutura.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TesteEstruturaIndice {
	private Indice indiceTeste ;
	
	@Before
	public void iniciaIndice(){
		indiceTeste = new IndiceSimples();
		//indiceTeste = new IndiceLight(1000);
		
		//casa apareceu 10 vezes no doc. 1
		indiceTeste.index("casa",1,10);
		
		//vermelho apareceu 3 vezes no doc. 1
		indiceTeste.index("vermelho",1,3);
		
		//verde apareceu 1 vez no doc. 1
		indiceTeste.index("verde",1,1);
		
		
		//casa apareceu 3 vezes no doc. 2
		indiceTeste.index("casa",2,3);
		
		
		//vermelho apareceu 1 vez no doc. 2
		indiceTeste.index("vermelho",2,1);
		
		//vermelho apareceu 1 vez no doc. 3
		indiceTeste.index("vermelho",3,1);
		
		indiceTeste.concluiIndexacao();
		System.out.println("========================Indice==================");
		System.out.println(indiceTeste);
	}

	
	@Test
	public void testGetNumDocumentos() {
		assertEquals(3,indiceTeste.getNumDocumentos());
			
	}
	@Test
	public void testGetListTermos() {
		List<String> lstTermos = new ArrayList<>(indiceTeste.getListTermos());
		Collections.sort(lstTermos);
		assertEquals(3,lstTermos.size());
		assertEquals("casa",lstTermos.get(0));
		assertEquals("verde",lstTermos.get(1));	
		assertEquals("vermelho",lstTermos.get(2));	
	}
	@Test
	public void testGetNumDocPerTerm() {
		 Map<String,Integer> mapNumDocPerTerm = indiceTeste.getNumDocPerTerm();
		 //casa apareceu em dois documentos
		 assertEquals(2,(int)mapNumDocPerTerm.get("casa"));
		 
		 //vermelho apareceu em tres documentos
		 assertEquals(3,(int)mapNumDocPerTerm.get("vermelho"));
		 
		 //verde apareceu em um documento
		 assertEquals(1,(int)mapNumDocPerTerm.get("verde"));
		 
	}
	public void verificaDuplaOcMesmoDoc(List<Ocorrencia> lstOcorrencias,String termo){
		Set<Integer> doc = new HashSet<Integer>();
		for(Ocorrencia o : lstOcorrencias){
			if(doc.contains(o.getDocId())){
				fail("A lista de ocorrencias do termo "+termo+" possui mais de uma ocorrencia do documento "+o.getDocId());
			}
			doc.add(o.getDocId());
			
		}
	}
	@Test 
	public void testGetListOccur(){
		List<Ocorrencia> lstOcorrencias = indiceTeste.getListOccur("casa");
				
		for(Ocorrencia o : lstOcorrencias){
			switch(o.getDocId()){
				case 1://casa apareceu 10 vezes no doc. 1
					assertEquals(10, o.getFreq());
					break;
				case 2://casa apareceu 3 vezes no doc. 2
					assertEquals(3, o.getFreq());
					break;
				default:
					fail("Casa nao apareceu no doc. "+o.getDocId()+" e deveria");
					break;
			}
		}
		assertEquals("casa possui 2 ocorrencias: nos doc 1 e 2",2,lstOcorrencias.size());
		verificaDuplaOcMesmoDoc(lstOcorrencias,"casa");

		
		lstOcorrencias = indiceTeste.getListOccur("verde");
		
		for(Ocorrencia o : lstOcorrencias){
			switch(o.getDocId()){
				case 1://verde apareceu 10 vezes no doc. 1
					assertEquals(1, o.getFreq());
					break;
				default:
					fail("Casa nao apareceu no doc. "+o.getDocId()+" e deveria");
					break;
			}
		}
		assertEquals("verde possui 1 ocorrencia: no doc 1",1,lstOcorrencias.size());
		
		verificaDuplaOcMesmoDoc(lstOcorrencias,"verde");
		
		
		lstOcorrencias = indiceTeste.getListOccur("vermelho");
		
		for(Ocorrencia o : lstOcorrencias){
			switch(o.getDocId()){
				case 1://vermelho apareceu 3 vezes no doc. 1
					assertEquals(3, o.getFreq());
					break;
				case 2://vermelho apareceu 1 vez no doc. 2
					assertEquals(1, o.getFreq());
					break;
				case 3://vermelho apareceu 1 vez no doc. 3
					assertEquals(1, o.getFreq());
					break;
				default:
					fail("vermelho nao apareceu no doc. "+o.getDocId()+" e deveria");
					break;
			}
		}
		assertEquals("vermelho possui 3 ocorrencias: nos doc 1, 2 e 3",3,lstOcorrencias.size());
		verificaDuplaOcMesmoDoc(lstOcorrencias,"vermelho");

	
	}
	

}
