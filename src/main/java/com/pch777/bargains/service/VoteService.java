package com.pch777.bargains.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pch777.bargains.model.ActivityType;
import com.pch777.bargains.model.Bargain;
import com.pch777.bargains.model.User;
import com.pch777.bargains.model.Vote;
import com.pch777.bargains.model.VoteDto;
import com.pch777.bargains.model.VoteType;
import com.pch777.bargains.repository.BargainRepository;
import com.pch777.bargains.repository.UserRepository;
import com.pch777.bargains.repository.VoteRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VoteService {

	private final VoteRepository voteRepository;
	private final BargainRepository bargainRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final ActivityService activityService;

	@Transactional
	public boolean vote(VoteDto voteDto, Long bargainId, String email) {
		Bargain bargain = bargainRepository.findById(bargainId).get();

		Optional<Vote> voteByBargainAndUser = voteRepository
				.findByBargainIdAndUserEmail(bargainId, email);
		
		if (!voteByBargainAndUser.isPresent() && !(bargain.getUser().getEmail().equals(email))) {

			if (VoteType.UPVOTE.equals(voteDto.getVoteType())) {
				bargain.setVoteCount(bargain.getVoteCount() + 1);
			} else {
				bargain.setVoteCount(bargain.getVoteCount() - 1);
			}
			
			User user = userService.findUserByEmail(email);
			Vote vote = mapToVote(voteDto, bargain, email);
			voteRepository.save(vote);
			activityService.addActivity(user, vote.getCreatedAt(), bargain, ActivityType.VOTE);
			return true;
		}

		return false;
	}
	
	@Transactional
	public void initVote(Long bargainId, Long userId, VoteType voteType) throws Exception {
		Bargain bargain = bargainRepository.findById(bargainId)
				.orElseThrow(() -> new IllegalArgumentException("Not found a bargain with id: " + bargainId));
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("Not found a bargain with id: " + userId));
		
		Optional<Vote> voteByBargainAndUser = voteRepository.findByBargainIdAndUserEmail(bargainId, user.getEmail());

		if (!voteByBargainAndUser.isPresent() && userId!=bargain.getUser().getId()) {
			if (VoteType.UPVOTE.equals(voteType)) {
				bargain.setVoteCount(bargain.getVoteCount() + 1);
			} else {
				bargain.setVoteCount(bargain.getVoteCount() - 1);
			}
			Vote vote = new Vote(voteType, LocalDateTime.now(), bargain, user);
			voteRepository.save(vote);
			activityService.addActivity(user, vote.getCreatedAt(), bargain, ActivityType.VOTE);
		}
		
	}

	private Vote mapToVote(VoteDto voteDto, Bargain bargain, String email) {
		
		return Vote.builder()
				.voteType(voteDto.getVoteType())
				.createdAt(LocalDateTime.now())
				.bargain(bargain)
				.user(userService.findUserByEmail(email))
				.build();
	}

	public static boolean userVoted(List<Vote> votes, User user) {
		Vote vote = votes.stream()
				.filter(v -> user.getId().equals(v.getUser().getId()))
				.findAny()
				.orElse(null);
		return vote != null;
	}
	
	public Page<Vote> getVotesByUserId(Pageable pageable, Long userId) {
		return voteRepository.findByUserId(pageable, userId);
	}
	
	public Page<Vote> getVotesByVoteTypeAndUserId(Pageable pageable, VoteType voteType, Long userId) {
		return voteRepository.findVotesByVoteTypeAndUserId(pageable, voteType, userId);
	}
	
	public List<Vote> getAllByVoteTypeAndUserId(VoteType voteType, Long userId) {
		return voteRepository.findByVoteTypeAndUserId(voteType, userId);
	}
	
	public List<Vote> getAllVotesByUserId(Long userId) {
		return voteRepository.findVoteByUserId(userId);
	}

	public List<Vote> getAllVotes() {
		return voteRepository.findAll();
	}
	
	public Optional<Vote> getById(Long id) {
		return voteRepository.findById(id);
	}
	
	public void deleteVoteById(Long id) {
		voteRepository.deleteById(id);	
	}

	public List<Vote> getAllVotesByBargainId(Long bargainId) {		
		return voteRepository.findByBargainId(bargainId);
	}
}
