package engine.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {

	/**
	 * Puts a whole file into a String
	 *
	 * @return The entire source of a file as a single string. Null if something
	 *         went wrong
	 */
	public static String readFromFile(String name) {
		StringBuilder source = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(name)));
			String line;
			while ((line = reader.readLine()) != null) {
				source.append(line).append("\n");
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + name + ".");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return source.toString();
	}
}
