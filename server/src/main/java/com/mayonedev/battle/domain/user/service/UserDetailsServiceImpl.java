package com.mayonedev.battle.domain.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mayonedev.battle.domain.user.dao.UserDao;
import com.mayonedev.battle.domain.user.dto.UserDetailsDTO;
import com.mayonedev.battle.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if(email == null || email.isEmpty()){
            throw new UsernameNotFoundException("Invalid email");
        }
        User user = userDao.findByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return new UserDetailsDTO(user, null);
    }

}
