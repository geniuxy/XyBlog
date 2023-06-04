package com.xy;

import java.io.*;

import com.aliyun.oss.*;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.xy.XyBlogApplication;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)//在正常情况下测试类是需要@RunWith的，作用是告诉java你这个类通过用什么运行环境运行，
// 例如启动和创建spring的应用上下文。否则你需要为此在启动时写一堆的环境配置代码。你在IDEA里去掉@RunWith仍然能跑
// 是因为在IDEA里识别为一个JUNIT的运行环境，相当于就是一个自识别的RUNWITH环境配置。但在其他IDE里并没有。
public class OssTest {
    private String key = "4.png";
    private String bucketName="zju-xy-blog";
    @Autowired
    OSS ossClient;

    @Test
    public void uploadTest() throws FileNotFoundException {

        InputStream inputStream=new FileInputStream("C:\\Users\\22383\\Desktop\\XyBlogPicture\\4.png");

        ossClient.putObject(bucketName,key,inputStream);

        ossClient.shutdown();
    }
}
