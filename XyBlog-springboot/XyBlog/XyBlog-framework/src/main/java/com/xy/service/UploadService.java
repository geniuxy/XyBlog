package com.xy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Comment;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService{

    ResponseResult uploadImg(MultipartFile img);
}
