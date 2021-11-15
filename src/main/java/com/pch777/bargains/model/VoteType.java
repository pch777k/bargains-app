package com.pch777.bargains.model;

public enum VoteType {
	DOWNVOTE(-1), UPVOTE(1);

    private int direction;

    VoteType(int direction) {
    }

    public Integer getDirection() {
        return direction;
    }
}
