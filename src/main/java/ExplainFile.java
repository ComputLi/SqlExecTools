import java.io.*;
import java.util.concurrent.BlockingQueue;

/**
 * @ClassName ExplainFile
 * @Description explain sql script file
 * @Author Lixuyi
 * @Data 2024/12/30 10:22
 * @Version 1.0
 **/
public class ExplainFile {

    protected static final String endMark = "!0end mark0!"; // bolckQueue end mark

    /**
     * @desc read sql script by line, sql mast end with ; ,not support function and procedure create,if want to support this,
     *       change code 27-35 lines,and get begin\end words
     * @param sqlQueue 多线程传输sql
     * @param filePath 文件的读取路径
     * @throws FileNotFoundException
     */
    public void getFileSqlStream(BlockingQueue<String> sqlQueue, String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        try {
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null){
                if (line.trim().endsWith(";")){
                    builder.append(line);
                    sqlQueue.offer(builder.toString());
                    builder = new StringBuilder();
                }else
                    builder.append(line);
            }
            sqlQueue.offer(endMark); // add file finish read mark
            reader.close();
        }catch (IOException e){
            throw new RuntimeException(e);
        }finally {
            try {
                if (reader != null)
                    reader.close();
            }catch (IOException e2){}
        }
    }

}
