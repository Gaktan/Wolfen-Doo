package engine.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileUtil{

	/**
	 * Puts a whole file into a String
	 * 
	 * @return The entire source of a file as a single string. Null if something went wrong
	 */
	public static String readFromFile(String name){
		StringBuilder source = new StringBuilder();
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(new File(name)));
			
			String line;
			while ((line = reader.readLine()) != null){
				source.append(line).append("\n");
			}

			reader.close();
		}
		catch (Exception e){
			System.err.println("Error loading file: " + name);
			e.printStackTrace();
			return null;
		}

		return source.toString();
	}
}
