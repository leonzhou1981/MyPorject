import java.util.Iterator;
import java.util.TreeMap;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeTracerForGit implements ChangeTracer {

//    private static final String REPO_NAME = "kff";
//    private static final String REMOTE_REPO_URL = "https://bitbucket.blujaysolutions.com/scm/tmff/" + REPO_NAME + ".git";
//    private static final String LOCAL_REPO_URL = "C:\\TMFF\\NEW_REPO\\" + REPO_NAME + "\\.git";
//    private static final String BRANCH = "trunk";
//    private static final String GIT_USERNAME = "liang.zhou";
//    private static final String GIT_PASSWORD = "";
//    private static final String PROJECT_ROOT = "C:\\TMFF\\NEW_REPO"; //need file separator as end

    public static void main(String[] args) {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);

    }




    private static List<File> findLocalRepos(String localProjectDirectory) {
        final List<File> repos = new ArrayList<>();
        if (localProjectDirectory != null) {
            File f_ProjectDirectory = new File(localProjectDirectory);
            if (f_ProjectDirectory.exists()) {
                FileUtil.iterateAllFilesUnderOneDirectory(localProjectDirectory, new IFileAction() {
                    @Override
                    public void doFileProcess(File file) {

                    }

                    @Override
                    public void doDirectoryProcess(File file) {
                        if (".git".equals(file.getName())) {
                            repos.add(file);
                        }
                    }

                    @Override
                    public void doProcess(File file) {

                    }
                });
            }
        }

        return repos;
    }

    private static void getDiffPerCommit(String localProjectDirectory) {
        try {
            Connection dbConnection = DatabaseUtil.getDBConnection();
            if (dbConnection == null) {
                System.out.println("Cannot find database...");
                return;
            }
            List<File> repos = new ArrayList<>();
            if (localProjectDirectory != null) {
                File f_ProjectDirectory = new File(localProjectDirectory);
                if (f_ProjectDirectory.exists()) {
                    repos = findLocalRepos(localProjectDirectory);
                }
            }

            Map changeJars = new HashMap();
            for (File localRepo : repos) {
                if (localRepo.exists()) {
                    FileRepositoryBuilder builder = new FileRepositoryBuilder();
                    Repository repository = builder.setGitDir(localRepo)
                        .readEnvironment() // scan environment GIT_* variables
                        .findGitDir() // scan up the file system tree
                        .build();
                    Git git = new Git(repository);
                    String repo = repository.toString();
                    String branch = repository.getBranch();
                    Map<String, Map> jarsPattern = findJarsPattern(dbConnection, branch);

                    RevWalk revWalk = new RevWalk(repository);
                    CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                    CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                    ObjectReader reader = git.getRepository().newObjectReader();
                    ObjectId latestCommit = git.getRepository().resolve("origin/" + branch + "^{commit}");
                    RevCommit newCommit = revWalk.parseCommit(latestCommit);

                    //get the last pack commit id from database
                    String findLastPackCommitSQL = "select commitid from gitlog where reponame = ? and branch = ? "
                        + "and packdone = 1 order by batchid desc";
                    List findLastPackCommitParams = new ArrayList();
                    findLastPackCommitParams.add(repo);
                    findLastPackCommitParams.add(branch);
                    List lstResult = DatabaseUtil.executeQuery(
                        dbConnection, findLastPackCommitSQL, findLastPackCommitParams);
                    Map firstResult = (Map) lstResult.get(0);
                    String lastPackCommitId = (String) firstResult.get("commitid");
                    ObjectId lastPackCommit = git.getRepository().resolve(lastPackCommitId);
                    RevCommit oldCommit = revWalk.parseCommit(lastPackCommit);

                    //Compare
                    oldTreeIter.reset(reader, oldCommit.getTree().getId());
                    newTreeIter.reset(reader, newCommit.getTree().getId());
                    List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
                    for (DiffEntry entry : diffs) {
                        String oldPath = entry.getOldPath();
                        String newPath = entry.getNewPath();

                        for (Iterator jarPatternIter = jarsPattern.keySet().iterator(); jarPatternIter.hasNext();) {
                            String jarPattern = (String) jarPatternIter.next();
                            if (oldPath.startsWith(jarPattern) || newPath.startsWith(jarPattern)) {
                                changeJars.put(jarPattern, jarsPattern.get(jarPattern));
                            }
                        }
                    }
                }
            }
            //sort change jars according to dependency

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Map> findJarsPattern(Connection dbConnection, String branch) {
        Map<String, Map> jarsPattern = new HashMap<>();
        String findJarsPatternSQL = "select * from gitjarmap where branch = ?";
        List findJarsPatternParams = new ArrayList();
        findJarsPatternParams.add(branch);
        List<Map> lstResult = DatabaseUtil.executeQuery(dbConnection, findJarsPatternSQL, findJarsPatternParams);
        for (Map mResult : lstResult) {
            String pattern = (String) mResult.get("pattern");
            jarsPattern.put(pattern, mResult);
            //find dependency
            String artifactId = (String) mResult.get("artifactid");
            String findDependencySQL = "select * from mvndependency where branch = ? and artifactid = ?";
            List findDependencyParams = new ArrayList();
            findDependencyParams.add(branch);
            findDependencyParams.add(artifactId);
            List<Map> lstDependency = DatabaseUtil.executeQuery(dbConnection, findDependencySQL, findDependencyParams);
            Map mDependency = new HashMap();
            for (Map mSubResult : lstDependency) {
                String dependency = (String) mSubResult.get("dependency");
                mDependency.put(dependency, null);
            }
            mResult.put("dependency", mDependency);
        }

        return jarsPattern;
    }

}
