package gitlet;

import java.io.File;
import java.util.Map;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 * GITLET construction
 *<pre>
 * .gitlet
 *    ├── HEAD (file)       // Where is the head commit ref: refs/heads/master
 *    ├──index (file)       // Stage area. Serialized objects are saved into index file.
 *    ├── objects  (directory)   // hash table that contains SHA-1 of Serialized objects (blob, commit, ...)
 *    ├──refs
 *       └── heads
 *              └── branches (file)  // SHA-1 of current commit that head pointer points to
 * </pre>
 */


public class Repository {
    /** List all instance variables.
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The object directory. */
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    /** The reference directory. */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** The heads directory. */
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    /** The HEAD file. */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /** The index file. */
    public static final File INDEX = join(GITLET_DIR, "index");
    /** The initial message. */
    public static final String INITIAL_MESSAGE = "initial commit";
    /** The name of initial branch*/
    public static final String INITIAL_BRANCH = "master";
    /** Default prefix for HEAD. */
    public static final String DEFAULT_HEAD_PREFIX = "ref: refs/heads/";

    /** init
     * Usage: java gitlet.Main init
     * Description: Creates Gitlet repository in the current directory.
     * 1. new commit contains no files.
     * 2. commit message: initial commit.
     * 3. master will be the current branch, pointer to the current branch.
     * 4. timestamp 00:00:00 UTC, Thursday, 1 January 1970
     * 5. This commit the same UID
     * 6. If there is already a Gitlet system, it should abort.
     * 7. contains failure cases.
     *
     * */
    public static void init() {
        if (GITLET_DIR.exists()) {
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
        initializeCommit();
        new StagingArea();
    }


    /**
     * add
     * @param fileName String of the name of file that need to be added.
     * Description: Adds a copy of the file as it currently exists to the staging area.
     * 1. Overwrite the previous entry in staging area.
     * 2. There is a staging area in .getlet directory.
     * 3. Remove the file which is identical to current commit in the staging areas.
     * 4. The file will no longer be staged for removal, if it was at the time of the command.
     * 5. runtime should be lgN.
     *
     * */
    public static void add(String fileName) {
        gitletRepoExists();
        File file = join(CWD, fileName);
        fileExists(file);
        getStagingArea().add(file);
    }

    /**
     * rm
     * @param fileName String of name of file that need to be removed.
     * 1. Unstage the file if it is currently staged for addition.
     * 2. If the file is tracked in the current commit, stage it for removal.
     * 3. remove the file from the working directory if the user has not already done so.
     * 4. If the file is neither staged nor tracked by the head commit, print the error message.
     * */
    public static void removal(String fileName) {
        gitletRepoExists();
        File file = join(CWD, fileName);
        fileExists(file);
        if (!getStagingArea().remove(file)) {
            exit("No reason to remove the file.");
        }
    }

    /**
     * commit
     * Read from my computer the head commit object and the staging area.
     * Clone the HEAD commit.
     * Modify its message and timestamp according to user input
     * Use the staging area in order to modify the files tracked by the new commit.
     * Write back any new object made or any modified objects read earlier.
     * The staging area is cleared after a commit.
     */
    public static void commit(String message) {
        Map<String, String> tracked = getStagingArea().stageCommit();
        String parentID = readContentsAsString(getActiveBranchFile());
        Commit newCommit = new Commit(message, parentID, tracked);
        writeContents(getActiveBranchFile(), newCommit.sha1ID());
    }

    public void checkout() {

    }

    public void log() {

    }



    /** The methods below are private help method. */

    /** initialize a null commit. */
    private static void initializeCommit() {
        Commit initialCommit = new Commit(INITIAL_MESSAGE, null, null);
        activeBranch(INITIAL_BRANCH);
        writeContents(createBranchFile(INITIAL_BRANCH), initialCommit.sha1ID());
    }
    /**
     * Change HEAD file that points to the active branch.
     * @param branchName String of the name of branch.
     * */
    private static void activeBranch(String branchName) {
        writeContents(HEAD, DEFAULT_HEAD_PREFIX + branchName);
    }
    /**
     * create a certain branch file.
     * @param branchName String of the name of branch.
     * @return File of branch pointer*/
    private static File createBranchFile(String branchName) {
        return join(HEADS_DIR, branchName);
    }
    /** Get active branch File.
     * @return File of active branch.
     */
    private static File getActiveBranchFile() {
        String activeBranchFilePath = readContentsAsString(HEAD).split(":")[1];
        return join(GITLET_DIR,activeBranchFilePath);
    }
    /** Judge whether gitlet repository exist, and quit with a message if not. */
    private static void gitletRepoExists() {
        if (!GITLET_DIR.exists()) {
            exit("Not yet initialize a Getlet Repository.");
        }
    }
    /** Judge whether the file exist, and quit with a message if not. */
    private static void fileExists(File file) {
        if (!file.exists()) {
            exit("File does not exists. ");
        }
    }
    private static StagingArea getStagingArea() {
        return readObject(INDEX, StagingArea.class);
    }

}
