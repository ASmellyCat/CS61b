package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

/**Represents a staging area
 * @author ASmellyCat
 * contains:
 * 1. addition staging area
 * 2. removal staging area
 * 3. tracked files
 */

public class StagingArea implements Serializable {

    /** Map of added blobs with filePath as key and blob as values. */
    private final Map<String, Blob> added;

    /** Set of removed files with file path as key. */
    private final Set<String> removed;
    /** Set of tracked files with filepath as key and fileID(SHA1) as values. */
    private final Map<String, String> tracked;
    /**
     * Create a Staging object with specified parameters.
     */
    public StagingArea() {
        added = new HashMap<>();
        removed = new HashSet<>();
        tracked = new HashMap<>();
        save();
    }

    /**
     * Add file to the staging addition area.
     * @param file File that need to be added.
     * 1. Overwrite the previous entry in staging area.
     * 2. Remove the file that is identical to current commit in the staging areas.
     * 3. runtime should be lgN.
     */
    public void add(File file) {
        Blob blob = new Blob(file);
        String filePath = blob.absolutePath();
        if (isRemoved(filePath)) {
            removed.remove(filePath);
        }
        if (!isModified(blob)) {
            if (isAdded(filePath)) {
                added.remove(filePath);
            }
        } else {
            added.put(filePath, blob);
        }
        save();
    }

    /**
     * Add file to the removal area.
     * @param filePath String of the file that need to be added.
     * 1. Unstage the file if it is currently staged for addition.
     * 2. If the file is tracked in the current commit, stage it for removal.
     */
    public boolean remove(String filePath) {
        filePath = absolutePath(filePath);
        boolean flag = false;;
        if (isAdded(filePath)) {
            added.remove(filePath);
            flag = true;
        }
        if (isTracked(filePath)) {
            removed.add(filePath);
            restrictedDelete(filePath);
            flag = true;
        }
        save();
        return flag;
    }

    /**
     * Unstage a file.
     * @param filePath String of absolute filepath.
     * */
    public void unstage(String filePath, String fileID) {
        if (added.containsKey(filePath)) {
            added.remove(filePath);
        }
        if (removed.contains(filePath)) {
            removed.remove(filePath);
        }
        tracked.put(filePath, fileID);
        save();

    }
    /**
     * Change all current tracked files to tracked files of a given branch points to.
     * @
     * */
    public void updateAllTracked(Map<String, String> t) {
        tracked.clear();
        tracked.putAll(t);
        clear();
        save();
    }

    /**
     * Staging area change in each commit.
     * 1. each commit add the files in addition staging.
     * 2. remove files in removed staging area.
     * 3. clear the staging area.
     */
    public Map<String, String> toCommit() {
        for (Map.Entry<String, Blob> entry : added.entrySet()) {
            tracked.put(entry.getKey(), entry.getValue().shaID());
            entry.getValue().save();
        }
        for (String filePath : removed) {
            tracked.remove(filePath);
        }
        clear();
        save();
        return tracked;
    }

    /** get list of staged files path*/
    public List<String> getStagedFiles() {
        return new ArrayList<>(added.keySet());
    }

    /** get list of tracked files path*/
    public List<String> getTrackedFiles() {
        return new ArrayList<>(tracked.keySet());
    }

    /** get list of removed files path*/
    public List<String> getRemovedFiles() {
        return new ArrayList<>(removed);
    }

    /** get list of modified files path but not Staged.
     * Tracked in the current commit, changed in the working directory, but not staged;
     * Staged for addition, but with different contents than in the working directory;
     * Staged for addition, but deleted in the working directory;
     * Not staged for removal, but tracked in the current commit and deleted from the working directory.
     * */
    public List<String> getModifiedFilesButNotStaged() {
        List<String> returnFileNames = new ArrayList<>();
        List<String> fileNames = new ArrayList<>(tracked.keySet());
        for (String filePath : fileNames) {
            File workFile = join(filePath);
            boolean isremoved = isRemoved(filePath);
            boolean istracked = isTracked(filePath);
            boolean isstaged = isAdded(filePath);
            if (workFile.exists()) {
                Blob workBlob = new Blob(workFile);
                boolean ismodified = isModified(workBlob);
                boolean isstagedButModified = isAddedButModified(workBlob);
                if (ismodified && !isstaged) {
                    returnFileNames.add(filePath + " (modified)");
                } else if (isstagedButModified) {
                    returnFileNames.add(filePath + " (modified)");
                }
            } else {
                if (isstaged) {
                    returnFileNames.add(filePath + " (deleted)");
                } else if (!isremoved && istracked) {
                    returnFileNames.add(filePath + " (deleted)");
                }
            }
        }
        return returnFileNames;
    }

    /** files present in the working directory but neither staged for addition nor tracked.
     * This includes files that have been staged for removal, but then re-created without Gitlet’s knowledge.
     * Ignore any subdirectories that may have been introduced, since Gitlet does not deal with them. */
    public List<String> getUntrackedFiles() {
        List<String> returnFileNames = new ArrayList<>();
        List<String> fileNames = new ArrayList<>(plainFilenamesIn(Repository.CWD));
        for (String fileName : fileNames) {
            File file = join(fileName);
            String filePath = file.getAbsolutePath();
            boolean isstaged = isAdded(filePath);
            boolean istracked = isTracked(filePath);
            boolean isremoved = isRemoved(filePath);
            if (!isstaged && !istracked) {
                returnFileNames.add(filePath);
            } else if (isremoved) {
                returnFileNames.add(filePath);
            }
        }
        return returnFileNames;
    }

    /** If there are staged additions or removals present */
    public boolean noStaged() {
        return (added.isEmpty() && removed.isEmpty());
    }

    /** private Help method. /
    /**
     * Judge whether the file has been modified from last commit.
     * @return boolean of whether the file is modified.
     */
    private boolean isModified(Blob blob) {
        return !tracked.containsValue(blob.shaID());
    }
    /**
     * Judge whether the file with the same path has been added from last commit.
     * @return boolean of whether the file is added.
     */
    private boolean isAdded(String filePath) {
        return added.containsKey(filePath);
    }

    /** Staged for addition, but with different contents than a given file. */
    private boolean isAddedButModified(Blob blob) {
        return added.containsKey(blob.absolutePath()) && (!added.containsValue(blob.shaID()));
    }
    /**
     * Judge whether the file has the same path been removed from last commit.
     * @return boolean of whether the file is removed.
     */
    private boolean isRemoved(String filePath) {
        return removed.contains(filePath);
    }
    /**
     * Judge whether the file has the same path been tracked from last commit.
     * @return boolean of whether the file is tracked.
     */
    private boolean isTracked(String filePath) {
        return tracked.containsKey(filePath);
    }

    /** save this Staging Area instance. */
    private void save() {
        writeObject(Repository.INDEX, this);
    }
    /**
     * Clear the staging area.
     */
    private void clear() {
        added.clear();
        removed.clear();
    }
}
