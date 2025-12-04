package com.mayonedev.battle.service;

import com.mayonedev.battle.dao.UserDao;
import com.mayonedev.battle.dto.UserDto;
import com.mayonedev.battle.dto.UserRefreshTokenDTO;
import com.mayonedev.battle.dto.UserRefreshTokenUpdateDTO;
import com.mayonedev.battle.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.annotations.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        log.info("모든 사용자 조회");
        return userDao.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        log.info("사용자 조회 - ID: {}", userId);
        return userDao.findById(userId);
    }

    @Override
    public User getUserByEmail(String email) {
        log.info("사용자 조회 - 이메일: {}", email);
        return userDao.findByEmail(email);
    }

    @Override
    public User getUserByNickname(String nickname) {
        log.info("사용자 조회 - 닉네임: {}", nickname);
        return userDao.findByNickname(nickname);
    }

    @Override
    @Transactional
    public User createUser(UserDto userDto) {
        log.info("사용자 등록 - 이메일: {}, 닉네임: {}", userDto.getEmail(), userDto.getNickname());

        // 중복 체크
        if (existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다: " + userDto.getEmail());
        }

        if (existsByNickname(userDto.getNickname())) {
            throw new RuntimeException("이미 존재하는 닉네임입니다: " + userDto.getNickname());
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setNickname(userDto.getNickname());
        user.setCreatedAt(LocalDateTime.now());

        userDao.insert(user);
        return user;
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserDto userDto) {
        log.info("사용자 정보 수정 - ID: {}", userId);

        User existingUser = getUserById(userId);
        if (existingUser == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + userId);
        }

        // 이메일 변경 시 중복 체크
        if (!existingUser.getEmail().equals(userDto.getEmail()) && existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다: " + userDto.getEmail());
        }

        // 닉네임 변경 시 중복 체크
        if (!existingUser.getNickname().equals(userDto.getNickname()) && existsByNickname(userDto.getNickname())) {
            throw new RuntimeException("이미 존재하는 닉네임입니다: " + userDto.getNickname());
        }

        existingUser.setEmail(userDto.getEmail());
        existingUser.setNickname(userDto.getNickname());
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        userDao.update(existingUser);
        return existingUser;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        log.info("사용자 삭제 - ID: {}", userId);
        return userDao.deleteById(userId) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userDao.existsByNickname(nickname);
    }

    @Override
    public User login(String email, String password) {
        log.info("로그인 시도 - 이메일: {}", email);

        User user = getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("존재하지 않는 이메일입니다: " + email);
        }

        // 실제로는 암호화된 비밀번호와 비교해야 함
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        log.info("로그인 성공 - 사용자: {}", user.getNickname());
        return user;
    }
}