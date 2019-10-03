import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogRenamer {
	public static void renameAllLogs(String subfolder) {
		Path resourceDirectoryPath = Paths.get("src","main", "resources", subfolder);
		File resourceDirectory = new File(resourceDirectoryPath.toUri());
		File[] listOfFiles = resourceDirectory.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				File f = new File(resourceDirectory.getAbsoluteFile() + "\\" +listOfFiles[i].getName());
				String oldName = listOfFiles[i].getName();
				if (oldName.startsWith("DASH_RUNTIME_LOG")) {
					String[] nameParts = oldName.split("_");
					String newName = nameParts[5] + "_" + nameParts[4];
					f.renameTo(new File(resourceDirectory.getAbsoluteFile() + "\\" +newName+".log"));
				}
			}
		}

		System.out.println("Rename is done");
	}
}
