package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

import java.util.Map;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/** Represents a gitlet commit object.
 * @author ASmellyCat
 *  A commit contains:
 *  1. parent ID (SHA1)
 *  2. meta data such as: timestamp, log message
 *  3. files ID (SHA1)
 *  4. own commit ID (SHA1)
 *
 *  this class contains the methods:
 *  1. initialize;
 *  2. get own ID (SHA1);
 *  3. get timestamp;
 */
public class Commit implements Serializable {

    /**
     * The message of this commit.
     */
    private final String message;
    /**
     * The date of this commit
     */
    private final Date date;
    /**
     * ID of parent commit.
     */
    private final String parentID;
    /**
     * ID of files this commit recorded.
     */
    private final Map<String, String> tracked;
    /**
     * ID of this commit.
     */
    private final String commitID;

    /**
     * Creates a commit object with the specified parameters.
     * @param m String of the commit message.
     * @param p String of the parent SHA-1 ID of this new commit.
     * @param t ArrayList<String> the files SHA-1 IDs it points to.
     */
    public Commit(String m, String p, Map<String, String> t) {
        message = m;
        parentID = p;
        if (parentID == null) {
            date = new Date(0);
        } else {
            date = new Date();
        }
        tracked = t;
        commitID = generateCommitID();
        save();
    }

    /**
     * get the SHA-1 ID of this commit.
     * @return String of SHA-1 ID.
     * */
    public String sha1ID() {
        return commitID;
    }

    /** private HELP method. */

    /**
     * Generate this commit SHA-1 ID.
     * @return String of SHA-1 ID.
     */
    private String generateCommitID() {
        return sha1(getTimestamp(), message, parentID, tracked.toString());
    }

    /**
     * Get the timestamp string in UTC.
     * @return String of timestamp of the time when this commit generated.
     */
    private String getTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss 'UTC,' EEEE',' d MMMM yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    private void save() {
        File objectFile = objectFile(commitID);
        saveObjectFile(objectFile, this);
    }
}
