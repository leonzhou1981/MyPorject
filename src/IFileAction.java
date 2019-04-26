import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: lzhou
 * Date: Jan 20, 2010
 * Time: 9:28:03 PM
 */
public interface IFileAction {

    void doFileProcess(File file);

    void doDirectoryProcess(File file);

    void doProcess(File file);
}
