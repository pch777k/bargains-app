package com.pch777.bargains.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pch777.bargains.dto.VoteDto;
import com.pch777.bargains.service.VoteService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class VoteController {

    private VoteService voteService;
    
    @RequestMapping("/votes/{bargainId}")
    public String vote(@PathVariable Long bargainId, VoteDto voteDto) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
        voteService.vote(voteDto, bargainId, email);      
        return "redirect:/"; 
    }
    
    @PostMapping("/vote-bargain/{bargainId}")
    public String voteBargain(@PathVariable Long bargainId, VoteDto voteDto) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
    	voteService.vote(voteDto, bargainId, email);
        return "redirect:/bargains/" + bargainId;
    }
}
