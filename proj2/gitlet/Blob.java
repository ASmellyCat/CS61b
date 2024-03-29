package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.CWD;
import static gitlet.Repository.OBJECT_DIR;
import static gitlet.Utils.*;
import static gitlet.HelpMethod.*;

/**Represents a blob object.
 * @author ASmellyCat
 * a blob contains:
 * 1. sha1 id
 * 2. contents
 * 4. file path
 */

public class Blob implements Serializable {
    /** the content of a file that converted into a blob. */
    private final String fileContents;
    /** the SHA-1 ID of a blob. */
    private final String fileID;
    /** the absolute filepath of a blob. */
    private String filePath;
    /** the source file of a blob. */
    private final File currentFile;
    /**
     * Creates a blob object with the specified parameters.
     * @param file currentFile.
     */
    public Blob(File file) {
        currentFile = file;
        fileContents = readContentsAsString(file);
        filePath = file.getAbsolutePath();
        fileID = generateBlobID();
    }

    /**
     * Get SHA-1 ID of this blob object.
     * @return String SHA-1 of this blob object.
     */
    public String shaID() {
        return fileID;
    }
    /**
     * Get the contents of this file in string format.
     * @return String contents of this file, which converted into a blob.
     * */
    public String getFileContents() {
        return fileContents;
    }

    /**
     * Get the absolute file path of this blob.
     * @return String that demonstrates the current blob file path.
     */
    public String absolutePath() {
        return filePath;
    }

    /** save this blob. */
    public void save() {
        save(OBJECT_DIR);
    }
    public void save(File fileDir) {
        File objectFile = objectFile(fileID, fileDir);
        saveObjectFile(objectFile, this);
    }

    /** update the file path of blob. */
    public void updatePath() {
        filePath = join(CWD, currentFile.getName()).getAbsolutePath();
        save();
    }
    public void updatePath(File fileDir) {
        filePath = join(fileDir.getParentFile(), currentFile.getName()).getAbsolutePath();
        save(join(fileDir, OBJECT_DIR.getName()));
    }

    /** Get blob source file. */
    public File getCurrentFile() {
        return currentFile;
    }
    /** private HELP method. */

    /**
     * generate the SHA-1 ID of Blob, using the absolute filepath and file contents.
     * @return String that generated by SHA-1.
     */
    private String generateBlobID() {
        return sha1(filePath, fileContents);
    }
}
