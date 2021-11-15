package com.pch777.bargains.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pch777.bargains.model.VoteDto;
import com.pch777.bargains.service.VoteService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class VoteController {

    private final VoteService voteService;
    
    @RequestMapping("/votes")
    public String vote(VoteDto voteDto) throws Exception {
        voteService.vote(voteDto);      
        return "redirect:/"; 
    }
    
    @PostMapping("/vote-bargain")
    public String voteBargain(VoteDto voteDto) throws Exception {
        voteService.vote(voteDto);
        return "redirect:/bargains/" + voteDto.getBargainId();
    }
}
