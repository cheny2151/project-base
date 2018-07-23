package com.cheney.utils.mybatis;

import com.cheney.entity.dto.Admin;
import com.cheney.utils.mybatis.chain.SwitchChain;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

public class XMLGenerator {

    private String tableName;

    private final static String end = "Mapper.xml";

    private final static Class IDType = long.class;

    private final static String generatePath = "/mybatis/template/";

    public static void main(String[] args) throws IOException {
        generate(Admin.class);
    }

    private static void generate(Class target) throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource("/mybatis/template/Template.xml").getInputStream()
                )
        );
        String t;
        File file = new File(generatePath + target.getSimpleName() + end);
        file.createNewFile();
        FileWriter fileWriter = new FileWriter();
        while ((t = reader.readLine()) != null) {
            t = rewrite(t, target.getCanonicalName());
            fileWriter.write(t);
        }
    }

    private static String rewrite(String t, String fullPath) {
        int start = t.indexOf("@");
        if (start == -1)
            return t;
        else
            return replaceAt(t, fullPath);
    }

    private static String replaceAt(String t, String fullPath) {
        return SwitchChain.replaceAll(t, fullPath);
    }


}
