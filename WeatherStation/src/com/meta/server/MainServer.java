package com.meta.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ListenerHolder;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.Container.Listener;

import com.meta.server.servlet.MainServlet;
import com.meta.server.servlet.WSServlet;
import com.meta.server.weather.RecieverData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class MainServer {

	public static void main(String[] args) throws Exception {
		
		
		
		//saveResources();
		Server server = new Server(80);
		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(new ServletHolder(new MainServlet()), "/*");
		handler.addServletWithMapping(new ServletHolder(new WSServlet()), "/ws");
		server.setHandler(handler);
		RecieverData.start();
		System.out.println("test987");
		server.start();
		server.join();
		
	}

	private static void saveResources() throws IOException, URISyntaxException {
		File assets= new File("assets/");
		if(!assets.exists()) {
			assets.mkdir();
		}
		
		JarFile thisjar = new JarFile(new File(MainServer.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
		Enumeration<JarEntry> insides =thisjar.entries();
		JarEntry entry;
		File UploadFile;
		OutputStream writer;
		InputStream reader;
		while(insides.hasMoreElements()) {
			entry = insides.nextElement();
			if(entry.getName().startsWith("assets/") && !(UploadFile = new File(entry.getName() )).exists()) {
				UploadFile.getParentFile().mkdirs();
				UploadFile.createNewFile();
				writer = new FileOutputStream(UploadFile);
				reader = MainServer.class.getResourceAsStream(entry.getName());
				int size;
				byte[] buf;
				while((size = reader.available())>0) {
					buf= new byte[size];
					reader.read(buf);
					writer.write(buf);
				}
				writer.close();
				reader.close();
			}
		}
	}
	
}