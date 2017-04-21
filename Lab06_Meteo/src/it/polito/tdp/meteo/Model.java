package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.List;


import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;


public class Model {

	private final static int COST = 50;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	MeteoDAO dao;
	List<Rilevamento> ril;
	List<SimpleCity> parziale;
	List<Rilevamento> rilevamenti;
	int livello;
	List<SimpleCity> best;
	int meseG ;
	List<Citta> citta;

	public Model() {
		dao = new MeteoDAO();
		ril = new ArrayList<Rilevamento>();
		parziale = new ArrayList<SimpleCity>();
		rilevamenti = new ArrayList<Rilevamento>();
		best = new ArrayList<SimpleCity>();
		livello = 0;
		meseG = 0;
		citta = new ArrayList<Citta>();
	}

	public String getUmiditaMedia(int mese) {
		Double genova = dao.getAvgRilevamentiLocalitaMese(mese, "Genova");
		Double torino = dao.getAvgRilevamentiLocalitaMese(mese, "Torino");
		Double milano = dao.getAvgRilevamentiLocalitaMese(mese, "Milano");
		String risultato = "Genova:" + genova + " Torino:" + torino + " Milano:" +milano;
		return risultato;
	}

	public String trovaSequenza(int mese) {
		meseG = mese;
		if(ril.isEmpty()){
			ril.addAll(dao.getAllRilevamentiLocalitaMese(mese, "Genova"));
			ril.addAll(dao.getAllRilevamentiLocalitaMese(mese, "Milano"));
			ril.addAll(dao.getAllRilevamentiLocalitaMese(mese, "Torino"));
		}
		
		if(livello >= NUMERO_GIORNI_TOTALI){
			//do something
			//valuto soluzione trovata
			if(this.controllaParziale(parziale) == true &&
				this.punteggioSoluzione(parziale)< this.punteggioSoluzione(best)){
				best.clear();
				best.addAll(parziale);
				System.out.println(parziale);
			}
		}
		
		for(Rilevamento r : ril){
			
			if(!rilevamenti.contains(r)){
				
				SimpleCity s = new SimpleCity(r.getLocalita(), r.getUmidita()* COST);	
				parziale.add(s);
				rilevamenti.add(r);
				livello++;
				this.trovaSequenza(meseG);
				parziale.remove(s);

			}
		
		}
		return best.toString();
	}
	
	
	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {
		double score = 0.0;
		String citta = "";
		for(SimpleCity s : soluzioneCandidata){
			if(s.getNome().compareTo(citta) == 0)
				score += s.getCosto();
			else{
				citta = s.getNome();
				s.setCosto(s.getCosto() + 100);
				score += s.getCosto();
			}
		}
		
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {
		SimpleCity temp = new SimpleCity("");
		int count = 3;
		
		if(parziale.contains(new SimpleCity("Torino")) &&
				parziale.contains(new SimpleCity("Milano")) && 
				parziale.contains(new SimpleCity("Genova"))){
			for(SimpleCity s : parziale){
				if(s.equals(temp)){
					count ++;
				}
				else{
					if(count < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN )
						return false;
					else
						count = 0;
				}
				Citta c = new Citta (s.getNome(),dao.getAllRilevamentiLocalitaMese(meseG, s.getNome()));
				if(citta.contains(c)){
					citta.get(citta.indexOf(c)).increaseCounter();
				}
				else{
					citta.add(c);
					c.increaseCounter();
				}
			}
			for(Citta c : citta){
				if(c.getCounter() > NUMERO_GIORNI_CITTA_MAX)
					return false;
			}
			
			return true;	
		}
		
		return false;
	}

}
