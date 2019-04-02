import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class POMFileHelper extends AbstractFileHelper {

    private static boolean changeFromSVNToGit = true;
    private static String svnPrefix = "scm:svn:http://svn.kewill-asia.com/svn/";
    private static String svnSuffux = "/";
    private static boolean changeFromGitToSVN = false;
    private static String gitPrefix = "scm:git:https://bitbucket.blujaysolutions.com/scm/tmff/";
    private static String gitSuffix = ".git";

    @Override
    protected boolean isSpecifiedFormat(File aFile) {
        return aFile != null && aFile.exists() && aFile.getName() != null
                && aFile.getName().toLowerCase().equals("pom.xml");
    }

    @Override
    protected void doSpecifiedThingsForFile(File aFile) {
        StringBuffer sb = new StringBuffer();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(aFile);

            Element root_project = doc.getDocumentElement();
            NodeList nodeList = root_project.getChildNodes();
            if (nodeList != null && nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node projectSubNode = nodeList.item(i);
                    if ("scm".equals(projectSubNode.getNodeName())) {
                        NodeList scmNodeList = projectSubNode.getChildNodes();
                        if (scmNodeList != null && scmNodeList.getLength() > 0) {
                            String repoName = null;
                            String lineSeparator = System.getProperty("line.separator");
                            for (int j = 0; j < scmNodeList.getLength(); j++) {
                                Node scmSubNode = scmNodeList.item(j);
                                if ("connection".equals(scmSubNode.getNodeName())) {
                                    String connection = scmSubNode.getFirstChild().getNodeValue().trim();
                                    if (changeFromSVNToGit) {
                                        if (connection != null && connection.startsWith(svnPrefix) && connection.endsWith(svnSuffux)) {
                                            sb.append("Before Change: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                            repoName = connection.substring(svnPrefix.length(), connection.length() - svnSuffux.length());
                                            sb.append(repoName).append(lineSeparator);
                                            scmSubNode.getFirstChild().setNodeValue(gitPrefix + repoName + gitSuffix);
                                            sb.append("After Change: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                        }
                                    }
                                    if (changeFromGitToSVN) {
                                        if (connection != null && connection.startsWith(gitPrefix) && connection.endsWith(gitSuffix)) {
                                            sb.append("Before Change: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                            repoName = connection.substring(gitPrefix.length(), connection.length() - gitSuffix.length());
                                            sb.append(repoName).append(lineSeparator);
                                            scmSubNode.getFirstChild().setNodeValue(svnPrefix + repoName + svnSuffux);
                                            sb.append("After Change: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                        }
                                    }
                                }
                            }

                            for (int j = 0; j < scmNodeList.getLength(); j++) {
                                Node scmSubNode = scmNodeList.item(j);
                                if ("url".equals(scmSubNode.getNodeName())) {
                                    String url = scmSubNode.getFirstChild().getNodeValue().trim();
                                    if (changeFromSVNToGit) {
                                        if (url != null && url.startsWith(svnPrefix) && url.endsWith(svnSuffux)) {
                                            sb.append("Before Change: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                            repoName = url.substring(svnPrefix.length(), url.length() - svnSuffux.length());
                                            sb.append(repoName).append(lineSeparator);
                                            scmSubNode.getFirstChild().setNodeValue(gitPrefix + repoName + gitSuffix);
                                            sb.append("After Change: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                        }
                                    }
                                    if (changeFromGitToSVN) {
                                        if (url != null && url.startsWith(gitPrefix) && url.endsWith(gitSuffix)) {
                                            sb.append("Before Change: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                            repoName = url.substring(gitPrefix.length(), url.length() - gitSuffix.length());
                                            sb.append(repoName).append(lineSeparator);
                                            scmSubNode.getFirstChild().setNodeValue(svnPrefix + repoName + svnSuffux);
                                            sb.append("After Change: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                        }
                                    }
                                }
                                if ("developerConnection".equals(scmSubNode.getNodeName())) {
                                    sb.append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                    scmSubNode.getParentNode().removeChild(scmSubNode);
                                }
                            }
                        }
                    }
                }
            }

            if (sb.length() > 0) {
                System.out.println("Parse " + aFile.getAbsolutePath());
                System.out.println(sb);
            }


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult outputFile = new StreamResult(aFile);
            transformer.transform(source, outputFile);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println(aFile.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        POMFileHelper pomFileHelper = new POMFileHelper();
        pomFileHelper.iterateDirectoryAndFiles(pomFileHelper.openSelectDir());
        System.exit(0);
    }
}
