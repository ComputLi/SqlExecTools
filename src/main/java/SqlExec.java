
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @ClassName SqlExec
 * @Description execute sql
 * @Author Lixuyi
 * @Data 2024/3/1 18:29
 * @Version 1.0
 **/
public class SqlExec {

    public void ExecSql(Connection conn, String sql){
        System.out.println("executing sql => " + sql);
        if (sql.trim().toLowerCase().startsWith("select")){ // select sql
            querySql(conn, sql);
        }else {
            if (sql.trim().toLowerCase().startsWith("desc")){ // desc sql
                showColumnName(conn, sql);
            }else {
                executeSql(conn, sql);
            }
        }
    }

    private void executeSql(Connection conn, String sql){
        try {
            conn.createStatement().execute(sql);
        }catch (SQLException e){
            System.out.println("input data error : " + e);
        }
    }

    private void querySql(Connection conn, String sql){
        Statement stmt = null;
        ResultSet res = null;
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            stmt.setFetchSize(1000);
            res = stmt.executeQuery(sql);
            int columnNum = res.getMetaData().getColumnCount();
            byte printName = 0;
            try { // use format tools to display the data
                ResultSetPrinter.printResultSet(res);
            }catch (Exception e) { // format data failed
                System.out.println("-*-print format error-*-print format error-*-print format error-*-print format error-*-");
                System.out.println("-*-print format error-*-print format error-*-print format error-*-print format error-*-");
                System.out.println("-*-print format error-*-print format error-*-print format error-*-print format error-*-");
                System.out.println("-*-print format error-*-print format error-*-print format error-*-print format error-*-");
                System.out.println("-----using result without format type-----");
                System.out.println();
                res.beforeFirst();
                while (res.next()) {
                    if (printName == 0) {
                        for (int i = 1; i <= columnNum; i++) {
                            System.out.print(" | " + res.getMetaData().getColumnName(i));
                        }
                        System.out.println(" |");
                        System.out.print("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
                        printName++;
                    }
                    System.out.println();
                    for (int i = 1; i <= columnNum; i++) {
                        System.out.print(" | " + res.getString(i));
                    }
                    System.out.print(" | ");
                }
            }
        }catch (SQLException e){
            System.out.println("----exec sql error---- : " + e.getMessage());
            if (e.getNextException() != null)
                System.out.println(" NextException Message : " + e.getNextException().getMessage());
        }finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (res != null)
                    res.close();
            }catch (SQLException e){}
        }
    }

    private void showColumnName(Connection conn, String sql){
        ResultSet res = null;
        sql = "select * from " + sql.replaceFirst("desc", "") + " where 1=0";
        try {
            res = conn.createStatement().executeQuery(sql);
            int columnNum = res.getMetaData().getColumnCount();
            for (int i=1; i<=columnNum; i++){
                System.out.println(res.getMetaData().getColumnName(i));
            }
        }catch (SQLException e){
            System.out.println("exec sql desc error : " + e);
        }finally {
            if (res != null){
                try {
                    res.close();
                }catch (SQLException e){
                }
            }
        }
    }

}
