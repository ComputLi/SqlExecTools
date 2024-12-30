import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName JdbcJarLoader
 * @Description TODO
 * @Author Lixuyi
 * @Data 2024/12/27 10:45
 * @Version 1.0
 **/
public final class JdbcJarLoader {

    public static URLClassLoader loadJdbcDriver(JSONObject dbInfoJson, String jdbcJarRootPath) {
        Object jarPackage = dbInfoJson.get("jarPackage");
        try {
            if (jarPackage instanceof List) {
                List<String> packageList = (List) jarPackage;
                List<URL> urlLi = new ArrayList<>();
                for (String jarName : packageList) {
                    File jarFile = new File(jdbcJarRootPath + jarName);
                    if (jarFile.exists()) {
                        URL jarUrl = new URL(jarFile.toURI().toURL().toString());
                        urlLi.add(jarUrl);
                    }else
                        throw new RuntimeException("jdbcJarFile is not exists, please set the jar package to this path : " + jdbcJarRootPath);
                }
                return new URLClassLoader(urlLi.toArray(URL[]::new));
            } else {
                File jarFile = new File(jdbcJarRootPath + jarPackage.toString());
                if (jarFile.exists()) {
                    URL[] urls = new URL[]{new URL(jarFile.toURI().toURL().toString())};
                    return new URLClassLoader(urls);
                } else {
                    throw new RuntimeException("jdbcJarFile is not exists, please set the jar package to this path : " + jdbcJarRootPath);
                }
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("load jdbc jar package failed => " + jdbcJarRootPath, e);
        }
    }

}
