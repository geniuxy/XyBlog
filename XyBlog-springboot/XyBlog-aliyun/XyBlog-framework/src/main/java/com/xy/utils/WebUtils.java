package com.xy.utils;

import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WebUtils
{
    /**
     * 将字符串渲染到客户端
     * 
     * @param response 渲染对象
     * @param string 待渲染的字符串
     * @return null
     */
    public static void renderString(HttpServletResponse response, String string) {
        try
        {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * //设置下载文件的请求头
     * @param filename
     * @param response
     * @throws UnsupportedEncodingException
     */
    public static void setDownLoadHeader(String filename, HttpServletResponse response) throws UnsupportedEncodingException {
        //这些是ali-EasyExcel相关的设置
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fname= URLEncoder.encode(filename,"UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition","attachment; filename="+fname);

//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setCharacterEncoding("utf-8");
    }
}