# git-project-zack
GitInitialize() - Initializes all folders and files in a constructor.
hashFile(String filePath) - Returns the SHA-1 value for any file.
createBlob(String filePath) - Creates a new file with a name of the hash and the file contents of the file's contents
Getters and Setters made
addEntry(String filePath) - Adds and entry to the index file with its hash and file path

note that the methods from 3.2 and 3.3 have comments above them to keep track of what they are doing - most were tested but not all sorry

GP-4.3 — GitWrapper

What bugs did you discover in your partner's code, and which ones did you fix?**

I identified an issue with blob creation where reading files line by line could remove trailing newline characters, causing the stored contents to differ from their calculated hashes. I fixed this by copying the exact file contents when creating blobs. I also corrected a string comparison in the directory traversal logic to use .equals() instead of !=.

What functionality was missing? Did you implement it?**

The project was missing a standardized interface for initializing repositories, staging files, and creating commits. I implemented GitWrapper with static init(), add(), and commit() methods that call the existing GitInitialize methods. The optional checkout() functionality has not been implemented.