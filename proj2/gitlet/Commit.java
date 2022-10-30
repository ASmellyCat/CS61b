package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.HelpMethod.*;
import static gitlet.MyUtils.updateIntoFile;
import static gitlet.Utils.*;

import java.util.Locale;
import java.util.Map;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
// import java.util.TimeZone;

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
     * The input message of this commit.
     */
    private final String message;
    /**
     * The date of this commit created.
     */
    private final Date date;
    /**
     * SHA-1 ID of parent commit.
     */
    private final String parentID;
    /**
     * SHA-1 ID of second parent ID (a given branch head commit when merged).
     */
    private final String secondParentID;
    /**
     * Map of tracked files with filepath as key and fileID(SHA1) as values.
     */
    private final Map<String, String> tracked;
    /**
     * SHA-1 ID of this commit.
     */
    private final String commitID;

    /**
     * Creates a commit object with the specified parameters.
     * @param m String of the commit message.
     * @param p String of the parent SHA-1 ID of this new commit.
     * @param t ArrayList<String> the files SHA-1 IDs it points to.
     */
    public Commit(String m, String p, String p2,Map<String, String> t) {
        message = m;
        parentID = p;
        secondParentID = p2;
        if (parentID == null) {
            date = new Date(0);
        } else {
            date = new Date();
        }
        tracked = t;
        commitID = generateCommitID();
        updateIntoFile(Repository.COMMITS, commitID);
        writeContents(getActiveBranchFile(), commitID);
        save();
    }

    /**
     * get the SHA-1 ID of this commit.
     * @return String of SHA-1 ID.
     * */
    public String getCommitID() {
        return commitID;
    }

    /**
     * get the parent SHA-1 ID of this commit.
     * @return String of parent SHA-1 ID
     * */
    public String getParentID() {
        return parentID;
    }

    /**
     * get the second parent SHA-1 ID of this commit.
     * */
    public String getSecondParentID() {
        return secondParentID;
    }
    /**
     * get date of the time that this commit was created.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Get the timestamp string like Thu Nov 9 20:00:05 2017 -0800
     * @return String of timestamp of the time when this commit generated.
     */
    public String getTimestamp() {
        //DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss 'UTC,' EEEE',' d MMMM yyyy");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(date);
    }
    /**
     * get the message of this commit.
     * @return String of commit message.
     * */
    public String getMessage() {
        return message;
    }

    /**
     *@return Map of tracked files.
     * */
    public Map<String, String> getFiles() {
        return tracked;
    }


    /** private HELP method. */

    /**
     * Generate this commit SHA-1 ID.
     * @return String of SHA-1 ID.
     */
    private String generateCommitID() {
        return sha1(getTimestamp() + message +  parentID + tracked.toString());
    }
    /** save this commit to Object file directory. */
    private void save() {
        File objectFile = objectFile(commitID);
        saveObjectFile(objectFile, this);
    }
}
