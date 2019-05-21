import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
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

    public static void main(String[] args) {
        String localProjectRoot = null;
        String branch = null;
        if (args == null || args.length != 2) {
            System.out.println("Please enter the project directory and the branch.");
            return;
        } else {
            localProjectRoot = args[0];
            branch = args[1];
        }
        getDiffPerCommit(localProjectRoot, branch);
        updateStatus(localProjectRoot,branch);
    }

    private static boolean updateStatus(String localProjectDirectory, String branch) {
        Connection dbConnection = DatabaseUtil.getDBConnection();
        if (dbConnection == null) {
            System.out.println("Cannot find database...");
            return false;
        }

        List<File> repos = findLocalRepos(localProjectDirectory);
        for (File repo : repos) {
            String repoName = GitUtil.getRepoNameFromLocalRepoDirectory(repo);
            String updateStatusSQL = "update gitlog set packdate = sysdate, packdone = 1"
                + " where (reponame, branch, batchid) in "
                + "  (select reponame, branch, max(batchid)"
                + "     from gitlog "
                + "    where reponame = ? and branch = ? and packdate is null and packdone is null"
                + " group by reponame, branch)";
            List updateStatusParams = new ArrayList();
            updateStatusParams.add(repoName);
            updateStatusParams.add(branch);
            DatabaseUtil.executeUpdate(dbConnection, updateStatusSQL, updateStatusParams);
        }
        return true;
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

    private static void getDiffPerCommit(String localProjectDirectory, String branch) {
        Connection dbConnection = null;
        try {
            dbConnection = DatabaseUtil.getDBConnection();
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

            Map<String, Map> changeJars = new HashMap<>();
            Map<String, Map> jarsPattern = null;
            //generate new batch id
            String genBatchIdSQL = "select gitlogseq.nextVal from dual";
            DatabaseUtil.executeQuery(dbConnection, genBatchIdSQL, new ArrayList());
            //search for differences
            for (File localRepo : repos) {
                if (localRepo.exists()) {
                    FileRepositoryBuilder builder = new FileRepositoryBuilder();
                    Repository repository = builder.setGitDir(localRepo)
                        .readEnvironment() // scan environment GIT_* variables
                        .findGitDir() // scan up the file system tree
                        .build();
                    Git git = new Git(repository);
                    String repo = GitUtil.getRepoNameFromLocalRepoDirectory(localRepo);
                    if (jarsPattern == null) {
                        jarsPattern = findJarsPattern(dbConnection, branch);
                    }
                    RevWalk revWalk = new RevWalk(repository);

                    //get the last old commit id from database
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

                    //get the new commit id from git and write it to database
                    CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                    CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                    ObjectReader reader = git.getRepository().newObjectReader();
                    ObjectId latestCommit = git.getRepository().resolve("origin/" + branch + "^{commit}");
                    RevCommit newCommit = revWalk.parseCommit(latestCommit);
                    String addNewCommitSQL = "insert into gitlog (batchid, reponame, branch, commitid) values (gitlogseq.currval,?,?,?)";
                    List addNewCommitParams = new ArrayList();
                    addNewCommitParams.add(repo);
                    addNewCommitParams.add(branch);
                    addNewCommitParams.add(latestCommit.getName());
                    DatabaseUtil.executeUpdate(dbConnection, addNewCommitSQL, addNewCommitParams);

                    //Compare
                    oldTreeIter.reset(reader, oldCommit.getTree().getId());
                    newTreeIter.reset(reader, newCommit.getTree().getId());
                    List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
                    for (DiffEntry entry : diffs) {
                        String oldPath = File.separator + repo + File.separator + entry.getOldPath();
                        oldPath = StringUtil.replaceAll(oldPath, "\\", "/");
                        String newPath = File.separator + repo + File.separator + entry.getNewPath();
                        newPath = StringUtil.replaceAll(newPath, "\\", "/");

                        for (Iterator jarPatternIter = jarsPattern.keySet().iterator(); jarPatternIter.hasNext();) {
                            String jarPattern = (String) jarPatternIter.next();
                            String formattedJarPattern = StringUtil.replaceAll(jarPattern, "\\", "/");
                            if (oldPath.startsWith(formattedJarPattern) || newPath.startsWith(formattedJarPattern)) {
                                changeJars.put(jarPattern, jarsPattern.get(jarPattern));
                            }
                        }
                    }
                }
            }

            LinkedList<String> sortedChangeJars = new LinkedList<>();
            LinkedList<String> unsortedChangeJars = new LinkedList<>();
            Map<String, Map> changeJarsWithArtifactIdAsKey = new HashMap<>();
            if (changeJars != null) {
                for (String key : changeJars.keySet()) {
                    Map jarInfo = changeJars.get(key);
                    String artifactid = (String) jarInfo.get("artifactid");
                    unsortedChangeJars.add(artifactid);
                    changeJarsWithArtifactIdAsKey.put(artifactid, jarInfo);
                }
            }

            //sort change jars according to dependency
//            sortedChangeJars(unsortedChangeJars, sortedChangeJars, changeJarsWithArtifactIdAsKey);

            for (String key : unsortedChangeJars) {//TODO: sort
                Map jarInfo = changeJarsWithArtifactIdAsKey.get(key);
                System.out.println(jarInfo.get("pattern"));
            }
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        } finally {
            if (dbConnection != null) {
                DatabaseUtil.closeDBConnection(dbConnection);
            }
        }
    }

    private static void sortedChangeJars(
        LinkedList<String> unsortedChangeJars, LinkedList<String> sortedChangeJars,
        Map<String, Map> changeJarsWithArtifactIdAsKey) {
        for (String artifactid : unsortedChangeJars) {
            Map jarInfo = changeJarsWithArtifactIdAsKey.get(artifactid);
            Map mDependency = (Map) jarInfo.get("dependency");
            if (mDependency != null && mDependency.size() > 0) {
                int notMatch = 0;
                for (Object dependency : mDependency.keySet()) {
                    if (dependency != null) {
                        int notMatch2 = 0;
                        for (String artifactid2 : unsortedChangeJars) {
                            Map jarInfo2 = changeJarsWithArtifactIdAsKey.get(artifactid2);
                            if (dependency.equals(jarInfo2.get("artifactid"))) {

                            } else {
                                notMatch2++;
                            }
                        }
                        if (notMatch2 == unsortedChangeJars.size()) {
                            notMatch++;
                        }
                    }
                }
                if (notMatch == mDependency.size()) {
                    sortedChangeJars.add(artifactid);
                }
            } else {
                sortedChangeJars.add(artifactid);
            }
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
