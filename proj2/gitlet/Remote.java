package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;
import static gitlet.MyUtils.*;
import static gitlet.HelpMethod.*;

public class Remote implements Serializable {
    /** Map to store the name of remote directory */
    Map<String, File> remote;

    /** Initialize remote instance. */
    public Remote() {
        remote = new HashMap<>();
        save();
    }

    public void add(String remoteName, String remoteAdress) {
        if (remote.containsKey(remoteName)) {
            exit("A remote with that name already exists.");
        }
        File remoteFile = join(Repository.CWD.getParentFile(), remoteAdress.substring(3));
        remote.put(remoteName, remoteFile);
        remoteToLocalFile(remoteName).mkdir();
        save();
    }

    public void remove(String remoteName) {
        if (!remote.containsKey(remoteName)) {
            exit("A remote with that name does not exist.");
        }
        remote.remove(remoteName);
        save();
    }

    /** get the remote file*/
    public File getRemoteFile(String remoteName) {
        return remote.get(remoteName);
    }

    /** fetch. */
    public void fetch(String remoteName, String remoteBranchName) {
        File remoteHeadDir = join(remote.get(remoteName), "refs/heads/");
        File objectDir = join(remote.get(remoteName), "objects");
        if (!remote.get(remoteName).exists()) {
            exit("Remote directory not found.");
        }
        if (!branchExists(remoteBranchName, remoteHeadDir)) {
            exit("That remote does not have that branch.");
        }
        String commitID = getCommitIDByBranchName(remoteBranchName, remoteHeadDir);
        Set<Commit> commitSet = getAllParentCommits(commitID, objectDir);
        for (Commit commit : commitSet) {
            //saveObjectFile(objectFile(commit.getCommitID()), objectFile(commit.getCommitID(), objectDir));
            commit.updatePath(GITLET_DIR);
            for (String fileID : commit.getFiles().values()) {
                //saveObjectFile(objectFile(fileID), objectFile(fileID, objectDir));
                getBlob(fileID, objectDir).updatePath();
            }
        }
        writeContents(join(REMOTE_DIR, remoteName, remoteBranchName), commitID);
    }


    /** push */
    public void push(String remoteName, String remoteBranchName) {
        File remoteHeadDir = join(remote.get(remoteName), "refs/heads/");
        if (!remote.get(remoteName).exists()) {
            exit("Remote directory not found.");
        }
        String remoteCommitID = getCommitIDByBranchName(remoteBranchName, remoteHeadDir);
        Set<String> parentIDs = getAllParentCommitsID(getCurrentCommitID());
        if (!parentIDs.contains(remoteCommitID)) {
            exit("Please pull down remote changes before pushing.");
        }
        Commit commit = getCommit(getCurrentCommitID());
        while (!commit.getCommitID().equals(remoteCommitID)) {
            commit.updatePath(remote.get(remoteName));
            for (String fileID : commit.getFiles().values()) {
                getBlob(fileID).updatePath(remote.get(remoteName));
            }
            commit = getCommit(commit.getParentID());
        }
        File branchFile = join(remote.get(remoteName), "refs/heads", remoteBranchName);
        writeContents(branchFile, getCurrentCommitID());
        writeContents(join(remote.get(remoteName),"HEAD"), "ref: " +
                branchFile.getAbsolutePath());
    }

    /** save this remote instance. */
    private void save() {
        writeObject(Repository.REMOTE, this);
    }

    /** find the remote branch file directory in local. */
    private static File remoteToLocalFile(String remoteName) {
        //return join(Repository.REMOTE_DIR, remoteName);
        return join(REMOTE_DIR, remoteName);
    }



}

