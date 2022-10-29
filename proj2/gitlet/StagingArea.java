package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.MyUtils.exit;
import static gitlet.Utils.*;

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
    /** set of tracked files with filepath as key and fileID(SHA1) as values. */
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
     * @param file File that need to be added.
     * 1. Unstage the file if it is currently staged for addition.
     * 2. If the file is tracked in the current commit, stage it for removal.
     */
    public boolean remove(File file) {
        boolean flag = false;
        String filePath = file.getAbsolutePath();
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

    public List<String> getStagedFiles() {
        return new ArrayList<>(added.keySet());
    }

    public List<String> getTrackedFiles() {
        return new ArrayList<>(tracked.keySet());
    }

    public List<String> getRemovedFiles() {
        return new ArrayList<>(removed);
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
