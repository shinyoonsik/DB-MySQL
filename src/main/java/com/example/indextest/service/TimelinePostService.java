package com.example.indextest.service;

import com.example.indextest.common.CursorRequest;
import com.example.indextest.common.PageCursor;
import com.example.indextest.entity.Follow;
import com.example.indextest.entity.Post;
import com.example.indextest.repository.FollowRepository;
import com.example.indextest.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TimelinePostService {

    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    /*
        1. memberId -> follow조회
        2. 1번 결과로 게시물 조회
     */
    public PageCursor<Post> execute(Long memberId, CursorRequest cursorRequest) {
        List<Long> followingIds = this.followRepository.findAllByMemberId(memberId).stream()
                .map(Follow::getToMemberId)
                .toList();

        return getPosts(followingIds, cursorRequest);
    }

    public PageCursor<Post> getPosts(List<Long> memberIds, CursorRequest cursorRequest) {
        List<Post> posts = findAllBy(memberIds, cursorRequest);
        long nextKey = posts.stream()
                .mapToLong(Post::getId)
                .min()
                .orElse(CursorRequest.NONE_KEY);
        return new PageCursor<Post>(cursorRequest.next(nextKey), posts);
    }

    private List<Post> findAllBy(List<Long> memberIds, CursorRequest cursorRequest) {
        if (cursorRequest.hasKey()) {
            return this.postRepository.findAllByLessThanIdAndMemberIdsAndOrderByIdDesc(memberIds, cursorRequest.size(), cursorRequest.key());
        }
        return this.postRepository.findAllByMemberIdsAndOrderByIdDesc(memberIds, cursorRequest.size());
    }
}
