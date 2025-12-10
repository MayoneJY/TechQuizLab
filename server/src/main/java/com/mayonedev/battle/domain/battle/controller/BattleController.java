package com.mayonedev.battle.domain.battle.controller;

import com.mayonedev.battle.domain.user.dto.UserDetailsDTO;
import com.mayonedev.battle.domain.battle.entity.Battle;
import com.mayonedev.battle.domain.battle.service.BattleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battles")
@RequiredArgsConstructor
@Tag(name = "Battle", description = "배틀(싱글/레이드) API")
public class BattleController {

    private final BattleService battleService;

    @PostMapping
    @Operation(summary = "배틀 생성 및 시작")
    public ResponseEntity<Battle> createBattle(
            @RequestBody CreateBattleRequest request,
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        Battle battle = battleService.createBattle(
                request.getStageId(),
                userDetails.getUserId());
        return ResponseEntity.ok(battle);
    }

    @GetMapping("/{id}")
    @Operation(summary = "배틀 상태 조회")
    public ResponseEntity<Battle> getBattle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        return ResponseEntity.ok(battleService.getBattle(userDetails.getUserId(), id));
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "배틀 문제 및 상세 조회")
    public ResponseEntity<java.util.List<com.mayonedev.battle.domain.battle.entity.BattleDetail>> getBattleDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        return ResponseEntity.ok(battleService.getBattleDetails(userDetails.getUserId(), id));
    }

    @PostMapping("/{id}/turn")
    @Operation(summary = "턴 진행 (답안 제출)")
    public ResponseEntity<Void> processTurn(
            @PathVariable Long id,
            @RequestBody TurnRequest request,
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        battleService.processTurn(userDetails.getUserId(), id, request.getAnswer());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/finish")
    @Operation(summary = "배틀 종료 및 채점")
    public ResponseEntity<java.util.Map<String, Object>> finishBattle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        return ResponseEntity.ok(battleService.finishBattle(userDetails.getUserId(), id));
    }

    @PostMapping("/{battleId}/details/{detailId}/bookmark")
    @Operation(summary = "배틀 문제 북마크 (오답노트)")
    public ResponseEntity<Void> bookmarkBattleDetail(
            @PathVariable Long battleId,
            @PathVariable Long detailId,
            @RequestBody BookmarkRequest request,
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        // memo is optional, pass null or empty string if not provided
        String memo = (request != null) ? request.getMemo() : null;
        battleService.bookmarkBattleDetail(userDetails.getUserId(), battleId, detailId, memo);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "내 배틀 히스토리 조회")
    public ResponseEntity<java.util.List<Battle>> getMyBattles(
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        return ResponseEntity.ok(battleService.getMyBattles(userDetails.getUserId()));
    }

    @GetMapping("/bookmarks")
    @Operation(summary = "내 오답노트(북마크) 조회")
    public ResponseEntity<java.util.List<com.mayonedev.battle.domain.battle.entity.BattleBookmark>> getMyBookmarks(
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        return ResponseEntity.ok(battleService.getMyBookmarks(userDetails.getUserId()));
    }

    @PostMapping("/practice/bookmarks")
    @Operation(summary = "오답노트 기반 복습 배틀 생성")
    public ResponseEntity<Battle> createPracticeBattle(
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        return ResponseEntity.ok(battleService.createPracticeBattle(userDetails.getUserId()));
    }

    @Data
    public static class CreateBattleRequest {
        private Long stageId;
        private String mode; // SINGLE, RAID (Ignored for now as per service update)
        private Integer difficulty; // (Ignored)
        // private Long userId; // Use Auth ID
    }

    @Data
    public static class TurnRequest {
        // private Long userId; // Use Auth ID
        private String answer;
    }

    @Data
    public static class BookmarkRequest {
        private String memo;
    }
}
