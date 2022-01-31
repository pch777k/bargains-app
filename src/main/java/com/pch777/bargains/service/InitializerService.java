package com.pch777.bargains.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.ActivityType;
import com.pch777.bargains.model.Bargain;
import com.pch777.bargains.model.BargainPhoto;
import com.pch777.bargains.model.Category;
import com.pch777.bargains.model.Comment;
import com.pch777.bargains.model.Role;
import com.pch777.bargains.model.Shop;
import com.pch777.bargains.model.User;
import com.pch777.bargains.model.UserPhoto;
import com.pch777.bargains.model.VoteType;
import com.pch777.bargains.repository.RoleRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
public class InitializerService {

	private UserService userService;
	private UserPhotoService userPhotoService;
	private RoleRepository roleRepository;
	private ShopService shopService;
	private BargainService bargainService;
	private BargainPhotoService bargainPhotoService;
	private CommentService commentService;
	private VoteService voteService;
	private ActivityService activityService;
	private RestTemplate restTemplate;
	private final String GUEST_USER_PHOTO_URL;
	private final String NO_BARGAIN_PHOTO_URL;
	
	public InitializerService(UserService userService,
			UserPhotoService userPhotoService,
			RoleRepository roleRepository, 
			ShopService shopService,
			BargainService bargainService,
			BargainPhotoService bargainPhotoService,
			CommentService commentService, 
			VoteService voteService, 
			ActivityService activityService,
			RestTemplate restTemplate, 
			@Value("${bargainapp.no-user-photo-url}") String gUEST_USER_PHOTO_URL,
			@Value("${bargainapp.no-bargain-photo-url}") String nO_BARGAIN_PHOTO_URL) {
		this.userService = userService;
		this.userPhotoService = userPhotoService;
		this.roleRepository = roleRepository;
		this.shopService = shopService;
		this.bargainService = bargainService;
		this.bargainPhotoService = bargainPhotoService;
		this.commentService = commentService;
		this.voteService = voteService;
		this.activityService = activityService;
		this.restTemplate = restTemplate;
		this.NO_BARGAIN_PHOTO_URL = nO_BARGAIN_PHOTO_URL;
		this.GUEST_USER_PHOTO_URL = gUEST_USER_PHOTO_URL;
	}
	
	public void initUserData() {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("users.csv").getInputStream()))){
			CsvToBean<CsvUser> build = new CsvToBeanBuilder<CsvUser>(reader)
					.withType(CsvUser.class)
					.withIgnoreLeadingWhiteSpace(true)
					.build();
			build.stream().forEach(a -> {
				try {
					initUser(a);
				} catch (ResourceNotFoundException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException("Failed to parse csv file users.csv", e);
		}
	}
	
	public void initShopData() {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("shops.csv").getInputStream()))){
			CsvToBean<CsvShop> build = new CsvToBeanBuilder<CsvShop>(reader)
					.withType(CsvShop.class)
					.withIgnoreLeadingWhiteSpace(true)
					.build();
			build.stream().forEach(this::initShop);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to parse csv file shops.csv", e);
		}
	}
	
	public void initBargainData() {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("bargains.csv").getInputStream()))){
			CsvToBean<CsvBargain> build = new CsvToBeanBuilder<CsvBargain>(reader)
					.withType(CsvBargain.class)
					.withIgnoreLeadingWhiteSpace(true)
					.build();
			build.stream().forEach(b -> {
				try {
					initBargain(b);
				} catch (ResourceNotFoundException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException("Failed to parse csv file bargains.csv", e);
		}
	}
	
	public void initCommentData() {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("comments.csv").getInputStream()))){
			CsvToBean<CsvComment> build = new CsvToBeanBuilder<CsvComment>(reader)
					.withType(CsvComment.class)
					.withIgnoreLeadingWhiteSpace(true)
					.build();
			build.stream().forEach(this::initComment);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to parse csv file comments.csv", e);
		}
	}
	
	private void initUser(CsvUser csvUser) throws ResourceNotFoundException {
		if(!userService.isUserPresent(csvUser.getEmail())) {
		
			String url = csvUser.getPhoto();
			byte[] imageBytes = restTemplate.getForObject(url, byte[].class);
			UserPhoto userPhoto = UserPhoto.builder()
					.file(imageBytes)
					.filename(csvUser.getFilename())
					.contentType(csvUser.getContentType())
					.createdAt(LocalDate.now())
					.fileLength(imageBytes.length)
					.build();
			userPhotoService.saveUserPhoto(userPhoto);
			User user = new User(csvUser.getEmail(), csvUser.getNickname(), csvUser.getPassword(), userPhoto);
			userService.registerUser(user);

		}
	}
	
	public void initGuestUser() throws ResourceNotFoundException {
		if(!userService.isUserPresent("guest@demomail.com")) {
			UserPhoto userPhoto = userPhotoService.getUserPhotoById(1L).orElseThrow(ResourceNotFoundException::new);
			User user = new User("guest@demomail.com", "guest", "guest", userPhoto);
			userService.registerUser(user);
		}
	}
	
	public void initAdmin() throws ResourceNotFoundException {
		if(!userService.isUserPresent("admin@demomail.com")) {
			UserPhoto userPhoto = userPhotoService.getUserPhotoById(1L).orElseThrow(ResourceNotFoundException::new);
			User admin = new User("admin@demomail.com", "admin", "admin", userPhoto);
			userService.registerAdmin(admin);
		}
	}
	
	public void initPhotos() {
		byte[] userPhotoBytes = restTemplate.getForObject(GUEST_USER_PHOTO_URL, byte[].class);
		UserPhoto userPhoto = UserPhoto.builder()
				.file(userPhotoBytes)
				.filename("guest.png")
				.contentType("image/png")
				.fileLength(userPhotoBytes.length)
				.createdAt(LocalDate.now())
				.build();
		userPhotoService.saveUserPhoto(userPhoto);
		byte[] bargainPhotoBytes = restTemplate.getForObject(NO_BARGAIN_PHOTO_URL, byte[].class);
		BargainPhoto bargainPhoto = BargainPhoto.builder()
				.file(bargainPhotoBytes)
				.filename("no-bargain-photo.png")
				.contentType("image/png")
				.fileLength(bargainPhotoBytes.length)
				.createdAt(LocalDate.now())
				.build();
		bargainPhotoService.saveBargainPhoto(bargainPhoto);
	}
	
	public void initRoles() {
		if(roleRepository.findAll().isEmpty()) {
			roleRepository.save(new Role("USER"));
			roleRepository.save(new Role("ADMIN"));
		}	
	}
	
	public void initShop(CsvShop csvShop) {
		Shop shop = new Shop(csvShop.getName(), csvShop.getWebpage());
		shopService.addShop(shop);
	}
	
	private void initBargain(CsvBargain csvBargain) throws ResourceNotFoundException {
		String url = csvBargain.getPhoto();
	
		byte[] imageBytes = restTemplate.getForObject(url, byte[].class);
		BargainPhoto bargainPhoto = BargainPhoto.builder()
				.file(imageBytes)
				.filename(csvBargain.getFilename())
				.contentType(csvBargain.getContentType())
				.createdAt(LocalDate.now())
				.fileLength(imageBytes.length)
				.build();
			
		bargainPhotoService.saveBargainPhoto(bargainPhoto);
		int plusDays = randomStartBargain();
		
		Shop shop = shopService.getShopById(csvBargain.getShopId());
		
		Bargain bargain = Bargain.builder()
				.title(csvBargain.getTitle())
				.description(csvBargain.getDescription())
				.reducePrice(csvBargain.getReducePrice())
				.normalPrice(csvBargain.getNormalPrice())
				.delivery(csvBargain.getDelivery())
				.coupon(csvBargain.getCoupon())
				.link(csvBargain.getLink())
				.bargainPhoto(bargainPhoto)
				.closed(csvBargain.getClosed())
				.voteCount(0)				
				.startBargain(LocalDate.now().plusDays(plusDays))
				.endBargain(LocalDate.now().plusDays(randomEndBargain(plusDays)))
				.category(csvBargain.getCategory())
				.shop(shop)
				.user(userService.randomUser())
				.build(); 
		
		bargainService.addBargain(bargain);
		activityService.addActivity(bargain.getUser(), bargain.getCreatedAt(), bargain, ActivityType.BARGAIN);
	}
	
	private void initComment(CsvComment csvComment) {
		Comment comment = new Comment(csvComment.getContent());
		comment.setBargain(randomBargain());
		comment.setUser(userService.randomUser());
		commentService.addComment(comment);	
		activityService.addActivity(comment.getUser(), comment.getCreatedAt(), comment.getBargain(), ActivityType.COMMENT);
	}
	
	
	public Bargain randomBargain() {
		List<Bargain> bargains = bargainService.getAllBargains();
		Random rand = new Random();
		return bargains.get(rand.nextInt(bargains.size()));
	}
		
	public void randomVote() throws Exception {
		Random rand = new Random();
		int number = rand.nextInt(10);
		VoteType voteType = VoteType.UPVOTE;
		if(number % 5 == 0) voteType = VoteType.DOWNVOTE;
		voteService.initVote(randomBargain().getId(), userService.randomUser().getId(), voteType);
	}
	
	public void initVoteData(int amountOfVotes) throws Exception {		
		for(int i=0; i<amountOfVotes; i++) {
			randomVote();
		}		
	}
	
	public int randomEndBargain(int startBargain) {
		Random rand = new Random();
		int number = rand.nextInt(14);
		return number + startBargain;
	}
	
	public int randomStartBargain() {
		Random rand = new Random();
		return rand.nextInt(7);
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CsvUser {
		
		@CsvBindByName
		private String email;
		
		@CsvBindByName
		private String nickname;
		
		@CsvBindByName
		private String password;
		
		@CsvBindByName
		private String photo;
		
		@CsvBindByName
		private String filename;
		
		@CsvBindByName
		private String contentType;
		
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CsvShop {
		
		@CsvBindByName
		private String name;
		
		@CsvBindByName
		private String webpage;
		
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CsvBargain {
		
		@CsvBindByName
		private String title;
		
		@CsvBindByName
		private String description;
		
		@CsvBindByName
		private double reducePrice;
		
		@CsvBindByName
		private double normalPrice;
		
		@CsvBindByName
		private double delivery;
		
		@CsvBindByName
		private String coupon;
		
		@CsvBindByName
		private String link;
		
		@CsvBindByName
		private String photo;
		
		@CsvBindByName
		private String filename;
		
		@CsvBindByName
		private String contentType;
		
		@CsvBindByName
		private Boolean closed;
		
		@CsvBindByName
		private Category category;
		
		@CsvBindByName
		private Long shopId;
		
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CsvComment {
		
		@CsvBindByName
		private String content;
		
	}

	
}
