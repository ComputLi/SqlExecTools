
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;

import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @ClassName ExecTools
 * @Description main class input parameter control
 * @Author Lixuyi
 * @Data 2022/3/1 17:58
 * @Version 1.0
 **/
public class ExecTools {

    public static void main(String[] args) throws Exception {
        JsonParameterLoader jsonLoader = new JsonParameterLoader();
        System.out.println("**** please input database type, if database not exists in the list, try to add it into Configuration.json ****");
        String dbList = jsonLoader.getDBList();
        System.out.println(dbList);
        System.out.print("choose database type : ");
        Scanner scanner = new Scanner(System.in);
        String dbType = scanner.nextLine();
        if (!dbList.contains(dbType))
            throw new RuntimeException("not support this database => " + dbType);
        JSONObject dbJson = jsonLoader.getDBJson(dbType); // load this database jdbc parameter from Configuration.json
        List parameterNames = dbJson.getObject("parameterName", List.class);
        String jarRootPath = jsonLoader.getJdbcJarRootPath();
        URLClassLoader loader = JdbcJarLoader.loadJdbcDriver(dbJson, jarRootPath); // load jdbc jar package from Configuration jarPath
        Class<?> jdbcDriver = Class.forName(dbJson.getString("jdbcDriver"), true, loader);
        List<String> parameterLi = new ArrayList<>();
        for (Object name : parameterNames){ // get jdbc url format parameter
            System.out.print("please input database " + name +": ");
            parameterLi.add(scanner.nextLine());
        }
        String url = dbJson.getString("url");
        String formatUrl = MessageFormat.format(url, parameterLi.toArray()); // build jdbc url
        System.out.println("full url is => " + formatUrl);
        Connection connection = null;
        for (int i=3; i>0; i--) { // invalid password or username, try three times
            System.out.print("please input username : ");
            String username = scanner.nextLine();
            System.out.print("please input password : ");
            String password = scanner.nextLine();
            Properties info = new Properties();
            info.put("user", username);
            info.put("password", password);
            try {
                connection = ((Driver) jdbcDriver.getDeclaredConstructor().newInstance()).connect(formatUrl, info);
                break;
            } catch (SQLException e) {
                String message = e.getMessage();
                if (message.toLowerCase().contains("user") || message.contains("password")) {
                    System.out.println("you left " + i +" chance to have a try...");
                    System.out.println(message);
                    continue;
                }else
                    throw e;
            }
        }
        SqlExec sqlExec = new SqlExec();
        StringBuilder sql = new StringBuilder(); // using builder to combine more than one line sql, all sql mast be end with ;
        while (true){
            if (sql.length() == 0)
                System.out.print("please input sql, sql must end with [;], use @[filePath] to execute script, input exit to end this program : \n");
            String input = scanner.nextLine();
            if (input.toLowerCase().trim().startsWith("exit") && sql.length() == 0){ // exit this program
                connection.close();
                break;
            }else if (input.trim().startsWith("@")) { // execute sql scripts
                BlockingQueue<String> queue = new ArrayBlockingQueue(10); // use blockQueue and FuTure to load sql scripts,valid big file out of memory
                Thread readFileThread = new MyFuture(queue, input.substring(1));
                readFileThread.start();
                String readSql;
                while (!(readSql = queue.poll()).equals(ExplainFile.endMark)){
                    sqlExec.ExecSql(connection, readSql.substring(0, readSql.length()-1)); // jdbc execute sql can't end with ; delete ;
                }
            }else {
                sql.append(input);
                if (input.trim().endsWith(";")) {
                    sqlExec.ExecSql(connection, sql.substring(0, sql.length()-1));
                    sql = new StringBuilder(); // re initial builder
                    System.out.println();
                }
            }
        }
    }

    /**
     * @desc new Thread to read sql scripts file
     */
    static class MyFuture extends Thread{
        String filePath;
        BlockingQueue<String> sqlQueue;

        public MyFuture(BlockingQueue<String> sqlQueue, String filePath){
            this.filePath = filePath;
            this.sqlQueue = sqlQueue;
        }

        @SneakyThrows
        @Override
        public void run(){
            ExplainFile explain = new ExplainFile();
            explain.getFileSqlStream(sqlQueue, filePath);
        }
    }

}
