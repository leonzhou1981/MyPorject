import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

public class MyExcelReader extends MyReader {

    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel files", "*.xls"));
        int fileForRead = fileChooser.showDialog(null, "Open file");

        if (fileForRead == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileInputStream fis = new FileInputStream(file);
                HSSFWorkbook workbook = new HSSFWorkbook(fis);
                fis.close();

                HSSFSheet sheet = workbook.getSheetAt(0); //existing sheet
                HSSFSheet updateSQL_sheet = workbook.createSheet("Update SQL");
                HSSFRow updateSQL_firstRow = updateSQL_sheet.createRow(0);
                HSSFCell updateSQLTitle = updateSQL_firstRow.createCell(0, CellType.STRING);
                updateSQLTitle.setCellValue("Update SQL");

                HSSFSheet rollbackSQL_sheet = workbook.createSheet("Rollback SQL");
                HSSFRow rollbackSQL_firstRow = rollbackSQL_sheet.createRow(0);
                HSSFCell rollbackSQLTitle = rollbackSQL_firstRow.createCell(0, CellType.STRING);
                rollbackSQLTitle.setCellValue("Rollback SQL");

                for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                    HSSFRow row = sheet.getRow(i);
                    Long source_unid = new BigDecimal(row.getCell(0).getNumericCellValue()).longValue();
                    String sourcetype = row.getCell(1).getStringCellValue();
                    Integer sno = new BigDecimal(row.getCell(2).getNumericCellValue()).intValue();
                    String postdate = row.getCell(3).getStringCellValue();
                    String postby = row.getCell(4).getStringCellValue();
                    String batchno = row.getCell(5).getStringCellValue();
                    String ivpostdate = row.getCell(6).getStringCellValue();
                    String ivpostby = row.getCell(7).getStringCellValue();
                    String ivbatchno = row.getCell(8).getStringCellValue();

                    HSSFRow updateSQL_row = updateSQL_sheet.createRow(i);
                    HSSFCell updateSQL = updateSQL_row.createCell(0, CellType.STRING);
                    updateSQL.setCellValue("update revenue c set c.batchno = '" + ivbatchno + "', c.postby = '" + ivpostby + "', c.postdate = to_date('" + ivpostdate + "', 'YYYY-MM-DD HH24:MI:SS') where c.source_unid = " + source_unid + " and c.sourcetype = '" + sourcetype + "' and c.sno = " + sno + ";");

                    HSSFRow rollbackSQL_row = rollbackSQL_sheet.createRow(i);
                    HSSFCell rollbackSQL = rollbackSQL_row.createCell(0, CellType.STRING);
                    rollbackSQL.setCellValue("update revenue c set c.batchno = '" + batchno + "', c.postby = '" + postby + "', c.postdate = to_date('" + postdate + "', 'YYYY-MM-DD HH24:MI:SS') where c.source_unid = " + source_unid + " and c.sourcetype = '" + sourcetype + "' and c.sno = " + sno + ";");
                }

                FileOutputStream fos = new FileOutputStream(file);
                workbook.write(fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
