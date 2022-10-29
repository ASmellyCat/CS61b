package gitlet;

/** Additional utilities.
 *  @author ASmellyCat
 */
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
    public static void fileExists(File file) {
        if (!file.exists()) {
            exit("File does not exists. ");
        }
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
