import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
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

    private static final String REPO_NAME = "kff";
    private static final String REMOTE_REPO_URL = "https://bitbucket.blujaysolutions.com/scm/tmff/" + REPO_NAME + ".git";
    private static final String LOCAL_REPO_URL = "C:\\TMFF\\NEW_REPO\\" + REPO_NAME + "\\.git";
    private static final String BRANCH = "trunk";
    private static final String GIT_USERNAME = "liang.zhou";
    private static final String GIT_PASSWORD = "";
    private static final String PROJECT_ROOT = "C:\\TMFF\\NEW_REPO"; //need file separator as end


    public static void main(String[] args) {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        try {
            Connection dbConnection = DatabaseUtil.getDBConnection();
            if (dbConnection == null) {
                System.out.println("Cannot find database...");
                return;
            }

            initChangeTracerDataBase(PROJECT_ROOT, BRANCH);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initChangeTracerDataBase(final String localProjectRoot, String branch) throws IOException {
        Connection dbConnection = DatabaseUtil.getDBConnection();
        if (dbConnection == null) {
            System.out.println("Cannot find database...");
            return;
        }

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);

        //init gitmaven
        final Map<String, Map> jarsPattern = new HashMap();
        FileUtil.iterateAllFilesUnderOneDirectory(localProjectRoot, new IFileAction() {
            public void doFileProcess(File file) {
                try {
                    if ("pom.xml".equals(file.getName())) {
                        Document doc = XMLUtil.readXMLFile(file);
                        String path = file.getAbsolutePath().substring(localProjectRoot.length());
                        path = path.substring(0, path.length() - "pom.xml".length());
                        path = path.replace("\\", "/");
                        if (path != null && !(path.startsWith(File.separator + "Deployment" + File.separator)
                            || path.startsWith(File.separator + "kff" + File.separator + "RegressionTest" + File.separator))) {
                            Element root = doc.getDocumentElement();
                            if (root != null) {
                                String packaging = null;
                                String artifactId = null;
                                String groupId = null;
                                NodeList nodes = root.getChildNodes();
                                if (nodes != null && nodes.getLength() > 0) {
                                    for (int i = 0; i < nodes.getLength(); i++) {
                                        Node node = nodes.item(i);
                                        if (Node.ELEMENT_NODE == node.getNodeType()) {
                                            if ("packaging".equals(node.getNodeName())) {
                                                packaging = node.getFirstChild().getNodeValue();
                                            }
                                            if ("artifactId".equals(node.getNodeName())) {
                                                artifactId = node.getFirstChild().getNodeValue();
                                            }
                                            if ("groupId".equals(node.getNodeName())) {
                                                groupId = node.getFirstChild().getNodeValue();
                                            }
                                        }
                                    }
                                    if ("jar".equals(packaging)) {
                                        Map mvnMap = new HashMap();
                                        mvnMap.put("groupId", groupId);
                                        mvnMap.put("artifactId", artifactId);
                                        mvnMap.put("pattern", path);
                                        jarsPattern.put(path, mvnMap);
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doDirectoryProcess(File file) {

            }

            @Override
            public void doProcess(File file) {

            }
        });

        if (jarsPattern != null && jarsPattern.keySet().size() > 0) {
            String gitmaven_reset = "delete from gitmaven where branch = ?";
            List resetParams = new ArrayList();
            resetParams.add(branch);
            DatabaseUtil.executeUpdate(dbConnection,gitmaven_reset, resetParams);
            for (String key : jarsPattern.keySet()) {
                String groupId = (String) jarsPattern.get(key).get("groupId");
                String artifactId = (String) jarsPattern.get(key).get("artifactId");
                String pattern = (String) jarsPattern.get(key).get("pattern");
                String addPatternSQL = "insert into gitmaven (groupid, artifactid, pattern, branch) values (?,?,?,?)";
                List addPatternParams = new ArrayList();
                if (groupId != null && artifactId != null && pattern != null && branch != null) {
                    addPatternParams.add(groupId);
                    addPatternParams.add(artifactId);
                    addPatternParams.add(pattern);
                    addPatternParams.add(branch);
                    DatabaseUtil.executeUpdate(dbConnection, addPatternSQL, addPatternParams);
                } else {
                    System.out.println("groupId: " + groupId);
                    System.out.println("artifactId: " + artifactId);
                    System.out.println("pattern: " + pattern);
                    System.out.println("branch: " + branch);
                }
            }
        }

        //init gitlog
        final List<File> repos = new ArrayList<>();

        FileUtil.iterateAllFilesUnderOneDirectory(localProjectRoot, new IFileAction() {
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

        if (repos != null && repos.size() > 0) {
            String gitlogseq_increase = "select gitlogseq.nextVal from dual";
            DatabaseUtil.executeUpdate(dbConnection, gitlogseq_increase, null);
            for (File repo : repos) {
                Repository repository = Git.open(repo).getRepository();
                ObjectId latestCommitId = repository.resolve("origin/" + branch + "^{commit}");
                String addCommitSQL = "insert into gitlog (batchid, reponame, branch, commitid, packdate, packdone) values (gitlogseq.currVal,?,?,?,?,?)";
                List addCommitParams = new ArrayList();
                addCommitParams.add(repo.getParent().substring(repo.getParent().lastIndexOf(File.separator) + 1));
                addCommitParams.add(BRANCH);
                addCommitParams.add(latestCommitId.getName());
                addCommitParams.add(new Timestamp(new Date().getTime()));
                addCommitParams.add(new BigDecimal(1));
                DatabaseUtil.executeUpdate(dbConnection, addCommitSQL, addCommitParams);
            }
        }
    }

    private static Map<String, Map> findAllJarsPattern(Connection dbConnection, final String projectDirectory, String branch, boolean useCache) {

        final Map<String, Map> jarsPattern = new HashMap();

        if (useCache) {
            String findPatternSQL = "select * from gitmaven where branch = ?";
            List findPatternParams = new ArrayList();
            findPatternParams.add(branch);
            List<Map> lstResult = DatabaseUtil.executeQuery(dbConnection, findPatternSQL, findPatternParams);
            for (Map result : lstResult) {
                jarsPattern.put((String) result.get("pattern"), result);
            }
        } else {

        }

        return jarsPattern;
    }

    private static List<File> findLocalRepos(String projectDirectory, String branch, boolean useCache) throws IOException {
        final List<File> repos = new ArrayList<>();
        List<String> repoNames = new ArrayList<>();

        File cacheFile = new File("conf/repository.txt");
        if (useCache && cacheFile.exists()) {
            repoNames = FileUtil.readTextFile(cacheFile);
            for (String repoName : repoNames) {
                repos.add(new File(repoName));
            }
        } else {

        }

        return repos;
    }

    private static void getDiffPerCommit() {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            Connection dbConnection = DatabaseUtil.getDBConnection();
            if (dbConnection == null) {
                System.out.println("Cannot find database...");
                return;
            }

            Git git = GitUtil.getGit("", "Portal", "trunk");

            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            ObjectReader reader = git.getRepository().newObjectReader();

            /*System.out.println("Starting fetch " + REPO_NAME);
            FetchResult result = git.fetch().setRefSpecs(new RefSpec("refs/heads/" + BRANCH))
                .setForceUpdate(true).setCheckFetchedObjects(true).setCredentialsProvider(cp).call();
//            FetchResult result = git.fetch().setCheckFetchedObjects(true).setCredentialsProvider(cp).call();
            System.out.println("Messages: " + result.getMessages());

            RevWalk revWalk = new RevWalk(repository);
            Collection<TrackingRefUpdate> updates = result.getTrackingRefUpdates();
            for (TrackingRefUpdate update : updates) {
                ObjectId oldObjectId = update.getOldObjectId();
                ObjectId newObjectId = update.getNewObjectId();

                RevCommit oldCommit = revWalk.parseCommit(oldObjectId);
                System.out.println("getFullMessage: " + oldCommit.getFullMessage());
                revWalk.reset();
                RevCommit newCommit = revWalk.parseCommit(newObjectId);
                System.out.println("getFullMessage: " + newCommit.getFullMessage());

                oldTreeIter.reset(reader, oldCommit.getTree().getId());
                newTreeIter.reset(reader, newCommit.getTree().getId());
                List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
                for (DiffEntry entry : diffs) {
                    System.out.println("getChangeType: " + entry.getChangeType().name());
                    String changeType = entry.getChangeType().name();
                    System.out.println("getOldPath: " + entry.getOldPath());
                    String oldPath = entry.getOldPath();
                    System.out.println("getNewPath: " + entry.getNewPath());
                    String newPath = entry.getNewPath();
                }
            }*/


//            Iterable<RevCommit> logs = git.log().addRange(repository.resolve("9c61166ccff83d675fa0c2b97928da2ca4a9c6a8"),
//                repository.resolve("931fd27bb8cca46b995785702e23630fb5d5d12e")).call();
            ObjectId latestCommit = git.getRepository().resolve("origin/" + BRANCH + "^{commit}");
//            Iterable<RevCommit> logs = git.log().addRange(repository.resolve("ec7f380b13737408a43b8e224552f38c34a57926"), latestCommit).call();
            Iterable<RevCommit> logs = git.log().add(git.getRepository().resolve("origin/" + BRANCH)).call();
            RevCommit newCommit = null;
            long count = 0;
            for (RevCommit oldCommit : logs) {
                if (newCommit != null) {
//                    System.out.println("newCommitId: " + newCommit.toObjectId().getName());
                    String commitId = newCommit.toObjectId().getName();
//                    for (int i = 0; i < newCommit.getParentCount(); i++) {
//                        System.out.println("newCommitParentId: " + newCommit.getParents()[i].toObjectId().getName());
//                    }
//                    System.out.println("oldCommitId: " + oldCommit.toObjectId().getName());
                    String previousCommitId = oldCommit.toObjectId().getName();

//                    System.out.println("author: " + newCommit.getAuthorIdent().getName());
                    String author = newCommit.getAuthorIdent().getName();
//                    System.out.println("authDate: " + newCommit.getAuthorIdent().getWhen());
                    Timestamp authDate = new Timestamp(newCommit.getAuthorIdent().getWhen().getTime());
//                    System.out.println("authTimeZone: " + newCommit.getAuthorIdent().getTimeZone());

//                    System.out.println("committer: " + newCommit.getCommitterIdent().getName());
                    String committer = newCommit.getCommitterIdent().getName();
//                    System.out.println("commitDate: " + newCommit.getCommitterIdent().getWhen());
                    Timestamp commitDate = new Timestamp(newCommit.getCommitterIdent().getWhen().getTime());
//                    System.out.println("committerTimeZone: " + newCommit.getCommitterIdent().getTimeZone());

//                    System.out.println("getFullMessage: " + newCommit.getFullMessage());
                    String remark = newCommit.getFullMessage();
                    String bugno = StringUtil.getFirstNumberAsString(remark, 3, 5);

                    String addGitlogSQL = "insert into gitlog (reponame, branch, commitid, previouscommitid, author, authdate, committer, commitdate, bugno, remark)" +
                        " values(?,?,?,?,?,?,?,?,?,?)";
                    List addGitlogParams = new ArrayList();
                    addGitlogParams.add(REPO_NAME);
                    addGitlogParams.add(BRANCH);
                    addGitlogParams.add(commitId);
                    addGitlogParams.add(previousCommitId);
                    addGitlogParams.add(author);
                    addGitlogParams.add(authDate);
                    addGitlogParams.add(committer);
                    addGitlogParams.add(commitDate);
                    addGitlogParams.add(bugno);
                    addGitlogParams.add(remark);

                    DatabaseUtil.executeUpdate(dbConnection, addGitlogSQL, addGitlogParams);
                    count++;
                    if (count % 100 == 0) {
                        System.out.println(count + " logs added.");
                    }

                    oldTreeIter.reset(reader, oldCommit.getTree().getId());
                    newTreeIter.reset(reader, newCommit.getTree().getId());
                    List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
                    for (DiffEntry entry : diffs) {
//                        System.out.println("getChangeType: " + entry.getChangeType().name());
                        String changeType = entry.getChangeType().name();
//                        System.out.println("getOldPath: " + entry.getOldPath());
                        String oldPath = entry.getOldPath();
//                        System.out.println("getNewPath: " + entry.getNewPath());
                        String newPath = entry.getNewPath();

                        String addGitlogdtlSQL = "insert into gitlogdtl (reponame, branch, commitid, changetype, oldpath, newpath)" +
                            " values(?,?,?,?,?,?)";
                        List addGitlogdtlParams = new ArrayList();
                        addGitlogdtlParams.add(REPO_NAME);
                        addGitlogdtlParams.add(BRANCH);
                        addGitlogdtlParams.add(commitId);
                        addGitlogdtlParams.add(changeType);
                        addGitlogdtlParams.add(oldPath);
                        addGitlogdtlParams.add(newPath);

                        DatabaseUtil.executeUpdate(dbConnection, addGitlogdtlSQL, addGitlogdtlParams);
                    }
//                    System.out.println("---------------------------------------------------------");


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
