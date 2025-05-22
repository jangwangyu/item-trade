package org.example.itemtrade.dto.request;


import org.example.itemtrade.domain.Comment;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;

public record CommentCreateRequest(String content) {

    public Comment of(Member writer, ItemPost itemPost) {
        return Comment.builder()
            .content(this.content)
            .writer(writer)
            .itemPost(itemPost)
            .build();
    }
}