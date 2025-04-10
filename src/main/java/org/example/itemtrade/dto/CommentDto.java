package org.example.itemtrade.dto;


import java.time.LocalDateTime;
import org.example.itemtrade.domain.Comment;

public record CommentDto(
    Long id,
    String writerNickname,
    String content,
    LocalDateTime createdAt
) {
    public static CommentDto from(Comment comment) {
        return new CommentDto(
            comment.getId(),
            comment.getWriter().getNickName(),
            comment.getContent(),
            comment.getCreatedAt()
        );
    }
}