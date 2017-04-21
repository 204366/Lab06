package it.polito.tdp.meteo;

public class TestModel {

	public static void main(String[] args) {

		Model m = new Model();
		
		System.out.println(m.getUmiditaMedia(11));
		
		System.out.println(m.trovaSequenza(5));
		
		System.out.println(m.trovaSequenza(4));
	}

}
