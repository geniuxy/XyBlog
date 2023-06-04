package com.xy.controller;

import com.xy.domain.ResponseResult;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.exception.SystemException;
import com.xy.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class UploadController {
    @Autowired
    private UploadService uploadService;

    /**
     * 后台上传博文的缩略图
     * @param multipartFile
     * @return
     */
    @PostMapping("/upload")
    public ResponseResult uploadImg(@RequestParam("img") MultipartFile multipartFile) {
        /**
         * 从目前来看
         * 参数里注解有@PathVariable:接收/{id}之类的数据 @RequestBody:接收请求体的
         * @RequestParam("img")->postman里，body里的form-data
         * 这些应该都是接收请求体里的内容 也就是postman里的body
         */
        try {//上传缩略图
            return uploadService.uploadImg(multipartFile);
        } catch (Exception e) {//这边注意因为SystemException继承的是Exception，所以不能抛出IOException
            //失败的话抛出异常
            throw new SystemException(AppHttpCodeEnum.IMG_UPLOAD_FAILED);
        }
    }
}
