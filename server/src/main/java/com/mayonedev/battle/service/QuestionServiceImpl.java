package com.mayonedev.battle.service;

import com.mayonedev.battle.dao.QuestionBookmarkDao;
import com.mayonedev.battle.dao.QuestionDao;
import com.mayonedev.battle.dao.UserDao;
import com.mayonedev.battle.dao.UserQuestionHistoryDao;
import com.mayonedev.battle.dao.UserTopicLevelDao;
import com.mayonedev.battle.entity.Question;
import com.mayonedev.battle.entity.QuestionBookmark;
import com.mayonedev.battle.entity.User;
import com.mayonedev.battle.entity.UserQuestionHistory;
import com.mayonedev.battle.entity.UserTopicLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionDao questionDao;
    private final UserQuestionHistoryDao historyDao;
    private final QuestionBookmarkDao bookmarkDao;
    private final UserDao userDao;
    private final UserTopicLevelDao userTopicLevelDao;

    @Override
    public List<Question> getQuestionsByTopic(Long topicId) {
        return questionDao.findByTopicId(topicId);
    }

    @Override
    public Question getQuestionById(Long id) {
        return questionDao.findById(id);
    }

    @Override
    @Transactional
    public boolean submitAnswer(Long userId, Long questionId, String answer) {
        Question question = questionDao.findById(questionId);
        if (question == null) {
            throw new IllegalArgumentException("Question not found");
        }

        boolean isCorrect = question.getAnswer().equalsIgnoreCase(answer.trim());

        // Save history
        UserQuestionHistory history = new UserQuestionHistory();
        history.setUserId(userId);
        history.setQuestionId(questionId);
        history.setIsCorrect(isCorrect);
        history.setCreatedAt(LocalDateTime.now());
        historyDao.insert(history);

        if (isCorrect) {
            // Give EXP
            int expGain = question.getDifficulty() * 10;
            
            // 1. Global Level
            User user = userDao.findById(userId);
            user.setExp(user.getExp() + expGain);
            // Simple level up logic: 100 * level
            if (user.getExp() >= user.getLevel() * 100) {
                user.setExp(user.getExp() - user.getLevel() * 100);
                user.setLevel(user.getLevel() + 1);
            }
            userDao.update(user);

            // 2. Topic Level
            UserTopicLevel topicLevel = userTopicLevelDao.findByUserIdAndTopicId(userId, question.getTopicId());
            if (topicLevel == null) {
                topicLevel = new UserTopicLevel(null, userId, question.getTopicId(), 1, 0);
                userTopicLevelDao.insert(topicLevel);
            }
            topicLevel.setExp(topicLevel.getExp() + expGain);
            if (topicLevel.getExp() >= topicLevel.getLevel() * 50) {
                topicLevel.setExp(topicLevel.getExp() - topicLevel.getLevel() * 50);
                topicLevel.setLevel(topicLevel.getLevel() + 1);
            }
            userTopicLevelDao.update(topicLevel);
        }

        return isCorrect;
    }

    @Override
    public void bookmarkQuestion(Long userId, Long questionId, String memo) {
        if (!bookmarkDao.existsByUserIdAndQuestionId(userId, questionId)) {
            QuestionBookmark bookmark = new QuestionBookmark();
            bookmark.setUserId(userId);
            bookmark.setQuestionId(questionId);
            bookmark.setMemo(memo);
            bookmark.setCreatedAt(LocalDateTime.now());
            bookmarkDao.insert(bookmark);
        }
    }

    @Override
    public List<QuestionBookmark> getBookmarks(Long userId) {
        return bookmarkDao.findByUserId(userId);
    }

    @Override
    public List<UserQuestionHistory> getHistory(Long userId) {
        return historyDao.findByUserId(userId);
    }
}
