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

import com.pch777.bargains.exception.ForbiddenException;
import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.Activity;
import com.pch777.bargains.model.Bargain;
import com.pch777.bargains.model.Comment;
import com.pch777.bargains.model.PasswordDto;
import com.pch777.bargains.model.PhotoFileDto;
import com.pch777.bargains.model.User;
import com.pch777.bargains.model.UserDto;
import com.pch777.bargains.model.UserPhoto;
import com.pch777.bargains.model.UserProfileDto;
import com.pch777.bargains.model.Vote;
import com.pch777.bargains.model.VoteDto;
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
        return "signup_form";
    }
    
    @PostMapping("/process_register")
    @Transactional 
    public String processRegister(@Valid UserDto userDto, BindingResult bindingResult, Model model) throws IOException, ResourceNotFoundException {
    	if (bindingResult.hasErrors()) {
			return "signup_form";
		}
    	
    	if (userService.isUserPresent(userDto.getEmail())) {
			model.addAttribute("exist", true);
			return "signup_form";
		}
    	
    	if (userService.isUserNicknamePresent(userDto.getNickname())) {
			model.addAttribute("nicknameExist", true);
			return "signup_form";
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
    			
		model.addAttribute("currentUser", userService.findUserByEmail(email));    
		model.addAttribute("profileUser", userService.findUserByEmail(email));  
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("currentPage", page);
        model.addAttribute("pageUsers", pageUsers);
        model.addAttribute("totalUsers", pageUsers.getTotalElements());
        model.addAttribute("totalPages", pageUsers.getTotalPages());
                
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
			throw new ForbiddenException("Access denied");
		}
		return "redirect:/users";
	}
    
    @GetMapping("/users/{userId}/overview")
    public String showUserOverview(@PathVariable Long userId, Model model, 
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {
    	
    	Sort sort = Sort.by("createdAt").descending();
    	Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
    	Page<Activity> pageActivitiesByUser = activityService.getActivitiesByUserId(pageable, userId);
    	
    	double average = 0;
    	int hottest = 0;
    	List<Bargain> listBargainsByUser = bargainService.getAllBargainsByUserId(userId);
    	if(listBargainsByUser.size()>0) {
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
    	
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("profileUser", userService.findUserById(userId)); 	
		model.addAttribute("activities", pageActivitiesByUser);
		model.addAttribute("totalActivities", pageActivitiesByUser.getTotalElements());
		model.addAttribute("totalBargains", listBargainsByUser.size());
		model.addAttribute("totalComments", userComments.size());
		model.addAttribute("totalVotes", userVotes.size());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", pageActivitiesByUser.getTotalPages());
		model.addAttribute("voteDto", new VoteDto());
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
    	
    	User user = userService.findUserById(userId);
    	
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
		
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("profileUser", user); 	
		model.addAttribute("title", keyword);
		model.addAttribute("bargains", pageBargainsByUser);
		model.addAttribute("totalActivities", userActivities.size());
		model.addAttribute("totalBargains", totalBargains);
		model.addAttribute("totalDisplayBargains", totalDisplayBargains);
		model.addAttribute("totalComments", userComments.size());
		model.addAttribute("totalVotes", userVotes.size());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("currentPage", page);		
		model.addAttribute("totalPages", pageBargainsByUser.getTotalPages());
		model.addAttribute("voteDto", new VoteDto());
		model.addAttribute("closed", ended);
		model.addAttribute("noResultsFound", noResultsFound);
		model.addAttribute("resultsFound", resultsFound);
    	
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
    	
    	User user = userService.findUserById(userId);
    	
    	Sort sort = Sort.by("createdAt").descending();
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
		
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("profileUser", user); 	
		model.addAttribute("title", keyword);
		model.addAttribute("bargains", pageBargainsByUser);
		model.addAttribute("totalActivities", userActivities.size());
		model.addAttribute("totalBargains", totalBargains);
		model.addAttribute("totalDisplayBargains", totalDisplayBargains);
		model.addAttribute("totalComments", userComments.size());
		model.addAttribute("totalVotes", userVotes.size());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("currentPage", page);	
		model.addAttribute("totalPages", pageBargainsByUser.getTotalPages());
		model.addAttribute("voteDto", new VoteDto());
		model.addAttribute("closed", ended);
		model.addAttribute("noResultsFound", noResultsFound);
		model.addAttribute("resultsFound", resultsFound);
    	
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
    	
    	User user = userService.findUserById(userId);
    	
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
		
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("profileUser", user); 	
		model.addAttribute("title", keyword);
		model.addAttribute("bargains", pageBargainsByUserIdOrderByCommentSize);
		model.addAttribute("totalActivities", userActivities.size());
		model.addAttribute("totalBargains", totalBargains);
		model.addAttribute("totalDisplayBargains", totalDisplayBargains);
		model.addAttribute("totalComments", userComments.size());
		model.addAttribute("totalVotes", userVotes.size());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("currentPage", page);	
		model.addAttribute("totalPages", pageBargainsByUserIdOrderByCommentSize.getTotalPages());
		model.addAttribute("voteDto", new VoteDto());
		model.addAttribute("closed", ended);
		model.addAttribute("noResultsFound", noResultsFound);
		model.addAttribute("resultsFound", resultsFound);
    	
		return "user_bargains_commented";
    }
    
    @GetMapping("/users/{userId}/comments")
    public String showCommentsByUser(@PathVariable Long userId, Model model, 
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {
    	
    	User user = userService.findUserById(userId);
    	
    	Sort sort = Sort.by("createdAt").descending();
    	Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
    	Page<Comment> pageComments = commentService.getAllCommentsByUser(pageable, user);
    	List<Activity> userActivities = activityService.getActivitiesByUserId(userId);
    	List<Vote> userVotes = voteService.getAllVotesByUserId(userId);
    	int totalUserBargains = bargainService.getAllBargainsByUserId(userId).size();
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("profileUser", user); 	
		model.addAttribute("totalComments", pageComments.getTotalElements());
		model.addAttribute("totalVotes", userVotes.size());
		model.addAttribute("totalActivities", userActivities.size());
		model.addAttribute("pageComments", pageComments);
		model.addAttribute("totalBargains", totalUserBargains);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("currentPage", page);		
		model.addAttribute("totalPages", pageComments.getTotalPages());
		    	
		return "user_comments";
    }   
    
    @GetMapping("/users/{userId}/votes")
    public String showVotesByUser(@PathVariable Long userId, Model model, 
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "12") int pageSize) {
    	
    	User user = userService.findUserById(userId);
    	
    	Sort sort = Sort.by("createdAt").descending();
    	Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
    	Page<Vote> pageVotes = voteService.getVotesByUserId(pageable, userId);
    	List<Activity> userActivities = activityService.getActivitiesByUserId(userId);
    	
    	List<Comment> userComments = commentService.getAllCommentsByUserId(userId);
    	int totalUserBargains = bargainService.getAllBargainsByUserId(userId).size();
    	int totalPositiveVotes = voteService.getAllByVoteTypeAndUserId(VoteType.UPVOTE, userId).size();
    	int totalNegativeVotes = voteService.getAllByVoteTypeAndUserId(VoteType.DOWNVOTE, userId).size();
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("profileUser", user); 	
		model.addAttribute("totalVotes", pageVotes.getTotalElements());
		model.addAttribute("totalPositiveVotes", totalPositiveVotes);
		model.addAttribute("totalNegativeVotes", totalNegativeVotes);
		model.addAttribute("pageVotes", pageVotes);
		model.addAttribute("totalActivities", userActivities.size());
		model.addAttribute("totalBargains", totalUserBargains);
		model.addAttribute("totalComments", userComments.size());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("currentPage", page);		
		model.addAttribute("totalPages", pageVotes.getTotalPages());
		    	
		return "user_votes";
    }   
    
    @GetMapping("/users/{userId}/votes/plus")
    public String showPositiveVotesByUser(@PathVariable Long userId, Model model, 
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "12") int pageSize) {
    	
    	User user = userService.findUserById(userId);
    	
    	Sort sort = Sort.by("createdAt").descending();
    	Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
    	Page<Vote> pageVotes = voteService.getVotesByVoteTypeAndUserId(pageable, VoteType.UPVOTE, userId);
    	int totalNegativeVotes = voteService.getAllByVoteTypeAndUserId(VoteType.DOWNVOTE, userId).size();
    	int totalVotes = voteService.getAllVotesByUserId(userId).size();
    	
    	List<Activity> userActivities = activityService.getActivitiesByUserId(userId);
    	List<Comment> userComments = commentService.getAllCommentsByUserId(userId);
    	int totalUserBargains = bargainService.getAllBargainsByUserId(userId).size();
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("profileUser", user); 	
		model.addAttribute("totalVotes", totalVotes);
		model.addAttribute("totalPositiveVotes", pageVotes.getTotalElements());
		model.addAttribute("totalNegativeVotes", totalNegativeVotes);
		model.addAttribute("pageVotes", pageVotes);
		model.addAttribute("totalActivities", userActivities.size());
		model.addAttribute("totalBargains", totalUserBargains);
		model.addAttribute("totalComments", userComments.size());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("currentPage", page);		
		model.addAttribute("totalPages", pageVotes.getTotalPages());
		    	
		return "user_votes_plus";
    }   
    
    @GetMapping("/users/{userId}/votes/minus")
    public String showNegativeVotesByUser(@PathVariable Long userId, Model model, 
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "12") int pageSize) {
    	
    	User user = userService.findUserById(userId);
    	
    	Sort sort = Sort.by("createdAt").descending();
    	Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
    	Page<Vote> pageVotes = voteService.getVotesByVoteTypeAndUserId(pageable, VoteType.DOWNVOTE, userId);
    	
    	int totalVotes = voteService.getAllVotesByUserId(userId).size();
    	int totalPositiveVotes = voteService.getAllByVoteTypeAndUserId(VoteType.UPVOTE, userId).size();
    	
    	List<Activity> userActivities = activityService.getActivitiesByUserId(userId);
    	List<Comment> userComments = commentService.getAllCommentsByUserId(userId);
    	int totalUserBargains = bargainService.getAllBargainsByUserId(userId).size();
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("profileUser", user); 	
		model.addAttribute("totalVotes", totalVotes);
		model.addAttribute("totalPositiveVotes", totalPositiveVotes);
		model.addAttribute("totalNegativeVotes", pageVotes.getTotalElements());
		model.addAttribute("pageVotes", pageVotes);
		model.addAttribute("totalActivities", userActivities.size());
		model.addAttribute("totalBargains", totalUserBargains);
		model.addAttribute("totalComments", userComments.size());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("currentPage", page);		
		model.addAttribute("totalPages", pageVotes.getTotalPages());
		    	
		return "user_votes_minus";
    }   
    
    @GetMapping("/users/{userId}/profile")
	public String showUserProfile(@PathVariable Long userId, Model model) {
		
		User user = userService.findUserById(userId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();	
		if(userSecurity.isOwnerOrAdmin(user.getEmail(), email)) {	
	
			UserProfileDto userProfileDto = UserProfileDto.builder()
					.nickname(user.getNickname())
					.email(user.getEmail())
					.build(); 
			
			model.addAttribute("userProfileDto", userProfileDto);
			model.addAttribute("photoFileDto", new PhotoFileDto());
			model.addAttribute("currentUser", userService.findUserByEmail(email)); 
		} else {
			throw new ForbiddenException("Access denied");
		}
		
		return "profile";
	}   
    
    @Transactional
    @RequestMapping("/users/{userId}/photo/edit") 
	public String updatePhoto(@PathVariable Long userId,
			 RedirectAttributes redirectAttributes,
			 @Valid @ModelAttribute("photoFileDto") PhotoFileDto photoFileDto, 
			 BindingResult result, Model model,
			 @ModelAttribute("userProfileDto") UserProfileDto userProfileDto) throws IOException    {
    	
    	User user = userService.findUserById(userId);
		
		if (result.hasErrors()) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String email = auth.getName();
			userProfileDto = UserProfileDto.builder()
					.nickname(user.getNickname())
					.email(user.getEmail())
					.build(); 
			model.addAttribute("userProfileDto", userProfileDto);
    		model.addAttribute("currentUser", userService.findUserByEmail(email));
    		return "profile";
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
		
		return "redirect:/users/" + userId + "/profile"; 	
	}
    
    @Transactional
    @RequestMapping("/users/{userId}/nickname")
	public String updateNickname(@PathVariable Long userId, 
			@Valid @ModelAttribute("userProfileDto") UserProfileDto userProfileDto,
			BindingResult bindingResult, Model model, 
			@ModelAttribute("photoFileDto") PhotoFileDto photoFileDto, RedirectAttributes redirectAttributes)    {

    	User user = userService.findUserById(userId);
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
    	if (bindingResult.hasErrors()) {
    		model.addAttribute("currentUser", userService.findUserByEmail(email));
    		return "profile";
    	}
    	
    	if (user.getNickname().equals(userProfileDto.getNickname())) {
    		model.addAttribute("currentUser", userService.findUserByEmail(email));
    		model.addAttribute("currentNickname", true);
			return "profile";
		}
    	
    	if (userService.isUserNicknamePresent(userProfileDto.getNickname()) 
    			&& !(user.getNickname().equals(userProfileDto.getNickname()))) {
    		model.addAttribute("currentUser", userService.findUserByEmail(email));
    		model.addAttribute("nicknameExist", true);
			return "profile";
		}
    	
		user.setNickname(userProfileDto.getNickname());
		redirectAttributes.addFlashAttribute("editedNickname", "The nickname has been edited successfully.");	

		return "redirect:/users/" + userId + "/profile"; 	
	}
    
    @GetMapping("/users/{userId}/password")
	public String showChangePassword(@PathVariable Long userId, Model model) {
		
		User user = userService.findUserById(userId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
			
		if(userSecurity.isOwnerOrAdmin(user.getEmail(), email)) {			
			model.addAttribute("passwordDto", new PasswordDto());
			model.addAttribute("currentUser", userService.findUserByEmail(email)); 			
		} else {
			throw new ForbiddenException("Access denied");
		}

		return "change_password_form";
	}  
    
    @Transactional
    @RequestMapping("/users/{userId}/password")
	public String updatePassword(@PathVariable Long userId,
			@Valid @ModelAttribute("passwordDto") PasswordDto passwordDto,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	
    	if (bindingResult.hasErrors()) {
    		model.addAttribute("currentUser", userService.findUserByEmail(email));
			return "change_password_form";
		}
    	
		User user = userService.findUserById(userId);

    	if (!bCryptPasswordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
    		model.addAttribute("currentUser", userService.findUserByEmail(email));
			model.addAttribute("oldPasswordNotCorrect", true);
			return "change_password_form";
		}
		
		user.setPassword(bCryptPasswordEncoder.encode(passwordDto.getNewPassword()));
		redirectAttributes.addFlashAttribute("changedPassword", "The password has been changed successfully.");
		return "redirect:/users/" + userId + "/password"; 	
	}
    
    @GetMapping("users/{userId}/photo")
    public void getImage(@PathVariable("userId") Long userId, HttpServletResponse response) throws Exception {
        User user = userService.findUserById(userId);
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
