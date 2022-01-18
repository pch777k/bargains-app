package com.pch777.bargains.web;

import com.pch777.bargains.model.ActivityType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActivityResponse {
	private Long activityId;
	private Long bargainId;
	private String bargainTitle;
	private Long userId;
	private String userNickname;
	private ActivityType activity_type;
}
