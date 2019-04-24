import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ChangeTracerByGitFetch {


    private static final String REMOTE_REPO_NAME = "kff-Portal";

    public static void main(String[] args) {
        try {
            Git git = GitUtil.getGit(REMOTE_REPO_NAME, "Portal");

            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            ObjectReader reader = git.getRepository().newObjectReader();

            System.out.println("Starting fetch " + REMOTE_REPO_NAME);
            FetchResult result = git.fetch().setCheckFetchedObjects(true).setCredentialsProvider(GitUtil.getCP()).call();
            System.out.println("Finished fetch: " + REMOTE_REPO_NAME);

            RevWalk revWalk = new RevWalk(git.getRepository());
            Collection<TrackingRefUpdate> updates = result.getTrackingRefUpdates();
            for (TrackingRefUpdate update : updates) {
                ObjectId oldObjectId = update.getOldObjectId();
                ObjectId newObjectId = update.getNewObjectId();

                RevCommit oldCommit = revWalk.parseCommit(oldObjectId);
                revWalk.reset();
                RevCommit newCommit = revWalk.parseCommit(newObjectId);

                oldTreeIter.reset(reader, oldCommit.getTree().getId());
                newTreeIter.reset(reader, newCommit.getTree().getId());
                List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
                for (DiffEntry entry : diffs) {
                    String changeType = entry.getChangeType().name();
                    String oldPath = entry.getOldPath();
                    String newPath = entry.getNewPath();
                    System.out.println("getChangeType: " + changeType);
                    System.out.println("getOldPath: " + oldPath);
                    System.out.println("getNewPath: " + newPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}
