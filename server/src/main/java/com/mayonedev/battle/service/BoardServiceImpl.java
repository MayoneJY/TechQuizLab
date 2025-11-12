package com.mayonedev.battle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mayonedev.battle.dao.BoardPostDao;
import com.mayonedev.battle.dto.BoardPostDto;
import com.mayonedev.battle.entity.Board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardPostDao postDao;

    @Override
    public List<Board> getAllPosts() {
        return postDao.findAll();
    }

    //특정 사용자 게시글 전체 보기 
    @Override
    public List<Board> getPostByUserId(long userId) {
        return postDao.findByUserId(userId);
    }

    //게시글 상세 보기
    @Override
    public Board getPost(long postId) {
        return postDao.findByPostId(postId);
    }



    @Override
    public Board createPost(long id, BoardPostDto dto) {
        Board board = new Board();
        board.setTitle(dto.getTitle());
        board.setText(dto.getText());
        board.setUserId(id);
        
        //내용이 비어 있는지 확인
        if(board.getTitle() == null || board.getText() == null){
            throw new RuntimeException("내용을 작성해주세요.");
        }
        
        return postDao.insert(board);
    }

    @Override
    public Board updatePost(long userId, long postId, BoardPostDto dto) {
        Board board = postDao.findByPostId(postId);
        
        //수정한 사람의 id 와 dto id 가 다르다면 수정할 수 없음.
        if(userId != board.getUserId()){
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        board.setText(dto.getText());
        board.setTitle(dto.getTitle());
        board.setUpdated_at(dto.getUpdatedAt());

        return postDao.update(board);
    }

    @Override
    public int deletePost(long userId, long postId) {
        Board board = postDao.findByPostId(postId);
        
        if(userId != board.getUserId()){
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        return postDao.deletePostByPostId(postId);
    
    }


    @Override
    public void increaseViewCount() {
        // TODO 조회수 해야함
        throw new UnsupportedOperationException("Unimplemented method 'increaseViewCount'");
    }



}