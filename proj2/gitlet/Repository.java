package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;
import static gitlet.HelpMethod.*;

/** Represents a gitlet repository.
 * GITLET construction
 *<pre>
 * .gitlet
 *    ├── HEAD (file)       // Where is the head commit ref: refs/heads/master
 *    ├── index (file)       // Stage area. Serialized objects are saved into index file.
 *    ├── commits (file)    // Store the SHA-1 ID of all the commits.
 *    ├── objects  (directory)   // hashtable with SHA-1 of Serialized objects (blob, commit)
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

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The object directory.
     */
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    /**
     * The reference directory.
     */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /**
     * The local heads directory.
     */
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    /**
     * The remote heads directory.
     */
    public static final File ROMOTE_DIR = join(REFS_DIR, "remote");
    /**
     * The HEAD file.
     */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /**
     * The index file.
     */
    public static final File INDEX = join(GITLET_DIR, "index");
    /**
     * The file that contains all SHA-1 code of all commits.
     */
    public static final File COMMITS = join(GITLET_DIR, "commits");
    /**
     * The initial message.
     */
    public static final String INITIAL_MESSAGE = "initial commit";
    /**
     * The name of initial branch
     */
    public static final String INITIAL_BRANCH = "master";
    /**
     * Default prefix for HEAD.
     */
    public static final String DEFAULT_HEAD_PREFIX = "ref: refs/heads/";

    /**
     * check whether it is initialized
     */
    public static void isInitialized() {
        if (!(GITLET_DIR.exists() && GITLET_DIR.isDirectory())) {
            exit("Not in an initialized Gitlet directory.");
        }
    }

    /**
     * init
     * Usage: java gitlet.Main init
     * Description: Creates Gitlet repository in the current directory.
     * 1. new commit contains no files.
     * 2. commit message: initial commit.
     * 3. master will be the current branch, pointer to the current branch.
     * 4. timestamp 00:00:00 UTC, Thursday, 1 January 1970
     * 5. This commit the same UID
     * 6. If there is already a Gitlet system, it should abort.
     * 7. contains failure cases.
     */
    public static void init() {
        if (GITLET_DIR.exists()) {
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
        ROMOTE_DIR.mkdir();
        activateBranch(INITIAL_BRANCH);
        new Commit(INITIAL_MESSAGE, null, null, new HashMap<>());
        new StagingArea();

    }


    /**
     * add
     *
     * @param fileName String of the name of file that need to be added.
     *                 Description: Adds a copy of the file as it currently
     *                 exists to the staging area.
     *                 1. Overwrite the previous entry in staging area.
     *                 2. There is a staging area in .getlet directory.
     *                 3. Remove the file which is identical to current
     *                 commit in the staging areas.
     *                 4. The file will no longer be staged for removal,
     *                 if it was at the time of the command.
     *                 5. runtime should be lgN.
     */
    public static void add(String fileName) {
        gitletRepoExists();
        fileExists(fileName);
        getStagingArea().add(getFileByName(fileName));
    }

    /**
     * rm
     *
     * @param fileName String of name of file that need to be removed.
     *                 1. Unstage the file if it is currently staged for addition.
     *                 2. If the file is tracked in the current commit,
     *                 stage it for removal.
     *                 3. remove the file from the working directory
     *                 if the user has not already done so.
     *                 4. If the file is neither staged nor tracked by the head commit,
     *                 print the error message.
     */
    public static void removal(String fileName) {
        gitletRepoExists();
        if (!getStagingArea().remove(fileName)) {
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
        commit(message, null);
    }

    private static void commit(String message, String secondParentID) {
        StagingArea stageArea = getStagingArea();
        if (stageArea.getStagedFiles().isEmpty() && stageArea.getRemovedFiles().isEmpty()) {
            exit("No changes added to the commit.");
        }
        Map<String, String> tracked = getStagingArea().toCommit();
        new Commit(message, getCurrentCommitID(), secondParentID, tracked);
    }

    /**
     * log
     * display information about each commit backwards along the commit tree.
     * ===
     * commit 3e8bf1d794ca2e9ef8a4007275acf3751c7170ff
     * Date: Thu Nov 9 17:01:33 2017 -0800
     * Another commit message.
     * <p>
     * ===
     * commit e881c9575d180a215d1a636545b8fd9abfb1d2bb
     * Date: Wed Dec 31 16:00:00 1969 -0800
     * initial commit
     */
    public static void log() {
        printLog(getCommit(getCurrentCommitID()));
    }

    /**
     * global-log
     * Like log, except displays information about all commits ever made.
     */
    public static void globalLog() {
        for (Commit commit : getAllCommits()) {
            printOneLog(commit);
        }
    }

    /**
     * find
     * Prints out the ids of all commits that have the given commit message.
     * it prints the ids out on separate lines.
     * If no such commit exists, prints the error message.
     */
    public static void find(String message) {
        boolean flag = true;
        for (Commit commit : getAllCommits()) {
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.getCommitID());
                flag = false;
            }
        }
        if (flag) {
            exit("Found no commit with that message.");
        }
    }

    /**
     * status
     * === Branches ===
     * *master
     * other-branch
     * <p>
     * === Staged Files ===
     * wug.txt
     * wug2.txt
     * <p>
     * === Removed Files ===
     * goodbye.txt
     * <p>
     * === Modifications Not Staged For Commit ===
     * junk.txt (deleted)
     * wug3.txt (modified)
     * <p>
     * === Untracked Files ===
     * random.stuff
     */

    public static void status() {
        StagingArea stageArea = getStagingArea();
        printStatusFormat("Branches", getBranchNames());
        printStatusFormat("Staged Files", stageArea.getStagedFiles());
        printStatusFormat("Removed Files", stageArea.getRemovedFiles());
        printStatusFormat("Modifications Not Staged For Commit",
                stageArea.getModifiedFilesButNotStaged());
        printStatusFormat("Untracked Files", stageArea.getUntrackedFiles());
    }

    /**
     * gitlet.Main checkout -- [file name]
     * checkout filename
     * Takes the version of the file into working directory.
     * The new version of the file is not staged.
     */
    public static void checkout(String fileName) {
        checkout(getCurrentCommitID(), fileName);
    }

    /**
     * gitlet.Main checkout [commit id] -- [file name]
     * Takes the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     */
    public static void checkout(String commitID, String fileName) {
        Map<String, String> tracked = getCommit(commitID).getFiles();
        String filePath = absolutePath(fileName);
        if (tracked.containsKey(filePath)) {
            Blob blob = getBlob(tracked.get(filePath));
            updateFileWithBlob(filePath, blob);
            getStagingArea().unstage(filePath, blob.shaID());
        } else {
            exit("File does not exist in that commit.");
        }
    }

    /**
     * gitlet.Main checkout [branch name]
     * Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory,
     * overwriting the versions of the files that are already there if they exist.
     * at the end of this command,
     * the given branch will now be considered the current branch (HEAD).
     * Any files that are tracked in the current branch
     * but are not present in the checked-out branch are deleted.
     * The staging area is cleared, unless the checked-out branch is the current branch.
     */
    public static void checkoutBranch(String branchName) {
        if (!branchExists(branchName)) {
            exit("No such branch exists.");
        }
        if (getActiveBranchFile().getName().equals(branchName)) {
            exit("No need to checkout the current branch.");
        }

        resetACommit(getCommitIDByBranchName(branchName));
        activateBranch(branchName);
    }

    /**
     * branch
     * Creates a new branch with the given name
     * points it at the current head commit.
     * A branch is nothing more than a name
     * for a reference (a SHA-1 identifier) to a commit node.
     */

    public static void branch(String branchName) {
        if (branchExists(branchName)) {
            exit("A branch with that name already exists.");
        }
        writeContents(createBranchFile(branchName), getCurrentCommitID());
    }

    /**
     * rm-branch
     * Deletes the branch with the given name.
     * This only means to delete the pointer associated with the branch;
     * it does not mean to delete all commits that were created under the branch,
     * or anything like that.
     */
    public static void rmBranch(String branchName) {
        if (!branchExists(branchName)) {
            exit("A branch with that name does not exist.");
        }
        if (branchName.equals(getActiveBranchFile().getName())) {
            exit("Cannot remove the current branch.");
        }
        getBranchFile(branchName).delete();

    }

    /**
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch’s head to that commit node.
     * The staging area is cleared.
     * <p>
     * If no commit with the given id exists, print No commit with that id exists.
     * <p>
     * If a working file is untracked in the current branch
     * and would be overwritten by the reset,
     * print `There is an untracked file in the way; delete it,
     * or add and commit it first.`
     * perform this check before doing anything else.
     */
    public static void reset(String commitID) {
        resetACommit(commitID);
        writeContents(getActiveBranchFile(), commitID);
    }

    /**
     * merge
     * 1. find the latest common ancestor.
     * 2. if the split point is the current branch,
     * then the effect is to check out the given branch.
     * 3. Any files that have been modified in the given branch since the split point,
     * but not changed in the current branch should be
     * changed to their versions in the given branch.
     * 4. Any files that have been modified in the current branch
     * but not in the given branch since the split point
     * should stay as they are.
     * 5. if both changed in the same way, left unchanged.
     * if a file is both removed, but same name presents, l
     * eave it continue to be absent.
     * 6. if no file in split point but only present in current branch, keep.
     * 7. Any files that were not present at the split point
     * and are present only in the given branch should be checked out and staged.
     * 8. Any files present at the split point, unmodified in the current branch,
     * and absent in the given branch should be removed (and untracked).
     * 9. Any files present at the split point, unmodified in the given branch,
     * and absent in the current branch should remain absent.
     * 10. conflict: replace the contents of the conflicted file with
     * <<<<<<< HEAD
     * contents of file in current branch
     * =======
     * contents of file in given branch
     * >>>>>>>
     * Merged [given branch name] into [current branch name].
     * <p>
     * Merge commits differ from other commits:
     * they record as parents both the head of the current branch
     * (called the first parent)
     * and the head of the branch given on the command line to be merged in.
     */
    public static void merge(String branchName) {
        if (!branchExists(branchName)) {
            exit("A branch with that name does not exist.");
        }
        if (branchName.equals(getActiveBranchFile().getName())) {
            exit("Cannot merge a branch with itself.");
        }
        if (!getStagingArea().noStaged()) {
            exit("You have uncommitted changes.");
        }
        Commit otherCommit = getCommit(getCommitIDByBranchName(branchName));
        Commit headCommit = getCommit(getCurrentCommitID());
        Commit splitCommit = getSplitCommit(headCommit, otherCommit);
        String otherID = otherCommit.getCommitID();
        String headID = headCommit.getCommitID();
        String splitID = splitCommit.getCommitID();
        if (splitID.equals(headID)) {
            checkoutBranch(branchName);
            System.out.print("Current branch fast-forwarded.");
        } else if (splitID.equals(otherID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
        } else {
            boolean isConflict = toMerge(splitCommit, headCommit, otherCommit);
            commit("Merged " + branchName + " into " + getActiveBranchFile().getName()
                    + ".", otherCommit.getCommitID());
            if (isConflict) {
                System.out.print("Encountered a merge conflict.");
            }

        }
    }
}
