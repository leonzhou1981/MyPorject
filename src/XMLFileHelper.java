import java.io.File;

public class XMLFileHelper extends AbstractFileHelper {

    @Override
    protected boolean isSpecifiedFormat(File aFile) {
        return aFile != null && aFile.exists() && aFile.getName() != null
                && aFile.getName().toLowerCase().endsWith(".xml");
    }

    @Override
    protected void doSpecifiedThingsForFile(File aFile) {

    }
}
