package com.zhibinwang.security;

import com.zhibinwang.enity.Permission;
import com.zhibinwang.enity.User;
import com.zhibinwang.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 花开
 * @create 2019-09-01 15:40
 * @desc 用于springcurity 动态查询账号,根据userName
 **/
@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User byUsername = userMapper.findByUsername(username);
        if (username == null){
            System.out.println("用户不存在");
            throw  new UsernameNotFoundException(username);

        }

        List<Permission> permissionByUsername = userMapper.findPermissionByUsername(username);

        if (permissionByUsername != null && permissionByUsername.size() > 0){

            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            for (Permission permission:permissionByUsername
            ) {
                authorities.add(new SimpleGrantedAuthority(permission.getPermTag()));
            }

            byUsername.setAuthorities(authorities);
        }


        return byUsername;
    }
}
