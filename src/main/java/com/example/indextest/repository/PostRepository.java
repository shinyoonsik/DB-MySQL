package com.example.indextest.repository;

import com.example.indextest.dto.DailyPostCountDTO;
import com.example.indextest.dto.DailyPostCountReqDTO;
import com.example.indextest.entity.Post;
import com.example.indextest.util.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    static final String TABLE = "Post";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<Post> POST_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Post.builder()
            .id(resultSet.getLong("id"))
            .memberId(resultSet.getLong("memberId"))
            .contents(resultSet.getString("contents"))
            .createdDate(resultSet.getObject("createdDate", LocalDate.class))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
            .likeCount(resultSet.getLong("likeCount"))
            .version(resultSet.getLong("version"))
            .build();

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

    public Page<Post> findAllByMemberId(Long memberId, Pageable pageable) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        String sql = String.format("""
                select *
                from %s
                where memberId = :memberId
                order by %s
                limit :size
                offset :offset
                """, TABLE, PageHelper.orderBy(pageable.getSort()));

        List<Post> posts = namedParameterJdbcTemplate.query(sql, params, POST_ROW_MAPPER);

        return new PageImpl<Post>(posts, pageable, getTotalCount(memberId));
    }

    private Long getTotalCount(Long memberId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId);

        String sql = String.format("""
                select count(id)
                from %s
                where memberId = :memberId
                """, TABLE);

        return namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
    }

    public List<Post> findAllByMemberIdAndOrderByIdAsc(Long memberId, int size) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", size);

        // 커서기반 페이징에서는 cursor key를 기준으로 반드시 정렬되어있어야 한다
        String sql = String.format("""
                select *
                from %s
                where memberId = :memberId
                order by id asc
                limit :size
                """, TABLE);

        return namedParameterJdbcTemplate.query(sql, params, POST_ROW_MAPPER);
    }

    public List<Post> findAllByMemberIdAndGTKeyOrderByIdAsc(Long memberId, Long key, int size) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("key", key)
                .addValue("size", size);

        // 커서기반 페이징에서는 cursor key를 기준으로 반드시 정렬되어있어야 한다
        String sql = String.format("""
                select *
                from %s
                where memberId = :memberId and id > :key
                order by id asc
                limit :size
                """, TABLE);

        return namedParameterJdbcTemplate.query(sql, params, POST_ROW_MAPPER);
    }

    public List<Post> findAllByMemberIdAOrderByCreatedDate(Long memberId, int size) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", size);

        // 커서기반 페이징에서는 cursor key를 기준으로 반드시 정렬되어있어야 한다
        String sql = String.format("""
                select *
                from %s
                where memberId = :memberId
                order by createdAt desc
                limit :size
                """, TABLE);

        return namedParameterJdbcTemplate.query(sql, params, POST_ROW_MAPPER);
    }

    public List<Post> findAllByMemberIdAndLTKeyOrderByCreatedDate(Long memberId, LocalDateTime createdAt, int size) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("key", createdAt)
                .addValue("size", size);

        String sql = String.format("""
                select *
                from %s
                where memberId = :memberId and createdAt <= :key
                order by createdAt desc
                limit :size
                """, TABLE);

        return namedParameterJdbcTemplate.query(sql, params, POST_ROW_MAPPER);
    }

    public List<Post> findAllByMemberIdsAndOrderByIdDesc(List<Long> memberIds, int size) {
        if (memberIds.isEmpty()) {
            return Collections.emptyList();
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberIds", memberIds)
                .addValue("size", size);

        String sql = String.format("""
                select *
                from %s
                where memberId in (:memberIds)
                order by id desc
                limit :size
                """, TABLE);

        return namedParameterJdbcTemplate.query(sql, params, POST_ROW_MAPPER);
    }

    public List<Post> findAllByLessThanIdAndMemberIdsAndOrderByIdDesc(List<Long> memberIds, int size, Long id) {
        if (memberIds.isEmpty()) {
            return Collections.emptyList();
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberIds", memberIds)
                .addValue("size", size)
                .addValue("key", id);

        String sql = String.format("""
                select *
                from %s
                where memberId in (:memberIds) and id < :key
                order by id desc
                limit :size
                """, TABLE);

        return namedParameterJdbcTemplate.query(sql, params, POST_ROW_MAPPER);
    }

    public Optional<Post> findById(Long postId, boolean requiredLock) {
        String sql = String.format(" select * from %s where id = :postId ", TABLE);
        if(requiredLock){
            sql += "for share";
        }
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("postId", postId);
        Post post = namedParameterJdbcTemplate.queryForObject(sql, params, POST_ROW_MAPPER);
        return Optional.ofNullable(post);
    }

    public Post update(Post post){
        String sql = String.format("""
                update %s set
                    memberId = :memberId,
                    contents = :contents,
                    createdDate = :createdDate,
                    likeCount = :likeCount,
                    createdAt = :createdAt,
                    version = :version + 1
                where id = :id and version = :version
                """, TABLE);
        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(post);
        int updatedColumnCnt = namedParameterJdbcTemplate.update(sql, params);

        // TODO 1회 재시도 로직 추가
        if(updatedColumnCnt == 0) throw new RuntimeException("업데이트 실패");

        return post;
    }

}
