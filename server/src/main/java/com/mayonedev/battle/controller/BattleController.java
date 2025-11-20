package com.mayonedev.battle.controller;

import com.mayonedev.battle.entity.Battle;
import com.mayonedev.battle.entity.BattleTurn;
import com.mayonedev.battle.service.BattleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battles")
@RequiredArgsConstructor
@Tag(name = "Battle", description = "배틀(싱글/레이드) API")
public class BattleController {

    private final BattleService battleService;

    @PostMapping
    @Operation(summary = "배틀 생성 및 시작")
    public ResponseEntity<Battle> createBattle(@RequestBody CreateBattleRequest request) {
        Battle battle = battleService.createBattle(
                request.getStageId(),
                request.getMode(),
                request.getDifficulty(),
                request.getUserId()
        );
        return ResponseEntity.ok(battle);
    }

    @GetMapping("/{id}")
    @Operation(summary = "배틀 상태 조회")
    public ResponseEntity<Battle> getBattle(@PathVariable Long id) {
        return ResponseEntity.ok(battleService.getBattle(id));
    }

    @PostMapping("/{id}/turn")
    @Operation(summary = "턴 진행 (답안 제출)")
    public ResponseEntity<BattleTurn> processTurn(
            @PathVariable Long id,
            @RequestBody TurnRequest request) {
        BattleTurn turn = battleService.processTurn(id, request.getUserId(), request.getAnswer());
        return ResponseEntity.ok(turn);
    }

    @PostMapping("/{id}/finish")
    @Operation(summary = "배틀 종료")
    public ResponseEntity<Void> finishBattle(@PathVariable Long id) {
        battleService.finishBattle(id);
        return ResponseEntity.ok().build();
    }

    @Data
    public static class CreateBattleRequest {
        private Long stageId;
        private String mode; // SINGLE, RAID
        private Integer difficulty;
        private Long userId;
    }

    @Data
    public static class TurnRequest {
        private Long userId;
        private String answer;
    }
}
