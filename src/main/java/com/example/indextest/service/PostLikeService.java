package com.example.indextest.service;

import com.example.indextest.dto.MemberDTO;
import com.example.indextest.entity.Post;
import com.example.indextest.entity.PostLike;
import com.example.indextest.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;

    public Long create(Post post, MemberDTO memberDTO){
        PostLike postLike = PostLike.builder()
                .postId(post.getId())
                .memberId(memberDTO.id())
                .build();

        return postLikeRepository.save(postLike).getPostId();
    }
}
