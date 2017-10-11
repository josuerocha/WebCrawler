package indice.teste;

import static org.junit.Assert.*;

import java.util.Arrays;

import indice.estrutura.IndiceLight;

import org.junit.Test;

public class TesteIndiceLight {
	/*
	public static int[] arrTermIDOrig = {1,2,3,1,2,3,4,5,2,5,6,7};
	public static int[] arrDocIDOrig = {10,20,30,10,20,30,40,50,20,50,60,70};
	public static int[] arrFreqTOrig = {1,2,3,4,5,6,7,8,9,10,11,12};
	*/
	
	public static int[] arrTermIDOrig = {1,1 ,1 ,2 ,2 ,3 ,3 ,3 ,4 ,4 ,5 ,6};
	public static int[] arrDocIDOrig = {10,20,30,10,20,30,40,50,20,50,60,70};
	public static int[] arrFreqTOrig = {1 , 2, 3, 4, 5, 6, 7, 8, 9,10,11,12};
	private int[] arrDocId = {};
	private int[] arrFreqTermo = {};
	private int[] arrTermId = {};
	
	@Test
	public void testOrdenacao() {
		
		IndiceLight indiceTeste = new IndiceLight(1000);
		
		reordena(indiceTeste);
		indiceTeste.setArrs(arrDocId, arrTermId, arrFreqTermo);
		testaOrdenacao(indiceTeste);
		
		//ordena em ordem decrescente
		for(int i =0 ; i<arrDocId.length/2; i++){
			exchange(i,arrDocId.length-i-1);
		}
		indiceTeste.setArrs(arrDocId, arrTermId, arrFreqTermo);

		testaOrdenacao(indiceTeste);
		//ordem aleatoria
		for(int r=0 ; r<100 ;r++){
			reordena(indiceTeste);
			for(int i = 0 ; i<arrDocId.length ; i++){
				int posJ = (int) Math.random()*(arrDocId.length-1);
				if(i!=posJ){
					exchange(i,posJ);
				}
			}
			indiceTeste.setArrs(arrDocId, arrTermId, arrFreqTermo);
			testaOrdenacao(indiceTeste);
		}
	}

	private void reordena(IndiceLight indiceTeste) {
		arrDocId =  Arrays.copyOf(TesteIndiceLight.arrDocIDOrig, TesteIndiceLight.arrDocIDOrig.length);;
		arrTermId = Arrays.copyOf(TesteIndiceLight.arrTermIDOrig, TesteIndiceLight.arrTermIDOrig.length);;
		arrFreqTermo = Arrays.copyOf(TesteIndiceLight.arrFreqTOrig, TesteIndiceLight.arrFreqTOrig.length);;
		indiceTeste.setArrs(arrDocIDOrig, arrTermIDOrig, arrFreqTOrig);
	}

	public void exchange(int posI,int posJ){
		int docAux = this.arrDocId[posI];
		int freqAux = this.arrFreqTermo[posI];
		int termAux = this.arrTermId[posI];
		
		this.arrDocId[posI] = this.arrDocId[posJ];
		this.arrFreqTermo[posI] = this.arrFreqTermo[posJ];
		this.arrTermId[posI] = this.arrTermId[posJ];
		
		this.arrDocId[posJ] = docAux;
		this.arrFreqTermo[posJ] = freqAux;
		this.arrTermId[posJ] = termAux;
		
	}
	private void testaOrdenacao(IndiceLight indiceTeste) {
		// TODO Auto-generated method stub
		System.out.println("====================== Teste==================");
		System.out.println("TermId: "+Arrays.toString(arrTermId));
		System.out.println("DocId: "+Arrays.toString(arrDocId));

		System.out.println("Freq: "+Arrays.toString(arrFreqTermo));
		indiceTeste.ordenaIndice();
		
		int[] arrDocIdR =  indiceTeste.getArrDocId();
		int[] arrTermIdR = indiceTeste.getArrTermId();
		int[] arrFreqTermoR = indiceTeste.getArrFreq();
		System.out.println("====================== Teste Apos ordenação==================");
		System.out.println("TermId: "+Arrays.toString(arrTermIdR));
		System.out.println("DocId: "+Arrays.toString(arrDocIdR));

		System.out.println("Freq: "+Arrays.toString(arrFreqTermoR));
		for(int i =0 ; i<arrDocIDOrig.length ; i++){
			assertEquals("A posicao "+i+" do array está diferente",arrDocIDOrig[i],arrDocIdR[i]);
			assertEquals("A posicao "+i+" do array está diferente",arrTermIDOrig[i],arrTermIdR[i]);
			assertEquals("A posicao "+i+" do array está diferente",arrFreqTOrig[i],arrFreqTermoR[i]);
		}
		assertEquals(arrDocIDOrig.length,arrTermIdR.length);
		System.out.println("ok");
	}

	
}
