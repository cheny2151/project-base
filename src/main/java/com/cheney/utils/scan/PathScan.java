package com.cheney.utils.scan;

import com.cheney.utils.scan.filter.ScanFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描包下得所有class
 *
 * @author cheney
 * @date 2019/6/26
 */
@Slf4j
public class PathScan {

    private final static String CLASS_EXTENSION = ".class";

    public static final String TARGET_CLASSES = "/target/classes/";

    // .class长度
    public static final int CLASS_END_LEN = 6;

    // 空路径
    private final static String EMPTY_PATH = "";

    // 分隔符
    public static final String SEPARATE_CHARACTER = ".";

    // 为root的路径
    private final static String[] ROOT_PATH = new String[]{SEPARATE_CHARACTER, EMPTY_PATH};

    // 过滤器
    private ScanFilter scanFilter;

    public PathScan() {
    }

    public PathScan(ScanFilter scanFilter) {
        this.scanFilter = scanFilter;
    }

    /**
     * 扫描项目中指定包名的所有类
     * 例如 expression.cheney
     * 使用"","."扫描根，即项目中所有类
     *
     * @param scanPath 包路径,以'.'为分隔符
     * @return 扫描到的Class
     */
    public List<Class<?>> scanClass(String scanPath) throws ScanException {
        scanPath = SEPARATE_CHARACTER.equals(scanPath) ? EMPTY_PATH : scanPath;
        String resourcePath = scanPath.replaceAll("\\.", "/");

        URL resource = PathScan.class.getClassLoader().getResource(resourcePath);
        if (resource == null) {
            throw new ScanException("目录\"" + scanPath + "\"不存在");
        }

        ArrayList<Class<?>> results = new ArrayList<>();
        if ("file".equals(resource.getProtocol())) {
            String file = resource.getFile();
            scanPath = scanPath.replace("/", ".");
            scanClassInFile(scanPath, new File(file), results, new boolean[]{true});
        }

        return results;
    }

    /**
     * 扫描项目中指定URL下的所有类
     *
     * @param resource url资源
     * @return 扫描到的Class
     */
    public List<Class<?>> scanClass(URL resource) throws ScanException {
        ArrayList<Class<?>> result = new ArrayList<>();
        if ("file".equals(resource.getProtocol())) {
            String file = resource.getFile();
            if (!file.contains(TARGET_CLASSES)) {
                throw new ScanException("Resource is not a class path");
            }
            String[] classPaths = file.split(TARGET_CLASSES);
            String scanPath = classPaths.length == 1 ? EMPTY_PATH : classPaths[1].replace("/", ".");
            scanClassInFile(scanPath, new File(file), result, new boolean[]{true});
        }

        return result;
    }

    /**
     * 从File中扫描所有类
     *
     * @param parentPath 上级目录
     * @param file       文件
     * @param result     扫描结果集
     * @param first      是否首次调用
     */
    private void scanClassInFile(String parentPath, File file, List<Class<?>> result, boolean[] first) {
        if (file.isFile()) {
            String className = file.getName().substring(0, file.getName().length() - CLASS_END_LEN);
            String fullClassName = parentPath + className;
            try {
                filterClass(fullClassName, result);
            } catch (ClassNotFoundException e) {
                log.error("can not find class name:{}", fullClassName);
            }
        } else if (file.isDirectory()) {
            File[] childFiles = file.listFiles((childFile) ->
                    childFile.isDirectory() || childFile.getName().endsWith(CLASS_EXTENSION)
            );
            if (childFiles == null || childFiles.length == 0) {
                return;
            }
            String nextScanPath = getNextScanPath(parentPath, file, first);
            for (File child : childFiles) {
                scanClassInFile(nextScanPath, child, result, first);
            }
        }
    }

    /**
     * 获取下个包路径
     *
     * @param parentPath 上级目录
     * @param directory  扫描的当前目录
     * @param first      是否首次调用
     * @return 下一个包路径
     */
    private String getNextScanPath(String parentPath, File directory, boolean[] first) {
        if (first[0]) {
            // 首次进入
            first[0] = false;
            if (EMPTY_PATH.equals(parentPath) || parentPath.endsWith(SEPARATE_CHARACTER)) {
                return parentPath;
            }
            return parentPath + SEPARATE_CHARACTER;
        } else {
            return parentPath + directory.getName() + SEPARATE_CHARACTER;
        }
    }

    /**
     * 过滤有效类添加到result中
     *
     * @param fullClassName 类全名
     * @param result        扫描结果集合
     * @throws ClassNotFoundException
     */
    private void filterClass(String fullClassName, List<Class<?>> result) throws ClassNotFoundException {
        Class<?> target = Class.forName(fullClassName);
        if (scanFilter != null) {
            Class<?> superClass = scanFilter.getSuperClass();
            if (superClass != null &&
                    (!superClass.isAssignableFrom(target) ||
                            superClass.equals(target))) {
                return;
            }
            List<Class<? extends Annotation>> hasAnnotations = scanFilter.getHasAnnotations();
            if (hasAnnotations != null && !hasAnnotations.isEmpty()) {
                boolean hasAll = hasAnnotations.stream().allMatch(a -> target.getAnnotation(a) != null);
                if (!hasAll) {
                    return;
                }
            }
        }
        result.add(target);
    }

}
