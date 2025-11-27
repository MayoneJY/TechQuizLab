package com.mayonedev.battle.controller;

import com.mayonedev.battle.dto.LoginResponseDTO;
import com.mayonedev.battle.dto.UserDetailsDTO;
import com.mayonedev.battle.dto.UserDto;
import com.mayonedev.battle.entity.User;
import com.mayonedev.battle.security.TokenProvider;
import com.mayonedev.battle.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/")
    @Operation(summary = "사용자 목록 조회", description = "모든 사용자의 목록을 조회합니다.")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("사용자 목록 조회 요청");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "사용자 조회", description = "특정 사용자의 정보를 조회합니다.")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "사용자 ID") @PathVariable Long id) {
        log.info("사용자 조회 요청 - ID: {}", id);
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "이메일로 사용자 조회", description = "이메일로 사용자 정보를 조회합니다.")
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "이메일") @PathVariable String email) {
        log.info("사용자 조회 요청 - 이메일: {}", email);
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/nickname/{nickname}")
    @Operation(summary = "닉네임으로 사용자 조회", description = "닉네임으로 사용자 정보를 조회합니다.")
    public ResponseEntity<User> getUserByNickname(
            @Parameter(description = "닉네임") @PathVariable String nickname) {
        log.info("사용자 조회 요청 - 닉네임: {}", nickname);
        User user = userService.getUserByNickname(nickname);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        log.info("사용자 등록 요청 - 이메일: {}, 닉네임: {}", userDto.getEmail(), userDto.getNickname());
        try {
            User createdUser = userService.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (RuntimeException e) {
            log.error("사용자 등록 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "사용자 정보 수정", description = "사용자의 정보를 수정합니다.")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "사용자 ID") @PathVariable Long id,
            @RequestBody UserDto userDto) {
        log.info("사용자 정보 수정 요청 - ID: {}", id);
        try {
            User updatedUser = userService.updateUser(id, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            log.error("사용자 정보 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "사용자 ID") @PathVariable Long id) {
        log.info("사용자 삭제 요청 - ID: {}", id);
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok("사용자가 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/email/{email}")
    @Operation(summary = "이메일 중복 확인", description = "이메일의 중복 여부를 확인합니다.")
    public ResponseEntity<Boolean> checkEmailExists(
            @Parameter(description = "이메일") @PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/nickname/{nickname}")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임의 중복 여부를 확인합니다.")
    public ResponseEntity<Boolean> checkNicknameExists(
            @Parameter(description = "닉네임") @PathVariable String nickname) {
        boolean exists = userService.existsByNickname(nickname);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/login")
    @Operation(summary = "사용자 로그인", description = "이메일과 비밀번호로 로그인합니다.")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("로그인 요청 - 이메일: {}", loginRequest.getEmail());
        try {
            // User user = userService.login(loginRequest.getEmail(),
            // loginRequest.getPassword());
            // // 비밀번호 정보는 응답에서 제거
            // user.setPassword(null);
            // return ResponseEntity.ok(user);

            // 로그인 성공 시
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            UserDetailsDTO userDetailsDTO = (UserDetailsDTO) authentication.getPrincipal();

            String accessToken = TokenProvider.generateJWT(userDetailsDTO, true);
            String refreshToken = TokenProvider.generateJWT(userDetailsDTO, false);

            return ResponseEntity.ok().body(new LoginResponseDTO(accessToken, refreshToken));

        } catch (RuntimeException e) {
            log.error("로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // 로그인 요청용 DTO
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}