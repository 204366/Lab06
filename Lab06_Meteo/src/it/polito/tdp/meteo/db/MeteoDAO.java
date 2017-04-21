package it.polito.tdp.meteo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;

public class MeteoDAO {

	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		mese = mese - 1;
		List<Rilevamento> rilevamenti = this.getAllRilevamenti();
		List<Rilevamento> ril = new ArrayList<Rilevamento>();
		for(Rilevamento r : rilevamenti){
			if(mese == 11){
				GregorianCalendar gregorianCalendar1 = new GregorianCalendar(2013,10 , 31);
				GregorianCalendar gregorianCalendar2 = new GregorianCalendar(2013,0, 1);

				Date data1 = gregorianCalendar1.getTime();
				Date data2 = gregorianCalendar2.getTime();
				if(r.getLocalita().compareTo(localita) == 0 && r.getData().before(data2) &&r.getData().after(data1))
					ril.add(r);
			}
			else{
				GregorianCalendar gregorianCalendar1 = new GregorianCalendar(2013,mese -1 , 31);
				GregorianCalendar gregorianCalendar2 = new GregorianCalendar(2013,mese + 1, 1);
	
				Date data1 = gregorianCalendar1.getTime();
				Date data2 = gregorianCalendar2.getTime();
				
				if(r.getLocalita().compareTo(localita) == 0 && r.getData().before(data2) &&r.getData().after(data1))
					ril.add(r);
			}
		}
			
		
		return ril;
		
	}

	public Double getAvgRilevamentiLocalitaMese(int mese, String localita) {
		double media = 0.0;
		
		for(Rilevamento r : this.getAllRilevamentiLocalitaMese(mese, localita))
			media += r.getUmidita();
		
		return media/ this.getAllRilevamentiLocalitaMese(mese, localita).size();
	}

}
