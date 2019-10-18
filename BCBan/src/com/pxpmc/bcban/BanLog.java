package com.pxpmc.bcban;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Date;

public class BanLog {
	private OutputStream out;
	private BufferedWriter writer;
	public synchronized void open(File file) throws FileNotFoundException, UnsupportedEncodingException {
         out=new FileOutputStream(file,true);
         writer = new BufferedWriter(new OutputStreamWriter(out,"utf-8"));
	}
	public synchronized void close() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public synchronized void write(String msg,Object...objects) {
		if(msg == null || msg.length() == 0) {
			return;
		}
		if(writer == null) {
			return;
		}
		String text = MessageFormat.format("[" + BcBanMain.SDF.format(new Date(System.currentTimeMillis())) + "] " + msg, objects);
		try {
			writer.write(text);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
