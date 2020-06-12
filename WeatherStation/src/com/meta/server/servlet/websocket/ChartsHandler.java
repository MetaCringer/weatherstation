package com.meta.server.servlet.websocket;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meta.SQL.SQLDatabase;
import com.meta.server.weather.Indications;

public class ChartsHandler {
	private JsonArray name;
	private JsonArray data;
	private JsonArray title;
	private int size;
	private ChartsHandler() {
		size=6;
		name=new JsonArray();
		name.add("Anemeter");
		name.add("RainMeter");
		name.add("Temperature");
		name.add("Humidity");
		name.add("Pressure");
		name.add("Direction");
		title=new JsonArray();
		title.add("Вітер");
		title.add("Опади");
		title.add("Температура");
		title.add("Волога");
		title.add("Тиск повітря");
		title.add("Напрям вітру");
		data=new JsonArray();
		for(int i = 0;i<size;i++) {
			JsonArray dat = new JsonArray();
			dat.add(new JsonArray());
			dat.get(0).getAsJsonArray().add("Години");
			dat.get(0).getAsJsonArray().add(title.get(i));
			
			data.add(dat);
		}
		
	}
	public JsonElement toJSON() {
		JsonObject obj = new JsonObject();
		obj.add("name", name);
		obj.add("data", data);
		obj.add("title",title);
		obj.addProperty("size", size);
		return obj;
	}
	public static ChartsHandler getInstance() throws SQLException {
		Connection c = SQLDatabase.getInstance().getConnection();
		Statement s = c.createStatement();
		long now= getRoundHours(new Date().getTime());
		ResultSet r = s.executeQuery(String.format(
				"SELECT * FROM weather WHERE %d <= `timestamp` AND %d > `timestamp` ORDER BY `timestamp` ASC LIMIT 24;",
				getTimeWithOffset(now, -24),now));
		int Size =0;
		List<JsonObject> result = new ArrayList<JsonObject>();
		JsonParser parser = new JsonParser();
		while(r.next()) {
			Size++;
			result.add(parser.parse(r.getString("data")).getAsJsonObject());
		}
		int i = 24 - Size;
		List<Indications> logs= new ArrayList<Indications>();
		Indications ind;
		while(i>0) {
			r = s.executeQuery(String.format("SELECT * FROM logs WHERE %d <= `timestamp` AND %d > `timestamp`;",
					getTimeWithOffset(now, -i),getTimeWithOffset(now, -(i-1))
					));
			logs.clear();
			while(r.next()) {
				logs.add(Indications.parse(r.getString("data")));
			}
			ind = Indications.getAverage(logs,getTimeWithOffset(now, -i));
			ind.saveWeather();
			result.add(ind.toJSON().getAsJsonObject());
			s.executeUpdate(String.format(
					"DELETE FROM logs WHERE %d <= `timestamp` AND %d > `timestamp`;",
					getTimeWithOffset(now, -i),getTimeWithOffset(now, -(i-1))
					));
			//System.out.println(ind.toJSON().getAsJsonObject().get("timestamp").getAsLong());
			i--;
		}
		ChartsHandler RESULT = new ChartsHandler();
		//System.out.println("=========");
		for (JsonObject obj : result) {
			int hour = new Date(obj.get("timestamp").getAsLong()).getHours();
			//System.out.println(obj.get("timestamp").getAsLong() +"    " + hour);
			RESULT.data.get(0).getAsJsonArray().add(packArr(hour,obj.get("anemeter").getAsFloat()));
			RESULT.data.get(1).getAsJsonArray().add(packArr(hour,obj.get("rainmeter").getAsInt()));
			RESULT.data.get(2).getAsJsonArray().add(packArr(hour,obj.get("temperature").getAsFloat()));
			RESULT.data.get(3).getAsJsonArray().add(packArr(hour,obj.get("humidity").getAsFloat()));
			RESULT.data.get(4).getAsJsonArray().add(packArr(hour,obj.get("pressure").getAsInt()));
			RESULT.data.get(5).getAsJsonArray().add(packArr(hour,obj.get("direction").getAsInt()));
		}
		s.close();
		c.close();
		return RESULT;
	}
	private static JsonArray packArr(int hour,Number data) {
		JsonArray arr=new JsonArray();
		arr.add(hour+"");
		arr.add(data);
		return arr;
	}
	private static long getTimeWithOffset(long time,int hour) {
		return time+(hour*60*60*1000);
	}
	private static long getRoundHours(long time) {
		return time -(time%(60*60*1000));
	}
}
	
