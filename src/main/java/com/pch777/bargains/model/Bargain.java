package com.pch777.bargains.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Bargain extends AuditModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@Column(length = 2147483647)
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String description;

	private Double reducePrice;

	private Double normalPrice;

	private Double delivery;

	private String coupon;
	
	private String link;
	
	private Boolean closed;

	private LocalDate startBargain;

	private LocalDate endBargain;

	@Builder.Default
	private Integer voteCount = 0;

	@Enumerated
	private Category category;
	
	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name = "bargain_photo_id")
	private BargainPhoto bargainPhoto;
	
	@ManyToOne
	@JoinColumn(name = "shop_id")
	private Shop shop;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonIgnoreProperties({"createdAt","updatedAt","email","password","userPhotoId","roles",
							"comments", "bargains", "votes", "activities" })
	private User user;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "bargain_id")
	@JsonIgnore
	private List<Comment> comments;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "bargain_id")
	@JsonIgnore
	private List<Activity> activities;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "bargain_id")
	@JsonIgnore
	private List<Vote> votes;

}
