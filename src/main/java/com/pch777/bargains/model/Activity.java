package com.pch777.bargains.model;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Activity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "created_at", columnDefinition = "TIMESTAMP", updatable = false)
    private LocalDateTime createdAt;
	
	@Enumerated
	private ActivityType activityType;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="bargain_id")
	@JsonIgnoreProperties({"createdAt","updatedAt","description", "reducePrice", "normalPrice", "delivery", "coupon", 
		"link","bargainPhotoId","closed","startBargain","endBargain","voteCount","category",
		"shop","user","comments","activities","votes"})
	private Bargain bargain;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	@JsonIgnoreProperties({"createdAt","updatedAt","email","password","userPhotoId","roles"})
	private User user;
	
}	
