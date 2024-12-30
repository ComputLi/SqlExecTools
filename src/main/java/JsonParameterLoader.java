import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @ClassName JsonParameterLoader
 * @Description load adn get information form Configuration.json
 * @Author Lixuyi
 * @Data 2024/12/26 17:13
 * @Version 1.0
 **/
public class JsonParameterLoader {

    private static String localJsonFile = "Configuration.json";
    private Charset characterSet = Charset.forName("UTF-8");

    public String getDBList() {
        JSONObject jsonObject = getJsonObject();
        List jdbcInfo = jsonObject.getObject("jdbcInfo", List.class);
        StringBuilder result = new StringBuilder();
        for (Object info : jdbcInfo) {
            if (result.length() != 0)
                result.append(" , ");
            result.append(((JSONObject) info).getString("dbType"));
        }
        return result.toString();
    }

    public String getJarRootPath() {
        JSONObject jsonObject = getJsonObject();
        String jarPath = jsonObject.getString("jarPath");
        if (jarPath == null || jarPath.length() == 0)
            return getJarAbsolutePath();
        else
            return jarPath;
    }

    public String getJdbcJarRootPath() {
        String jarPath = getJarRootPath();
        if (jarPath.startsWith("/")) // linux system
            jarPath = jarPath + "/";
        else // windows系统
            jarPath = jarPath + "\\";
        return jarPath;
    }

    public JSONObject getDBJson(String dbName) {
        JSONObject jsonObject = getJsonObject();
        List jdbcInfo = jsonObject.getObject("jdbcInfo", List.class);
        for (Object info : jdbcInfo) {
            if (((JSONObject) info).getString("dbType").equals(dbName))
                return (JSONObject) info;
        }
        return null;
    }

    private JSONObject getJsonObject() {
        JSONObject jsonObject = JSONObject.parseObject(getJsonConfig(), JSONObject.class);
        return jsonObject;
    }

    private String getJsonConfig() {
        String jarPath = getJarAbsolutePath();
        File configFile;
        if (jarPath.startsWith("/")) // linux system
            configFile = new File(jarPath + "/" + "Configuration.json");
        else
            configFile = new File(jarPath + "\\" + "Configuration.json");
        if (!configFile.exists())
            return getLocalJsonFile();
        else
            return getServerJsonFile(configFile.toString());
    }

    private String getJarAbsolutePath() {
        File jarFile = new File("");
        String jarPath = jarFile.getAbsolutePath();
        return jarPath;
    }

    private String getLocalJsonFile() {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(localJsonFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(input, characterSet));
        StringBuffer buffer = new StringBuffer();
        String line;
        try {
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("initial local Configuration json file error", e);
        }
        return buffer.toString();
    }

    private String getServerJsonFile(String fileWithPath) {
        try {
            File jsonFile = new File(fileWithPath);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer str = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                str.append((char) ch);
            }
            fileReader.close();
            reader.close();
            return str.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
