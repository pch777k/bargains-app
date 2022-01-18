package com.pch777.bargains.web;

import java.time.LocalDateTime;

import com.pch777.bargains.model.VoteType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteResponse {
	private Long voteId;
	private VoteType voteType;
	private LocalDateTime createdAt;
	private Long bargainId;
	private String bargainTitle;
	private Long userId;
	private String userNickname;
}
