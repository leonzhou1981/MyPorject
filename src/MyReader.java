import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Leon on 2014/3/27.
 */
public class MyReader {

    public static void main(String[] args) {
        JFileChooser fileopen = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("All files", "*.*");
        fileopen.addChoosableFileFilter(filter);

        fileopen.setCurrentDirectory(new File("C:\\Temp"));

        int ret = fileopen.showDialog(null, "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();

            List<String> result = getKeyLines(file);

//            List<String> result = getFilteredLog(file);

//            List<String> result = getOneSessionLog(file);

//            List<String> result = getUnPublishedSQL(file);
            File outputFile = new File("C:\\Temp\\result.txt");
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(outputFile);
                for (int i = 0; i < result.size(); i++) {
                    fileWriter.write(result.get(i));
                }
            } catch (IOException e) {

            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {

                    }
                }
            }
        }
    }

    private static List<String> getKeyLines(File log) {
        List<String> filteredLog = new ArrayList<String>();
        List mapKeys = new ArrayList();
        String filter = " execute time :";
        List<String> lstBuffer = new ArrayList<String>();
        try {
            FileReader fileReader = new FileReader(log);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            lstBuffer.add(line);
            while (null != line) {
                int pos = line.indexOf(filter);
                if (pos > -1) {
                    String piece = line.substring(pos + filter.length());
                    int time = getFirstNumber(piece);
                    if (time > 99) {
                        if (lstBuffer.size() > 1) {
                            int firstPrevLine = -1;
                            for (int i = lstBuffer.size(); i-- > 1;) {
                                String prevLine = lstBuffer.get(i - 1);
                                if (startsWithDateTime(prevLine) || startsWithTimeOnly(prevLine)) {
                                    firstPrevLine = i - 1;
                                    break;
                                }
                            }
                            for (int i = firstPrevLine; i < lstBuffer.size() - 1; i++) {
                                String prevLine = lstBuffer.get(i);
                                mapKeys.add(prevLine);
                            }
                            lstBuffer.clear();
                        }
                        mapKeys.add(line);
                    }
                }
                line = bufferedReader.readLine();
                lstBuffer.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Iterator it = mapKeys.iterator(); it.hasNext();) {
            filteredLog.add((String) it.next());
            filteredLog.add(System.getProperty("line.separator"));
        }
        return filteredLog;

    }

    private static int getFirstNumber(String piece) {
        StringBuilder sb = new StringBuilder();
        if (piece != null && piece.length() > 0) {
            char[] pieces = piece.toCharArray();
            boolean started = false;
            for (int i = 0; i < pieces.length; i++) {
                char oneChar = pieces[i];
                if (oneChar >= 48 && oneChar <= 57) {
                    sb.append(oneChar);
                    started = true;
                } else if (started) {
                    break;
                }
            }
        }
        return Integer.valueOf(sb.toString());
    }

    private static List<String> getOneSessionLog(File log) {
        List<String> filteredLog = new ArrayList<String>();
        String sessionId = "@[64C7E01A-F4B1-7E2C-EF46-28CB015D93F8]@[KFF9HO]";
        try {
            FileReader fileReader = new FileReader(log);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            boolean inPhrase = false;
            while (null != line) {
                if (line.indexOf(sessionId) > -1) {
                    filteredLog.add(line);
                    filteredLog.add(System.getProperty("line.separator"));
                    inPhrase = true;
                } else if (startsWithDateTime(line)){
                    inPhrase = false;
                } else if (inPhrase) {
                    filteredLog.add(line);
                    filteredLog.add(System.getProperty("line.separator"));
                }
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filteredLog;
    }

    private static boolean startsWithDateTime(String line) {
        if (line != null && line.length() > 19) {
            String date = line.substring(0, 10);
            String time = line.substring(11, 19);
            return isDateFormatString(date, "yyyy-MM-dd") && isTimeFormatString(time, "hh:mm:ss");
        } else {
            return false;
        }
    }

    private static boolean startsWithTimeOnly(String line) {
        if (line != null && line.length() > 12) {
            String time = line.substring(0, 12);
            return isTimeFormatString(time, "hh:mm:ss,SSS");
        } else {
            return false;
        }
    }

    private static boolean isTimeFormatString(String time, String pattern) {
        if ("hh:mm:ss".equals(pattern)) {
            try {
                int hour = Integer.valueOf(time.substring(0, 2));
                String fh1 = time.substring(2,3);
                int minute = Integer.valueOf(time.substring(3, 5));
                String fh2 = time.substring(5,6);
                int second = Integer.valueOf(time.substring(6, 8));
                return hour < 24 && minute < 60 && second < 60 && ":".equals(fh1) && ":".equals(fh2);
            } catch (NumberFormatException e) {
                return false;
            }
        } else if ("hh:mm:ss,SSS".equals(pattern)) {
            try {
                int hour = Integer.valueOf(time.substring(0, 2));
                String fh1 = time.substring(2,3);
                int minute = Integer.valueOf(time.substring(3, 5));
                String fh2 = time.substring(5,6);
                int second = Integer.valueOf(time.substring(6, 8));
                String dh1 = time.substring(8,9);
                int ms = Integer.valueOf(time.substring(10, 12));
                return hour < 24 && minute < 60 && second < 60 && ms < 1000 && ":".equals(fh1) && ":".equals(fh2) && ",".equals(dh1);
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean isDateFormatString(String date, String pattern) {
        if ("yyyy-MM-dd".equals(pattern)) {
            try {
                int year = Integer.valueOf(date.substring(0, 4));
                int month = Integer.valueOf(date.substring(5, 7));
                int day = Integer.valueOf(date.substring(8, 10));
                return year < 10000 && month < 13 && day < 31;
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }


    private static List<String> getFilteredLog(File log) {
        List<String> filteredLog = new ArrayList<String>();
        String filter = "@[3036DF72-A97D-82B9-5B99-B54C7B53370E]@[JYAN]";
        String negativeFilter = "2017-07-27";
        try {
            FileReader fileReader = new FileReader(log);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String previousLine3 = "";
            String previousLine2 = "";
            String previousLine1 = "";
            String line = bufferedReader.readLine();
            int counter = 0;
            while (null != line) {
                if (line.indexOf(filter) > -1) {
                    filteredLog.add(line);
                    filteredLog.add(System.getProperty("line.separator"));
                } else if (line.indexOf(negativeFilter) == -1 && previousLine1.indexOf(filter) > -1) {
                    filteredLog.add(line);
                    filteredLog.add(System.getProperty("line.separator"));
                } else if (line.indexOf(negativeFilter) == -1 && previousLine1.indexOf(negativeFilter) == -1
                        && previousLine2.indexOf(filter) > -1) {
                    filteredLog.add(line);
                    filteredLog.add(System.getProperty("line.separator"));
                } else if (line.indexOf(negativeFilter) == -1 && previousLine1.indexOf(negativeFilter) == -1
                        && previousLine2.indexOf(negativeFilter) == -1 && previousLine3.indexOf(filter) > -1) {
                    filteredLog.add(line);
                    filteredLog.add(System.getProperty("line.separator"));
                }
                if (counter == 0) {
                    previousLine1 = line;
                } else if (counter == 1) {
                    previousLine2 = previousLine1;
                    previousLine1 = line;
                } else {
                    previousLine3 = previousLine2;
                    previousLine2 = previousLine1;
                    previousLine1 = line;
                }
                line = bufferedReader.readLine();
                counter++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filteredLog;
    }

    private static List<String> getUnPublishedSQL(File sqlScript) {
        List<String> unPublishedSQL = new ArrayList<String>();
        Pattern patternBegin = Pattern.compile("--\\d{4}/\\d{1,2}/\\d{1,2}");
        Pattern patternEnd = Pattern.compile("commit;");
        String strRelease = "--published to";
        StringBuffer current = new StringBuffer();
        int release = 0;
        try {
            FileReader fileReader = new FileReader(sqlScript);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (null != line) {
                if ((patternBegin.matcher(line).matches() || patternEnd.matcher(line).matches())) {
                    if (release == 0) {
                        if (current.length() > 0) {
                            String copy = current.toString();
                            unPublishedSQL.add(copy);
                        }
                    } else {
                        release = 0;
                    }
                    current = new StringBuffer();
                }
                if (line.toLowerCase().startsWith(strRelease)) {
                    release = 1;
                }
                current.append(line).append(System.getProperty("line.separator"));
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return unPublishedSQL;
    }
}
