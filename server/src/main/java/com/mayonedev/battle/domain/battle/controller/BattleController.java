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
}
