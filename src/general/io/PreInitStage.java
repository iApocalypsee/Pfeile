package general.io;

import java.io.File;

/**
 * @author Josip Palavra
 */
public class PreInitStage {

	public static void execute() {
		makeSavegaveDirectory();
	}

	/**
	 * Creates the savegame directory, if it not exists.
	 * If it cannot be created for some reason, the method is going to throw
	 * a {@link java.lang.RuntimeException}.
	 *
	 * @throws java.lang.RuntimeException if the savegame directory does not exist
	 * and it cannot be created for some reason.
	 */
	private static void makeSavegaveDirectory() {
		File f = FolderStructure.SAVEGAMES;
		if(!f.exists()) {
			boolean isDirectoryMade = f.mkdir();

			if (isDirectoryMade) {
				System.out.println("Savegame directory created at " + f.getAbsolutePath());
			} else {
				throw new RuntimeException("Savegame directory could not be created!");
			}
		} else {
			System.out.println("Savegame directory located at " + f.getAbsolutePath());
		}
	}

}

/**
 * Constants for keeping the folder structure of the game.
 */
class FolderStructure {

	/**
	 * Directory in which every save game is stored.
	 */
	public static final File SAVEGAMES = new File("saves/");

	private FolderStructure() {
	}

}
