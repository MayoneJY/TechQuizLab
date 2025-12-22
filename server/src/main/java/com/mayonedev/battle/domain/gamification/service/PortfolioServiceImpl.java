package com.mayonedev.battle.domain.gamification.service;

import com.mayonedev.battle.domain.gamification.dao.PortfolioDao;
import com.mayonedev.battle.domain.gamification.entity.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioDao portfolioDao;
    private final com.mayonedev.battle.domain.battle.dao.BattleDao battleDao;

    @Override
    public java.util.Map<String, Object> getMyPortfolios(Long userId, String search, int page, int size) {
        int offset = (page - 1) * size;
        List<Portfolio> content = portfolioDao.findPortfolios(userId, search, size, offset);
        int totalElements = portfolioDao.countPortfolios(userId, search);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return java.util.Map.of(
                "content", content,
                "totalPages", totalPages,
                "totalElements", totalElements,
                "currentPage", page);
    }

    @Override
    public Portfolio getPortfolio(Long userId, Long pfId) {
        return portfolioDao.findByUserAndPfId(userId, pfId);
    }

    @Override
    @Transactional
    public Portfolio createPortfolio(Portfolio portfolio) {
        Long maxPfId = portfolioDao.findMaxPfIdByUserId(portfolio.getUserId());
        portfolio.setPfId(maxPfId + 1);
        portfolio.setCreatedAt(LocalDateTime.now());
        portfolioDao.insert(portfolio);
        return portfolio;
    }

    @Override
    @Transactional
    public Portfolio updatePortfolio(Long userId, Long pfId, Portfolio portfolio) {
        Portfolio existing = portfolioDao.findByUserAndPfId(userId, pfId);
        if (existing == null) {
            throw new RuntimeException("Portfolio not found");
        }
        existing.setTitle(portfolio.getTitle());
        existing.setContent(portfolio.getContent());
        portfolioDao.update(existing);
        return existing;
    }

    @Override
    @Transactional
    public void deletePortfolio(Long userId, Long pfId) {
        // 1. Ensure 'Trash' portfolio (ID 0) exists to hold references
        long trashPfId = 0L;
        Portfolio trash = portfolioDao.findByUserAndPfId(userId, trashPfId);
        if (trash == null) {
            trash = new Portfolio();
            trash.setUserId(userId);
            trash.setPfId(trashPfId);
            trash.setTitle("Deleted Portfolio");
            trash.setContent("");
            trash.setCreatedAt(LocalDateTime.now());
            portfolioDao.insert(trash);
        }

        // 2. Relink battles from the portfolio-to-be-deleted to the Trash portfolio
        battleDao.relinkPortfolio(userId, pfId, trashPfId);

        // 3. Delete the actual portfolio
        portfolioDao.delete(userId, pfId);
    }
}
