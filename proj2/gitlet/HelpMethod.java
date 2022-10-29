package gitlet;

/** Additional utilities.
 *  @author ASmellyCat
 */
import static gitlet.Utils.*;
import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.MyUtils.*;

/**represents some methods that need to be used in Repository to simplify the code.
 * @author ASmellyCat
 * */

public class HelpMethod implements Serializable{

    /** Judge whether gitlet repository exist, and quit with a message if not. */
    public static void gitletRepoExists() {
        if (!GITLET_DIR.exists()) {
            exit("Not yet initialize a Getlet Repository.");
        }
    }
    /** initialize a null commit. */
    public static void initializeCommit() {
        activateBranch(INITIAL_BRANCH);
        new Commit(INITIAL_MESSAGE, null, new HashMap<>());
    }
    /**
     * Get current commit SHA-1 ID
     * @return String of current commit SHA-1 ID
     * */
    public static String getCurrentCommitID() {
        return readContentsAsString(getActiveBranchFile());
    }
    /** get commit from OBJECT file. */
    public static Commit getCommit(String id) {
        if (id == null) {
            return null;
        }
        File file = objectFile(id);
        if (file.length() == 0) {
            exit("No commit with that id exists.");
        }
        return readObject(objectFile(id), Commit.class);
    }
    /**
     * @param branchName String of a given branch name.
     * @return Commit of a given branch name. */
    public static String getCommitIDByBranchName(String branchName) {
        File file = join(HEADS_DIR, branchName);
        return readContentsAsString(file);
    }
    /** get all commits and save it into a Set*/
    public static Set<Commit> getAllCommits() {
        Set<Commit> commitsSet = new HashSet<>();
        String commitsID = readContentsAsString(COMMITS);
        return getAllCommitsHelp(commitsSet, commitsID);
    }
    /** Using recursive method to get all the commits*/
    private static Set<Commit> getAllCommitsHelp(Set<Commit> commitsSet, String commitsID) {
        if (!commitsID.isEmpty()) {
            commitsSet.add(getCommit(commitsID.substring(0, 40)));
            return getAllCommitsHelp(commitsSet, commitsID.substring(40));
        }
        return commitsSet;
    }
    /** reset a commit files.
     * @param commitID String of a given commit SHA-1 ID */
    public static void resetACommit(String commitID) {
        StagingArea stageArea = getStagingArea();
        Commit commitGiven = getCommit(commitID);
        Map<String, String> trackedGiven = commitGiven.getFiles();
        List<String> trackedCurrent =  stageArea.getTrackedFiles();
        for (Map.Entry<String,String> entry: trackedGiven.entrySet()) {
            String filePath = entry.getKey();
            Blob blob = readObject(objectFile(entry.getValue()), Blob.class);
            if (!trackedCurrent.contains(filePath)) {
                if (getFileByAbsolutePath(filePath).length() != 0) {
                    exit("There is an untracked file in the way; delete it, or add and commit it first.");
                }
            }
            updateFileWithBlob(blob.getCurrentFile(), blob);
            trackedCurrent.remove(filePath);
        }
        for (String filePath : trackedCurrent) {
            restrictedDelete(filePath);
        }
        stageArea.updateAllTracked(trackedGiven);
    }
    /**
     * Change HEAD file that points to the active branch.
     * @param branchName String of the name of branch.
     * */
    public static void activateBranch(String branchName) {
        writeContents(HEAD, DEFAULT_HEAD_PREFIX + branchName);
    }

    /**
     * create a certain branch file.
     * @param branchName String of the name of branch.
     * @return File of branch pointer*/
    public static File createBranchFile(String branchName) {
        return join(HEADS_DIR, branchName);
    }
    /** Get active branch File.
     * @return File of active branch.
     */
    public static File getActiveBranchFile() {
        String activeBranchFilePath = readContentsAsString(HEAD).split(": ")[1].trim();
        return join(GITLET_DIR, activeBranchFilePath);
    }

    /**
     * get a branch file given a branch name
     * @param branchName the branch name
     * @return File of the branch file in heads provided a branch name.
     * */
    public static File getBranchFile(String branchName) {
        return join(HEADS_DIR, branchName);
    }

    /** @return boolean of whether a branch has created. */
    public static boolean branchExists(String branchName) {
        List<String> branchNames = plainFilenamesIn(HEADS_DIR);
        if (!(branchNames.isEmpty())) {
            return branchNames.contains(branchName);
        }
        return false;
    }

    /** Using recursive method to print log. */
    public static void printLog(Commit commit) {
        if (commit != null) {
            printOneLog(commit);
            printLog(getCommit(commit.getParentID()));
        }
    }
    /** printed one commit in required format. */
    public static void printOneLog(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getCommitID());
        System.out.println("Date: " + commit.getTimestamp());
        System.out.println(commit.getMessage());
        System.out.println();
    }

    /** get the branch names in a list. */
    public static List<String> getBranchNames() {
        List<String> branchNames = new ArrayList<>(plainFilenamesIn(HEADS_DIR));
        String activaBranchName = getActiveBranchFile().getName();
        branchNames.add(0,"*" + activaBranchName);
        branchNames.remove(activaBranchName);
        return branchNames;
    }

    /** the format to print status. */
    public static void printStatusFormat(String outline, List<String> names) {
        System.out.print("=== ");
        System.out.print(outline);
        System.out.println(" ===");
        if (names != null) {
            names.forEach(any ->{
                System.out.println(getRelativeFileName(any));
            });
        }
        System.out.println();
    }

    /**
     * get Staging Area instance from file ".gitlet/index".
     * @return StagingArea instance.
     */
    public static StagingArea getStagingArea() {
        return readObject(INDEX, StagingArea.class);
    }

    /**
     * save the object to temp.
     * @param file File that need to be used to store object.
     * @param obj Serializable object that need to be stored.
     * */
    public static void saveObjectFile(File file, Serializable obj) {
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdir();
        }
        writeObject(file, obj);
    }
    /** get the temp object file stored in OBJECT_FILE. */
    public static File objectFile(String id) {
        File fileDir = join(OBJECT_DIR, id.substring(0, 2));
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        String fileName = id.substring(2);
        // check the short input commit ID such as 1d575e62
        if (fileName.length() < 38) {
            // get the full 38 digits file ID.
            String fullFileName = shortCommitIDFind(fileDir, fileName.substring(0, 4));
            if (fullFileName == null) {
                exit("No commit with that id exists.");
            }
            return join(fileDir, fullFileName);
        }
        return join(fileDir, fileName);
    }

    /**
     * if the intput commit ID in command "Check out" is 6 digits,
     * then find the corresponding file.
     * @param fileDir File directory of that id (first two digits)
     * @param id String of 6 digits input commit ID.
     * @return String of fileName in full SHA-1 ID.
     * */
    public static String shortCommitIDFind(File fileDir, String id) {
        List<String> fileNames = plainFilenamesIn(fileDir);
        for (String fileName : fileNames) {
            if (id.equals(fileName.substring(0,4))) {
                return fileName;
            }
        }
        return null;
    }

    /**
     * @param fileName String of a given fileName
     * @return File that in CWD.
     * */
    public static File getFileByName(String fileName) {
        return join(CWD, fileName);
    }

    public static File getFileByAbsolutePath(String filePath) {
        return getFileByName(getRelativeFileName(filePath));
    }

    /**convert absolute file name to relative file name. */
    public static String getRelativeFileName(String filePath) {
        String[] s = filePath.split("/");
        return s[s.length - 1];

    }
    /**
     * @param file File of a existing file that needs to be written into.
     * @param blob Blob of a stored file that need to overwrite a file.
     * */
    public static void updateFileWithBlob(File file, Blob blob) {
        writeContents(file, blob.getFileContents());
    }

}


