package com.pch777.bargains.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pch777.bargains.model.Bargain;
import com.pch777.bargains.model.Category;

@Repository
public interface BargainRepository extends JpaRepository<Bargain, Long> {
	
	@Query ("SELECT b FROM Bargain b where lower(b.title) LIKE %?1%")
	Page<Bargain> findByTitleLike(Pageable pageable, String keyword);
	
	@Query ("SELECT b FROM Bargain b where lower(b.title) LIKE %?1% and b.closed = ?2")
	Page<Bargain> findByTitleLikeAndClosed(Pageable pageable, String keyword, boolean closed);
	
	@Query ("SELECT b FROM Bargain b where (b.shop.id = ?2 and lower(b.title) LIKE %?1%)")
	Page<Bargain> findByTitleLikeAndShopId(Pageable pageable, String keyword, Long shopId);
	
	@Query ("SELECT b FROM Bargain b where (b.category = ?2 and lower(b.title) LIKE %?1%)")
	Page<Bargain> findByTitleLikeAndCategory(Pageable pageable, String keyword, Category category);
	
	@Query ("SELECT b FROM Bargain b where (b.category = ?2 and lower(b.title) LIKE %?1% and b.closed = ?3)")
	Page<Bargain> findNotClosedByTitleLikeAndCategory(Pageable pageable, String keyword, Category category, boolean closed);
	
	@Query ("SELECT b FROM Bargain b where (b.user.id = ?2 and lower(b.title) LIKE %?1% and b.closed = ?3)")
	Page<Bargain> findNotClosedByTitleLikeAndUserId(Pageable pageable, String keyword, Long userId, boolean closed);
	
	List<Bargain> findByUserId(Long userId);
	
	List<Bargain> findByCategory(Category category);
	
	@Query ("SELECT b FROM Bargain b where (b.user.id = ?2 and lower(b.title) LIKE %?1%)")
	Page<Bargain> findByTitleLikeAndUserId(Pageable pageable, String keyword, Long userId);
	
	@Query ("SELECT b FROM Bargain b where lower(b.title) LIKE %?1%  order by size(b.comments) desc, b.createdAt")
	Page<Bargain>  findMostCommented(Pageable pageable, String keyword);
	
	@Query ("SELECT b FROM Bargain b where (lower(b.title) LIKE %?1% and b.closed = ?2) order by size(b.comments) desc, b.createdAt")
	Page<Bargain>  findNotClosedMostCommented(Pageable pageable, String keyword, boolean closed);
	
	@Query ("SELECT b FROM Bargain b where (b.category = ?2 and lower(b.title) LIKE %?1%) order by size(b.comments) desc, b.createdAt")
	Page<Bargain>  findByCategoryOrderByCommentsSize(Pageable pageable, String keyword, Category category);
	
	@Query ("SELECT b FROM Bargain b where (b.category = ?2 and lower(b.title) LIKE %?1% and b.closed = ?3) order by size(b.comments) desc, b.createdAt")
	Page<Bargain>  findNotClosedByCategoryOrderByCommentsSize(Pageable pageable, String keyword, Category category, boolean closed);
	
	@Query ("SELECT b FROM Bargain b where (b.user.id = ?2 and lower(b.title) LIKE %?1% and b.closed = ?3) order by size(b.comments) desc, b.createdAt")
	Page<Bargain>  findNotClosedByUserIdOrderByCommentsSize(Pageable pageable, String keyword, Long userId, boolean closed);
	
	@Query ("SELECT b FROM Bargain b where (b.user.id = ?2 and lower(b.title) LIKE %?1%) order by size(b.comments) desc, b.createdAt")
	Page<Bargain>  findByUserIdOrderByCommentsSize(Pageable pageable, String keyword, Long userId);
	
	List<Bargain> findByClosedAndEndBargainLessThan(boolean closed, LocalDate endBargain);
	
	@Query ("SELECT distinct b FROM Bargain b, User u JOIN b.votes bv JOIN bv.bargain bvb JOIN bv.user bvu where b.id = bvb.id and bvu.id = ?1")
	Page<Bargain>  findBargainsVotedByUserId(Pageable pageable, Long userId);
}
