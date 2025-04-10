package org.example.itemtrade.dto.request;


import org.example.itemtrade.domain.Comment;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;

public class CommentCreateRequest {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Comment toEntity(Member writer, ItemPost itemPost) {
        return Comment.builder()
            .content(this.content)
            .writer(writer)
            .itemPost(itemPost)
            .build();
    }
}