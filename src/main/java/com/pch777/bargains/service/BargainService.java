package com.pch777.bargains.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pch777.bargains.dto.BargainDto;
import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.Bargain;
import com.pch777.bargains.model.BargainPhoto;
import com.pch777.bargains.model.Category;
import com.pch777.bargains.repository.BargainRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BargainService {

	private static final String NOT_FOUND_A_BARGAIN_WITH_ID = "Not found a bargain with id: ";
	private BargainRepository bargainRepository;
	
	public List<Bargain> getAllBargains() {
		return bargainRepository.findAll();
	}
	
	public List<Bargain> getAllBargainsByCategory(Category category) {
		return bargainRepository.findByCategory(category);
	}
	
	public Page<Bargain> getBargainsMostCommented(Pageable pageable, String keyword) {
		return bargainRepository.findMostCommented(pageable, "%" + keyword.toLowerCase() + "%");
	}
	
	public Page<Bargain> getBargainsNotClosedMostCommented(Pageable pageable, String keyword, boolean closed) {
		return bargainRepository.findNotClosedMostCommented(pageable, "%" + keyword.toLowerCase() + "%", closed);
	}
	
	public Page<Bargain> getBargainsByTitleLikeAndShopId(Pageable pageable, String keyword, Long shopId) {
		return bargainRepository.findByTitleLikeAndShopId(pageable, "%" + keyword.toLowerCase() + "%", shopId);
	}
	
	public Page<Bargain> getBargainsMostCommentedByCategory(Pageable pageable, String keyword, Category category) {
		return bargainRepository.findByCategoryOrderByCommentsSize(pageable, "%" + keyword.toLowerCase() + "%", category);
	}
	public Page<Bargain> getBargainsNotClosedMostCommentedByCategory(Pageable pageable, String keyword, Category category, boolean closed) {
		return bargainRepository.findNotClosedByCategoryOrderByCommentsSize(pageable, "%" + keyword.toLowerCase() + "%", category, closed);
	}
	
	public Page<Bargain> getBargainsNotClosedMostCommentedByUserId(Pageable pageable, String keyword, Long userId, boolean closed) {
		return bargainRepository.findNotClosedByUserIdOrderByCommentsSize(pageable, "%" + keyword.toLowerCase() + "%", userId, closed);
	}
	
	public Page<Bargain> getBargainsNotClosedByUserId(Pageable pageable, String keyword, Long userId, boolean closed) {
		return bargainRepository.findNotClosedByTitleLikeAndUserId(pageable, "%" + keyword.toLowerCase() + "%", userId, closed);
	}
	
	public Page<Bargain> getBargainsMostCommentedByUserId(Pageable pageable, String keyword, Long userId) {
		return bargainRepository.findByUserIdOrderByCommentsSize(pageable, "%" + keyword.toLowerCase() + "%", userId);
	}
	
	public Page<Bargain> getAllBargains(Pageable pageable) {
		return bargainRepository.findAll(pageable);
	}
	
	public Page<Bargain> getAllBargainsByTitleLike(Pageable pageable, String keyword) {
		return bargainRepository.findByTitleLike(pageable, "%" + keyword.toLowerCase() + "%");
	}
	
	public Page<Bargain> getAllBargainsByTitleLikeAndClosed(Pageable pageable, String keyword, boolean closed) {
		return bargainRepository.findByTitleLikeAndClosed(pageable, "%" + keyword.toLowerCase() + "%", closed);
	}
	
	public Page<Bargain> getAllBargainsByTitleLikeByCategory(Pageable pageable, String keyword, Category category) {
		return bargainRepository.findByTitleLikeAndCategory(pageable, "%" + keyword.toLowerCase() + "%", category);
	}
	
	public Page<Bargain> getNotClosedBargainsByTitleLikeByCategory(Pageable pageable, String keyword, Category category, boolean closed) {
		return bargainRepository.findNotClosedByTitleLikeAndCategory(pageable, "%" + keyword.toLowerCase() + "%", category, closed);
	}
	
	public Page<Bargain> getAllBargainsByTitleLikeByUserId(Pageable pageable, String keyword, Long userId) {
		return bargainRepository.findByTitleLikeAndUserId(pageable, "%" + keyword.toLowerCase() + "%", userId);
	}
	
	public Page<Bargain> getAllBargainsVotedByUserId(Pageable pageable, Long userId) {
		return bargainRepository.findBargainsVotedByUserId(pageable, userId);
	}
	
	public boolean existsById(Long id) {
        return bargainRepository.existsById(id);
    }
	
	public Bargain getBargainById(Long id) throws ResourceNotFoundException {
		return bargainRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_A_BARGAIN_WITH_ID + id));
	}
	
	
	public Optional<Bargain> getById(Long id) {
		return bargainRepository.findById(id);
	}
	
	public Bargain addBargain(Bargain bargain) {
		return bargainRepository.save(bargain);
	}
	
	public void deleteBargainById(Long id) {
		bargainRepository.deleteById(id);
	}

	public Bargain editBargain(Bargain bargain, Long id) throws ResourceNotFoundException {
		if (!existsById(id)) {
			throw new ResourceNotFoundException(NOT_FOUND_A_BARGAIN_WITH_ID + id);
		}

		bargain.setId(id);

		return bargainRepository.save(bargain);		
	}
	
	public void editBargainTitle(String title, Long id) {
		Bargain bargain = bargainRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_A_BARGAIN_WITH_ID + id));
		bargain.setTitle(title);
		bargainRepository.save(bargain);
	}
	
	public void closeBargainById(Long id) {
		Bargain bargain = bargainRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_A_BARGAIN_WITH_ID + id));
		bargain.setClosed(true);
		bargainRepository.save(bargain);		
	}
	
	public void openBargainById(Long id) {
		Bargain bargain = bargainRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_A_BARGAIN_WITH_ID + id));
		bargain.setClosed(false);
		bargainRepository.save(bargain);		
	}

	public List<Bargain> getAllBargainsByUserId(Long userId) {
		return bargainRepository.findByUserId(userId);
	}
	
	public static boolean isBargainFinished(LocalDate date) {
		LocalDate today = LocalDate.now();
		if(date==null) return false;
		return today.isAfter(date);
	}
	
	public BargainPhoto fileToBargainPhoto(MultipartFile multipartFile) throws IOException {
		return BargainPhoto.builder()
				.file(multipartFile.getBytes())
				.filename(multipartFile.getOriginalFilename())
				.contentType(multipartFile.getContentType())
				.createdAt(LocalDate.now())
				.fileLength(multipartFile.getSize())
				.build();
	}
	
	public Bargain bargainDtoToBargain(BargainDto bargainDto) {
		return Bargain.builder()
				.title(bargainDto.getTitle())
				.description(bargainDto.getDescription())
				.reducePrice(bargainDto.getReducePrice())
				.normalPrice(bargainDto.getNormalPrice())
				.delivery(bargainDto.getDelivery())
				.coupon(bargainDto.getCoupon())
				.link(bargainDto.getLink())
				.startBargain(bargainDto.getStartBargain())
				.endBargain(bargainDto.getEndBargain())
				.closed(bargainDto.getClosed())
				.category(bargainDto.getCategory())
				.shop(bargainDto.getShop())
				.build();
	}
	
	public BargainDto bargainToBargainDto(Bargain bargain) {
		return BargainDto.builder()
				.title(bargain.getTitle())
				.description(bargain.getDescription())
				.reducePrice(bargain.getReducePrice())
				.normalPrice(bargain.getNormalPrice())
				.delivery(bargain.getDelivery())
				.coupon(bargain.getCoupon())
				.link(bargain.getLink())
				.startBargain(bargain.getStartBargain())
				.endBargain(bargain.getEndBargain())
				.closed(bargain.getClosed())
				.category(bargain.getCategory())
				.shop(bargain.getShop())
				.build();
	}
	
	public static String whenElementAdded(LocalDateTime date) {
		LocalDateTime today = LocalDateTime.now();
		long seconds = ChronoUnit.SECONDS.between(date, today);
		long hours = seconds/3600;
		long minutes = (seconds - (hours * 3600))/60;
		
		if(seconds < 60) {
			return seconds + " sec";
		} else if (seconds > 59 && seconds< 3599) {
			return (seconds/60) +  " min";
		} else if (seconds > 3599 && seconds < 86399 && minutes == 0) {
			return hours + " h";
		} else if (seconds > 3599 && seconds < 86399) {
			return hours + " h, " + minutes + " min";
		} else if (today.getYear() == date.getYear()) {
			return date.getDayOfMonth() + " " + monthValueToThreeLettersShortcut(date.getMonthValue());
		} else {
			return date.getDayOfMonth() + " " + monthValueToThreeLettersShortcut(date.getMonthValue()) + " " + date.getYear();
		}

	}
	
	public static String monthValueToThreeLettersShortcut(int month) {
		String monthShortcut;
		switch(month) {
		case 1: monthShortcut = "Jan"; break;
		case 2: monthShortcut = "Feb"; break;
		case 3: monthShortcut = "Mar"; break;
		case 4: monthShortcut = "Apr"; break;
		case 5: monthShortcut = "May"; break;
		case 6: monthShortcut = "Jun"; break;
		case 7: monthShortcut = "Jul"; break;
		case 8: monthShortcut = "Aug"; break;
		case 9: monthShortcut = "Sep"; break;
		case 10: monthShortcut = "Oct"; break;
		case 11: monthShortcut = "Nov"; break;
		default: monthShortcut = "Dec";
		}
		return monthShortcut;
	}

}
