package com.example.indextest.util;


import com.example.indextest.entity.Post;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.function.Predicate;

import static org.jeasy.random.FieldPredicates.*;

public class PostFixtureFactory {

    public static EasyRandom get(Long memberId, LocalDate firstDate, LocalDate lastDate){
        // Predicate<Field>를 정의함으로써, 특정 필드에 대한 조건을 정의할 수 있다.
        // Field 객체는 Java 리플렉션 API의 일부로, 클래스의 필드를 나타낸다.

        Predicate<Field> idPredicate = named("id")
                .and(ofType(Long.class))
                .and(inClass(Post.class));

        Predicate<Field> memberIdPredicate = named("memberId")
                .and(ofType(Long.class))
                .and(inClass(Post.class));

        EasyRandomParameters param = new EasyRandomParameters()
                .excludeField(idPredicate)
                .dateRange(firstDate, lastDate)
                .randomize(memberIdPredicate, () -> memberId);

        return new EasyRandom(param);
    }


}
