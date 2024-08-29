package com.example.indextest.repository;

import com.example.indextest.entity.Member;
import com.example.indextest.entity.MemberHistory;
import com.example.indextest.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<Member> MEMBER_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Member.builder()
            .id(resultSet.getLong("id"))
            .email(resultSet.getString("email"))
            .nickname(resultSet.getObject("nickname", String.class))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
            .build();

    private static final RowMapper<MemberHistory> MEMBER_HISTORY_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> MemberHistory.builder()
            .id(resultSet.getLong("id"))
            .memberId(resultSet.getLong("memberId"))
            .nickname(resultSet.getObject("nickname", String.class))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
            .build();



    public Member save(Member member){
        if(ObjectUtils.isEmpty(member.getId())){
            return insert(member);
        }
        throw new UnsupportedOperationException("Member는 갱신할 수 없다");
    }

    private Member insert(Member member){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName("Member")
                .usingGeneratedKeyColumns("id");

        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(member);
        long id = jdbcInsert.executeAndReturnKey(params).longValue();

        return Member.builder()
                .id(id)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
                .build();
    }

    public MemberHistory save(MemberHistory memberHistory){
        if(ObjectUtils.isEmpty(memberHistory.getId())){
            return insert(memberHistory);
        }
        throw new UnsupportedOperationException("Member는 갱신할 수 없다");
    }

    private MemberHistory insert(MemberHistory memberHistory){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName("MemberHistory")
                .usingGeneratedKeyColumns("id");

        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(memberHistory);
        long id = jdbcInsert.executeAndReturnKey(params).longValue();

        return MemberHistory.builder()
                .id(id)
                .memberId(memberHistory.getMemberId())
                .nickname(memberHistory.getNickname())
                .createdAt(memberHistory.getCreatedAt())
                .build();
    }

}
