package com.example.indextest.repository;

import com.example.indextest.dto.DailyPostCountDTO;
import com.example.indextest.dto.DailyPostCountReqDTO;
import com.example.indextest.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    static final String TABLE = "Post";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<DailyPostCountDTO> DAILY_POST_COUNT_DTO_ROW_MAPPER = (ResultSet resultSet, int rowNums) -> new DailyPostCountDTO(
            resultSet.getLong("memberId"),
            resultSet.getObject("createdDate", LocalDate.class),
            resultSet.getLong("count")
    );

    public List<DailyPostCountDTO> groupByCreatedDate(DailyPostCountReqDTO request) {
        String sql = String.format("""
                SELECT createdDate, memberId, count(id) as 'count'
                FROM `%s`
                WHERE memberId = :memberId and createdDate between :firstDate and :lastDate
                GROUP BY memberId, createdDate
                """, TABLE);
        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(request);

        return this.namedParameterJdbcTemplate.query(sql, params, DAILY_POST_COUNT_DTO_ROW_MAPPER);
    }

    public Post save(Post post) {
        if (Objects.isNull(post.getId()))
            return insert(post);
        throw new UnsupportedOperationException("Post는 갱신할 수 없다");
    }

    public Post insert(Post post) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE)
                .usingGeneratedKeyColumns("id");

        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(post);
        long id = jdbcInsert.executeAndReturnKey(params).longValue();

        return Post.builder()
                .id(id)
                .memberId(post.getMemberId())
                .contents(post.getContents())
                .createdDate(post.getCreatedDate())
                .createdAt(post.getCreatedAt())
                .build();
    }

    /*
        bulkinsert테스트시에 heap memory설정및 모니터링

        mac: cmd + shift + a -> memory indicator: ON
        mac: cmd + shift + a -> Edit Custom VM => -Xmx2048m 사이즈 조정(필요시)
     */
    public void bulkInsert(List<Post> posts) {
        String sql = String.format("""
                INSERT INTO `%s` (memberId, contents, createdDate, createdAt)
                VALUES (:memberId, :contents, :createdDate, :createdAt)
                """, TABLE);

        SqlParameterSource[] params = posts.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, params);
    }
}
