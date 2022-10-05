package com.pch777.bargains.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pch777.bargains.dto.PasswordDto;
import com.pch777.bargains.dto.PhotoFileDto;
import com.pch777.bargains.dto.UserDto;
import com.pch777.bargains.dto.UserProfileDto;
import com.pch777.bargains.dto.VoteDto;
import com.pch777.bargains.exception.ForbiddenException;
import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.Activity;
import com.pch777.bargains.model.Bargain;
import com.pch777.bargains.model.Comment;
import com.pch777.bargains.model.User;
import com.pch777.bargains.model.UserPhoto;
import com.pch777.bargains.model.Vote;
import com.pch777.bargains.model.VoteType;
import com.pch777.bargains.security.UserSecurity;
import com.pch777.bargains.service.ActivityService;
import com.pch777.bargains.service.BargainService;
import com.pch777.bargains.service.CommentService;
import com.pch777.bargains.service.UserPhotoService;
import com.pch777.bargains.service.UserService;
import com.pch777.bargains.service.VoteService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
public class AppController {

	private static final String ACCESS_DENIED = "Access denied";
	private static final String CREATED_AT = "createdAt";
	private static final String PROFILE = "profile";
	private static final String PHOTO_FILE_DTO = "photoFileDto";
	private static final String USER_PROFILE_DTO = "userProfileDto";
	private static final String PAGE_VOTES = "pageVotes";
	private static final String TOTAL_NEGATIVE_VOTES = "totalNegativeVotes";
	private static final String TOTAL_POSITIVE_VOTES = "totalPositiveVotes";
	private static final String RESULTS_FOUND = "resultsFound";
	private static final String NO_RESULTS_FOUND = "noResultsFound";
	private static final String VOTE_DTO = "voteDto";
	private static final String TOTAL_VOTES = "totalVotes";
	private static final String TOTAL_COMMENTS = "totalComments";
	private static final String TOTAL_DISPLAY_BARGAINS = "totalDisplayBargains";
	private static final String TOTAL_BARGAINS = "totalBargains";
	private static final String TOTAL_ACTIVITIES = "totalActivities";
	private static final String BARGAINS = "bargains";
	private static final String TITLE = "title";
	private static final String LOGGED_USER = "loggedUser";
	private static final String TOTAL_PAGES = "totalPages";
	private static final String TOTAL_USERS = "totalUsers";
	private static final String PAGE_USERS = "pageUsers";
	private static final String CURRENT_PAGE = "currentPage";
	private static final String PAGE_SIZE = "pageSize";
	private static final String CURRENT_SIZE = "currentSize";
	private static final String PROFILE_USER = "profileUser";
	private static final String CURRENT_USER = "currentUser";
	private static final String CLOSED = "closed";
	private static final String SIGNUP_FORM = "signup_form";
	private static final String REDIRECT_USERS = "redirect:/users/";
	private static final String CHANGE_PASSWORD_FORM = "change_password_form";
	private UserService userService;
	private UserPhotoService userPhotoService;
	private BargainService bargainService;
	private CommentService commentService;
	private VoteService voteService;
	private ActivityService activityService;
	private UserSecurity userSecurity;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	
	
    @GetMapping("/login") 
	public String showLoginForm() {		
		return "login";  
	}
    
	@GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserDto());         
        return SIGNUP_FORM;
    }
    
    @PostMapping("/process_register")
    @Transactional 
    public String processRegister(@Valid UserDto userDto, BindingResult bindingResult, Model model) throws ResourceNotFoundException {
    	if (bindingResult.hasErrors()) {
			return SIGNUP_FORM;
		}
    	
    	if (userService.isUserPresent(userDto.getEmail())) {
			model.addAttribute("exist", true);
			return SIGNUP_FORM;
		}
    	
    	if (userService.isUserNicknamePresent(userDto.getNickname())) {
			model.addAttribute("nicknameExist", true);
			return SIGNUP_FORM;
		}
    	
    	User user = userService.userDtoToUser(userDto);
    	userService.registerUser(user);
		
    	model.addAttribute("nickname", userDto.getNickname());
        return "register_success";
    }
    
    @GetMapping("/users")
    public String listUsers(Model model,
    		@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "12") int pageSize) {
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<User> pageUsers = userService.getUsers(pageable);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    			
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));    
		model.addAttribute(PROFILE_USER, userService.findUserByEmail(email));  
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(CURRENT_PAGE, page);
        model.addAttribute(PAGE_USERS, pageUsers);
        model.addAttribute(TOTAL_USERS, pageUsers.getTotalElements());
        model.addAttribute(TOTAL_PAGES, pageUsers.getTotalPages());
                
        return "users_list";
    }
    
    @GetMapping("/users/{userId}/delete")
	@Transactional
	public String deleteUserById(@PathVariable Long userId) throws ResourceNotFoundException { 
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
    	if(userSecurity.isAdmin(email)) {
    		userService.deleteUserById(userId);
    	} else {
			throw new ForbiddenException(ACCESS_DENIED);
		}
		return "redirect:/users";
	}
    
    @GetMapping("/users/{userId}/overview")
    public String showUserOverview(@PathVariable Long userId, Model model, 
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {
    	
    	Sort sort = Sort.by(CREATED_AT).descending();
    	Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
    	Page<Activity> pageActivitiesByUser = activityService.getActivitiesByUserId(pageable, userId);
    	
    	double average = 0;
    	int hottest = 0;
    	List<Bargain> listBargainsByUser = bargainService.getAllBargainsByUserId(userId);
    	if(!listBargainsByUser.isEmpty()) {
    		average = bargainService.getAllBargainsByUserId(userId)
    				.stream()
    				.collect(Collectors.averagingInt(Bargain :: getVoteCount));
    	
    		hottest = bargainService.getAllBargainsByUserId(userId)
    				.stream()
    				.max(Comparator.comparing(Bargain :: getVoteCount))
    				.get()
    				.getVoteCount();
    	}
    	    	  	    	   	
    	List<Comment> userComments = commentService.getAllCommentsByUserId(userId); 
    	List<Vote> userVotes = voteService.getAllVotesByUserId(userId); 
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		User profileUser = userService.findById(userId).orElseThrow();
    	
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(PROFILE_USER, profileUser); 	
		model.addAttribute("activities", pageActivitiesByUser);
		model.addAttribute(TOTAL_ACTIVITIES, pageActivitiesByUser.getTotalElements());
		model.addAttribute(TOTAL_BARGAINS, listBargainsByUser.size());
		model.addAttribute(TOTAL_COMMENTS, userComments.size());
		model.addAttribute(TOTAL_VOTES, userVotes.size());
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(CURRENT_PAGE, page);
		model.addAttribute(TOTAL_PAGES, pageActivitiesByUser.getTotalPages());
		model.addAttribute(VOTE_DTO, new VoteDto());
		model.addAttribute("average", (int)Math.round(average));
		model.addAttribute("hottest", hottest);	
    	
		return "user_overview";
    }
    
    @GetMapping("/users/{userId}/bargains")
    public String showBargainsByUser(@PathVariable Long userId, Model model, 
    		@RequestParam(defaultValue = "") String keyword,
    		@RequestParam(value = "ended", required = false) String ended,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {
    	
    	boolean noResultsFound = false;
		boolean resultsFound = false;
    	
    	User user = userService.findById(userId).orElseThrow();
    	
    	Sort sort = Sort.by("voteCount").descending();
    	Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
    	Page<Bargain> pageBargainsByUser = bargainService.getAllBargainsByTitleLikeByUserId(pageable, keyword, userId);
    	Long totalBargains = pageBargainsByUser.getTotalElements();
    	if(("on").equals(ended)) {
    		pageBargainsByUser = bargainService.getBargainsNotClosedByUserId(pageable, keyword, userId, false);
		}
    	Long totalDisplayBargains = pageBargainsByUser.getTotalElements();
    	List<Activity> userActivities = activityService.getActivitiesByUserId(userId);
    	List<Comment> userComments = commentService.getAllCommentsByUserId(userId);
    	List<Vote> userVotes = voteService.getAllVotesByUserId(userId);   
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
		if(keyword.length() > 0 && totalDisplayBargains == 0) {
			noResultsFound = true;			
		}
		
		if(keyword.length() > 0 && totalDisplayBargains > 0) {
			resultsFound = true;
		}
		
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(PROFILE_USER, user); 	
		model.addAttribute(TITLE, keyword);
		model.addAttribute(BARGAINS, pageBargainsByUser);
		model.addAttribute(TOTAL_ACTIVITIES, userActivities.size());
		model.addAttribute(TOTAL_BARGAINS, totalBargains);
		model.addAttribute(TOTAL_DISPLAY_BARGAINS, totalDisplayBargains);
		model.addAttribute(TOTAL_COMMENTS, userComments.size());
		model.addAttribute(TOTAL_VOTES, userVotes.size());
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(CURRENT_PAGE, page);		
		model.addAttribute(TOTAL_PAGES, pageBargainsByUser.getTotalPages());
		model.addAttribute(VOTE_DTO, new VoteDto());
		model.addAttribute(CLOSED, ended);
		model.addAttribute(NO_RESULTS_FOUND, noResultsFound);
		model.addAttribute(RESULTS_FOUND, resultsFound);
    	
		return "user_bargains";
    } 
    
    @GetMapping("/users/{userId}/bargains/new")
    public String showNewBargainsByUser(@PathVariable Long userId, Model model, 
    		@RequestParam(defaultValue = "") String keyword,
    		@RequestParam(value = "ended", required = false) String ended,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {
    	
    	boolean noResultsFound = false;
		boolean resultsFound = false;
    	
    	User profileUser = userService.findById(userId).orElseThrow();
    	
    	Sort sort = Sort.by(CREATED_AT).descending();
    	Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
    	Page<Bargain> pageBargainsByUser = bargainService.getAllBargainsByTitleLikeByUserId(pageable, keyword, userId);
    	Long totalBargains = pageBargainsByUser.getTotalElements();
    	if(("on").equals(ended)) {
    		pageBargainsByUser = bargainService.getBargainsNotClosedByUserId(pageable, keyword, userId, false);
		}
    	Long totalDisplayBargains = pageBargainsByUser.getTotalElements();
    	
    	List<Activity> userActivities = activityService.getActivitiesByUserId(userId);
    	List<Comment> userComments = commentService.getAllCommentsByUserId(userId);
    	List<Vote> userVotes = voteService.getAllVotesByUserId(userId);
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
		if(keyword.length() > 0 && totalDisplayBargains == 0) {
			noResultsFound = true;			
		}
		
		if(keyword.length() > 0 && totalDisplayBargains > 0) {
			resultsFound = true;
		}
		
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(PROFILE_USER, profileUser); 	
		model.addAttribute(TITLE, keyword);
		model.addAttribute(BARGAINS, pageBargainsByUser);
		model.addAttribute(TOTAL_ACTIVITIES, userActivities.size());
		model.addAttribute(TOTAL_BARGAINS, totalBargains);
		model.addAttribute(TOTAL_DISPLAY_BARGAINS, totalDisplayBargains);
		model.addAttribute(TOTAL_COMMENTS, userComments.size());
		model.addAttribute(TOTAL_VOTES, userVotes.size());
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(CURRENT_PAGE, page);	
		model.addAttribute(TOTAL_PAGES, pageBargainsByUser.getTotalPages());
		model.addAttribute(VOTE_DTO, new VoteDto());
		model.addAttribute(CLOSED, ended);
		model.addAttribute(NO_RESULTS_FOUND, noResultsFound);
		model.addAttribute(RESULTS_FOUND, resultsFound);
    	
		return "user_bargains_new";
    }
    
    @GetMapping("/users/{userId}/bargains/commented")
    public String showMostCommentedBargainsByUser(@PathVariable Long userId, Model model, 
    		@RequestParam(defaultValue = "") String keyword,
    		@RequestParam(value = "ended", required = false) String ended,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {
    	
    	boolean noResultsFound = false;
		boolean resultsFound = false;
    	
    	User profileUser = userService.findById(userId).orElseThrow();
    	
       	Pageable pageable = PageRequest.of(page - 1, pageSize);
    	Page<Bargain> pageBargainsByUserIdOrderByCommentSize = bargainService.getBargainsMostCommentedByUserId(pageable, keyword, userId);
    	Long totalBargains = pageBargainsByUserIdOrderByCommentSize.getTotalElements();
    	if(("on").equals(ended)) {
    		pageBargainsByUserIdOrderByCommentSize = bargainService.getBargainsNotClosedMostCommentedByUserId(pageable, keyword, userId, false);
		}
    	Long totalDisplayBargains = pageBargainsByUserIdOrderByCommentSize.getTotalElements();
    	
    	List<Activity> userActivities = activityService.getActivitiesByUserId(userId);
    	List<Comment> userComments = commentService.getAllCommentsByUserId(userId);
    	List<Vote> userVotes = voteService.getAllVotesByUserId(userId);
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
		if(keyword.length() > 0 && totalDisplayBargains == 0) {
			noResultsFound = true;			
		}
		
		if(keyword.length() > 0 && totalDisplayBargains > 0) {
			resultsFound = true;
		}
		
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(PROFILE_USER, profileUser); 	
		model.addAttribute(TITLE, keyword);
		model.addAttribute(BARGAINS, pageBargainsByUserIdOrderByCommentSize);
		model.addAttribute(TOTAL_ACTIVITIES, userActivities.size());
		model.addAttribute(TOTAL_BARGAINS, totalBargains);
		model.addAttribute(TOTAL_DISPLAY_BARGAINS, totalDisplayBargains);
		model.addAttribute(TOTAL_COMMENTS, userComments.size());
		model.addAttribute(TOTAL_VOTES, userVotes.size());
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(CURRENT_PAGE, page);	
		model.addAttribute(TOTAL_PAGES, pageBargainsByUserIdOrderByCommentSize.getTotalPages());
		model.addAttribute(VOTE_DTO, new VoteDto());
		model.addAttribute(CLOSED, ended);
		model.addAttribute(NO_RESULTS_FOUND, noResultsFound);
		model.addAttribute(RESULTS_FOUND, resultsFound);
    	
		return "user_bargains_commented";
    }
    
    @GetMapping("/users/{userId}/comments")
    public String showCommentsByUser(@PathVariable Long userId, Model model, 
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {
    	
    	User profileUser = userService.findById(userId).orElseThrow();
    	
    	Sort sort = Sort.by(CREATED_AT).descending();
    	Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
    	Page<Comment> pageComments = commentService.getAllCommentsByUser(pageable, profileUser);
    	List<Activity> userActivities = activityService.getActivitiesByUserId(userId);
    	List<Vote> userVotes = voteService.getAllVotesByUserId(userId);
    	int totalUserBargains = bargainService.getAllBargainsByUserId(userId).size();
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(PROFILE_USER, profileUser); 	
		model.addAttribute(TOTAL_COMMENTS, pageComments.getTotalElements());
		model.addAttribute(TOTAL_VOTES, userVotes.size());
		model.addAttribute(TOTAL_ACTIVITIES, userActivities.size());
		model.addAttribute("pageComments", pageComments);
		model.addAttribute(TOTAL_BARGAINS, totalUserBargains);
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(CURRENT_PAGE, page);		
		model.addAttribute(TOTAL_PAGES, pageComments.getTotalPages());
		    	
		return "user_comments";
    }   
    
    @GetMapping("/users/{userId}/votes")
    public String showVotesByUser(@PathVariable Long userId, Model model, 
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "12") int pageSize) {
    	
    	User profileUser = userService.findById(userId).orElseThrow();
    	
    	Sort sort = Sort.by(CREATED_AT).descending();
    	Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
    	Page<Vote> pageVotes = voteService.getVotesByUserId(pageable, userId);
    	List<Activity> userActivities = activityService.getActivitiesByUserId(userId);
    	
    	List<Comment> userComments = commentService.getAllCommentsByUserId(userId);
    	int totalUserBargains = bargainService.getAllBargainsByUserId(userId).size();
    	int totalPositiveVotes = voteService.getAllByVoteTypeAndUserId(VoteType.UPVOTE, userId).size();
    	int totalNegativeVotes = voteService.getAllByVoteTypeAndUserId(VoteType.DOWNVOTE, userId).size();
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(PROFILE_USER, profileUser); 	
		model.addAttribute(TOTAL_VOTES, pageVotes.getTotalElements());
		model.addAttribute(TOTAL_POSITIVE_VOTES, totalPositiveVotes);
		model.addAttribute(TOTAL_NEGATIVE_VOTES, totalNegativeVotes);
		model.addAttribute(PAGE_VOTES, pageVotes);
		model.addAttribute(TOTAL_ACTIVITIES, userActivities.size());
		model.addAttribute(TOTAL_BARGAINS, totalUserBargains);
		model.addAttribute(TOTAL_COMMENTS, userComments.size());
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(CURRENT_PAGE, page);		
		model.addAttribute(TOTAL_PAGES, pageVotes.getTotalPages());
		    	
		return "user_votes";
    }   
    
    @GetMapping("/users/{userId}/votes/plus")
    public String showPositiveVotesByUser(@PathVariable Long userId, Model model, 
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "12") int pageSize) {
    	
    	User profileUser = userService.findById(userId).orElseThrow();
    	
    	Sort sort = Sort.by(CREATED_AT).descending();
    	Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
    	Page<Vote> pageVotes = voteService.getVotesByVoteTypeAndUserId(pageable, VoteType.UPVOTE, userId);
    	int totalNegativeVotes = voteService.getAllByVoteTypeAndUserId(VoteType.DOWNVOTE, userId).size();
    	int totalVotes = voteService.getAllVotesByUserId(userId).size();
    	
    	List<Activity> userActivities = activityService.getActivitiesByUserId(userId);
    	List<Comment> userComments = commentService.getAllCommentsByUserId(userId);
    	int totalUserBargains = bargainService.getAllBargainsByUserId(userId).size();
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(PROFILE_USER, profileUser); 	
		model.addAttribute(TOTAL_VOTES, totalVotes);
		model.addAttribute(TOTAL_POSITIVE_VOTES, pageVotes.getTotalElements());
		model.addAttribute(TOTAL_NEGATIVE_VOTES, totalNegativeVotes);
		model.addAttribute(PAGE_VOTES, pageVotes);
		model.addAttribute(TOTAL_ACTIVITIES, userActivities.size());
		model.addAttribute(TOTAL_BARGAINS, totalUserBargains);
		model.addAttribute(TOTAL_COMMENTS, userComments.size());
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(CURRENT_PAGE, page);		
		model.addAttribute(TOTAL_PAGES, pageVotes.getTotalPages());
		    	
		return "user_votes_plus";
    }   
    
    @GetMapping("/users/{userId}/votes/minus")
    public String showNegativeVotesByUser(@PathVariable Long userId, Model model, 
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "12") int pageSize) {
    	
    	User profileUser = userService.findById(userId).orElseThrow();
    	
    	Sort sort = Sort.by(CREATED_AT).descending();
    	Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
    	Page<Vote> pageVotes = voteService.getVotesByVoteTypeAndUserId(pageable, VoteType.DOWNVOTE, userId);
    	
    	int totalVotes = voteService.getAllVotesByUserId(userId).size();
    	int totalPositiveVotes = voteService.getAllByVoteTypeAndUserId(VoteType.UPVOTE, userId).size();
    	
    	List<Activity> userActivities = activityService.getActivitiesByUserId(userId);
    	List<Comment> userComments = commentService.getAllCommentsByUserId(userId);
    	int totalUserBargains = bargainService.getAllBargainsByUserId(userId).size();
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(PROFILE_USER, profileUser); 	
		model.addAttribute(TOTAL_VOTES, totalVotes);
		model.addAttribute(TOTAL_POSITIVE_VOTES, totalPositiveVotes);
		model.addAttribute(TOTAL_NEGATIVE_VOTES, pageVotes.getTotalElements());
		model.addAttribute(PAGE_VOTES, pageVotes);
		model.addAttribute(TOTAL_ACTIVITIES, userActivities.size());
		model.addAttribute(TOTAL_BARGAINS, totalUserBargains);
		model.addAttribute(TOTAL_COMMENTS, userComments.size());
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(CURRENT_PAGE, page);		
		model.addAttribute(TOTAL_PAGES, pageVotes.getTotalPages());
		    	
		return "user_votes_minus";
    }   
    
    @GetMapping("/users/{userId}/profile")
	public String showUserProfile(@PathVariable Long userId, Model model) {
		
		User user = userService.findById(userId).orElseThrow();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();	
		if(userSecurity.isOwnerOrAdmin(user.getEmail(), email)) {	
	
			UserProfileDto userProfileDto = UserProfileDto.builder()
					.nickname(user.getNickname())
					.email(user.getEmail())
					.build(); 
			
			model.addAttribute(USER_PROFILE_DTO, userProfileDto);
			model.addAttribute(PHOTO_FILE_DTO, new PhotoFileDto());
			model.addAttribute(CURRENT_USER, userService.findUserByEmail(email)); 
		} else {
			throw new ForbiddenException(ACCESS_DENIED);
		}
		
		return PROFILE;
	}   
    
    @Transactional
    @RequestMapping("/users/{userId}/photo/edit") 
	public String updatePhoto(@PathVariable Long userId,
			 RedirectAttributes redirectAttributes,
			 @Valid @ModelAttribute(PHOTO_FILE_DTO) PhotoFileDto photoFileDto, 
			 BindingResult result, Model model,
			 @ModelAttribute(USER_PROFILE_DTO) UserProfileDto userProfileDto) throws IOException    {
    	
    	User user = userService.findById(userId).orElseThrow();
		
		if (result.hasErrors()) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String email = auth.getName();
			userProfileDto = UserProfileDto.builder()
					.nickname(user.getNickname())
					.email(user.getEmail())
					.build(); 
			model.addAttribute(USER_PROFILE_DTO, userProfileDto);
    		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
    		return PROFILE;
    	}
			
		UserPhoto userPhoto = UserPhoto.builder()
				.file(photoFileDto.getFileImage().getBytes())
				.filename(photoFileDto.getFileImage().getOriginalFilename())
				.contentType(photoFileDto.getFileImage().getContentType())
				.createdAt(LocalDate.now())
				.fileLength(photoFileDto.getFileImage().getSize())
				.build();
		userPhotoService.saveUserPhoto(userPhoto);
		user.setUserPhoto(userPhoto);
		redirectAttributes.addFlashAttribute("editedPhoto", "The photo has been edited successfully.");		
		
		return REDIRECT_USERS + userId + "/profile"; 	
	}
    
    @Transactional
    @RequestMapping("/users/{userId}/nickname")
	public String updateNickname(@PathVariable Long userId, 
			@Valid @ModelAttribute(USER_PROFILE_DTO) UserProfileDto userProfileDto,
			BindingResult bindingResult, Model model, 
			@ModelAttribute(PHOTO_FILE_DTO) PhotoFileDto photoFileDto, RedirectAttributes redirectAttributes)    {

    	User user = userService.findById(userId).orElseThrow();
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
    	if (bindingResult.hasErrors()) {
    		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
    		return PROFILE;
    	}
    	
    	if (user.getNickname().equals(userProfileDto.getNickname())) {
    		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
    		model.addAttribute("currentNickname", true);
			return PROFILE;
		}
    	
    	if (userService.isUserNicknamePresent(userProfileDto.getNickname()) 
    			&& !(user.getNickname().equals(userProfileDto.getNickname()))) {
    		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
    		model.addAttribute("nicknameExist", true);
			return PROFILE;
		}
    	
		user.setNickname(userProfileDto.getNickname());
		redirectAttributes.addFlashAttribute("editedNickname", "The nickname has been edited successfully.");	

		return REDIRECT_USERS + userId + "/profile"; 	
	}
    
    @GetMapping("/users/{userId}/password")
	public String showChangePassword(@PathVariable Long userId, Model model) {
		
		User user = userService.findById(userId).orElseThrow();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
			
		if(userSecurity.isOwnerOrAdmin(user.getEmail(), email)) {			
			model.addAttribute("passwordDto", new PasswordDto());
			model.addAttribute(CURRENT_USER, userService.findUserByEmail(email)); 			
		} else {
			throw new ForbiddenException(ACCESS_DENIED);
		}

		return CHANGE_PASSWORD_FORM;
	}  
    
    @Transactional
    @RequestMapping("/users/{userId}/password")
	public String updatePassword(@PathVariable Long userId,
			@Valid @ModelAttribute("passwordDto") PasswordDto passwordDto,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
    	if (bindingResult.hasErrors()) {
    		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
			return CHANGE_PASSWORD_FORM;
		}
    	
		User user = userService.findById(userId).orElseThrow();

    	if (!bCryptPasswordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
    		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
			model.addAttribute("oldPasswordNotCorrect", true);
			return CHANGE_PASSWORD_FORM;
		}
		
		user.setPassword(bCryptPasswordEncoder.encode(passwordDto.getNewPassword()));
		redirectAttributes.addFlashAttribute("changedPassword", "The password has been changed successfully.");
		return REDIRECT_USERS + userId + "/password"; 	
	}
    
    @GetMapping("users/{userId}/photo")
    public void getImage(@PathVariable("userId") Long userId, HttpServletResponse response) throws Exception {
        User user = userService.findById(userId).orElseThrow();
        UserPhoto userPhoto = userPhotoService.getUserPhotoById(user.getUserPhoto().getId())
        									.orElseThrow(ResourceNotFoundException::new);
        byte[] bytes = userPhoto.getFile();
        InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(bytes));
        String mimeType = URLConnection.guessContentTypeFromStream(inputStream);
        response.setContentType(mimeType);
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }
    
}
