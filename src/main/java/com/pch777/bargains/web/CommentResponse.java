package com.pch777.bargains.web;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponse {
	Long commentId;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;
	String content;
	Long bargainId;
	String bargainTitle;
	Long userId;
	String userNickname;
}
