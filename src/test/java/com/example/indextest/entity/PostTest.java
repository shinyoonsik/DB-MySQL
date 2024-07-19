package com.example.indextest.entity;

import com.example.indextest.repository.PostRepository;
import com.example.indextest.util.PostFixtureFactory;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

// build전에 gradle test 작업에서는 아래의 테스트 코드는 제외되어야 한다
@SpringBootTest
class PostTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    public void bulkInsert() {

        EasyRandom easyRandom = PostFixtureFactory.get(
                4L,
                LocalDate.of(1970, 1, 1),
                LocalDate.of(2022, 2, 1)
        );

        StopWatch objStopWatch = new StopWatch();
        objStopWatch.start();
        List<Post> posts = IntStream.range(0, 1000000)
                .parallel() // 객체 생성이 오래 걸리므로 stream을 병렬로 구성해서 실행
                .mapToObj(i -> easyRandom.nextObject(Post.class))
                .toList();
        objStopWatch.stop();
        System.out.println("객체 생성시간 = " + objStopWatch.getTotalTimeSeconds());

        StopWatch insertStopWatch = new StopWatch();
        insertStopWatch.start();
        this.postRepository.bulkInsert(posts);
        insertStopWatch.stop();
        System.out.println("객체 삽입시간 = " + insertStopWatch.getTotalTimeSeconds());

    }
}