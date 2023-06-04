package com.xy.config;

import com.xy.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
/**
 * Springsecurity相关配置
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)//可以使Security的@PreAuthorize()和@PostAuthorize()两个注解生效
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;//jwt认证过滤器

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;//认证失败处理器

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;//授权失败处理器

    /**
     * 定义密码加密算法
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                //-----------------前台-----------------
                // 对于登录接口 允许匿名访问
                //.antMatchers("/login").anonymous()
                // 对于退出登录 需要携带token
                //.antMatchers("/logout").authenticated()
                // 对于查询登录用户个人信息 需要携带token
                //.antMatchers("/user/userInfo").authenticated()
                // 上传头像图片 需要携带token
                //.antMatchers("/upload").authenticated()
                //.antMatchers("/link/getAllLink").authenticated()//用于测试  /link/getAllLink这个请求需要携带token才能访问
                //-----------------后台-----------------
                .antMatchers("/user/login").anonymous()
                // 后台：除上面外的所有请求全部需要认证即可访问
                .anyRequest().authenticated();
        //配置异常处理器
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        http.logout().disable();
        //把jwtAuthenticationTokenFilter添加到SpringSecurity的过滤器链中
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        //允许跨域
        http.cors();
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
