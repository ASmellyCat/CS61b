package gitlet;

/** Additional utilities.
 *  @author ASmellyCat
 */
import static gitlet.Repository.CWD;
import static gitlet.Utils.*;
import java.io.File;
import java.io.Serializable;

public class MyUtils implements Serializable{
    public static void exit(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(0);
    }

    /** Judge whether the file exist, and quit with a message if not. */
    public static void fileExists(String filePath) {
        if (join(absolutePath(filePath)).length() == 0) {
            exit("File does not exists. ");
        }
    }

    /** get File by short name*/
    public static File getFileByName(String Name) {
        return join(absolutePath(Name));
    }

    /**convert absolute file name to relative file name. */
    public static String getRelativeFileName(String filePath) {
        String[] s = filePath.split("/");
        return s[s.length - 1];

    }

    /** change file name or absolute file path into absolute file path*/
    public static String absolutePath(String fileName) {
        if (fileName.contains("/")) {
            return fileName;
        }
        return join(Repository.CWD, fileName).getAbsolutePath();
    }


    public static void updateIntoFile(File file, String text) {
        String updatedText;
        if (!file.exists()) {
            updatedText = text;
        } else {
            updatedText = readContentsAsString(file) + text;
        }
        writeContents(file, updatedText);
    }


}
