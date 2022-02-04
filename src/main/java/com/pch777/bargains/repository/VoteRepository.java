package com.pch777.bargains.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pch777.bargains.model.Vote;
import com.pch777.bargains.model.VoteType;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

	Optional<Vote> findByBargainIdAndUserEmail(Long id, String email);
	List<Vote> findVoteByUserId(Long userId);
	List<Vote> findByBargainId(Long bargainId);
	
	@Query ("SELECT v FROM Vote v where (v.user.id = ?1)")
	Page<Vote> findByUserId(Pageable pageable, Long userId);
	
	@Query ("SELECT v FROM Vote v where (v.voteType = ?1 and v.user.id = ?2)")
	Page<Vote> findVotesByVoteTypeAndUserId(Pageable pageable, VoteType voteType, Long userId);
	
	@Query ("SELECT v FROM Vote v where (v.voteType = ?1 and v.user.id = ?2)")
	List<Vote> findByVoteTypeAndUserId(VoteType voteType, Long userId);
}
