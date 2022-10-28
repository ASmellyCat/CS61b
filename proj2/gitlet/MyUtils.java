package gitlet;

/** Additional utilities.
 *  @author ASmellyCat
 */
import static gitlet.Utils.*;
import java.io.File;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.function.Supplier;

public class MyUtils {
    public static void exit(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(0);
    }

    public static void saveObjectFile(File file, Serializable obj) {
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdir();
        }
        writeObject(file, obj);
    }

    /** get directory of temp object file stored in OBJECT_FILE. */
    public static File objectDir(String id) {
        return join(Repository.OBJECT_DIR, id.substring(0, 2));
    }
    /** get file of temp object file stored in OBJECT_FILE. */
    public static String objectFileName(String id) {
        return id.substring(2);
    }
    /** get the temp object file stored in OBJECT_FILE. */
    public static File objectFile(String id) {
        File fileDir = objectDir(id);
        fileDir.mkdir();
        return join(fileDir, objectFileName(id));
    }

}
