package com.mayonedev.battle.domain.question.service;

import com.mayonedev.battle.domain.question.entity.Question;
import com.mayonedev.battle.domain.question.entity.QuestionBookmark;
import com.mayonedev.battle.domain.question.entity.UserQuestionHistory;
import java.util.List;

public interface QuestionService {
    List<Question> getQuestionsByTopic(Long topicId);
    Question getQuestionById(Long id);
    boolean submitAnswer(Long userId, Long questionId, String answer);
    void bookmarkQuestion(Long userId, Long questionId, String memo);
    List<QuestionBookmark> getBookmarks(Long userId);
    List<UserQuestionHistory> getHistory(Long userId);
}
