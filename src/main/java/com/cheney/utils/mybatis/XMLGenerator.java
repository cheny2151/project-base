package com.cheney.utils.mybatis;

import com.cheney.entity.dto.Admin;
import com.cheney.utils.mybatis.chain.SwitchChain;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

public class XMLGenerator {

    private String tableName;

    private final static String end = "Mapper.xml";

    private final static String generatePath = "/mybatis/template/";

    public final static String ID_COLUMN = "id";

    public final static Class ID_TYPE = long.class;

    public final static String TABLE_NAME = "m_admin";

    //驼峰命名,false为下划线
    public final static boolean HUMP = false;

    public static void main(String[] args) throws IOException {
        generate(Admin.class);
    }

    private static void generate(Class target) {

        BufferedReader reader = null;
        FileWriter fileWriter = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new ClassPathResource("/mybatis/template/Template.xml").getInputStream()
                    )
            );
            String t;
//        File file = new File(generatePath + target.getSimpleName() + end);
            String generatePath = "C:\\Users\\47824\\IdeaProjects\\project-base\\src\\main\\resources\\mybatis\\";
            File file = new File(generatePath + target.getSimpleName() + end);
            file.createNewFile();
            fileWriter = new FileWriter(file);
            while ((t = reader.readLine()) != null) {
                t = rewrite(t, target);
                fileWriter.write(t);
                fileWriter.write(System.getProperty("line.separator"));
                fileWriter.flush();
            }
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


}
