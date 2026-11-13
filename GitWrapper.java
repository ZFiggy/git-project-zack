import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitWrapper {

    /**
     * Initializes a new Git repository.
     * Creates git/, git/objects/, git/index, and git/HEAD.
     */
    public static void init() {
        new GitInitialize();
    }

    /**
     * Stages a file for the next commit.
     * Throws IOException if the path does not exist or is a directory.
     * Does nothing if the file's content is already staged for that path.
     *
     * @param filePath The path to the file to stage.
     */
    public static void add(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IOException("File does not exist: " + filePath);
        }

        if (!file.isFile()) {
            throw new IOException("Path is not a file: " + filePath);
        }

        GitInitialize git = new GitInitialize();

        git.createBlob(filePath);
        git.addEntry(filePath);
    }

    /**
     * Creates a commit capturing the current state of the index.
     * Builds trees, writes the commit file, updates HEAD.
     *
     * Commit format:
     * tree: <tree_sha>
     * parent: <parent_sha> (omit line for initial commit)
     * author: <author>
     * date: <date>
     * message: <message>
     *
     * @param author  The author name.
     * @param message The commit message.
     * @return The SHA-1 hash of the new commit.
     */
    public static String commit(String author, String message) {
        GitInitialize git = new GitInitialize();

        try {
            return git.commit(author, message);
        } catch (IOException e) {
            throw new RuntimeException("Commit failed: " + e.getMessage());
        }
    }

    public static void createBranch(String branchName) throws IOException {
        GitInitialize git = new GitInitialize();
        git.createBranch(branchName);
    }

    public static void checkout(String branchName, boolean called) throws IOException {
        try {
            File branchFile = new File("git/refs/heads/" + branchName);

            if (!branchFile.exists()) {
                createBranch(branchName);
                Files.writeString(Path.of("git/HEAD"), "ref: refs/heads/" + branchName);
                return;
            }

            String commitHash = Files.readString(branchFile.toPath());

            checkout(commitHash);

            Files.writeString(Path.of("git/HEAD"), "ref: refs/heads/" + branchName);

        } catch (IOException e) {
            throw new IOException("Checkout failed.");
        }
    }

    /**
     * EXTRA CREDIT: Restores the working directory to the state of a specific
     * commit.
     * Traces the commit's root tree and recreates all files and directories.
     *
     * @param commitHash The SHA-1 hash of the commit to check out.
     */
    public static void checkout(String commitHash) {

        try {
            if (commitHash.length() != 40) {
                checkout(commitHash, true);
                return;
            }

        File commitFile = new File("git/objects/" + commitHash);

           if (!commitFile.exists()) {
               throw new IOException("Commit does not exist: " + commitHash);
           }

           String contents = Files.readString(commitFile.toPath());

           String[] lines = contents.split("\n");

           String rootHash = "";

           for (String line : lines) {
               if (line.startsWith("tree: ")) {
                   rootHash = line.substring(6);
                   break;
               }
           }

           if (rootHash.isEmpty()) {
               throw new IOException("Commit has no root tree: " + commitHash);
           }

           System.out.println("Checking out commit: " + commitHash);
           System.out.println("Root tree: " + rootHash);

           restoreTree(rootHash, "");

           Files.writeString(Path.of("git/HEAD"), commitHash);

           System.out.println("Checkout completed!");

       } catch (IOException e) {
           throw new RuntimeException("Checkout failed: " + e.getMessage());
       }
    }

    //this helper traverses the tree and recreates its directories and files
    public static void restoreTree(String treeHash, String path) throws IOException {

        File treeFile = new File("git/objects/" + treeHash);

        if (!treeFile.exists()) {
            throw new IOException("Tree does not exist: " + treeHash);
        }

        String contents = Files.readString(treeFile.toPath());

        String[] lines = contents.split("\n");

        for (String line : lines) {
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(" ", 3);

            if (parts.length != 3) {
                throw new IOException("Invalid tree entry: " + line);
            } 

            String type = parts[0];
            String hash = parts[1];
            String name = parts[2];

            String fullPath = "";

            if (path.isEmpty()) {
                fullPath = name;
            } else {
                fullPath = path + "/" + name;
            }

            if (type.equals("tree")) {
                File directory = new File(fullPath);

                if (!directory.exists()) {
                    Files.createDirectories(directory.toPath());
                }

                restoreTree(hash, fullPath);
            } else if (type.equals("blob")) {
                File blobFile = new File("git/objects/" + hash);

                if (!blobFile.exists()) {
                    throw new IOException("Blob does not exist: " + hash);
                }

                Path filePath = Path.of(fullPath);

                Path parent = filePath.getParent();

                if (parent != null) {
                    Files.createDirectories(parent);
                }

                byte[] content = Files.readAllBytes(blobFile.toPath());
                Files.write(filePath, content);

                System.out.println("Restored: " + fullPath);
            } else {
                throw new IOException("Unknown object type: " + type);
            }
        }
    }

}
