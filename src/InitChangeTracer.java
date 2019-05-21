import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class InitChangeTracer {

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

        //first of all, run the dbscripts/git_db_prepare.sql to set up oracle database
        System.out.println("Please ensure the database is prepared before initial (y/n): ");
        InputStreamReader isReader = new InputStreamReader(System.in);
        try {
            if ("Y".equals(new BufferedReader(isReader).readLine().toUpperCase())) {
                System.out.println("Start to initial Git Change Tracer...");
                if (initChangeTracerDataBase(localProjectRoot, branch)) {
                    System.out.println("Git Change Tracer initial successfully.");
                } else {
                    System.out.println("Git Change Tracer initial failed.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Map<String, Map> dependencies = new HashMap<>();

    private static boolean initChangeTracerDataBase(final String localProjectRoot, String branch) throws IOException {
        Connection dbConnection = DatabaseUtil.getDBConnection();
        if (dbConnection == null) {
            System.out.println("Cannot find database...");
            return false;
        }

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);

        //init gitmaven
        final Map<String, Map> jarsPattern = new HashMap<>();
        final Map<String, Map> poms = new HashMap<>();
        final Map<String, Map> paths = new HashMap<>();
        FileUtil.iterateAllFilesUnderOneDirectory(localProjectRoot, new IFileAction() {
            public void doFileProcess(File file) {
                try {
                    if ("pom.xml".equals(file.getName())) {
                        Document doc = XMLUtil.readXMLFile(file);
                        String path = file.getAbsolutePath().substring(localProjectRoot.length());
                        path = path.substring(0, path.length() - "pom.xml".length());
                        if (path != null && !(
                            path.startsWith(File.separator + "ksb" + File.separator)
                                || path.startsWith(File.separator + "Deployment" + File.separator)
                                || path
                                .startsWith(File.separator + "kff" + File.separator + "RegressionTest" + File.separator)
                                || path.startsWith(
                                File.separator + "platform" + File.separator + "dev" + File.separator + "components"
                                    + File.separator + "Plugins" + File.separator + "Toolkits" + File.separator
                                    + "Signer" + File.separator))) {
                            Element root = doc.getDocumentElement();
                            if (root != null) {
                                String packaging = null;
                                String artifactId = null;
                                String groupId = null;
                                String parentArtifactid = null;
                                List<String> lstModule = new ArrayList<>();
                                Map<String, String> mDependency = new HashMap<>();
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

                                            if ("parent".equals(node.getNodeName())) {
                                                NodeList parentInfos = node.getChildNodes();
                                                if (parentInfos != null && parentInfos.getLength() > 0) {
                                                    for (int j = 0; j < parentInfos.getLength(); j++) {
                                                        Node parentInfo = parentInfos.item(j);
                                                        if (Node.ELEMENT_NODE == parentInfo.getNodeType()) {
                                                            if ("artifactId".equals(parentInfo.getNodeName())) {
                                                                parentArtifactid =
                                                                    parentInfo.getFirstChild().getNodeValue();
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            if ("modules".equals(node.getNodeName())) {
                                                NodeList modules = node.getChildNodes();
                                                if (modules != null && modules.getLength() > 0) {
                                                    for (int j = 0; j < modules.getLength(); j++) {
                                                        Node module = modules.item(j);
                                                        if (Node.ELEMENT_NODE == module.getNodeType()) {
                                                            if ("module".equals(module.getNodeName())) {
                                                                String modulePath =
                                                                    module.getFirstChild().getNodeValue();
                                                                lstModule.add(modulePath);
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            if ("dependencies".equals(node.getNodeName())) {
                                                NodeList dependencies = node.getChildNodes();
                                                if (dependencies != null && dependencies.getLength() > 0) {
                                                    for (int j = 0; j < dependencies.getLength(); j++) {
                                                        Node dependency = dependencies.item(j);
                                                        if (Node.ELEMENT_NODE == dependency.getNodeType()) {
                                                            String dependencyArtifactId = null;
                                                            String dependencyGroupId = null;
                                                            if ("dependency".equals(dependency.getNodeName())) {
                                                                NodeList dependencyInfos = dependency.getChildNodes();
                                                                if (dependencyInfos != null
                                                                    && dependencyInfos.getLength() > 0) {
                                                                    for (
                                                                        int k = 0; k < dependencyInfos.getLength();
                                                                        k++) {
                                                                        Node dependencyInfo = dependencyInfos.item(k);
                                                                        if (Node.ELEMENT_NODE == dependencyInfo
                                                                            .getNodeType()) {
                                                                            if ("artifactId"
                                                                                .equals(dependencyInfo.getNodeName())) {
                                                                                dependencyArtifactId =
                                                                                    dependencyInfo.getFirstChild()
                                                                                        .getNodeValue();
                                                                            }
                                                                            if ("groupId"
                                                                                .equals(dependencyInfo.getNodeName())) {
                                                                                dependencyGroupId =
                                                                                    dependencyInfo.getFirstChild()
                                                                                        .getNodeValue();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            if (dependencyGroupId != null && dependencyGroupId
                                                                .contains(".kewill.")) {
                                                                mDependency
                                                                    .put(dependencyArtifactId, dependencyGroupId);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if ("jar".equals(packaging) || packaging == null) {
                                        Map mvnMap = new HashMap();
                                        mvnMap.put("groupId", groupId);
                                        mvnMap.put("artifactId", artifactId);
                                        mvnMap.put("pattern", path);
                                        jarsPattern.put(path, mvnMap);
                                    }
                                    //for dependency analysis
                                    Map pom = new HashMap();
                                    pom.put("artifactId", artifactId);
                                    pom.put("packaging", packaging);
                                    pom.put("parentArtifactid", parentArtifactid);
                                    pom.put("lstModule", lstModule);
                                    pom.put("mDependency", mDependency);
                                    pom.put("path", path);
                                    poms.put(artifactId, pom);
                                    paths.put(path, pom);
                                }
                            }
                        }
                    }
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    e.printStackTrace();
                }
            }

            public void doDirectoryProcess(File file) {

            }

            public void doProcess(File file) {

            }
        });

        //sort
        for (String artifactId : poms.keySet()) {
            Map<String, String> mDependency = findOneLevelDependencies(artifactId, poms, paths);
            dependencies.put(artifactId, mDependency);
        }

        /*Stack<String> chain = new Stack<>();
        for (String artifactId : dependencies.keySet()) {
            chain.push(artifactId);
            checkCycleDependency(artifactId, chain);
            chain.pop();
        }*/

        if (jarsPattern != null && jarsPattern.keySet().size() > 0) {
            String gitmaven_reset = "delete from gitjarmap where branch = ?";
            List resetParams = new ArrayList();
            resetParams.add(branch);
            DatabaseUtil.executeUpdate(dbConnection, gitmaven_reset, resetParams);

            String mvndependency_reset = "delete from mvndependency where branch = ?";
            List mvndependencyParams = new ArrayList();
            mvndependencyParams.add(branch);
            DatabaseUtil.executeUpdate(dbConnection, mvndependency_reset, mvndependencyParams);

            for (String key : jarsPattern.keySet()) {
                String groupId = (String) jarsPattern.get(key).get("groupId");
                String artifactId = (String) jarsPattern.get(key).get("artifactId");
                String pattern = (String) jarsPattern.get(key).get("pattern");
                String addPatternSQL = "insert into gitjarmap (groupid, artifactid, pattern, branch) values (?,?,?,?)";
                List addPatternParams = new ArrayList();
                if (groupId != null && artifactId != null && pattern != null && branch != null) {
                    addPatternParams.add(groupId);
                    addPatternParams.add(artifactId);
                    addPatternParams.add(pattern);
                    addPatternParams.add(branch);
                    DatabaseUtil.executeUpdate(dbConnection, addPatternSQL, addPatternParams);
                    if (dependencies.get(artifactId) != null) {
                        for (Object dependency : dependencies.get(artifactId).keySet()) {
                            if (dependency != null) {
                                String addDependencySQL = "insert into mvndependency (artifactid, dependency, branch) values (?,?,?)";
                                List addDependencyParams = new ArrayList();
                                addDependencyParams.add(artifactId);
                                addDependencyParams.add(dependency);
                                addDependencyParams.add(branch);
                                DatabaseUtil.executeUpdate(dbConnection, addDependencySQL, addDependencyParams);
                            }
                        }
                    }
                }
            }
        }

        //init gitlog
        final List<File> repos = new ArrayList<>();

        FileUtil.iterateAllFilesUnderOneDirectory(localProjectRoot, new IFileAction() {
            @Override
            public void doFileProcess(File file) {

            }

            public void doDirectoryProcess(File file) {
                if (".git".equals(file.getName())) {
                    repos.add(file);
                }
            }

            public void doProcess(File file) {

            }
        });

        if (repos != null && repos.size() > 0) {
            String gitlogseq_increase = "select gitlogseq.nextVal from dual";
            DatabaseUtil.executeUpdate(dbConnection, gitlogseq_increase, null);
            for (File repo : repos) {
                Repository repository = Git.open(repo).getRepository();
                ObjectId latestCommitId = repository.resolve("origin/" + branch + "^{commit}");
                String addCommitSQL =
                    "insert into gitlog (batchid, reponame, branch, commitid, packdate, packdone) values (gitlogseq.currVal,?,?,?,?,?)";
                List addCommitParams = new ArrayList();
                addCommitParams.add(GitUtil.getRepoNameFromLocalRepoDirectory(repo));
                addCommitParams.add(branch);
                addCommitParams.add(latestCommitId.getName());
                addCommitParams.add(new Timestamp(new Date().getTime()));
                addCommitParams.add(new BigDecimal(1));
                DatabaseUtil.executeUpdate(dbConnection, addCommitSQL, addCommitParams);
            }
        }

        DatabaseUtil.closeDBConnection(dbConnection);
        return true;
    }

    private static Map<String, Map> dependencyTree = new HashMap<>();
    private static Map<String, Map> dependencyAllNodes = new HashMap<>();

    private static void checkCycleDependency(String artifactId, Stack<String> chain) {
        Map<String, Map> mDependency = dependencies.get(artifactId);
        if (mDependency != null && mDependency.size() > 0) {
            for (String dependencyArtifactId : mDependency.keySet()) {
                if (dependencyArtifactId != null) {
                    if (chain.size() > 0 && chain.contains(dependencyArtifactId)) {
                        System.out.println("-----------------------Cycle Dependency Detected-------------------------");
                        StringBuilder sb = new StringBuilder();
                        for (String id: chain) {
                            sb.append(id).append("->");
                        }
                        sb.append(dependencyArtifactId);
                        System.out.println(sb);
                        System.out.println("-------------------------------------------------------------------------");
                    } else {
                        chain.push(dependencyArtifactId);
                        checkCycleDependency(dependencyArtifactId, chain);
                        chain.pop();
                    }
                }
            }
        } else {
            if (!dependencyAllNodes.containsKey(artifactId)) {
                dependencyAllNodes.put(artifactId, null);
                dependencyTree.put(artifactId, new HashMap<String, Map>());
            }
            Map<String, Map> temp = dependencyTree.get(artifactId);
            for (int i = chain.size() - 1; i-- > 0;) {
                String artifactIdInChain = chain.get(i);
                if (temp.containsKey(artifactIdInChain)) {
                    temp = temp.get(artifactIdInChain);
                } else {
                    if (dependencyAllNodes.containsKey(artifactIdInChain)) {
                        System.out.println("-----------------------Detect Multi Dependency---------------------------");
                        StringBuilder sb = new StringBuilder();
                        for (String id: chain) {
                            sb.append(id).append("->");
                        }
                        System.out.println(sb);
                        System.out.println("-------------------------------------------------------------------------");
                    }
                    temp.put(artifactIdInChain, new HashMap<String, Map>());
                }
                dependencyAllNodes.put(artifactIdInChain, null);
            }
        }
    }

    private static Map<String, String> findOneLevelDependencies(
        String artifactId, final Map<String, Map> poms, final Map<String, Map> paths) {
        Map mOneLevelDependency = null;
        Map pom = poms.get(artifactId);
        mOneLevelDependency = (Map) pom.get("mDependency");
        if (mOneLevelDependency == null) {
            mOneLevelDependency = new HashMap();
        }
        String parentArtifactid = (String) pom.get("parentArtifactid");
        while (parentArtifactid != null) {
            Map parentPom = poms.get(parentArtifactid);
            findDependencies(mOneLevelDependency, parentPom);
            if (parentPom.get("lstModule") != null) {
                List lstModule = (List) parentPom.get("lstModule");
                String path = (String) parentPom.get("path");
                if (lstModule != null && lstModule.size() > 0) {
                    for (int i = 0; i < lstModule.size(); i++) {
                        String modulePath = (String) lstModule.get(i);
                        Map modulePom = paths.get(path + StringUtil.replaceAll(modulePath, "/", File.separator) + File.separator);
                        if (!artifactId.equals(modulePom.get("artifactId"))) {
                            mOneLevelDependency.put(modulePom.get("artifactId"), modulePom.get("groupId"));
                        } else {
                            break;
                        }
                    }
                }
            }
            //for next loop
            parentArtifactid = (String) parentPom.get("parentArtifactid");
        }
        return mOneLevelDependency;
    }

    private static void findDependencies(Map mOneLevelDependency, Map pom) {
        Map mDependency = (Map) pom.get("mDependency");
        if (mDependency != null) {
            for (Iterator it = mDependency.keySet().iterator(); it.hasNext(); ) {
                String dependencyArtifactId = (String) it.next();
                String groupId = (String) mDependency.get(dependencyArtifactId);
                mOneLevelDependency.put(dependencyArtifactId, groupId);
            }
        }
    }
}
