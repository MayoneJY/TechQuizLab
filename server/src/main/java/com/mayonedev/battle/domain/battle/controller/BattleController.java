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
    public ResponseEntity<java.util.Map<String, Object>> getMyBattles(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        return ResponseEntity
                .ok(battleService.getMyBattles(userDetails.getUserId(), category, search, sort, page, size));
    }

    @GetMapping("/bookmarks")
    @Operation(summary = "내 오답노트(북마크) 조회")
    public ResponseEntity<java.util.Map<String, Object>> getMyBookmarks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        return ResponseEntity
                .ok(battleService.getMyBookmarks(userDetails.getUserId(), category, search, sort, page, size));
    }

    @GetMapping("/bookmarks/{id}")
    @Operation(summary = "내 오답노트(북마크) 상세 조회")
    public ResponseEntity<com.mayonedev.battle.domain.battle.entity.BattleBookmark> getBookmark(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        return ResponseEntity.ok(battleService.getBookmark(userDetails.getUserId(), id));
    }

    @PatchMapping("/bookmarks/{id}")
    @Operation(summary = "내 오답노트(북마크) 메모 수정")
    public ResponseEntity<Void> updateBookmark(
            @PathVariable Long id,
            @RequestBody BookmarkRequest request,
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        battleService.updateBookmark(userDetails.getUserId(), id, request.getMemo());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/bookmarks/{id}")
    @Operation(summary = "내 오답노트(북마크) 삭제")
    public ResponseEntity<Void> deleteBookmark(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        battleService.deleteBookmark(userDetails.getUserId(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bookmarks/categories")
    @Operation(summary = "내 오답노트 카테고리 목록 조회")
    public ResponseEntity<java.util.List<String>> getBookmarkCategories(
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        return ResponseEntity.ok(battleService.getBookmarkCategories(userDetails.getUserId()));
    }

    @GetMapping("/categories")
    @Operation(summary = "내 배틀 카테고리 목록 조회")
    public ResponseEntity<java.util.List<String>> getBattleCategories(
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        return ResponseEntity.ok(battleService.getBattleCategories(userDetails.getUserId()));
    }

    @PostMapping("/practice/bookmarks")
    @Operation(summary = "오답노트 기반 복습 배틀 생성")
    public ResponseEntity<Battle> createPracticeBattle(
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        return ResponseEntity.ok(battleService.createPracticeBattle(userDetails.getUserId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "배틀 삭제 (포기)")
    public ResponseEntity<Void> deleteBattle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        battleService.deleteBattle(userDetails.getUserId(), id);
        return ResponseEntity.ok().build();
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
