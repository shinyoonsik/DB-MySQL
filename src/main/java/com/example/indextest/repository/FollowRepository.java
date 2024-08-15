package com.example.indextest.repository;

import com.example.indextest.entity.Follow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FollowRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<Follow> FOLLOW_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Follow.builder()
            .id(resultSet.getLong("id"))
            .fromMemberId(resultSet.getLong("fromMemberId"))
            .toMemberId(resultSet.getLong("toMemberId"))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
            .build();

    // 내가 follow하는 사람들의 리스트 구하기
    public List<Follow> findAllByMemberId(Long memberId){
        if(ObjectUtils.isEmpty(memberId)) return Collections.emptyList();

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId);

        String sql = String.format("""
                select *
                from Follow
                where fromMemberId = :memberId
                """);

        return namedParameterJdbcTemplate.query(sql, params, FOLLOW_ROW_MAPPER);
    }
}
