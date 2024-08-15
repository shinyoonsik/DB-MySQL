package com.example.indextest.service;

import com.example.indextest.common.CursorRequest;
import com.example.indextest.common.CursorRequestByCreatedDate;
import com.example.indextest.common.PageCursor;
import com.example.indextest.common.PageCursorByCreatedDate;
import com.example.indextest.dto.DailyPostCountDTO;
import com.example.indextest.dto.DailyPostCountReqDTO;
import com.example.indextest.dto.PostDTO;
import com.example.indextest.entity.Post;
import com.example.indextest.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    private final String TABLE = "Post";

    public List<DailyPostCountDTO> getDailyPostCounts(DailyPostCountReqDTO reqDTO) {
        return this.postRepository.groupByCreatedDate(reqDTO);
    }

    public Long create(PostDTO postDto) {
        Post post = Post.builder()
                .memberId(postDto.memberId())
                .contents(postDto.contents())
                .build();

        return this.postRepository.save(post).getId();
    }

    public Page<Post> getPosts(Long memberId, Pageable pageable) {
        return this.postRepository.findAllByMemberId(memberId, pageable);
    }

    public PageCursor<Post> getPostsByCursorKey(Long memberId, CursorRequest cursorRequest) {
        List<Post> posts = findAllBy(memberId, cursorRequest);
        long nextKey = posts.stream()
                .mapToLong(Post::getId)
                .max()
                .orElse(CursorRequest.NONE_KEY);// 조회했는데 data가 없는 경우

        return new PageCursor<>(cursorRequest.next(nextKey), posts);
    }

    public PageCursorByCreatedDate<Post> getPostsByCreatedDate(Long memberId, CursorRequestByCreatedDate cursorRequest){
        List<Post> posts = findAllByCreatedDate(memberId, cursorRequest);
        LocalDateTime nextKey = posts.stream()
                .map(Post::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(CursorRequestByCreatedDate.NONE_KEY);

        LocalDateTime newKey = null;
        if(!ObjectUtils.isEmpty(nextKey)) newKey = nextKey.plusSeconds(1);
        return new PageCursorByCreatedDate<>(cursorRequest.next(newKey), posts);
    }

    private List<Post> findAllByCreatedDate(Long memberId, CursorRequestByCreatedDate cursorRequest) {
        if(cursorRequest.hasKey()){
            return this.postRepository.findAllByMemberIdAndLTKeyOrderByCreatedDate(memberId, cursorRequest.createdAt(), cursorRequest.size());
        }
        return this.postRepository.findAllByMemberIdAOrderByCreatedDate(memberId, cursorRequest.size());
    }

    private List<Post> findAllBy(Long memberId, CursorRequest cursorRequest) {
        if (cursorRequest.hasKey()) {
            return this.postRepository.findAllByMemberIdAndGTKeyOrderByIdAsc(memberId, cursorRequest.key(), cursorRequest.size());
        }
        return this.postRepository.findAllByMemberIdAndOrderByIdAsc(memberId, cursorRequest.size());
    }


}
