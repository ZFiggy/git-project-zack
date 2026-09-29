import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class GitInitialize {

    private File git;
    private File objects;
    private File index;
    private File head;

    public GitInitialize() {
        try {
            int count = 0;
            git = new File("git/");
            if (!git.mkdir()) {
                count++;
            }

            objects = new File(git, "objects/");
            if (!objects.mkdir()) {
                count++;
            }

            index = new File(git, "index");
            if (!index.createNewFile()) {
                count++;
            }

            head = new File(git, "HEAD");
            if (!head.createNewFile()) {
                count++;
            }

            if (count == 4) {
                System.out.println("Git Repository Already Exists");
            } else {
                System.out.println("Git Repository Created");
            }
        } catch (IOException e) {
            System.out.println("File Error: " + e);
        }
    }

    public static void main(String[] args) {
        GitInitialize git = new GitInitialize();
        try {
            System.out.println(hashFile("Hello.txt"));
            git.createBlob("Hello.txt");
            git.addEntry("Hello.txt");
        } catch (IOException e) {
            System.out.println("Hashing Error: " + e);
        }
    }

    public static String hashFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (!Files.isRegularFile(path)) {
            throw new IOException("no such file: " + filePath);
        }
        byte[] fileBytes = Files.readAllBytes(path);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 is not available", e);
        }

        byte[] hash = digest.digest(fileBytes);
        return HexFormat.of().formatHex(hash);
    }

    public void createBlob(String filePath) throws IOException {
        String hash = hashFile(filePath);
        File hashFile = new File(objects, hash);
        hashFile.createNewFile();
        FileWriter hashWriter = new FileWriter(hashFile.toPath().toString());
        BufferedReader hashReader = new BufferedReader(new FileReader(filePath));
        String contents = hashReader.readLine();
        while (contents != null) {
            hashWriter.write(contents + "\n");
            contents = hashReader.readLine();
        }
        hashReader.close();
        hashWriter.close();

    }

    public void addEntry(String filePath) throws IOException {
        String hash = hashFile(filePath);
        FileWriter hashWriter = new FileWriter(index.toPath().toString());
        BufferedReader indexReader = new BufferedReader(new FileReader(index.toPath().toString()));
        if (indexReader.readLine() == null) {
            hashWriter.write(hash + " " + filePath);
        } else {
            hashWriter.write("\n" + hash + " " + filePath);
        }
        hashWriter.close();
        indexReader.close();
    }

    /**
     * @return the git
     */
    public File getGit() {
        return git;
    }

    /**
     * @param git the git to set
     */
    public void setGit(File git) {
        this.git = git;
    }

    /**
     * @return the objects
     */
    public File getObjects() {
        return objects;
    }

    /**
     * @param objects the objects to set
     */
    public void setObjects(File objects) {
        this.objects = objects;
    }

    /**
     * @return the index
     */
    public File getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(File index) {
        this.index = index;
    }

    /**
     * @return the head
     */
    public File getHead() {
        return head;
    }

    /**
     * @param head the head to set
     */
    public void setHead(File head) {
        this.head = head;
    }
}