package com.mayonedev.battle.service;

import com.mayonedev.battle.entity.Question;
import com.mayonedev.battle.entity.QuestionBookmark;
import com.mayonedev.battle.entity.UserQuestionHistory;
import java.util.List;

public interface QuestionService {
    List<Question> getQuestionsByTopic(Long topicId);
    Question getQuestionById(Long id);
    boolean submitAnswer(Long userId, Long questionId, String answer);
    void bookmarkQuestion(Long userId, Long questionId, String memo);
    List<QuestionBookmark> getBookmarks(Long userId);
    List<UserQuestionHistory> getHistory(Long userId);
}
