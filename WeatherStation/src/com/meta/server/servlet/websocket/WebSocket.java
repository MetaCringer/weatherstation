package com.meta.server.servlet.websocket;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meta.server.weather.Indications;

@org.eclipse.jetty.websocket.api.annotations.WebSocket
public class WebSocket  {
	static Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>()); 
	public static void updateIndications() {
		
		String message = packPacket("updateIndications", Indications.getActualData().toJSON());
		for (Session session : sessions) {
			try {
				session.getRemote().sendString(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@OnWebSocketConnect
	public void onConnect(Session session) {
		sessions.add(session);
		System.out.println(session.getRemote().getInetSocketAddress() + " are connected");
		
		try {
			session.getRemote().sendString(packPacket("updateIndications",Indications.getActualData().toJSON()));
			session.getRemote().sendString(packPacket("initChart", ChartsHandler.getInstance().toJSON()));
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	@OnWebSocketMessage
	public void onWebSocketText(Session session,String text) {
		System.out.println("incoming: " + text);
	}
	@OnWebSocketClose
	public void onWebSocketClose(Session s,int statusCode, String reason) {
		System.out.println(s.getRemote().getInetSocketAddress() +" are disconnected " + statusCode + " " + reason);
		sessions.remove(s);
	}
	@OnWebSocketError
	public void onError(Session session, Throwable trouble) {
		trouble.printStackTrace();
	}
	private static String packPacket(String purpose,String data) {
		JsonObject packet = new JsonObject();
		packet.addProperty("purpose", purpose);
		packet.addProperty("data", data);
		return packet.toString();
	}
	private static String packPacket(String purpose,JsonElement data) {
		JsonObject packet = new JsonObject();
		packet.addProperty("purpose", purpose);
		packet.add("data", data);
		return packet.toString();
	}
}
