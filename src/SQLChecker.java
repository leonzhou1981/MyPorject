import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 16-2-6
 * Time: 上午10:19
 */
public class SQLChecker {

    public static void main(String[] args) {
        List<SQLScriptMeta> sqlScriptMetaListForMain = new ArrayList<SQLScriptMeta>();
        File mainScriptDir = new File("C:\\svn\\Database\\Oracle\\DBUpdate\\2017year");
        iterateDirectoryAndFiles(mainScriptDir, sqlScriptMetaListForMain);

//        List<SQLScriptMeta> sqlScriptMetaListForMain = new ArrayList<SQLScriptMeta>();
//        File mainScriptDir = new File("C:\\Users\\Leon\\Documents\\sqlscript\\main");
//        iterateDirectoryAndFiles(mainScriptDir, sqlScriptMetaListForMain);
//        System.out.println("Main Meta: " + sqlScriptMetaListForMain.size());
//
//        List<SQLScriptMeta> sqlScriptMetaListForRelease = new ArrayList<SQLScriptMeta>();
//        File releaseScriptDir = new File("C:\\Users\\Leon\\Documents\\sqlscript\\release");
//        iterateDirectoryAndFiles(releaseScriptDir, sqlScriptMetaListForRelease);
//        System.out.println("Release Meta: " + sqlScriptMetaListForRelease.size());
//
//        List<SQLScriptMeta> sqlScriptMetaListForSpecial = new ArrayList<SQLScriptMeta>();
//        File specialScriptDir = new File("C:\\Users\\Leon\\Documents\\sqlscript\\special");
//        iterateDirectoryAndFiles(specialScriptDir, sqlScriptMetaListForSpecial);
//        System.out.println("Special Meta: " + sqlScriptMetaListForSpecial.size());

        //check if a release sql is duplicated
        /*for (int i1 = 0; i1 < sqlScriptMetaListForRelease.size(); i1++) {
            SQLScriptMeta sqlScriptMeta1 = sqlScriptMetaListForRelease.get(i1);
            if ("003094".equals(sqlScriptMeta1.getSql_sno())) {
                if (sqlScriptMeta1.getSql_content() != null && sqlScriptMeta1.getSql_content().size() > 0) {
                    List sql_content1 = sqlScriptMeta1.getSql_content();
                    for (int s1 = 0; s1 < sql_content1.size(); s1++) {
                        String sql1 = (String) sql_content1.get(s1);
                        if (sql1 != null && !"".equals(sql1)) {
                            //compare with others
                            for (int i2 = 0; i2 < sqlScriptMetaListForRelease.size(); i2++) {
                                SQLScriptMeta sqlScriptMeta2 = sqlScriptMetaListForRelease.get(i2);
                                if (sqlScriptMeta2.getSql_content() != null && sqlScriptMeta2.getSql_content().size() > 0) {
                                    List sql_content2 = sqlScriptMeta2.getSql_content();
                                    for (int s2 = 0; s2 < sql_content2.size(); s2++) {
                                        String sql2 = (String) sql_content2.get(s2);
                                        if (sql2 != null && !"".equals(sql2) && sql2.equals(sql1)
                                                && sqlScriptMeta1.getSql_sno() != null && sqlScriptMeta2.getSql_sno() != null
                                                && !sqlScriptMeta1.getSql_sno().equals(sqlScriptMeta2.getSql_sno())) {
                                            if ("003094".equals(sqlScriptMeta1.getSql_sno())) {
                                                System.out.println("------------Duplicated SQL is found------------");
                                                System.out.println(sqlScriptMeta1.getSql_date());
                                                System.out.println(sqlScriptMeta1.getSql_sno());
                                                System.out.println(sqlScriptMeta2.getSql_date());
                                                System.out.println(sqlScriptMeta2.getSql_sno());
                                                System.out.println(sql2);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }*/
    }

    private static void iterateDirectoryAndFiles(File scriptDir, List sqlScriptMetaList) {
        if (scriptDir.isDirectory()) {
            File[] subDirectoryAndFiles = scriptDir.listFiles();
            for (int i = 0; i < subDirectoryAndFiles.length; i++) {
                File subDirectoryAndFile = subDirectoryAndFiles[i];
                if (subDirectoryAndFile.isFile() && isSQLScript(subDirectoryAndFile)) {
                    sqlScriptMetaList.addAll(readSQLScript(subDirectoryAndFile));
                    checkNonASCIICharacters(subDirectoryAndFile);
                } else if (subDirectoryAndFile.isDirectory()) {
                    iterateDirectoryAndFiles(subDirectoryAndFile, sqlScriptMetaList);
                }
            }
        }
    }

    private static boolean isSQLScript(File file) {
        return (file != null) && (file.getName().endsWith(".sql")||file.getName().endsWith(".xml"));
    }

    private static void checkNonASCIICharacters(File script) {
        try {
//            checkNonASCIIText(script, "GBK");
            checkNonASCIIText(script, "UTF-8");
        } catch (IOException e) {

        }
    }

    private static void checkNonASCIIText(File script, String encoding) throws IOException {
        System.out.println("Checking by " + encoding);
        FileInputStream fis = new FileInputStream(script);
        InputStreamReader isr = new InputStreamReader(fis, encoding);
        BufferedReader br = new BufferedReader(isr);
        String line;
        int lineNumber = 0;
        int printedLineNumber = 0;
        String printedFileName = "";
        while ((line = br.readLine()) != null) {
            lineNumber++;
            char[] chars = line.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] > 255) {
                    if (!printedFileName.equals(script.getName())) {
                        System.out.println("Script file name is: " + script.getName());
                        printedFileName = script.getName();
                    }
                    if (printedLineNumber != lineNumber) {
                        System.out.println("Line Number is: " + lineNumber);
                        printedLineNumber = lineNumber;
                    }
                    System.out.println("NonASCII Text is found: " + chars[i]);
                }
            }
        }
    }

    private static boolean checkNonASCIICharacters(byte[] bytes) {
        boolean isAnyNonASCII = false;
        try {
            //try GBK
            String gbkText = new String(bytes, "GBK");
            if (findNonASCIICharacters(gbkText)) {
                isAnyNonASCII = true;
            }
            String utf8Text = new String(bytes, "UTF-8");
            if (findNonASCIICharacters(utf8Text)) {
                isAnyNonASCII = true;
            }
        } catch (UnsupportedEncodingException e) {

        }
        return isAnyNonASCII;
    }

    private static boolean findNonASCIICharacters(String text) {
        boolean isAnyNonASCII = false;
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] > 255) {
                isAnyNonASCII = true;
                System.out.println("NonASCII Text is found: " + text.substring(i, i + 20));
            }
        }
        return isAnyNonASCII;
    }

    private static List<SQLScriptMeta> readSQLScript(File script) {
        List<SQLScriptMeta> sqlScriptMetaList = new ArrayList<SQLScriptMeta>();
        try {
            FileReader fileReader = new FileReader(script);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine(); //first line
            if (line != null) {
                line = removeBOMIfAny(line);  //http://blog.163.com/result_2205/blog/static/13981945020102954023564/
            }

            Pattern p_date = Pattern.compile("--[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}");
            Pattern p_sno = Pattern.compile("--00[0-9]{4}");
            Pattern p_author = Pattern.compile("--[a-zA-Z]+\\.[a-zA-Z]+");
            Pattern p_kffnum = Pattern.compile("--[^0-9]+KFF-[0-9]{5}");

            SQLScriptMeta sqlScriptMeta = new SQLScriptMeta();
            StringBuffer sqlStatement = new StringBuffer();
            boolean last_sql_end = false;

            while (null != line) {
                line = line.trim();
                if (p_date.matcher(line).matches() && last_sql_end) {
                    sqlScriptMetaList.add(sqlScriptMeta);
                    //start at a new script
                    sqlScriptMeta = new SQLScriptMeta();
                } else if (isCommitLine(line)) {
                    sqlScriptMetaList.add(sqlScriptMeta);
                }

                if (p_date.matcher(line).matches()) {
                    sqlScriptMeta.setSql_date(line.substring(2));
                } else if (p_sno.matcher(line).matches()) {
                    sqlScriptMeta.setSql_sno(line.substring(2));
                } else if (p_author.matcher(line).matches()) {
                    sqlScriptMeta.setSql_author(line.substring(2));
                } else if (p_kffnum.matcher(line).matches()) {
                    int pos = line.indexOf("KFF-");
                    sqlScriptMeta.setSql_kffnum(line.substring(pos));
                } else if (line.startsWith("--Published") || line.startsWith("--published")) {
                    sqlScriptMeta.setPublished(Boolean.TRUE);
                } else if (!isSQLStatementEnd(line) && !isSQLComment(line) && !"".equals(line)) {
                    //SQL statement is not end
                    last_sql_end = false;
                    sqlStatement.append(line).append(System.getProperty("line.separator"));
                } else if (isSQLStatementEnd(line) && !isSQLComment(line) && !"".equals(line) && !isCommitLine(line)) {
                    //SQL statement is end
                    last_sql_end = true;
                    sqlStatement.append(line);
                    sqlScriptMeta.getSql_content().add(sqlStatement.toString());
                    sqlStatement = new StringBuffer();
                }
                line = bufferedReader.readLine();  //next line
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sqlScriptMetaList;
    }

    private static String removeBOMIfAny(String line) {
        if (line.startsWith("﻿")) {
            line = line.substring(1);
        }
        return line;
    }

    private static boolean isCommitLine(String line) {
        return line != null && (line.contains("commit;") || line.contains("Commit;"));
    }

    private static boolean isSQLStatementEnd(String line) {
        return line != null && line.endsWith(";") && !isCommitLine(line);
    }

    private static boolean isSQLComment(String line) {
        return line != null && line.startsWith("--");
    }

    static class SQLScriptMeta {

        private String sql_date;
        private String sql_sno;
        private String sql_author;
        private String sql_kffnum;
        private Boolean isPublished = Boolean.FALSE;
        private List sql_content = new ArrayList();

        Boolean getPublished() {
            return isPublished;
        }

        void setPublished(Boolean published) {
            isPublished = published;
        }

        String getSql_author() {
            return sql_author;
        }

        void setSql_author(String sql_author) {
            this.sql_author = sql_author;
        }

        List getSql_content() {
            return sql_content;
        }

        void setSql_content(List sql_content) {
            this.sql_content = sql_content;
        }

        String getSql_date() {
            return sql_date;
        }

        void setSql_date(String sql_date) {
            this.sql_date = sql_date;
        }

        String getSql_kffnum() {
            return sql_kffnum;
        }

        void setSql_kffnum(String sql_kffnum) {
            this.sql_kffnum = sql_kffnum;
        }

        String getSql_sno() {
            return sql_sno;
        }

        void setSql_sno(String sql_sno) {
            this.sql_sno = sql_sno;
        }
    }

}
