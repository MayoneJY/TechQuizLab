package com.mayonedev.battle.domain.gamification.service;

import com.mayonedev.battle.domain.gamification.entity.Portfolio;
import java.util.List;

public interface PortfolioService {
    List<Portfolio> getPortfoliosByUserId(Long userId);

    Portfolio getPortfolio(Long userId, Long pfId);

    Portfolio createPortfolio(Portfolio portfolio);

    Portfolio updatePortfolio(Long userId, Long pfId, Portfolio portfolio);

    void deletePortfolio(Long userId, Long pfId);
}
