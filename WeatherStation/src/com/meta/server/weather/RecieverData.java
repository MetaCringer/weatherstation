package com.meta.server.weather;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.meta.SQL.SQLDatabase;
import com.meta.server.servlet.websocket.WebSocket;

public class RecieverData implements Runnable{
	public static void main(String[] args) {
		System.out.println(Integer.parseInt("100325"));
	}
	
	
	private static RecieverData instance;
	public Thread thread;
	
	public static void start() {
		instance = new RecieverData();
		Indications[] ind;
		try {
			if( ((ind = Indications.getLastIndications(1))!=null) && (ind[0]!=null)) {
				ind[0].updateActualData();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private RecieverData() {
		thread = new Thread(this);
		thread.start();
	}
	@Override
	public void run() {
		DatagramSocket soc;
		try {
			soc = new DatagramSocket(9001);
			DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
			while(true) {
				soc.receive(packet);
				save(packet);
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	private void save(DatagramPacket packet) {
		
		String data= new String(packet.getData());
		data.substring(0, data.length()-2);
		data=data.trim();
		
		String[] args = data.split(":");
		if(args.length<5) {
			System.out.println("error udp packet: " + data);
			return;
		}
		Indications indication;
		try {
			System.out.println(args[4]);
			indication= new Indications(
			System.currentTimeMillis(),
			Float.parseFloat(args[0]),
			Integer.parseInt(args[1]),
			Float.parseFloat(args[2]),
			Float.parseFloat(args[3]),
			Integer.parseInt(args[4]));
		}catch(NumberFormatException e) {
			e.printStackTrace();
			return;
		}
		indication.updateActualData();
		WebSocket.updateIndications();
		try {
			indication.saveLog(packet.getSocketAddress().toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
