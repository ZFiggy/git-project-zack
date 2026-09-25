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
    public static void main(String[] args) {
        initiate();
    }

    public static void initiate() {
        try {
            int count = 0;
            File git = new File("git/");
            if (!git.mkdir()) {
                count++;
            }

            File objects = new File(git, "objects/");
            if (!objects.mkdir()) {
                count++;
            }

            File index = new File(git, "index");
            if (!index.createNewFile()) {
                count++;
            }

            File head = new File(git, "HEAD");
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
}