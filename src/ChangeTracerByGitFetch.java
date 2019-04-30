import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChangeTracerByGitFetch {


    private static final String REMOTE_REPO_NAME = "kff-Portal";
    private static final String BRANCH = "RB_19_0_0";

    public static void main(String[] args) {
        findAllJarsPattern("C:\\TMFF\\NEW_REPO", true);
    }

    private static Map getChangesPerFetch(Map<String, Map> jarsPattern) {
        Map changeJars = new HashMap();
        Git git = GitUtil.getGit(REMOTE_REPO_NAME, "Portal", BRANCH);
        try {
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            ObjectReader reader = git.getRepository().newObjectReader();

            System.out.println("Starting fetch " + REMOTE_REPO_NAME);
            FetchResult result = git.fetch().setCheckFetchedObjects(true).setCredentialsProvider(GitUtil.getCP()).call();
            System.out.println("Finished fetch: " + REMOTE_REPO_NAME);

            RevWalk revWalk = new RevWalk(git.getRepository());
            Collection<TrackingRefUpdate> updates = result.getTrackingRefUpdates();
            for (TrackingRefUpdate update : updates) {
                if (update.getRemoteName().contains(BRANCH)) {
                    ObjectId oldObjectId = update.getOldObjectId();
                    ObjectId newObjectId = update.getNewObjectId();

                    RevCommit oldCommit = revWalk.parseCommit(oldObjectId);
                    revWalk.reset();
                    RevCommit newCommit = revWalk.parseCommit(newObjectId);

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
                    //merge after fetch
                    git.merge().include(newObjectId).setMessage("Merge Completed").call();
                }
            }
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }

        return changeJars;
    }

    private static Map findAllJarsPattern(final String projectDirectory, boolean useJarsPatternCache) {

        final String[] customizedJarSuffixes = new String[]{
            "-form-business",
            "-form-common",
            "-form-desktop",
            "-interface-business",
            "-interface-common",
            "-interface-desktop",
            "-report-business",
            "-report-common",
            "-report-desktop"
        };

        final Map<String, Map> jarsPattern = new HashMap();

        FileUtil.iterateAllFilesUnderOneDirectory(projectDirectory, new IFileAction() {
            public void doFileProcess(File file) {
                try {
                    if ("pom.xml".equals(file.getName())) {
                        Document doc = XMLUtil.readXMLFile(file);
                        String path = file.getAbsolutePath().substring(projectDirectory.length());
                        path = path.substring(0, path.length() - "pom.xml".length());
                        path = path.replace("\\", "/");
                        if (path != null && !(path.startsWith(File.separator + "Deployment" + File.separator)
                            || path.startsWith(File.separator + "kff" + File.separator + "RegressionTest" + File.separator)
                            || path.startsWith(File.separator + "platform" + File.separator + "dev" + File.separator + "components" + File.separator + "Plugins" + File.separator + "Toolkits" + File.separator + "Signer" + File.separator))) {
                            Element root = doc.getDocumentElement();
                            if (root != null) {
                                String packaging = null;
                                String artifactId = null;
                                String groupId = null;
                                String forwho = null;//for which customer
                                String parentArtifactId = null;
                                String parentGroupId = null;
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
                                                for (int k = 0; k < customizedJarSuffixes.length; k++) {
                                                    forwho = StringUtil.pickupWord(artifactId, "kewill-kff-", customizedJarSuffixes[k]);
                                                    if (forwho != null) {
                                                        break;
                                                    }
                                                }
                                            }
                                            if ("groupId".equals(node.getNodeName())) {
                                                groupId = node.getFirstChild().getNodeValue();
                                            }
                                            if ("parent".equals(node.getNodeName())) {
                                                NodeList parentSubNodes = node.getChildNodes();
                                                if (parentSubNodes != null && parentSubNodes.getLength() > 0) {
                                                    for (int j = 0; j < parentSubNodes.getLength(); j++) {
                                                        Node parentSubNode = parentSubNodes.item(j);


                                                    }
                                                }
                                            }

                                        }
                                    }
                                    if ("jar".equals(packaging) || packaging == null) {
                                        Map mvnMap = new HashMap();
                                        mvnMap.put("groupId", groupId);
                                        mvnMap.put("artifactId", artifactId);
                                        mvnMap.put("path", path);
                                        jarsPattern.put(path, mvnMap);
                                        if (forwho == null || "standard".equals(forwho) || "utilities".equals(forwho) || "product".equals(forwho)) {
                                            //standard jars
                                        } else {
                                            //customized jars
                                        }
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

        Connection dbConnection = DatabaseUtil.getDBConnection();
        if (dbConnection == null) {
            System.out.println("Cannot find database...");
            return null;
        }

        if (jarsPattern != null && jarsPattern.keySet().size() > 0) {
            String gitmaven_reset = "delete from gitjarmap where branch = ?";
            List resetParams = new ArrayList();
            resetParams.add("trunk");
            DatabaseUtil.executeUpdate(dbConnection,gitmaven_reset, resetParams);
            for (String key : jarsPattern.keySet()) {
                String groupId = (String) jarsPattern.get(key).get("groupId");
                String artifactId = (String) jarsPattern.get(key).get("artifactId");
                String pattern = (String) jarsPattern.get(key).get("pattern");
                String addPatternSQL = "insert into gitjarmap (groupid, artifactid, pattern, branch) values (?,?,?,?)";
                List addPatternParams = new ArrayList();
                if (groupId != null && artifactId != null && pattern != null) {
                    addPatternParams.add(groupId);
                    addPatternParams.add(artifactId);
                    addPatternParams.add(pattern);
                    addPatternParams.add("trunk");
                    DatabaseUtil.executeUpdate(dbConnection, addPatternSQL, addPatternParams);
                }
            }
        }

        return jarsPattern;
    }


    private static boolean isPOM(File file) {
        return "pom.xml".equals(file.getName());
    }
}
