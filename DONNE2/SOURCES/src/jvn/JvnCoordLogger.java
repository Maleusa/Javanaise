/***
 * JAVANAISE Implementation
 * CoordLogger class
 * This class implements a logger to prevent CoordImpl's crashes
 *
 * Authors: Florent Pouzol, Hugo Triolet, Yazid Cheriti
 */

package jvn;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
				// System.out.println("Parent folder not found.");
				throw new IOException("Parent folder not found.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method finds a parent folder upwards from the current directory.
	 * Largely inspired by ChatGPT
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
	 * it doesn't, it creates it.
	 * Largely inspired by ChatGPT
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

	// Method to create the log file if it doesn't exist
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

	// Method to write a serializable object to the log file
	public void writeObject(Serializable obj, String pathKey) {
		try (FileOutputStream fileOut = new FileOutputStream(logsPaths.get(pathKey), true);
				ObjectOutputStream out = new ObjectOutputStream(fileOut) {
					@Override
					protected void writeStreamHeader() throws IOException {
						reset(); // Avoid writing a new header when appending
					}
				}) {
			out.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method to read all serialized objects from the log file
	public List<JvnObject> readObjects(String pathKey) {
		List<JvnObject> objects = new ArrayList<JvnObject>();

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(logsPaths.get(pathKey)))) {
			while (true) {
				JvnObject obj = (JvnObject) in.readObject();
				objects.add(obj);
			}
		} catch (EOFException e) {
			// End of file reached, we can ignore this exception
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return objects;
	}

	// Method to erase the contents of the log file
	public void eraseLogFile(String pathKey) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(logsPaths.get(pathKey)))) {
			writer.write(""); // Writing an empty string to clear the file
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Optional: Method to get the file path (for testing or logging purposes)
	public String getFilePath(String pathKey) {
		return logsPaths.get(pathKey);
	}
}
