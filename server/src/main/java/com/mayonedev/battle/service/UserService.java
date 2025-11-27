package com.mayonedev.battle.service;

import com.mayonedev.battle.dto.UserDto;
import com.mayonedev.battle.dto.UserRefreshTokenDTO;
import com.mayonedev.battle.dto.UserRefreshTokenUpdateDTO;
import com.mayonedev.battle.entity.User;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface UserService {
    
    /**
     * 모든 사용자 조회
     */
    List<User> getAllUsers();
    
    /**
     * ID로 사용자 조회
     */
    User getUserById(Long id);
    
    /**
     * 이메일로 사용자 조회 (로그인용)
     */
    User getUserByEmail(String email);
    
    /**
     * 닉네임으로 사용자 조회
     */
    User getUserByNickname(String nickname);
    
    /**
     * 사용자 등록
     */
    User createUser(UserDto userDto);
    
    /**
     * 사용자 정보 수정
     */
    User updateUser(Long id, UserDto userDto);
    
    /**
     * 사용자 삭제
     */
    boolean deleteUser(Long id);
    
    /**
     * 이메일 중복 확인
     */
    boolean existsByEmail(String email);
    
    /**
     * 닉네임 중복 확인
     */
    boolean existsByNickname(String nickname);
    
    /**
     * 사용자 로그인 (이메일 + 비밀번호)
     */
    User login(String email, String password);
}