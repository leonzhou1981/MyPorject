import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class POMFileHelper extends AbstractFileHelper {
    @Override
    protected boolean isSpecifiedFormat(File aFile) {
        return aFile != null && aFile.exists() && aFile.getName() != null
                && aFile.getName().toLowerCase().equals("pom.xml");
    }

    @Override
    protected void doSpecifiedThingsForFile(File aFile) {
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
                            for (int j = 0; j < scmNodeList.getLength(); j++) {
                                Node scmSubNode = scmNodeList.item(j);
                                if ("url".equals(scmSubNode.getNodeName())) {
                                    System.out.println(scmSubNode.getFirstChild().getNodeValue());
                                }
                                if ("connection".equals(scmSubNode.getNodeName())) {
                                    System.out.println(scmSubNode.getFirstChild().getNodeValue());
                                }
                                if ("developerConnection".equals(scmSubNode.getNodeName())) {
                                    System.out.println(scmSubNode.getFirstChild().getNodeValue());
                                }
                            }
                        }
                    }
                }
            }

//            Element element_Connection = doc.getElementById("connection");
//            System.out.println(element_Connection.getNodeValue());
//
//            Element element_DeveloperConnection = doc.getElementById("developerConnection");
//            System.out.println(element_DeveloperConnection.getNodeValue());

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        POMFileHelper pomFileHelper = new POMFileHelper();
        pomFileHelper.iterateDirectoryAndFiles(pomFileHelper.openSelectDir());
    }
}
