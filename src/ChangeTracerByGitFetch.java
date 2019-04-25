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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ChangeTracerByGitFetch {


    private static final String REMOTE_REPO_NAME = "kff-Portal";
    private static final String BRANCH = "RB_19_0_0";

    public static void main(String[] args) {
        getChangesPerFetch();
//        getAllJarsPattern();
    }

    private static void getChangesPerFetch() {
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
                        String changeType = entry.getChangeType().name();
                        String oldPath = entry.getOldPath();
                        String newPath = entry.getNewPath();
                        System.out.println("getChangeType: " + changeType);
                        System.out.println("getOldPath: " + oldPath);
                        System.out.println("getNewPath: " + newPath);
                    }
                    //merge after fetch
                    git.merge().include(newObjectId).setMessage("Merge Completed").call();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } finally {

        }
    }

    private static void getAllJarsPattern() {
        final String[] dirs = new String[]{
            "C:\\TMFF\\NEW_REPO",
            "D:\\KFF_HEAD"
        };

        final String[] suffixes = new String[]{
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

        final StringBuffer svnjars = new StringBuffer("|");
        for (int i = 0; i < dirs.length; i++) {
            final int index = i;
            final int[] counter = {0};
            FileUtil.iterateAllFilesUnderOneDirectory(dirs[i], new IFileAction() {
                public void doFileProcess(File file) {
                    try {
                        if (isPOM(file)) {
                            Document doc = XMLUtil.readXMLFile(file);
                            String path = file.getAbsolutePath().substring(dirs[index].length());
                            path = path.substring(0, path.length() - "pom.xml".length());
                            path = path.replace("\\", "/");
                            if (path != null && !(path.startsWith(File.separator + "Deployment" + File.separator)
                                || path.startsWith(File.separator + "kff" + File.separator + "RegressionTest" + File.separator))) {
                                Element root = doc.getDocumentElement();
                                if (root != null) {
                                    String packaging = null;
                                    String artifactId = null;
                                    String groupId = null;
                                    String forwho = null;//for which customer
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
                                                    for (int k = 0; k < suffixes.length; k++) {
                                                        forwho = StringUtil.pickupWord(artifactId, "kewill-kff-", suffixes[k]);
                                                        if (forwho != null) {
                                                            break;
                                                        }
                                                    }
                                                }
                                                if ("groupId".equals(node.getNodeName())) {
                                                    groupId = node.getFirstChild().getNodeValue();
                                                }

                                            }
                                        }
    //                                    if ("jar".equals(packaging) || null == packaging) {
                                        if ("jar".equals(packaging)) {
                                            if (forwho == null || "standard".equals(forwho) || "utilities".equals(forwho) || "product".equals(forwho)) {
                                                if (index == 0) {
                                                    svnjars.append(groupId).append("|").append(artifactId).append("|");

                                                } else {
                                                    if (!svnjars.toString().contains("|" + groupId + "|" + artifactId + "|")) {
                                                        System.out.println(groupId);
                                                        System.out.println(artifactId);
                                                        System.out.println(path);
                                                        System.out.println("---------------------------------------------------------");
                                                    }
                                                }
                                                /*System.out.println(groupId);
                                                System.out.println(artifactId);
                                                System.out.println(path);
                                                System.out.println("---------------------------------------------------------");*/
                                                counter[0]++;
                                            } else {
                                                //customized jars
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (FileNotFoundException e) {

                    } catch (IOException e) {

                    } catch (ParserConfigurationException e) {

                    } catch (SAXException e) {

                    } finally {

                    }
                }
            });
            System.out.println(dirs[i] + " has " + counter[0] + " jars ");
        }

    }

    private static boolean isPOM(File file) {
        return "pom.xml".equals(file.getName());
    }
}
