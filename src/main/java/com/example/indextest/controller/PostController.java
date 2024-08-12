package com.example.indextest.controller;

import com.example.indextest.dto.DailyPostCountDTO;
import com.example.indextest.dto.DailyPostCountReqDTO;
import com.example.indextest.dto.PostDTO;
import com.example.indextest.entity.Post;
import com.example.indextest.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("/daily-post-counts")
    public List<DailyPostCountDTO> getDailyPostCounts(
            @RequestParam Long memberId,
            @RequestParam LocalDate firstDate,
            @RequestParam LocalDate lastDate) {

        DailyPostCountReqDTO reqDTO = new DailyPostCountReqDTO(memberId, firstDate, lastDate);
        return this.postService.getDailyPostCounts(reqDTO);
    }

    @PostMapping
    public Long create(@RequestBody PostDTO postDTO) {
        return this.postService.create(postDTO);
    }

    // URL: http://localhost:8080/test/posts/members/3?page=1&size=10
    @GetMapping("/members/{memberId}")
    public Page<Post> getPosts(@PathVariable Long memberId,
                               @RequestParam Integer page,
                               @RequestParam Integer size) {
        return this.postService.getPosts(memberId, PageRequest.of(page, size));
    }


    // URL: http://localhost:8080/test/posts/sorted-members/3?page=1&size=10&sort=createdDate,desc&sort=id,asc
    @GetMapping("/sorted-members/{memberId}")
    public Page<Post> getSortedPosts(@PathVariable Long memberId,
                                     Pageable pageable) {
        return this.postService.getPosts(memberId, pageable);
    }

}
