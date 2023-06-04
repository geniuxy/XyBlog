package com.xy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger文档信息配置
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xy.controller"))
                .build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("XyGroup", "url待定", "2238384910@qq.com");
        return new ApiInfoBuilder()
                .title("XySwagger")
                .description("Xy个人博客的对接文档")
                .contact(contact)   // 联系方式
                .version("1.0")  // 版本
                .build();
    }
}
