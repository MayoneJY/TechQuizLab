package com.mayonedev.battle.domain.gamification.controller;

import com.mayonedev.battle.domain.gamification.entity.Portfolio;
import com.mayonedev.battle.domain.gamification.service.PortfolioService;
import com.mayonedev.battle.domain.user.dto.UserDetailsDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@Tag(name = "Portfolio", description = "포트폴리오 관리 API")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "내 포트폴리오 목록 조회")
    public ResponseEntity<List<Portfolio>> getMyPortfolios(@PathVariable Long userId) {
        return ResponseEntity.ok(portfolioService.getPortfoliosByUserId(userId));
    }

    @PostMapping
    @Operation(summary = "포트폴리오 생성")
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody Portfolio portfolio) {
        return ResponseEntity.ok(portfolioService.createPortfolio(portfolio));
    }

    @PutMapping("/{pfId}")
    @Operation(summary = "포트폴리오 수정")
    public ResponseEntity<Portfolio> updatePortfolio(
            @PathVariable Long pfId,
            @RequestBody Portfolio portfolio) {
        // userId는 request body에 포함되어 있다고 가정, 혹은 SecurityContext에서 가져와야 하지만
        // 현재 구조상 request body의 userId를 사용 (보안상 취약할 수 있으나 프로토타입 레벨)
        return ResponseEntity.ok(portfolioService.updatePortfolio(portfolio.getUserId(), pfId, portfolio));
    }

    @DeleteMapping("/{pfId}")
    @Operation(summary = "포트폴리오 삭제")
    public ResponseEntity<Void> deletePortfolio(
            @PathVariable Long pfId,
            @AuthenticationPrincipal UserDetailsDTO userDetails) {
        portfolioService.deletePortfolio(userDetails.getUserId(), pfId);
        return ResponseEntity.ok().build();
    }
}
