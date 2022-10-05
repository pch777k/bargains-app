package com.pch777.bargains.utility;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pch777.bargains.model.Bargain;
import com.pch777.bargains.repository.BargainRepository;
import com.pch777.bargains.service.BargainService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ClosedBargainJob {
	
	private BargainRepository bargainRepository;
	private BargainService bargainService;
	
	@Transactional
	@Scheduled(cron = "0 0 0 * * *")
	//@Scheduled(fixedRate = 480_000)
	//@Scheduled(cron = "@midnight")
	public void run() {
		List<Bargain> bargains = bargainRepository
				.findByClosedAndEndBargainLessThan(false, LocalDate.now());
		bargains.forEach(bargain -> bargainService.closeBargainById(bargain.getId()));
	
	}
}
