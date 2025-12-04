package com.mayonedev.battle.domain.user.dao;

import com.mayonedev.battle.domain.user.entity.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FriendDao {
    void insert(Friend friend);

    List<Friend> findAllByUserId(Long userId);

    void delete(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
