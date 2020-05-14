package com.meta.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet{
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		resp.setStatus(HttpServletResponse.SC_OK);
		
		if(req.getRequestURI().equals("/")) {
			writeResource("assets/index.html", out);
			out.close();
			return;
		}
		if(!writeResource("assets"+req.getRequestURI(), out)) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}else {
			if(req.getContentType()!=null && req.getContentType().contains("text/html")) {
				resp.sendRedirect("/");
			}
		}
		
		
		out.close();
	}
	
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}
	private boolean writeResource(String res,PrintWriter out) throws IOException {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(res);
		if(in==null) {
			//out.write("file not found");
			return false;
		}
		int size;
		byte[] buf;
		while((size = in.available())>0) {
			buf = new byte[size];
			in.read(buf);
			out.write(new String(buf));
		}
		return true;
	}
}
