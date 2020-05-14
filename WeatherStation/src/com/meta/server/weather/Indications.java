package com.meta.server.weather;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meta.SQL.SQLDatabase;

public class Indications {
	private static Indications actual= new Indications(System.currentTimeMillis(), 0, 0, 0, 0, 0);
	private long timestamp;
	private int  rainmeter, pressure;
	private float anemeter, temperature, humidity;
	public Indications(long timestamp,float anemeter,int rainmeter,float temperature,float humidity,int pressure) {
		this.timestamp = timestamp;
		this.anemeter = anemeter;
		this.rainmeter = rainmeter;
		this.temperature = temperature;
		this.humidity = humidity;
		this.pressure = pressure;
	}
	public static Indications parse(String json) {
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(json).getAsJsonObject();
		return new Indications(
				obj.get("timestamp").getAsLong(),
				obj.get("anemeter").getAsFloat(),
				obj.get("rainmeter").getAsInt(),
				obj.get("temperature").getAsFloat(),
				obj.get("humidity").getAsFloat(),
				obj.get("pressure").getAsInt()
				);
	}
	public static Indications[] getLastIndications(int count) throws SQLException {
		Connection c = SQLDatabase.getInstance().getConnection();
		Statement s = c.createStatement();
		ResultSet resultsql = s.executeQuery("SELECT `data` FROM logs ORDER BY `timestamp` DESC LIMIT " + count + ";");
		Indications[] result = new Indications[count];
		for (int i = 0;i<count;i++) {
			if(resultsql.next()) {
				result[i] = Indications.parse(resultsql.getString("data"));
			}else {
				break;
			}	
		}
		
		s.close();
		c.close();
		return result;
	}
	public void saveLog(String ip) throws SQLException {
		Connection c = SQLDatabase.getInstance().getConnection();
		Statement s = c.createStatement();
		s.executeUpdate(
				String.format("INSERT INTO logs values(%d,'%s','%s')",
						timestamp,ip,toJSON().toString())
				);
		s.close();
		c.close();
	}
	public void saveWeather() throws SQLException {
		Connection c = SQLDatabase.getInstance().getConnection();
		Statement s = c.createStatement();
		s.executeUpdate(
				String.format("INSERT INTO weather values(%d,'%s')",
						timestamp,toJSON().toString())
				);
		s.close();
		c.close();
	}
	public JsonElement toJSON() {
		JsonObject obj = new JsonObject();
		obj.addProperty("timestamp", timestamp);
		obj.addProperty("anemeter", anemeter);
		obj.addProperty("rainmeter", rainmeter);
		obj.addProperty("temperature", temperature);
		obj.addProperty("humidity", humidity);
		obj.addProperty("pressure", pressure);
		
		return obj;
	}
	public static Indications getAverage(List<Indications> arr,long timestamp) {
		int size=arr.size();
		Indications result = new Indications(timestamp, 0, 0, 0, 0, 0);
		if(size < 1) {
			return result;
		}
		for (Indications ind : arr) {
			result.anemeter+=ind.anemeter;
			result.rainmeter+=ind.rainmeter;
			result.temperature+=ind.temperature;
			result.humidity+=ind.humidity;
			result.pressure+=ind.pressure;
		}
		result.anemeter/=size;
		result.temperature/=size;
		result.humidity/=size;
		result.pressure/=size;
		return result;
	}
	public void updateActualData() {
		actual=this;
	}
	public static Indications getActualData() {
		return actual;
	}
}
