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

public class HelpMethod implements Serializable {

    /** Judge whether gitlet repository exist, and quit with a message if not. */
    public static void gitletRepoExists() {
        if (!GITLET_DIR.exists()) {
            exit("Not yet initialize a Getlet Repository.");
        }
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
        return getCommit(id, OBJECT_DIR);
    }
    public static Commit getCommit(String id, File firDir) {
        if (id == null) {
            return null;
        }
        File file = objectFile(id, firDir);
        if (file.length() == 0) {
            exit("No commit with that id exists.");
        }
        return readObject(file, Commit.class);
    }
    /**
     * @param branchName String of a given branch name.
     * @return Commit of a given branch name. */
    public static String getCommitIDByBranchName(String branchName) {
        return getCommitIDByBranchName(correctBranchName(branchName), correctBranchDir(branchName));
    }
    public static String getCommitIDByBranchName(String branchName, File fileDir) {
        File file = join(fileDir, branchName);
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
            commitsSet.add(getCommit(commitsID.substring(0, 40), OBJECT_DIR));
            return getAllCommitsHelp(commitsSet, commitsID.substring(40));
        }
        return commitsSet;
    }
    /** get all parent commits of a given commit. */
    public static Set<Commit> getAllParentCommits(String id) {
        return getAllParentCommits(id, OBJECT_DIR);
    }
    public static Set<Commit> getAllParentCommits(String id, File fireDir) {
        Commit commit = getCommit(id, fireDir);
        Set<Commit> commitsSet = new HashSet<>();
        while (commit.getParentID() != null) {
            commitsSet.add(commit);
            commit = getCommit(commit.getParentID(), fireDir);
        }
        commitsSet.add(commit);
        return commitsSet;
    }

    public static Set<String> getAllParentCommitsID(String id) {
        return getAllParentCommitsID(id, OBJECT_DIR);
    }
    public static Set<String> getAllParentCommitsID(String id, File fireDir) {
        Commit commit = getCommit(id, fireDir);
        Set<String> commitsSet = new HashSet<>();
        while (commit.getParentID() != null) {
            commitsSet.add(commit.getCommitID());
            commit = getCommit(commit.getParentID(), fireDir);
        }
        commitsSet.add(commit.getCommitID());
        return commitsSet;
    }

    /** reset a commit files.
     * @param commitID String of a given commit SHA-1 ID */
    public static void resetACommit(String commitID) {
        StagingArea stageArea = getStagingArea();
        Commit commitGiven = getCommit(commitID, OBJECT_DIR);
        Map<String, String> trackedGiven = commitGiven.getFiles();
        List<String> trackedCurrent =  stageArea.getTrackedFiles();
        for (Map.Entry<String, String> entry: trackedGiven.entrySet()) {
            String filePath = entry.getKey();
            Blob blob = getBlob(entry.getValue());
            if (!trackedCurrent.contains(filePath)) {
                if (join(filePath).length() != 0) {
                    exit("There is an untracked file in the way; delete it, "
                            + "or add and commit it first.");
                }
            }
            updateFileWithBlob(filePath, blob);
            trackedCurrent.remove(filePath);
        }
        for (String filePath : trackedCurrent) {
            restrictedDelete(filePath);
        }
        stageArea.updateAllTracked(trackedGiven);
    }
    /**
     * get a latest common ancestor of two branches.
     * */
    public static Commit getSplitCommit(Commit headCommit, Commit otherCommit) {
        Comparator<Commit> commitComparator = Comparator.comparing(Commit::getDate).reversed();
        Queue<Commit> commitsQueue = new PriorityQueue<>(commitComparator);
        commitsQueue.add(headCommit);
        commitsQueue.add(otherCommit);
        Set<String> checkedCommitIDs = new HashSet<>();
        Commit commitSmaller = smallerCommit(headCommit, otherCommit);
        while (!commitsQueue.isEmpty()) {
            Commit latestCommit = commitsQueue.poll();
            String parentID = latestCommit.getParentID();
            Commit parentCommit = getCommit(parentID, OBJECT_DIR);
            if (checkedCommitIDs.contains(commitSmaller.getCommitID())) {
                return commitSmaller;
            }
            if (checkedCommitIDs.contains(parentID)) {
                return parentCommit;
            }
            commitsQueue.add(parentCommit);
            checkedCommitIDs.add(parentID);
            String secondParentID = latestCommit.getSecondParentID();
            Commit secondParentCommit = getCommit(secondParentID, OBJECT_DIR);
            if (secondParentID != null) {
                if (checkedCommitIDs.contains(secondParentID)) {
                    return secondParentCommit;
                }
                commitsQueue.add(secondParentCommit);
                checkedCommitIDs.add(secondParentID);
            }
        }
        return null;
    }

    /**
     * Compare two commit.
     * @return Commit of smaller one. */
    public static Commit smallerCommit(Commit a, Commit b) {
        Comparator<Commit> commitComparator = Comparator.comparing(Commit::getDate);
        if (commitComparator.compare(a, b) < 0) {
            return a;
        }
        return b;
    }

    /**
     * to merge by comparing files in HEAD Commit, other Commit and Split Commit.
     * */
    public static boolean toMerge(Map<String, String> splitTracked,
                                  Map<String, String> headTracked,
                                  Map<String, String> otherTracked) {
        boolean flag = false;
        Set<String> allFilePath = new HashSet<>();
        allFilePath.addAll(splitTracked.keySet());
        allFilePath.addAll(headTracked.keySet());
        allFilePath.addAll(otherTracked.keySet());
        for (String filePath : allFilePath) {
            String splitID = splitTracked.get(filePath);
            String headID = headTracked.get(filePath);
            String otherID = otherTracked.get(filePath);
            int action = check(splitID, headID, otherID);
            //System.out.println(action.toString());
            if (action == 1) { //Overwrite without alert because head is not empty.
                overwriteMerge(filePath, otherTracked, action);
            } else if (action == 2) { // Remove file from head.
                removeMerge(filePath);
            } else if (action == 3) { // overwrite with alert because head is empty.
                overwriteMerge(filePath, otherTracked, action);
            } else if (action == 4) { // Conflict. Add existing other content into existing head
                flag = conflictMerge(filePath,  headTracked, otherTracked);
            } else if (action == 5) { // Conflict. Add empty other content into existing head.
                flag = conflictOtherEmpty(filePath,  headTracked);
            } else if (action == 6) { // Conflict. Add existing other content into empty head.
                flag = conflictHeadEmpty(filePath, otherTracked);
            }
        }
        return flag;
    }

    /** check which situation need to be handled.
     * @return Integer indicates different situations.
     * */
    public static int check(String splitID, String headID, String otherID) {
        if (splitID != null) {
            if (splitID.equals(headID)) {
                if (otherID != null && !otherID.equals(headID)) {
                    return 1; // A A !A !A overwrite
                } else if (otherID == null) {
                    return 2; // D D X X remove
                }
            } else if (otherID != null && !otherID.equals(headID)) {
                if (!otherID.equals(splitID) && headID == null) {
                    return 6; // ConflictMerge with one empty files. HEAD empty.
                } else if (headID != null & !otherID.equals(splitID)) {
                    return 4; // ConflictMerge with two files.
                }
            } else if (otherID == null && headID != null) {
                return 5; // cConflictMerge with one empty files. other empty.
            }
        } else {
            if (headID == null && otherID != null) {
                return 3; // X X !A !A Overwrite with alert!
            } else if (headID != null && otherID != null && !headID.equals(otherID)) {
                return 4; // ConflictMerge with two files.
            }
        }
        return 0; // otherwise no change need to be added into HEAD.
    }

    /** Conflict. Add existing other content into existing head. */
    public static boolean conflictMerge(String filePath, Map<String, String> headMap,
                                        Map<String, String> otherMap) {
        Blob otherBlob = getBlob(otherMap.get(filePath));
        Blob headBlob = getBlob(headMap.get(filePath));
        updatedFileMerged(filePath, headBlob.getFileContents().toString(),
                otherBlob.getFileContents().toString());
        getStagingArea().add(join(filePath));
        return true;
    }

    /** Conflict. Add empty other content into existing head. */
    public static boolean conflictOtherEmpty(String filePath, Map<String, String> headMap) {
        Blob headBlob = getBlob(headMap.get(filePath));
        updatedFileMerged(filePath, headBlob.getFileContents().toString(), "");
        getStagingArea().add(join(filePath));
        return true;
    }

    /** Conflict. Add existing other content into empty head. */
    public static boolean conflictHeadEmpty(String filePath, Map<String, String> otherMap) {
        Blob headBlob = getBlob(otherMap.get(filePath));
        updatedFileMerged(filePath, "", headBlob.getFileContents().toString());
        getStagingArea().add(join(filePath));
        return true;
    }

    /**
     * Overwrite without alert because head is not empty.
     * Check if there need to appear an alarm.
     * */
    public static void overwriteMerge(String filePath, Map<String, String> otherMap,
                                      Integer action) {
        Blob otherBlob = getBlob(otherMap.get(filePath));
        if (action == 3 && getFileByName(filePath).length() != 0) {
            exit("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        updateFileWithBlob(filePath, otherBlob);
        getStagingArea().add(join(filePath));
    }

    /** Remove file from HEAD. */
    public static void removeMerge(String filePath) {
        restrictedDelete(filePath);
        getStagingArea().remove(filePath);
    }


    /**
     * Change HEAD file that points to the active branch.
     * @param branchName String of the name of branch.
     * */
    public static void activateBranch(String branchName) {
        writeContents(HEAD, "ref: "
                + join(correctBranchDir(branchName),
                correctBranchName(branchName)).getAbsolutePath());
    }


    /**
     * create a certain branch file.
     * @param branchName String of the name of branch.
     * @return File of branch pointer*/
    public static File createBranchFile(String branchName) {
        return createBranchFile(branchName, HEADS_DIR);
    }
    public static File createBranchFile(String branchName, File fileDir) {
        return join(fileDir, branchName);
    }
    /** Get active branch File.
     * @return File of active branch.
     */
    public static File getActiveBranchFile() {
        String activeBranchFilePath = readContentsAsString(HEAD).split(": ")[1].trim();
        return join(activeBranchFilePath);
    }

    /** check if the current active branch is the same as given branch. */
    public static boolean ifActiveBranch(String branchName) {
        return getActiveBranchFile().getAbsolutePath().equals(join(correctBranchDir(branchName),
                correctBranchName(branchName)).getAbsolutePath());
    }

    /**
     * get a branch file given a branch name
     * @param branchName the branch name
     * @return File of the branch file in heads provided a branch name.
     * */
    public static File getBranchFile(String branchName) {
        return getBranchFile(branchName, HEADS_DIR);
    }
    public static File getBranchFile(String branchName, File fileDir) {
        return join(fileDir, branchName);
    }

    /** @return boolean of whether a branch has created. */
    public static boolean branchExists(String branchName) {
        return branchExists(branchName, HEADS_DIR);
    }
    public static boolean branchExists(String branchName, File fileDir) {
        List<String> branchNames = new ArrayList<>(plainFilenamesIn(fileDir));
        if (!(branchNames.isEmpty())) {
            return branchNames.contains(branchName);
        }
        return false;
    }

    /** find the correct branch file directory. */
    public static File correctBranchDir(String branchName) {
        if (branchName.contains("/")) {
            String remoteName = branchName.split("/")[0];
            return join(REMOTE_DIR, remoteName);
        }
        return HEADS_DIR;
    }

    /**find the correct branch name. */
    public static String correctBranchName(String branchName) {
        if (branchName.contains("/")) {
            return branchName.split("/")[1];
        }
        return branchName;
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
        return getBranchNames(HEADS_DIR);
    }
    public static List<String> getBranchNames(File fileDir) {
        List<String> branchNames = new ArrayList<>(plainFilenamesIn(fileDir));
        String activaBranchName = getActiveBranchFile().getName();
        branchNames.add(0, "*" + activaBranchName);
        branchNames.remove(activaBranchName);
        return branchNames;
    }


    /** the format to print status. */
    public static void printStatusFormat(String outline, List<String> names) {
        System.out.print("=== ");
        System.out.print(outline);
        System.out.println(" ===");
        if (names != null) {
            names.forEach(any -> {
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
     * get Staging Area instance from file ".gitlet/index".
     * @return StagingArea instance.
     */
    public static Remote getRmote() {
        return readObject(REMOTE, Remote.class);
    }

    /**
     * get blob given a SHA-1 ID.
     * */
    public static Blob getBlob(String blobID) {
        return getBlob(blobID, OBJECT_DIR);
    }

    public static Blob getBlob(String blobID, File fireDir) {
        return readObject(objectFile(blobID, fireDir), Blob.class);
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
        return objectFile(id, OBJECT_DIR);
    }
    public static File objectFile(String id, File objectDir) {
        File fileDir = join(objectDir, id.substring(0, 2));
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
        List<String> fileNames = new ArrayList<>(plainFilenamesIn(fileDir));
        for (String fileName : fileNames) {
            if (id.equals(fileName.substring(0, 4))) {
                return fileName;
            }
        }
        return null;
    }

    /**
     * @param filePath String of a existing file that needs to be written into.
     * @param blob Blob of a stored file that need to overwrite a file.
     * */
    public static void updateFileWithBlob(String filePath, Blob blob) {
        writeContents(join(filePath), blob.getFileContents());
    }

    /** Rewrite merged new file, containing both contents from HEAD and given commit. */
    public static void updatedFileMerged(String filePath, String head, String given) {
        String text1 = "<<<<<<< HEAD" + "\n";
        String text2 = head;
        String text3 = "=======" + "\n";
        String text4 = given;
        String text5 = ">>>>>>>" + '\n';
        writeContents(join(filePath), text1 + text2 + text3 + text4 + text5);

    }

}


