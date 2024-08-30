package me.snaptime.user.repository;

import com.querydsl.core.Tuple;

import java.util.List;

public interface UserQdslRepository {

    List<Tuple> searchUserPaging(String searchKeyword, Long pageNum);
}
