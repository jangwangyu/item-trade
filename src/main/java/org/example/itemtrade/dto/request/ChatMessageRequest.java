package org.example.itemtrade.dto.request;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatMessageRequest {

    private String content;
    private Long senderId;
    private String type;



}