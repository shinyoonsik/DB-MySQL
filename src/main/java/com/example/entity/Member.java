package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Member")
public class Member {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id")
    private Long teamId;

    private String name;
    private int popularity; // 인기 투표 점수

    @Version
    private Long version;

    public Member(Long teamId, String name, int popularity, Long version) {
        this.teamId = teamId;
        this.name = name;
        this.popularity = popularity;
        this.version = version;
    }

    public void increasePopularity(int vote) {
        if (vote < 0) throw new IllegalArgumentException("인기투표는 0보다 작을 수 없다.");
        if (this.popularity + vote > Integer.MAX_VALUE) throw new IllegalArgumentException("인기 점수는 2^31 - 1보다 클 수 없다.");
        this.popularity += vote;
    }

}
