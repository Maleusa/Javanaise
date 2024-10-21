/***
 * JAVANAISE Implementation
 * CoordLogger class
 * This class implements a logger to prevent CoordImpl's crashes
 *
 * Authors: Florent Pouzol, Hugo Triolet, Yazid Cheriti
 */

package jvn;

import java.io.*;
import java.util.HashMap;
import java.io.IOException;
import java.nio.file.*;

public class JvnCoordLogger {
	// < Type_of_Coord_field , path_of_logs_containing_it >
	private HashMap<String, String> logsPaths;

	public JvnCoordLogger() throws IOException {
		logsPaths = new HashMap<String, String>();
		try {
			Path parentDir = findParentFolder("SOURCES", Paths.get(".").toAbsolutePath().normalize());
			if (parentDir != null) {
				Path childDir = createChildDirectory(parentDir, "logs");
				System.out.println("Child directory path: " + childDir.toAbsolutePath());
				this.logsPaths.put("ObjectIdList", childDir + "/" + "Coord_obj_id.txt");
				this.logsPaths.put("ServerList", childDir + "/" + "Coord_serv.txt");
				this.logsPaths.put("ObjectList", childDir + "/" + "Coord_obj.txt");
				this.logsPaths.put("readerList", childDir + "/" + "Coord_read.txt");
				this.logsPaths.put("writerList", childDir + "/" + "Coord_write.txt");
				createFileIfNotExists();
			} else {
				throw new IOException("Parent folder not found.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method finds a parent folder upwards from the current directory. Largely
	 * inspired by ChatGPT
	 */
	public static Path findParentFolder(String targetFolderName, Path currentDir) {
		// Traverse upwards until root
		while (currentDir != null) {
			Path targetPath = currentDir.resolve(targetFolderName);
			if (Files.isDirectory(targetPath)) {
				return targetPath; // Return the found parent directory
			}
			currentDir = currentDir.getParent(); // Move up one level
		}
		return null; // Not found
	}

	/**
	 * This method checks if a child directory exists in the parent folder, and if
	 * it doesn't, it creates it. Largely inspired by ChatGPT
	 */
	public static Path createChildDirectory(Path parentDir, String childDirName) throws IOException {
		Path childDir = parentDir.resolve(childDirName);
		if (Files.notExists(childDir)) {
			Files.createDirectory(childDir); // Create the directory if it doesn't exist
			System.out.println("Child directory created: " + childDir.toAbsolutePath());
		} else {
			System.out.println("Child directory already exists: " + childDir.toAbsolutePath());
		}
		return childDir;
	}

	/**
	 * Method to create the log file if it doesn't exist
	 */
	private void createFileIfNotExists() {
		this.logsPaths.forEach((k, v) -> {
			try {
				File file = new File(v);
				if (!file.exists()) {
					file.createNewFile();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Method to write a serializable object to the log file
	 */
	public void writeObject(Serializable obj, String pathKey) {
		try (FileOutputStream fileOut = new FileOutputStream(this.logsPaths.get(pathKey), true);
				ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
			out.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method to read all serialized objects from the log file
	public Object readObjects(String pathKey) {

		try (FileInputStream fileIn = new FileInputStream(this.logsPaths.get(pathKey));
				ObjectInputStream in = new ObjectInputStream(fileIn)) {
			Object obj = in.readObject();
			return obj;
		} catch (EOFException e) {
			// do nothing if the files are readed empty 
			// case occuring especially at start after normally stopped
			return null;
		} 
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Method to erase the contents of the log file
	public void eraseLogFile(boolean deleteFiles) {
		if (deleteFiles) {
			this.logsPaths.forEach((k, v) -> {
				if (v != null) {
					File file = new File(v);
					// Delete the file if it exists
					if (file.exists()) {
						if (file.delete()) {
							System.out.println("File deleted: " + v);
						}
					} else {
						System.out.println("File does not exist: " + v);
					}
				}
			});
		}
//		else {
//			try (BufferedWriter writer = new BufferedWriter(new FileWriter(logsPaths.get(pathKey)))) {
//				writer.write(""); // Writing an empty string to clear the file
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}

	// Optional: Method to get the file path (for testing or logging purposes)
	public String getFilePath(String pathKey) {
		return logsPaths.get(pathKey);
	}
}
