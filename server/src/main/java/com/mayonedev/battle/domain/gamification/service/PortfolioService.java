package com.mayonedev.battle.domain.gamification.service;

import com.mayonedev.battle.domain.gamification.entity.Portfolio;
import java.util.List;

public interface PortfolioService {
    java.util.Map<String, Object> getMyPortfolios(Long userId, String search, int page, int size);

    Portfolio getPortfolio(Long userId, Long pfId);

    Portfolio createPortfolio(Portfolio portfolio);

    Portfolio updatePortfolio(Long userId, Long pfId, Portfolio portfolio);

    void deletePortfolio(Long userId, Long pfId);
}
