package com.cheney.utils.fileupload;

import com.cheney.utils.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 文件上传
 */
@Slf4j
public class FileUploadHolder {

    private final static ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    public static String upload(MultipartFile file, FileTypeHolder.Image image, boolean sync) throws IOException {
        Assert.notNull(file, "upload file must not null");

        String fileFullName = createFileName(file.getOriginalFilename(), image);
        File tempFile = createTempFile(file);
        if (sync) {
            EXECUTOR_SERVICE.execute(new UploadTask(tempFile, fileFullName));
        } else {
            upload(tempFile, fileFullName);
        }
        return mapWebPath(fileFullName);
    }

    /**
     * 创建临时存储文件
     *
     * @param file 上传文件
     * @return 临时文件
     * @throws IOException
     */
    private static File createTempFile(MultipartFile file) throws IOException {
        File temp = new File(System.getProperty("java.io.tmpdir") + "/upload/" + UUID.randomUUID());
        File parentFile = temp.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdir();
        }
        file.transferTo(temp);
        return temp;
    }

    /**
     * 根据原始文件名创建服务器文件全路径名
     *
     * @param originName 原始文件名
     * @param image
     * @return 服务器文件全路径名
     */
    private static String createFileName(String originName, FileTypeHolder.Image image) {
        if (System.getProperty("os.name").toLowerCase().contains("win"))
            return SystemUtils.getValue("windowImagePath") + "/" + FilenameUtils.getBaseName(originName)
                    + "-uuid-" + UUID.randomUUID() + "." + FilenameUtils.getExtension(originName);
        else
            return FileTypeHolder.getUploadPath(image) + FilenameUtils.getBaseName(originName)
                    + "-uuid-" + UUID.randomUUID() + "." + FilenameUtils.getExtension(originName);
    }

    /**
     * 复制文件到指定上传目录
     *
     * @param file         文件
     * @param fileFullName 全路径名
     * @throws IOException
     */
    private static void upload(File file, String fileFullName) throws IOException {
        File toCopy = new File(fileFullName);
        File parentFile = toCopy.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdir();
        }
        FileUtils.copyFile(file, toCopy);
        file.delete();
    }

    /**
     * 映射图片web访问路径
     *
     * @param fileFullName 文件全路径名
     * @return 访问路径
     */
    private static String mapWebPath(String fileFullName) {
        return SystemUtils.getSite() + "/" + SystemUtils.getValue("staticAlias") +
                fileFullName.substring(SystemUtils.getStatic().length());
    }

    /**
     * 异步上传任务
     */
    private static class UploadTask implements Runnable {


        private File file;

        private String path;

        public UploadTask(File file, String path) {
            this.file = file;
            this.path = path;
        }

        @Override
        public void run() {
            try {
                FileUploadHolder.upload(file, path);
            } catch (IOException e) {
                log.error("异步上传文件失败", e);
            }
        }
    }

}
