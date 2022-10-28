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
        if (args.length == 0) {
            exit("Must have at least one arguments. ");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init": // Usage: java gitlet.Main add [file name]
                validateNumArgs("init", args, 1);
                Repository.init();
                break;
            case "add": // Usage: java gitlet.Main add [file name]
                validateNumArgs("add", args, 2);
                fileName = args[1];
                Repository.add(fileName);
                break;
            case "rm": // java gitlet.Main rm [file name]
                validateNumArgs("rm", args, 2);
                fileName = args[1];
                Repository.removal(fileName);
                break;
            case "commit": // java gitlet.Main commit [message]
                validateNumArgs("commit", args, 2);
                message = args[1];
                Repository.commit(message);
                break;
        }
    }

    private static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
}
