import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 17-7-5
 * Time: 上午9:27
 * To change this template use File | Settings | File Templates.
 */
public class MyXMLReader {

    public static void main(String[] args) {
        XMLInputFactory factory = XMLInputFactory.newInstance();

        //get Reader connected to XML input from somewhere..
        Reader reader = getXMLReader();

        if (reader != null) {
            try {
                XMLEventReader eventReader = factory.createXMLEventReader(reader);
                while(eventReader.hasNext()){
                    XMLEvent event = eventReader.nextEvent();
                    if(event.getEventType() == XMLStreamConstants.CHARACTERS){
                        Characters characters = event.asCharacters();
                        System.out.println(characters.getData());
                    }
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }

    private static Reader getXMLReader() {
        Reader xmlReader = null;
        File xmlFile = null;
        JFileChooser fileopen = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("XML files", "*.xml");
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showDialog(null, "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            xmlFile = fileopen.getSelectedFile();
            if (xmlFile != null && xmlFile.exists()) {
                try {
                    xmlReader = new FileReader(xmlFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return xmlReader;
    }
}
