import java.io.*;
import java.util.concurrent.BlockingQueue;

/**
 * @ClassName ExplainFile
 * @Description TODO
 * @Author Lixuyi
 * @Data 2024/12/30 10:22
 * @Version 1.0
 **/
public class ExplainFile {

    protected static final String endMark = "!0end mark0!";

    /**
     * @desc 逐行获取文件中的sql，暂时不支持存储过程以及函数的创建，如果需要支持，则需要修改while循环里面的判断逻辑，添加begin和end对的匹配
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
            sqlQueue.offer(endMark);
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
