# Gitlet Design Document

**Name**: ASmellyCat

## Classes and Data Structures

     * .gitlet
     *    ├── HEAD (file)       // Where is the head commit ref: refs/heads/master
     *    ├──index (file)       // Stage area. Serialized objects are saved into index file.
     *    ├── objects  (directory)   // hash table that contains SHA-1 of Serialized objects (blob, commit, ...).
     *    ├──refs
     *       └── heads
     *              └── branches (file)  // SHA-1 of current commit that head pointer points to.


* A branch is a pointer to a commit.
* The HEAD points to the active branch. "Check out" means move HEAD to another active branch.
* When we commit, only the active branch and HEAD move.

### Class Commit

#### Instance Variables
* Message - contains the message of a commit.
* Timestamp - time at which a commit was created. Assigned bu the constructor.
* Parent - the parent commit of a commit object.

1. Field 1
2. Field 2


### Class Blob

#### Fields

1. Field 1
2. Field 2


## Algorithms

## Persistence

