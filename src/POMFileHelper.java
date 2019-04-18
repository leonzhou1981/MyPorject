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

    private static boolean changeFromSVNToGit = false;
    private static String svnKey = "scm:svn:";
    private static String svnPrefix = "http://svn.kewill-asia.com/svn/";
    private static String svnSuffix = "/";
    private static boolean changeFromGitToSVN = true;
    private static String gitKey = "scm:git:";
    private static String gitPrefix = "https://bitbucket.blujaysolutions.com/scm/tmff/";
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
                            boolean hasDeveloperConnection = false;

                            for (int j = 0; j < scmNodeList.getLength(); j++) {
                                Node scmSubNode = scmNodeList.item(j);
                                //Get repo name
                                if ("connection".equals(scmSubNode.getNodeName())) {
                                    String connection = scmSubNode.getFirstChild().getNodeValue().trim();
                                    if (connection != null && connection.startsWith(svnKey + svnPrefix) && connection.endsWith(svnSuffix)) {
                                        repoName = connection.substring(svnKey.length() + svnPrefix.length(), connection.length() - svnSuffix.length());
                                        sb.append("Repo name is: ").append(repoName).append(lineSeparator);
                                    } else if (connection != null && connection.startsWith(gitKey + gitPrefix) && connection.endsWith(gitSuffix)) {
                                        repoName = connection.substring(gitKey.length() + gitPrefix.length(), connection.length() - gitSuffix.length());
                                        sb.append("Repo name is: ").append(repoName).append(lineSeparator);
                                    }
                                }
                                //Check if there is a <developerConnection> node
                                if ("developerConnection".equals(scmSubNode.getNodeName())) {
                                    hasDeveloperConnection = true;
                                }
                            }


                            for (int j = 0; j < scmNodeList.getLength(); j++) {
                                Node scmSubNode = scmNodeList.item(j);
                                if ("connection".equals(scmSubNode.getNodeName())) {
                                    String connection = scmSubNode.getFirstChild().getNodeValue().trim();
                                    if (changeFromSVNToGit) {
                                        if (connection != null && connection.startsWith(svnKey + svnPrefix) && connection.endsWith(svnSuffix) && repoName != null && repoName.length() > 0) {
                                            sb.append("Before Connection is: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                            scmSubNode.getFirstChild().setNodeValue(gitKey + gitPrefix + repoName + gitSuffix);
                                            sb.append("After Connection is: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                        }
                                    }
                                    if (changeFromGitToSVN) {
                                        if (connection != null && connection.startsWith(gitKey + gitPrefix) && connection.endsWith(gitSuffix) && repoName != null && repoName.length() > 0) {
                                            sb.append("Before Connection is: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                            scmSubNode.getFirstChild().setNodeValue(svnKey + svnPrefix + repoName + svnSuffix);
                                            sb.append("After Connection is: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                        }
                                    }
                                }

                                if ("url".equals(scmSubNode.getNodeName())) {
                                    String url = scmSubNode.getFirstChild().getNodeValue().trim();
                                    if (changeFromSVNToGit) {
                                        if (url != null && url.contains(svnPrefix) && url.endsWith(svnSuffix) && repoName != null && repoName.length() > 0) {
                                            sb.append("Before URL is: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                            scmSubNode.getFirstChild().setNodeValue(gitPrefix);
                                            sb.append("After URL is: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                        }
                                    }
                                    if (changeFromGitToSVN) {
                                        if (url != null && url.contains(gitPrefix) && repoName != null && repoName.length() > 0) {
                                            sb.append("Before URL is: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                            scmSubNode.getFirstChild().setNodeValue(svnPrefix + repoName + svnSuffix);
                                            sb.append("After URL is: ").append(scmSubNode.getFirstChild().getNodeValue().trim()).append(lineSeparator);
                                        }
                                    }
                                }

                                if ("developerConnection".equals(scmSubNode.getNodeName())) {
                                    if (changeFromGitToSVN) {
                                        scmSubNode.getParentNode().removeChild(scmSubNode);
                                        sb.append("Node <developerConnection> is removed for repo: " + repoName).append(lineSeparator);
                                    }
                                } else if (!hasDeveloperConnection) {
                                    if (changeFromSVNToGit) {
                                        if (repoName != null && repoName.length() > 0) {
                                            Element developerConneciton = doc.createElement("developerConnection");
                                            developerConneciton.appendChild(doc.createTextNode(gitKey + gitPrefix + repoName + gitSuffix));
                                            scmSubNode.getParentNode().appendChild(developerConneciton);
                                            sb.append("Node <developerConnection> is added for repo: " + repoName).append(lineSeparator);
                                            hasDeveloperConnection = true;
                                        }
                                    }
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
