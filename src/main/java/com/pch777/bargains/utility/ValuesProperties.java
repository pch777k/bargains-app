package com.pch777.bargains.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class ValuesProperties {
	
	private String noUserPhotoUrl;
	private String noBargainPhotoUrl;
	private int numberOfVotes;
	
	public ValuesProperties(@Value("${bargainapp.no-user-photo-url}") String noUserPhotoUrl, 
			@Value("${bargainapp.no-bargain-photo-url}") String noBargainPhotoUrl, 
			@Value("${bargainapp.number-of-votes}") int numberOfVotes) {
		this.noUserPhotoUrl = noUserPhotoUrl;
		this.noBargainPhotoUrl = noBargainPhotoUrl;
		this.numberOfVotes = numberOfVotes;
	}
	
	
}
