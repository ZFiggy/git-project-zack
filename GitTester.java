import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitTester {
    public static void main(String[] args) throws IOException {
        // GitWrapper.init();
        // GitWrapper.add("hello.txt");
        // GitWrapper.add("inner/world.txt");
        // String hash = GitWrapper.commit("John Doe", "Initial commit");
        // System.out.println("Commit: " + hash);

        // Test 1: Initialize repository
        System.out.println("=== Testing init() ===");

        GitWrapper.init();

        System.out.println("Git folder exists: " + new File("git").exists());

        System.out.println("Objects folder exists: " + new File("git/objects").exists());

        System.out.println("Index exists: " + new File("git/index").exists());

        System.out.println("HEAD exists: " + new File("git/HEAD").exists());

        // Create test directories
        Files.createDirectories(Path.of("inner"));

        // Create test files
        Files.writeString(Path.of("hello.txt"), "Hello World!");
        Files.writeString(Path.of("inner/world.txt"), "Testing Git!");

        // Test 2: Stage files
        System.out.println("\n=== Testing add() ===");

        GitWrapper.add("hello.txt");
        GitWrapper.add("inner/world.txt");

        System.out.println("Index contents:");
        System.out.println(Files.readString(new File("git/index").toPath()));

        // Test 3: Create commit
        System.out.println("\n=== Testing commit() ===");

        String commitHash = GitWrapper.commit("John Doe", "Initial commit");

        System.out.println("Commit: " + commitHash);

        // Test 4: Verify HEAD
        System.out.println("\n=== Verifying HEAD ===");

        String headHash = Files.readString(new File("git/HEAD").toPath()).trim();

        System.out.println("HEAD: " + headHash);

        System.out.println("HEAD matches commit: " + headHash.equals(commitHash));

        // Test 5: Verify commit object
        System.out.println("\n=== Verifying Commit Object ===");

        File commitFile = new File("git/objects/" + commitHash);

        System.out.println("Commit object exists: " + commitFile.exists());

        if (commitFile.exists()) {
            System.out.println("\nCommit contents:");
            System.out.println(Files.readString(commitFile.toPath()));
        }

        System.out.println("\n=== Testing checkout() ===");

        // Save the commit hash
        String savedCommit = commitHash;

        // Modify one of the working files
        Files.writeString(Path.of("hello.txt"), "This file has been modified!");

        System.out.println("Before checkout:");
        System.out.println(Files.readString(Path.of("hello.txt")));

        // Restore the saved commit
        GitWrapper.checkout(savedCommit);

        System.out.println("\nAfter checkout:");
        System.out.println(Files.readString(Path.of("hello.txt")));

        System.out.println("HEAD: " + Files.readString(Path.of("git/HEAD")));

        GitWrapper.checkout("main");

        Files.writeString(Path.of("a.txt"), "one");
        GitWrapper.add("a.txt");
        GitWrapper.commit("Zack", "one");

        GitWrapper.createBranch("feature");
        GitWrapper.checkout("feature");

        Files.writeString(Path.of("a.txt"), "two");
        GitWrapper.add("a.txt");
        GitWrapper.commit("Zack", "two");

        GitWrapper.checkout("main");
        System.out.println("On main, a.txt: " + Files.readString(Path.of("a.txt")));

        GitWrapper.checkout("feature");
        System.out.println("On feature, a.txt: " + Files.readString(Path.of("a.txt")));
    }
}
