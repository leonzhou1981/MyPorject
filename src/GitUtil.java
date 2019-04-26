import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class GitUtil {

    public static Git getGit(String remoteRepoName, String localRepoName, String branch) {
        Properties properties = new Properties();
        Git git = null;
        File localRepo = null;
        File newDirForRepo = null;
        try {
            FileInputStream fis = new FileInputStream("conf/git.properties");
            properties.load(fis);
            fis.close();

            String remoteUrl = properties.getProperty("remote.url");
            String localUrl = properties.getProperty("local.url");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");

            localRepo = new File(localUrl + File.separator + localRepoName + File.separator + ".git");
            if (localRepo.exists()) {
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                Repository repository = builder.setGitDir(localRepo)
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .build();
                git = new Git(repository);
            } else {
                newDirForRepo = new File(localUrl + File.separator + localRepoName + File.separator);
                if (newDirForRepo.exists() || newDirForRepo.mkdirs()) {
                    git = Git.cloneRepository()
                        .setURI(remoteUrl + "/" + remoteRepoName + ".git")
                        .setDirectory(newDirForRepo)
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                        .call();
                }
            }
            if (git != null && git.getRepository() != null) {
                if (git.getRepository().resolve(branch) == null) {
                    git.branchCreate().setName(branch).call();
                }
                Ref checkout = git.checkout().setName(branch).call();
                System.out.println("Result of checking out the branch: " + checkout);
            }
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        } finally {
            if (git == null && localRepo != null && !localRepo.exists() && newDirForRepo != null) {
                newDirForRepo.delete();
            }
        }

        return git;
    }


    public static CredentialsProvider getCP() {
        CredentialsProvider cp = null;
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream("conf/git.properties");
            properties.load(fis);
            fis.close();

            String username = properties.getProperty("username");
            String password = properties.getProperty("password");

            cp = new UsernamePasswordCredentialsProvider(username, password);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cp;
    }
}
