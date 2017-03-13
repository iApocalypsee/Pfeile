package general.io;

import general.LogFacility;

import java.io.File;

/** Before the game loads the gui components (or asynchronous to it) some class must be loaded. This will provide the
 *  method {@link PreInitStage#execute()} to do this. It also provides the save game directory and the folder structure
 *  for that. FolderStructure contains the static final File {@link FolderStructure#SAVEGAMES}.*/
public class PreInitStage {

	/** Executes every pre init entry such as save games. If possible the process is threaded.
	 *  Creating or locating the save game directory is executed in the Thread "SaveGameDictThread". */
	public static void execute() {
		Thread saveDicThread = new Thread(() -> {
			makeSavegameDirectory();
		}, "SaveGameDictThread");
		saveDicThread.setDaemon(true);
		saveDicThread.start();
	}

	/**
	 * Creates the savegame directory, if it not exists.
	 * If it cannot be created for some reason, the method is going to throw
	 * a {@link java.lang.RuntimeException}.
	 *
	 * @throws java.lang.RuntimeException if the savegame directory does not exist
	 * and it cannot be created for some reason.
	 */
	// TODO: use or store the save game values somewhere
	private static void makeSavegameDirectory () {
		File f = FolderStructure.SAVEGAMES;
		if(!f.exists()) {
			boolean isDirectoryMade = f.mkdir();

			if (isDirectoryMade) {
				LogFacility.log("Savegame directory created at: " + f.getAbsolutePath(), "Info", "init process");
			} else {
				throw new RuntimeException("Savegame directory could not be created!");
			}
		} else {
			LogFacility.log("Savegame directory located at: " + f.getAbsolutePath(), "Info", "init process");
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

	private FolderStructure() {}

}
