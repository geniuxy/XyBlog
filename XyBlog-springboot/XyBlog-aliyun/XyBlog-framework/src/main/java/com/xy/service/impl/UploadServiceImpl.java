package com.xy.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.xy.domain.ResponseResult;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.exception.SystemException;
import com.xy.service.UploadService;
import com.xy.utils.PathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    OSS ossClient;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;

    @Override
    public ResponseResult uploadImg(MultipartFile img) {
        //判断文件类型
        //获取原始文件名
        String originalFilename = img.getOriginalFilename();
        //对原始文件名进行判断
        if (!(originalFilename.endsWith(".png") || originalFilename.endsWith(".jpg"))) {
            throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
        }
        //生成oss中保存的路径 主要是根据日期对图片进行分类
        String filePath = PathUtils.generateFilePath(originalFilename);
        String url = null;
        try {
            url = uploadOss(img, filePath);
        } catch (Exception e) {
            throw new SystemException(AppHttpCodeEnum.OSS_UPLOAD_FAILED);
        }
        return ResponseResult.okResult(url);
    }

    private String uploadOss(MultipartFile imgFile, String filePath) throws Exception {
        //上传OSS步骤
        //第一步 得到inputstream
        InputStream inputStream = imgFile.getInputStream();
        //第二步 ossclient上传 三个参数 bucket,filePath(key),inputStream
        ossClient.putObject(bucket, filePath, inputStream);
        //返回一个url给前端
        return "https://" + bucket + "." + endpoint + "/" + filePath;
        // TODO 这边ossClient.shutdown()加上就报错 不加上貌似也会导致oom 不知道怎么设置
    }
}
