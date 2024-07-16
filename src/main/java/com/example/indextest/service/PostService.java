package com.example.indextest.service;

import com.example.indextest.dto.DailyPostCountDTO;
import com.example.indextest.dto.DailyPostCountReqDTO;
import com.example.indextest.dto.PostDTO;
import com.example.indextest.entity.Post;
import com.example.indextest.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public List<DailyPostCountDTO> getDailyPostCounts(DailyPostCountReqDTO reqDTO){
        return this.postRepository.groupByCreatedDate(reqDTO);
    }

    public Long create(PostDTO postDto){
        Post post = Post.builder()
                .memberId(postDto.memberId())
                .contents(postDto.contents())
                .build();

        return this.postRepository.save(post).getId();
    }
}
