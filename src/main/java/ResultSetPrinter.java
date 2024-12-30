import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @ClassName ResultSetPrinter
 * @Description output select sql result
 * @Author Lixuyi
 * @Data 2022/3/2 14:08
 * @Version 1.0
 **/
public class ResultSetPrinter {

    public static void printResultSet(ResultSet rs) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        // get max lines
        int ColumnCount = resultSetMetaData.getColumnCount();
        // sava max array length
        int[] columnMaxLengths = new int[ColumnCount];
        // cache result, using array list to save the data order
        ArrayList<String[]> results = new ArrayList<>();
        while (rs.next()) {
            // save all column for row data
            String[] columnStr = new String[ColumnCount];
            for (int i = 0; i < ColumnCount; i++) {
                // get column data
                columnStr[i] = rs.getString(i + 1);
                // compute max length for data
                columnMaxLengths[i] = Math.max(columnMaxLengths[i], (columnStr[i] == null) ? 0 : columnStr[i].length());
            }
            // cache this data
            results.add(columnStr);
            if (results.size() >= 1000) { // if the data rows more than 1000,print this data,and input y for continue
                outputResult(ColumnCount, columnMaxLengths, resultSetMetaData, results);
                results.clear();
                boolean continueKey = false;
                boolean stop = true;
                while (stop) {
                    System.out.println("continue display the data ? y/n");
                    String key = scanner.nextLine();
                    if (key.equalsIgnoreCase("y")){
                        stop = false;
                        continueKey = true;
                    }else if (key.equalsIgnoreCase("n")) {
                        stop = false;
                        continueKey = false;
                    }
                }
                if (!continueKey)
                    break;
            }
        }
        if (!results.isEmpty())
            outputResult(ColumnCount, columnMaxLengths, resultSetMetaData, results);
    }

    private static void outputResult(int ColumnCount, int[] columnMaxLengths, ResultSetMetaData resultSetMetaData, ArrayList<String[]> results) throws SQLException{
        printSeparator(columnMaxLengths);
        printColumnName(resultSetMetaData, columnMaxLengths);
        printSeparator(columnMaxLengths);
        // out put ResultSet
        Iterator<String[]> iterator = results.iterator();
        String[] columnStr;
        while (iterator.hasNext()) {
            columnStr = iterator.next();
            for (int i = 0; i < ColumnCount; i++) {
                System.out.printf("|%-" + columnMaxLengths[i] + "s", columnStr[i]);
            }
            System.out.println("|");
        }
        printSeparator(columnMaxLengths);
    }

    /**
     * @desc output column names for a table
     * @param resultSetMetaData jdbc result set select * from table where 1=0
     * @param columnMaxLengths  column max length
     * @throws SQLException
     */
    private static void printColumnName(ResultSetMetaData resultSetMetaData, int[] columnMaxLengths) throws SQLException {
        int columnCount = resultSetMetaData.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            columnMaxLengths[i] = columnMaxLengths[i] <4 ? 4 : columnMaxLengths[i];
            System.out.printf("|%-" + columnMaxLengths[i] + "s", resultSetMetaData.getColumnName(i + 1));
        }
        System.out.println("|");
    }

    /**
     * 输出分隔符.
     *
     * @param columnMaxLengths save max length for the column
     */
    private static void printSeparator(int[] columnMaxLengths) {
        for (int i = 0; i < columnMaxLengths.length; i++) {
            System.out.print("+");
            for (int j = 0; j < columnMaxLengths[i]; j++) {
                System.out.print("-");
            }
        }
        System.out.println("+");
    }
}
