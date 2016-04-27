package ca.team5406.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class DataLogger implements Loopable{

	private String name;
	private String contentToDump;
	
	public DataLogger(String filename){
		this.name = filename;
	}
	
	public void addLine(String data){
		contentToDump += data += "\n";
	}
	
	public void dump(){
		PrintWriter writer;
		try{
			writer = new PrintWriter(this.getFilename(), "UTF-8");
			writer.println(contentToDump);
			writer.close();
		}
		catch (FileNotFoundException | UnsupportedEncodingException e){
			System.out.println("Error Writing Data for " + this.name);
			e.printStackTrace();
		}
		resetBuffer();
	}
	
	public String get(){
		try {
			BufferedReader br = new BufferedReader(new FileReader(this.getFilename()));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
	
		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String data = sb.toString();
		    br.close();
		    return data;
		}
		catch (FileNotFoundException e) {
			System.out.println("File for " + this.name + " doesn't exist.");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("Error Reading File for " + this.name);
			e.printStackTrace();
		}
		return "";
		
	}
	
	public String getFilename(){
		return System.getProperty("user.home") + "/" + this.name + ".csv";
	}
	
	public String getBuffer(){
		return contentToDump;
	}
	
	public void resetBuffer(){
		contentToDump = "";
	}

	@Override
	public void runControlLoop() {
		dump();
	}
	
}
