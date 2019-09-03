package com.zhibinwang.security;

import com.zhibinwang.enity.Permission;
import com.zhibinwang.mapper.PermissionMapper;
import com.zhibinwang.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 花开
 * @create 2019-09-01 15:28
 * @desc
 **/

@Component
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private PermissionMapper permissionMapper;


    @Autowired
    private MyAuthenticationFailureHandler failureHandler;


    @Autowired
    private MyAuthenticationSuccessHandler successHandler;


    //配置用户认证信息
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        // 开启动态数据查询
        auth.userDetailsService(userDetailsService).passwordEncoder(new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                System.out.println(charSequence);

                return MD5Util.encode((String) charSequence);
            }

            /**
             *
             * @param formPassword 表单提交的password
             * @param dbPassword 数据库密码
             * @return
             */
            @Override
            public boolean matches(CharSequence formPassword, String dbPassword) {

                System.out.println(formPassword);
                System.out.println(MD5Util.encode((String) formPassword));
                //
                System.out.println(dbPassword.equals(MD5Util.encode((String) formPassword)));
                System.out.println(dbPassword);
                return dbPassword.equals(MD5Util.encode((String) formPassword));
            }
        });

    }


    //配置动态权限
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 动态配置数据库权限,确认访问什么url需要权限
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests = http
                .authorizeRequests();
        List<Permission> allPermission = permissionMapper.findAllPermission();
        for (Permission permission : allPermission) {
            //配置访问什么url需要什么权限
            authorizeRequests.antMatchers(permission.getUrl()).hasAuthority(permission.getPermTag());


        }


        authorizeRequests.antMatchers("/login").permitAll() //配置访问login不要全拦截
                . antMatchers("/**").fullyAuthenticated() //配置拦截全部的url
                .and().formLogin() //这里有两种模式,一种是fromlogin,一种是httpBasic, 记不记得大唐OA,就是这种模式
                .loginPage("/login") // 配置登陆页面
                .successHandler(successHandler) //配置登陆成功处理
                .failureHandler(failureHandler) // 配置登陆失败处理
                .and().csrf().disable(); // 禁用CSRF 跨域请求,正常生产是会开启的

    }
}
