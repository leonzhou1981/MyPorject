import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChangeTracerForGit implements ChangeTracer {

    private static final String REPO_NAME = "framework";
    private static final String REMOTE_REPO_URL = "https://bitbucket.blujaysolutions.com/scm/tmff/" + REPO_NAME + ".git";
    private static final String LOCAL_REPO_URL = "C:\\TMFF\\NEW_REPO\\" + REPO_NAME + "\\.git";
    private static final String BRANCH = "RB_19_1_0";
    private static final String GIT_USERNAME = "liang.zhou";
    private static final String GIT_PASSWORD = "";

    public static void main(String[] args) {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            /*UsernamePasswordCredentialsProvider cp =
                new UsernamePasswordCredentialsProvider(GIT_USERNAME, GIT_PASSWORD);
            Git git = Git.cloneRepository()
                .setURI(REMOTE_REPO_URL)
                .setBranch(BRANCH)
                .setDirectory(new File(LOCAL_REPO_URL))
                .setCredentialsProvider(cp)
                .call();*/

            Repository repository = builder.setGitDir(new File(LOCAL_REPO_URL))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();

            Git git = new Git(repository);
//            Iterable<RevCommit> logs = git.log().addRange(repository.resolve("9c61166ccff83d675fa0c2b97928da2ca4a9c6a8"),
//                repository.resolve("931fd27bb8cca46b995785702e23630fb5d5d12e")).call();
            ObjectId latestCommit = repository.resolve("origin/" + BRANCH + "^{commit}");
            Iterable<RevCommit> logs = git.log().addRange(repository.resolve("ec7f380b13737408a43b8e224552f38c34a57926"), latestCommit).call();
            RevCommit newCommit = null;
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            ObjectReader reader = git.getRepository().newObjectReader();
            for (RevCommit oldCommit : logs) {
                if (newCommit != null) {
                    System.out.println("newCommitId: " + newCommit.toObjectId().getName());
                    for (int i = 0; i < newCommit.getParentCount(); i++) {
                        System.out.println("newCommitParentId: " + newCommit.getParents()[i].toObjectId().getName());
                    }
                    System.out.println("oldCommitId: " + oldCommit.toObjectId().getName());
                    System.out.println("getName: " + newCommit.getAuthorIdent().getName());
                    System.out.println("getWhen: " + newCommit.getAuthorIdent().getWhen());
                    System.out.println("getTimeZone: " + newCommit.getAuthorIdent().getTimeZone());
                    System.out.println("getFullMessage: " + newCommit.getFullMessage());
//                    oldTreeIter.reset(reader, oldCommit.getTree().getId());
//                    newTreeIter.reset(reader, newCommit.getTree().getId());
//                    List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
//                    for (DiffEntry entry : diffs) {
//                        System.out.println("getChangeType: " + entry.getChangeType());
//                        System.out.println("getOldPath: " + entry.getOldPath());
//                        System.out.println("getNewPath: " + entry.getNewPath());
//                    }
                    System.out.println("---------------------------------------------------------");
                }
                newCommit = oldCommit;
            }


            /*Repository repository = builder.setGitDir(new File("C:\\TMFF\\TRUNK_BB\\kff\\Base"))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();
            System.out.println("Having repository: " + repository.getDirectory());

            // the Ref holds an ObjectId for any type of object (tree, commit, blob, tree)
            Ref head = repository.exactRef("refs/heads/master");
            System.out.println("Ref of refs/heads/master: " + head);*/
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

}
