package com.xy.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 自动生成oss保存的key 保存路径
 */
public class PathUtils {
    //传进来文件名称 如：1.png 2.jpg
    public static String generateFilePath(String fileName){
        //根据日期生成路径
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd/");
        String datePath = sdf.format(new Date());
        //uuid作为文件名
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //后缀和文件后缀一致
        int index = fileName.lastIndexOf(".");
        // test.jpg -> .jpg
        String fileType = fileName.substring(index);
        return new StringBuilder().append(datePath).append(uuid).append(fileType).toString();
    }
}
