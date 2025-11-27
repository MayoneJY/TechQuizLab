package com.mayonedev.battle.dao;

import com.mayonedev.battle.dto.UserRefreshTokenDTO;
import com.mayonedev.battle.dto.UserRefreshTokenUpdateDTO;
import com.mayonedev.battle.entity.User;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao {
    
    /**
     * 사용자 목록 조회
     */
    List<User> findAll();
    
    /**
     * ID로 사용자 조회
     */
    User findById(@Param("id") Long id);
    
    /**
     * 이메일로 사용자 조회 (로그인용)
     */
    User findByEmail(@Param("email") String email);
    
    /**
     * 닉네임으로 사용자 조회
     */
    User findByNickname(@Param("nickname") String nickname);
    
    /**
     * 사용자 등록
     */
    int insert(User user);
    
    /**
     * 사용자 정보 수정
     */
    int update(User user);
    
    /**
     * 사용자 삭제
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 이메일 중복 확인
     */
    boolean existsByEmail(@Param("email") String email);
    
    /**
     * 닉네임 중복 확인
     */
    boolean existsByNickname(@Param("nickname") String nickname);
    
    /**
     * 총 사용자 수 조회
     */
    int count();
}