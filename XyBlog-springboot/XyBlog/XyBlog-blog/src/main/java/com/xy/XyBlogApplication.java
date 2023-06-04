package com.xy;

import org.mapstruct.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 这个是 -> 启动类
 */
@SpringBootApplication
@MapperScan("com.xy.mapper")
@EnableScheduling//这个是Spring提供的一个实现定时任务的api
@EnableSwagger2//启动Swagger2 //Swagger2就可以根据代码自动生成可交互式的文档，跨语言性，减少前后段工程师的沟通成本
public class XyBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(XyBlogApplication.class,args);
    }

}
