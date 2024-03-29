package com.pch777.bargains.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pch777.bargains.dto.CommentDto;
import com.pch777.bargains.exception.ForbiddenException;
import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.ActivityType;
import com.pch777.bargains.model.Bargain;
import com.pch777.bargains.model.Comment;
import com.pch777.bargains.security.UserSecurity;
import com.pch777.bargains.service.ActivityService;
import com.pch777.bargains.service.BargainService;
import com.pch777.bargains.service.CommentService;
import com.pch777.bargains.service.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
public class CommentController {

	private static final String REDIRECT_BARGAINS = "redirect:/bargains/";
	private BargainService bargainService;
	private CommentService commentService;
	private UserService userService;
	private ActivityService activityService;
	private UserSecurity userSecurity;

	@RequestMapping("/comments/add")
	@Transactional
	public String addComment(@ModelAttribute CommentDto commentDto) throws ResourceNotFoundException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		Comment comment = new Comment();
		comment.setContent(commentDto.getContent());
		comment.setUser(userService.findUserByEmail(email));
		comment.setBargain(bargainService.getBargainById(commentDto.getBargainId()));
		commentService.addComment(comment);

		activityService.addActivity(comment.getUser(), comment.getCreatedAt(), comment.getBargain(), ActivityType.COMMENT);
		return REDIRECT_BARGAINS + commentDto.getBargainId();
	}
	
	@GetMapping("/bargains/{bargainId}/comments/{commentId}/edit")
	public String showEditComment(@PathVariable Long bargainId,
			@PathVariable Long commentId, 
			Model model) throws ResourceNotFoundException {
		
		Bargain bargain = bargainService.getBargainById(bargainId);
		Comment comment = commentService.getCommentById(commentId);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		if(userSecurity.isOwnerOrAdmin(commentService.getCommentById(commentId).getUser().getEmail(), email)) {
			model.addAttribute("currentUser", userService.findUserByEmail(email));
			model.addAttribute("comment", comment);
			model.addAttribute("bargain", bargain);
		} else {
			throw new ForbiddenException("Access denied");
		}
		
		return "bargain_edit_comment";
	}
	
	@PostMapping("/bargains/{bargainId}/comments/{commentId}/edit")
	@Transactional
	public String editComment(@ModelAttribute("comment") Comment comment,
			@PathVariable Long bargainId,
			@PathVariable Long commentId, 
			Model model) throws ResourceNotFoundException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		comment.setId(commentId);
		comment.setUser(userService.findUserByEmail(email));
		comment.setBargain(bargainService.getBargainById(bargainId));
		commentService.addComment(comment);

		return REDIRECT_BARGAINS + bargainId;
	}
	
	@GetMapping("/bargains/{bargainId}/comments/{commentId}/cite")
	public String showCiteComment(@PathVariable Long bargainId,
			@PathVariable Long commentId, 
			Model model) throws ResourceNotFoundException {
		
		Bargain bargain = bargainService.getBargainById(bargainId);
		Comment comment = null;
		comment = commentService.getCommentById(commentId);
		
		comment.setContent(comment.getUser().getNickname() + " " + changeDateToString(comment.getCreatedAt())
				+ comment.getContent() + "<hr><br>");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("comment", comment);
		model.addAttribute("bargain", bargain);
		
		return "bargain_cite_comment";
	}	
	
	@PostMapping("/bargains/{bargainId}/comments/{commentId}/cite")
	@Transactional
	public String citeComment(@ModelAttribute("comment") Comment comment,
			@PathVariable Long bargainId,
			@PathVariable Long commentId, 
			Model model) throws ResourceNotFoundException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		comment.setUser(userService.findUserByEmail(email));
		comment.setBargain(bargainService.getBargainById(bargainId));
		commentService.addComment(comment);
		
		activityService.addActivity(comment.getUser(), comment.getCreatedAt(), comment.getBargain(), ActivityType.COMMENT);
		return REDIRECT_BARGAINS + bargainId;
	}
	
	@GetMapping("/bargains/{bargainId}/comments/{commentId}/delete")
	@Transactional
	public String deleteCommentById(@PathVariable Long bargainId, @PathVariable Long commentId) throws ResourceNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		if(userSecurity.isOwnerOrAdmin(commentService.getCommentById(commentId).getUser().getEmail(), email)) {
			commentService.deleteCommentById(commentId);
		} else {
			throw new ForbiddenException("Access denied");
		}
		return REDIRECT_BARGAINS + bargainId;
	}
	
	private String changeDateToString (LocalDateTime date) {
		int day = date.getDayOfMonth();
		int month = date.getMonthValue();
		int year = date.getYear();
		int hour = date.getHour();
		
		String minute = "" + date.getMinute();
		if(date.getMinute() < 10) minute = "0" + minute;
		
		return day + "/" + month + "/" + year + " " + hour + ":" + minute;
	}
}
