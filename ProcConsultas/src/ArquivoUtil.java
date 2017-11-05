

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArquivoUtil
{
	public static String leTexto(File arquivo) throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(arquivo));
		String str;
		StringBuilder texto = new StringBuilder();
		while ((str = in.readLine()) != null)
		{
			texto = texto.append(str + "\n");
		}
		in.close();
		
		return texto.toString();

	}

	public static void gravaTexto(String texto, File arquivo, boolean append)
			throws IOException
	{
		
		// System.out.println("GRAVANDO: "+arquivo.getAbsolutePath());
		BufferedWriter out = new BufferedWriter(new FileWriter(arquivo, append),100);
		out.write(texto);
		out.close();
	}
	public static void mergeMultipleFiles(File outFile,File ... arqs) throws IOException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(outFile, false));
		
		for(int i = 0; i<arqs.length;i++)
		{
			BufferedReader in = new BufferedReader(new FileReader(arqs[i]));
			String str = "";
			while ((str = in.readLine()) != null)
			{
				out.write(str+"\n");
			}
			in.close();
		}
		out.close();
	}
	public static void mergeFiles(File arq1,File arq2,File outFile) throws IOException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(outFile, false));
		
		BufferedReader in = new BufferedReader(new FileReader(arq1));
		//arquivo 1
		String str = "";
		while ((str = in.readLine()) != null)
		{
			out.write(str+"\n");
		}
		in.close();
		in = new BufferedReader(new FileReader(arq2));
		while ((str = in.readLine()) != null)
		{
			out.write(str+"\n");
		}
		in.close();
		
		out.close();
	}
	public static List<File> procuraArqContendo(File diretorio,String match)
	{
		List<File> arqs = new ArrayList<File>();  
		if(diretorio.isDirectory())
		{
			File[] lstFiles = diretorio.listFiles();
			for(int i = 0 ; i<lstFiles.length ; i++)
			{
				if(lstFiles[i].getName().contains(match))
				{
					arqs.add(lstFiles[i]);
				}
			}
		}
		return arqs;
	}
	public static List<File> procuraArqEndsWith(File diretorio,String match)
	{
		List<File> arqs = new ArrayList<File>();  
		if(diretorio.isDirectory())
		{
			File[] lstFiles = diretorio.listFiles();
			for(int i = 0 ; i<lstFiles.length ; i++)
			{
				if(lstFiles[i].getName().endsWith(match))
				{
					arqs.add(lstFiles[i]);
				}
			}
		}
		return arqs;
	}
	public static Object leObject(File f) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		
		ObjectInputStream arqInput = new  ObjectInputStream(new BufferedInputStream(new FileInputStream( f),50*1024*1024));
		//ObjectInputStream arqInput = new  ObjectInputStream(new FileInputStream( f));
		
		Object o = arqInput.readObject();
		arqInput.close();
		return o;
		/*
		Kryo kryo = new Kryo();
		
		FitnessCalculator ft = kryo.readObject(new Input(new FileInputStream( f)), FitnessCalculator.class);
		
		return ft;
		*/
	}

	public static void gravaObject(File f, Object obj) throws IOException
	{
		
		ObjectOutputStream arqOutput = new ObjectOutputStream(	new FileOutputStream(f));

		arqOutput.writeObject(obj);
		arqOutput.close();
	}
	/*
	public static void gravaListObjects(File f, List<T> obj) throws IOException
	{
		
		ObjectOutputStream arqOutput = new ObjectOutputStream(
				new FileOutputStream(f));

		arqOutput.writeObject(obj);
		arqOutput.close();
	}
	*/
	public static void validaChavesObrigatorias(File fileCnf,Set<String> keys,String ... chavesObrigatorias) throws Exception
	{
		List<String> lstChaveNaoEnc = new ArrayList<String>();
		for(String chave : chavesObrigatorias)
		{
			
			if(!keys.contains(chave))
			{
				lstChaveNaoEnc.add(chave);

			}
		}
		if(lstChaveNaoEnc.size() > 0 )
		{
			throw new Exception("Não foi possivel encontrar as chaves "+lstChaveNaoEnc+" no arquivo de  configuração "+fileCnf.getAbsolutePath());
			
		}
	}
	public static Map<String,String> leKeyValueFile(File arq,boolean toLowerCase) throws IOException
	{
		String[] arrLinhas = ArquivoUtil.leTexto(arq).split("\n");
		Map<String,String> mapKeyValue = new HashMap<String, String>();
		for(int i = 0; i<arrLinhas.length; i++)
		{
			arrLinhas[i] = arrLinhas[i].replaceAll("#.*", "");
			if(arrLinhas[i].length()>0)
			{
				String[] keyValue = arrLinhas[i].split("=");
				if(keyValue.length>=2)
				{
					keyValue[0] = toLowerCase?keyValue[0].toLowerCase().trim():keyValue[0].trim();
					String value = keyValue[1].trim();
					for(int j=2 ; j<keyValue.length ; j++)
					{
						value = "="+keyValue[j].trim();
					}
					mapKeyValue.put(keyValue[0], value);
				}else
				{
					System.err.println("A linha "+i+" do arquivo "+arq.getAbsolutePath()+" não corresponde a um par chave=valor");
					System.exit(0);
				}
			}
		}
		return mapKeyValue;
	}
	/**
	 * copiado de: https://examples.javacodegeeks.com/core-java/io/copy-binary-file-with-streams/
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException 
	 */
	public static void copyfileBin(File sourceFile, File destFile) throws IOException{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
			fis = new FileInputStream(sourceFile);
			fos = new FileOutputStream(destFile);
			
			byte[] buffer = new byte[1024];
			int noOfBytes = 0;

			System.out.println("Copying file using streams");

			// read bytes from source file and write to destination file
			while ((noOfBytes = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, noOfBytes);
			}
		}
		finally {
			// close the streams using close method

				if (fis != null) {
					fis.close();
				}
				if (fos != null) {
					fos.close();
				}

		}
	}
	public static void copyfile(File f1, File f2)
	{
		try
		{
			//le
			String text = ArquivoUtil.leTexto(f1);

			
			// For Append the file.
			// OutputStream out = new FileOutputStream(f2,true);

			// For Overwrite the file.
			ArquivoUtil.gravaTexto(text, f2, false);			
			
			// System.out.println("File copied.");
		} catch (FileNotFoundException ex)
		{
			System.err
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e)
		{
			System.err.println(e.getMessage());
		}
	}

}
