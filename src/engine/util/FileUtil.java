package engine.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

import engine.game.Game;
import engine.game.ShaderProgram;

public class FileUtil{

	/**
	 * @return The entire source of a file as a single string
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
			Game.end();
		}

		return source.toString();
	}
}
