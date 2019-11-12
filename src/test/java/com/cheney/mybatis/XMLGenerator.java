package com.cheney.mybatis;

import com.cheney.entity.AuthUser;
import com.cheney.entity.Role;
import com.cheney.mybatis.chain.AbstractSwitch;
import com.cheney.mybatis.chain.SwitchChain;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class XMLGenerator {

    private final static Class TARGET_ENTITY_CLASS = AuthUser.class;

    public final static String TABLE_NAME = "sys_auth_user";

    public final static String ID_COLUMN = "id";

    public final static String ID = "id";

    //驼峰命名,false为下划线
    public final static boolean HUMP = false;

    public final static Class ID_TYPE = Long.class;

    public final static String END = "Mapper";

    private static String currentKey;

    /**
     * 模板文件位置
     */
    private final static Map<String, String> dataFile;

    /**
     * 生成文件位置
     */
    private final static Map<String, String> generatorFilePackage;

    static {
        dataFile = new HashMap<>();
        dataFile.put("Mapper", "src|test|resources|template|XMLTemplate.data");
//        dataFile.put("Dao", "src|test|resources|template|DaoTemplate.data");
//        dataFile.put("Service", "src|test|resources|template|ServiceTemplate.data");
//        dataFile.put("ServiceImpl", "src|test|resources|template|ServiceImplTemplate.data");

        generatorFilePackage = new HashMap<>();
        generatorFilePackage.put("Mapper", "src|main|resources|mybatis");
        generatorFilePackage.put("Dao", "src|main|java|com|cheney|dao|mybatis");
        generatorFilePackage.put("Service", "src|main|java|com|cheney|service");
        generatorFilePackage.put("ServiceImpl", "src|main|java|com|cheney|service|impl");

        buildSystemPath(dataFile);
        buildSystemPath(generatorFilePackage);
    }

    private static void buildSystemPath(Map<String, String> pathMap) {
        for (Map.Entry<String, String> entry : pathMap.entrySet()) {
            String path = entry.getValue();
            pathMap.put(entry.getKey(), path.replaceAll("\\|", AbstractSwitch.FILE_SEPARATOR));
        }
    }

    public static void main(String[] args) throws IOException {
        generate(TARGET_ENTITY_CLASS);
    }

    private static void generate(Class target) {

        for (String key : dataFile.keySet()) {
            generateFromData(target, key);
        }

    }

    private static void generateFromData(Class target, String key) {
        currentKey = key;
        String dataPath = dataFile.get(key);
        String generatorFilePath = generatorFilePackage.get(key);
        if (dataPath == null || generatorFilePath == null) {
            System.out.println("key" + key + "缺少文件目录");
            return;
        }

        BufferedReader reader = null;
        FileWriter fileWriter = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(dataPath)
                    )
            );
            String t;
            File file;
            //区分文件类型
            if ("Mapper".equalsIgnoreCase(key)) {
                file = new File(generatorFilePath + "\\" + target.getSimpleName() + key + ".xml");
            } else if ("dao".equalsIgnoreCase(key)) {
                file = new File(generatorFilePath + "\\" + target.getSimpleName() + "Mapper" + ".java");
            } else {
                file = new File(generatorFilePath + "\\" + target.getSimpleName() + key + ".java");
            }
            file.createNewFile();
            fileWriter = new FileWriter(file);
            while ((t = reader.readLine()) != null) {
                t = rewrite(t, target);
                fileWriter.write(t);
                fileWriter.write(AbstractSwitch.LINE_BREAK);
                fileWriter.flush();
            }
            System.out.println("生成" + key + "对应文件成功");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (fileWriter != null)
                    fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String rewrite(String t, Class clazz) {
        int start = t.indexOf("@");
        if (start == -1)
            return t;
        else
            return replaceAt(t, clazz);
    }

    private static String replaceAt(String t, Class clazz) {
        return SwitchChain.replaceAll(t, clazz);
    }

    public static Map<String, String> getGeneratorFilePackage() {
        return generatorFilePackage;
    }

    public static String getCurrentKey() {
        return currentKey;
    }
}
