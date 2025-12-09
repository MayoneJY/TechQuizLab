package com.mayonedev.battle.domain.gamification.controller;

import com.mayonedev.battle.domain.gamification.entity.Achievement;
import com.mayonedev.battle.domain.gamification.entity.UserAchievement;
import com.mayonedev.battle.domain.gamification.entity.UserDailyMission;
import com.mayonedev.battle.domain.gamification.dto.FriendDTO;
import com.mayonedev.battle.domain.gamification.dto.RankingDTO;
import com.mayonedev.battle.domain.gamification.service.GamificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Map;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
@Tag(name = "Gamification", description = "업적 및 일일 미션 API")
public class GamificationController {

    private final GamificationService gamificationService;

    @GetMapping("/achievements")
    @Operation(summary = "전체 업적 목록 조회")
    public ResponseEntity<List<Achievement>> getAllAchievements() {
        return ResponseEntity.ok(gamificationService.getAllAchievements());
    }

    @GetMapping("/my-achievements")
    @Operation(summary = "나의 달성 업적 조회")
    public ResponseEntity<List<UserAchievement>> getUserAchievements(@RequestParam Long userId) {
        // Check for new achievements before returning
        gamificationService.checkAchievements(userId);
        return ResponseEntity.ok(gamificationService.getUserAchievements(userId));
    }

    @GetMapping("/missions/daily")
    @Operation(summary = "오늘의 일일 미션 조회 (자동 할당)")
    public ResponseEntity<List<UserDailyMission>> getDailyMissions(@RequestParam Long userId) {
        System.out.println("GAMIFICATION CONTROLLER HIT: getDailyMissions USER=" + userId);
        return ResponseEntity.ok(gamificationService.getDailyMissions(userId));
    }

    @PostMapping("/missions/{id}/claim")
    @Operation(summary = "일일 미션 보상 수령")
    public ResponseEntity<Void> claimMissionReward(@PathVariable Long id, @RequestParam Long userId) {
        System.out.println("GAMIFICATION CONTROLLER HIT: claimMissionReward ID=" + id + ", USER=" + userId);
        gamificationService.claimMissionReward(userId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rankings")
    @Operation(summary = "랭킹 조회")
    public ResponseEntity<List<RankingDTO>> getRankings() {
        return ResponseEntity.ok(gamificationService.getRankings());
    }

    @GetMapping("/friends")
    @Operation(summary = "친구 목록 조회")
    public ResponseEntity<List<FriendDTO>> getFriends(@RequestParam Long userId) {
        return ResponseEntity.ok(gamificationService.getFriends(userId));
    }

    @PostMapping("/friends")
    @Operation(summary = "친구 추가")
    public ResponseEntity<Void> addFriend(@RequestParam Long userId, @RequestBody Map<String, String> body) {
        String nickname = body.get("nickname");
        gamificationService.addFriend(userId, nickname);
        return ResponseEntity.ok().build();
    }
}
