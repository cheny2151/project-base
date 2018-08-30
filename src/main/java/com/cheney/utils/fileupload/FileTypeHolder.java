package com.cheney.utils.fileupload;

import com.cheney.utils.DateUtils;
import com.cheney.utils.SystemUtils;

import java.util.Date;

public class FileTypeHolder {

    public enum FileType {

        image,

        other;

    }

    public enum Image {

        avatar,

        articleimg,

        background
    }

    public static String getUploadPath(Image image) {
        String imagePath = SystemUtils.getValue("imagePath");
        return uploadPath(imagePath.replaceAll("\\$\\{imageType}", image.toString()));
    }

    private static String uploadPath(String path) {
        path = path.replaceAll("\\$\\{now}", DateUtils.formatDay(new Date()));
        return SystemUtils.getValue("static") + path + "/";
    }

}