package com.pch777.bargains;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import com.pch777.bargains.dto.VoteDto;
import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.Bargain;
import com.pch777.bargains.model.Category;
import com.pch777.bargains.model.User;
import com.pch777.bargains.model.VoteType;
import com.pch777.bargains.service.BargainService;
import com.pch777.bargains.service.UserService;
import com.pch777.bargains.service.VoteService;

//@DataJpaTest
@AutoConfigureTestDatabase
@SpringBootTest
//@Import({BargainService.class, UserService.class, VoteService.class})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class VoteServiceTest {

	@Autowired
	BargainService bargainService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	VoteService voteService;
	
	@Test
	void shouldCheckIsOwnerCanVoteOnHisBargain() throws ResourceNotFoundException {
		// given
		Bargain bargain = bargainService.addBargain(givenHtc());
		User owner = givenOwner();
		userService.registerUser(owner);
		bargain.setUser(owner);
		
		// when
		voteService.vote(new VoteDto(VoteType.UPVOTE), bargain.getId(), owner.getEmail());
		
		// then
		Assertions.assertEquals(0, bargain.getVoteCount());
	}
	
	@Test
	void shouldCheckIsUserCanVoteOnBargain() throws ResourceNotFoundException {
		// given
		Bargain bargain = bargainService.addBargain(givenHtc());
		User owner = givenOwner();
		userService.registerUser(owner);
		bargain.setUser(owner);
		User user = givenUser();
		userService.registerUser(user);
		
		
		// when
		voteService.vote(new VoteDto(VoteType.UPVOTE), bargain.getId(), user.getEmail());
		
		// then
		Assertions.assertEquals(1, bargain.getVoteCount());
	}
	
//	@Test
//	public void shouldGetBargainById() {
//		// given
//		bargainService.addBargain(givenHtc());
//		bargainService.addBargain(givenAdidas());
//		
//		// when
//		Bargain bargain = bargainService.getById(1L)
//				.orElseThrow(() -> new IllegalArgumentException("Not found bargain with id " + 1));
//		
//		// then
//		Assertions.assertEquals("HTC Desire 21 Pro 5G 8/128GB Blue 90Hz", bargain.getTitle());
//	}
//	
//	@Test
//	public void shouldEditBargain() throws ResourceNotFoundException {
//		// given
//		bargainService.addBargain(givenHtc());
//		Bargain bargain = givenAdidas();
//		
//		// when
//		bargainService.editBargain(bargain,1L);
//		
//		// then
//		Assertions.assertEquals("adidas TREFOIL HOODIE", bargainService.getBargainById(1L).getTitle());
//	}
//	
//	@Test
//	public void shouldEditBargainTitle() throws ResourceNotFoundException {
//		// given
//		bargainService.addBargain(givenHtc());
//		bargainService.addBargain(givenAdidas());
//		
//		// when
//		bargainService.editBargainTitle("HTC", 1L);
//		
//		// then
//		Assertions.assertEquals("HTC", bargainService.getBargainById(1L).getTitle());
//	}
//	
//	@Test
//	public void shouldDeleteBargainById() throws ResourceNotFoundException {
//		// given
//		bargainService.addBargain(givenHtc());
//		bargainService.addBargain(givenAdidas());
//		
//		// when
//		bargainService.deleteBargainById(1L);
//		List<Bargain> all = bargainService.getAllBargains();
//		
//		// then
//		Assertions.assertEquals(1, all.size());
//		Assertions.assertFalse(bargainService.existsById(1L));
//
//	}
	
	private Bargain givenHtc() {
		
		return Bargain.builder()
				.title("HTC Desire 21 Pro 5G 8/128GB Blue 90Hz")
				.description("Smartphone htc desire 21")
				.reducePrice(1499.00)
				.normalPrice(1999.00)
				.delivery(0.0)
				.coupon("coupon-discount")
				.link("https://www.x-kom.pl/p/644074-smartfon-telefon-htc-desire-21-pro-5g-8-128gb-blue-0hz.html")
				.bargainPhoto(null)
				.closed(false)
				.category(Category.ELECTRONICS)
				.build();
		
	}
	
	private Bargain givenAdidas() {
		
		return Bargain.builder()
				.title("adidas TREFOIL HOODIE")
				.description("Adidas hoodie")
				.reducePrice(200.00)
				.normalPrice(249.00)
				.delivery(0.0)
				.coupon("sale")
				.link("https://www.zalando.pl/adidas-originals-trefoil-hoodie-unisex-bluza-crew-blue-ad122s084-k13.html?size=L")
				.bargainPhoto(null)
				.closed(false)
				.category(Category.FASHION)
				.build();
		
	}
	
	private User givenOwner() {
		return User.builder()
				.email("owner@demomail.com")
				.nickname("owner")
				.password("pass1234")
				.build();
	}
	
	private User givenUser() {
		return User.builder()
				.email("user@demomail.com")
				.nickname("user")
				.password("pass1234")
				.build();
	}
	
}
