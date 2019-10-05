import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogRenamer {
	public static void renameAllLogs(String subfolder) {
		Path resourceDirectoryPath = Paths.get("src","main", "resources", subfolder);
		File resourceDirectory = new File(resourceDirectoryPath.toUri());
		File[] listOfFiles = resourceDirectory.listFiles();
		if (listOfFiles == null) {
			return;
		}

		for (File listOfFile : listOfFiles) {
			if (listOfFile.isFile()) {
				File f = new File(resourceDirectory.getAbsoluteFile() + "\\" + listOfFile.getName());
				String oldName = listOfFile.getName();
				if (oldName.startsWith("DASH_RUNTIME_LOG")) {
					String[] nameParts = oldName.split("_");
					String newName = nameParts[5] + "_" + nameParts[4];
					f.renameTo(new File(resourceDirectory.getAbsoluteFile() + "\\" + newName + ".log"));
				}
			}
		}

		System.out.println("Rename is done");
	}
}
