package com.example.indextest.controller;

import com.example.indextest.dto.DailyPostCountDTO;
import com.example.indextest.dto.DailyPostCountReqDTO;
import com.example.indextest.dto.PostDTO;
import com.example.indextest.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
