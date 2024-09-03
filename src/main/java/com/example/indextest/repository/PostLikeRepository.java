package com.example.indextest.repository;

import com.example.indextest.entity.Post;
import com.example.indextest.entity.PostLike;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class PostLikeRepository {
    private final String TABLE = "PostLike";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    static final RowMapper<PostLike> POST_LIKE_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> PostLike.builder()
            .id(resultSet.getLong("id"))
            .postId(resultSet.getLong("postId"))
            .memberId(resultSet.getLong("memberId"))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
            .build();

    public PostLike save(PostLike postLike){
        if(postLike.getId() == null){
            return insert(postLike);
        }
        throw new UnsupportedOperationException("PostLike를 업데이트할 수 없습니다.");
    }

    private PostLike insert(PostLike postLike){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE)
                .usingGeneratedKeyColumns("id");

        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(postLike);
        long id = jdbcInsert.executeAndReturnKey(params).longValue();

        return PostLike.builder()
                .id(id)
                .memberId(postLike.getMemberId())
                .postId(postLike.getPostId())
                .createdAt(postLike.getCreatedAt())
                .build();
    }



}
