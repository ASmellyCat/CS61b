package gitlet;

import static gitlet.MyUtils.*;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author ASmellyCat
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     *  java gitlet.Main add hello.txt
     *  java gitlet.Main commit "modified"
     */
    public static void main(String[] args) {
        String fileName;
        String message;
        String commitID;
        String branchName;
        String remoteName;
        String localName;
        if (args.length == 0) {
            exit("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init": // Usage: java gitlet.Main add [file name]
                validateNumArgs("init", args, 1);
                Repository.init();
                break;
            case "add": // Usage: java gitlet.Main add [file name]
                Repository.isInitialized();
                validateNumArgs("add", args, 2);
                fileName = args[1];
                Repository.add(fileName);
                break;
            case "rm": // Usage: java gitlet.Main rm [file name]
                Repository.isInitialized();
                validateNumArgs("rm", args, 2);
                fileName = args[1];
                Repository.removal(fileName);
                break;
            case "commit": // Usage: java gitlet.Main commit [message]
                Repository.isInitialized();
                validateNumArgs("commit", args, 2);
                message = args[1];
                if (message.length() == 0) {
                    exit("Please enter a commit message.");
                }
                Repository.commit(message);
                break;
            case "log": // Usage: java gitlet.Main log
                Repository.isInitialized();
                validateNumArgs("log", args, 1);
                Repository.log();
                break;
            case "global-log": // Usage: java gitlet.Main global-log
                Repository.isInitialized();
                validateNumArgs("global-log", args, 1);
                Repository.globalLog();
                break;
            case "find": // Usage: java gitlet.Main find [commit message]
                Repository.isInitialized();
                validateNumArgs("find", args, 2);
                message = args[1];
                Repository.find(message);
                break;
            case "status": // Usage: java gitlet.Main status
                Repository.isInitialized();
                validateNumArgs("status", args, 1);
                Repository.status();
                break;
            case "checkout": // Usage: java gitlet.Main status
                Repository.isInitialized();
                switch(args.length) {
                    case 4: // java gitlet.Main checkout [commit id] -- [file name]
                        if (!args[2].equals("--")) {
                            exit("Incorrect operands.");
                        }
                        commitID = args[1];
                        fileName = args[3];
                        Repository.checkout(commitID, fileName);
                        break;
                    case 3:// java gitlet.Main checkout -- [file name]
                        if (!args[1].equals("--")) {
                            exit("Incorrect operands.");
                        }
                        fileName = args[2];
                        Repository.checkout(fileName);
                        break;
                    case 2: // java gitlet.Main checkout [branch name]
                        branchName = args[1];
                        Repository.checkoutBranch(branchName);
                        break;
                }
                break;
            case "branch": // Usage: java gitlet.Main branch [branch name]
                Repository.isInitialized();
                validateNumArgs("branch", args, 2);
                branchName = args[1];
                Repository.branch(branchName);
                break;
            case "rm-branch": // Usage: java gitlet.Main branch [branch name]
                Repository.isInitialized();
                validateNumArgs("rm-branch", args, 2);
                branchName = args[1];
                Repository.rmBranch(branchName);
                break;
            case "reset": // Usage: java gitlet.Main reset [commit id]
                Repository.isInitialized();
                validateNumArgs("reset", args, 2);
                commitID = args[1];
                Repository.reset(commitID);
                break;
            case "merge": // Usage: java gitlet.Main merge [branch name]
                Repository.isInitialized();
                validateNumArgs("merge", args, 2);
                branchName = args[1];
                Repository.merge(branchName);
                break;
            default:
                exit("No command with that name exists.");
        }
    }

    private static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
}
