# Gitlet Design Document

**Name**: ASmellyCat

Classes and Data Structures

     * .gitlet
     *    ├── HEAD (file)       // Where is the head commit ref: refs/heads/master
     *    ├──index (file)// Stage area. Serialized objects are saved into index file.
     *    ├── commits (file)    // Store the SHA-1 ID of all the commits. 
     *    ├── objects  (directory)   // hash table that contains SHA-1 of Serialized objects (blob, commit, ...).
     *    ├──refs
     *       └── heads
     *              └── branches (file)  // SHA-1 of current commit that head pointer points to.


* A branch is a pointer to a commit.
* The HEAD points to the active branch. "Check out" means move HEAD to another active branch.
* When we commit, only the active branch and HEAD move.

### Class Commit
#### Instance Variables
* message - The input message of this commit.
* date - The date of this commit created. 
* ParentID - SHA-1 ID of parent commit.
* tracked - Map of tracked files with filepath as key and fileID(SHA1) as values.
* commitID - SHA-1 ID of this commit.

### Class Blob
#### Instance Variables
* fileContents - the content of a file that converted into a blob.
* fileID - the SHA-1 ID of a blob.
* filePath - the absolute filepath of a blob.
* currentFile - the source file of a blob. 

### Class StagingArea 
#### Instance Variables
* added - Map of added blobs with filePath as key and blob as values.
* removed - Set of removed files with file path as key.
* tracked - Create a Staging object with specified parameters.


