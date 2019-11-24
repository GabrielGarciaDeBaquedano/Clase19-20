package pr4;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.sun.xml.internal.fastinfoset.algorithm.HexadecimalEncodingAlgorithm;

public class Recursividad {

	public static void main(String[] args) {
		System.out.println(invertirFrase("Hola mundo"));
		System.out.println(invertirPalabras("Hola mundo"));
		longAHexa();
		System.out.println(sacaPalabras("Texto"));
//		ordenaQuick();
//		buscaPalabra();
	}
	
	public static String invertirFrase(String frase) {
		if (frase.length() == 1) {
			return frase;
		}else {
			return invertirFrase(frase.substring(1)) + frase.charAt(0);
		}
	}

	public static String invertirPalabras(String frase) {
		String[] separadas = frase.split(" ");
		if(separadas.length == 1) {
			return frase;
		}else {
			return invertirPalabras(frase.split(" ",2)[1]) + " " + frase.split(" ",2)[0];
		}
	}
	
	private static void longAHexa() {
		long l = 32;
		long l2 = 361;  
		System.out.println(cambio(l)+"\t numero en DEC " + l);
		System.out.println(cambio(l2)+"\t numero en DEC " + l2);


	}

	private static String cambio (Long l ) {


		int resto = (int) (l % 16); 

		String resultado = ""; 
		if (l == 0) {
			return Integer.toString(0); 
		}else {
			if (resto < 10) {
				resultado = resto + resultado; 

			}else if(resto == 10 ) {
				resultado = "A" + resultado; 

			}else if (resto == 11) {
				resultado = "B" + resultado; 

			}
			else if (resto == 12) {
				resultado = "C" + resultado; 
			}
			else if (resto == 13) {
				resultado = "D" + resultado; 
			}
			else if (resto == 14) {
				resultado = "E" + resultado; 
			}else if (resto == 15) {
				resultado = "F" + resultado;
			}else {

				int r = (int) (l/16);
				resultado = Integer.toString(r) + resultado;
			}
			return (cambio(l/16) + resultado);
		}
		

	}
	
	public static ArrayList<String> sacaPalabras(String nomFic){
		ArrayList<String> palabras = new ArrayList<String>();
		try {
			Scanner fE = new Scanner( new FileInputStream( nomFic ) );
			while (fE.hasNext()) {
				String linea = fE.nextLine();
				// Trabajo con cada línea
				try {
					for (String palabra : linea.split(" ")) {
						palabras.add(palabra);
					}
				} catch (Exception e) {
					System.out.println( "Problema en la línea " + linea );
				}
			}
			fE.close();
		} catch (IOException e) {
			System.out.println( "No ha sido posible leer el fichero." );
		}
		return reOrden(palabras, 1);
	}
	
	public static ArrayList<String> reOrden(ArrayList<String> palabras, Integer n){
		if( palabras.size() == 1) {
			return palabras;
		}else if (palabras.size()==n-1) {
			ArrayList<String> resultado = new ArrayList<String>();
			for (int i = (palabras.size()/2); i < palabras.size(); i++) {
				resultado.add(palabras.get(i));
			}
			return resultado;
		}
		else {
			palabras.add(palabras.get(palabras.size()-n));
			return reOrden(palabras, n+2);
		}
	}
	
}
