package com.pch777.bargains.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.pch777.bargains.dto.BargainDto;
import com.pch777.bargains.dto.CommentDto;
import com.pch777.bargains.dto.PhotoFileDto;
import com.pch777.bargains.dto.VoteDto;
import com.pch777.bargains.exception.ForbiddenException;
import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.ActivityType;
import com.pch777.bargains.model.Bargain;
import com.pch777.bargains.model.BargainPhoto;
import com.pch777.bargains.model.Category;
import com.pch777.bargains.model.Comment;
import com.pch777.bargains.model.Shop;
import com.pch777.bargains.security.UserSecurity;
import com.pch777.bargains.service.ActivityService;
import com.pch777.bargains.service.BargainPhotoService;
import com.pch777.bargains.service.BargainService;
import com.pch777.bargains.service.CommentService;
import com.pch777.bargains.service.ShopService;
import com.pch777.bargains.service.UserService;
import com.pch777.bargains.utility.StringToEnumConverter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
public class BargainController {

	private static final String BARGAIN_CATEGORY = "category";
	private static final String SHOPS = "shops";
	private static final String YOU_DON_T_HAVE_PERMISSION_TO_DO_IT = "You don't have permission to do it.";
	private static final String CREATED_AT = "createdAt";
	private static final String TODAY = "today";
	private static final String RESULTS_FOUND = "resultsFound";
	private static final String NO_RESULTS_FOUND = "noResultsFound";
	private static final String TOTAL_DISPLAY_BARGAINS = "totalDisplayBargains";
	private static final String TOTAL_BARGAINS = "totalBargains";
	private static final String TOTAL_PAGES = "totalPages";
	private static final String CURRENT_SIZE = "currentSize";
	private static final String CURRENT_PAGE = "currentPage";
	private static final String PAGE_SIZE = "pageSize";
	private static final String TITLE = "title";
	private static final String CURRENT_USER = "currentUser";
	private static final String LOGGED_USER = "loggedUser";
	private static final String CLOSED = "closed";
	private static final String VOTE_DTO = "voteDto";
	private static final String BARGAINS = "bargains";
	private static final String REDIRECT_BARGAINS = "redirect:/bargains/";
	private BargainService bargainService;
	private BargainPhotoService bargainPhotoService;
	private ShopService shopService;
	private CommentService commentService;
	private UserService userService;
	private ActivityService activityService;
	private UserSecurity userSecurity;
	private StringToEnumConverter converter;	

	@GetMapping("/")
	public String listBargains(Model model, 
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(value = "ended", required = false) String ended,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {

		Sort sort = Sort.by("voteCount").descending().and(Sort.by(CREATED_AT));
		Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
		
		Page<Bargain> pageBargains = bargainService.getAllBargainsByTitleLike(pageable, keyword);
		long totalBargains = pageBargains.getTotalElements();
		if(("on").equals(ended)) {
			pageBargains = bargainService.getAllBargainsByTitleLikeAndClosed(pageable, keyword, false);
		}
		long totalDisplayBargains = pageBargains.getTotalElements();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
	
		boolean isEmpty = bargainService.getAllBargains().isEmpty();	
		boolean noResultsFound = false;
		boolean resultsFound = false;
		
		if(!isEmpty && keyword.length() > 0 && totalDisplayBargains == 0) {
				noResultsFound = true;			
		}
		
		if(!isEmpty && keyword.length() > 0 && totalDisplayBargains > 0) {
			resultsFound = true;
		}
		
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(TITLE, keyword);
		model.addAttribute(BARGAINS, pageBargains);
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_PAGE, page);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(TOTAL_PAGES, pageBargains.getTotalPages());
		model.addAttribute(VOTE_DTO, new VoteDto());
		model.addAttribute(TODAY, LocalDate.now());	
		model.addAttribute(CLOSED, ended);
		model.addAttribute(TOTAL_BARGAINS, totalBargains);
		model.addAttribute(TOTAL_DISPLAY_BARGAINS, totalDisplayBargains);
		model.addAttribute("isEmptyListOfBargains", isEmpty);
		model.addAttribute(NO_RESULTS_FOUND, noResultsFound);
		model.addAttribute(RESULTS_FOUND, resultsFound);
		
		return BARGAINS;
	}
	
	@GetMapping("/new")
	public String listNewBargains(Model model, 
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(value = "ended", required = false) String ended,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {

		boolean noResultsFound = false;
		boolean resultsFound = false;
				
		Sort sort = Sort.by(CREATED_AT).descending();
		Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
		Page<Bargain> pageBargains = bargainService.getAllBargainsByTitleLike(pageable, keyword);
		long totalBargains = pageBargains.getTotalElements();
		if(("on").equals(ended)) {
			pageBargains = bargainService.getAllBargainsByTitleLikeAndClosed(pageable, keyword, false);
		}
		long totalDisplayBargains = pageBargains.getTotalElements();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
				
		if(keyword.length() > 0 && totalDisplayBargains == 0) {
			noResultsFound = true;			
		}
		
		if(keyword.length() > 0 && totalDisplayBargains > 0) {
			resultsFound = true;
		}

		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(TITLE, keyword);
		model.addAttribute(BARGAINS, pageBargains);
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_PAGE, page);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(TOTAL_PAGES, pageBargains.getTotalPages());
		model.addAttribute(VOTE_DTO, new VoteDto());
		model.addAttribute(TODAY, LocalDate.now());
		model.addAttribute(CLOSED, ended);
		model.addAttribute(TOTAL_BARGAINS, totalBargains);
		model.addAttribute(TOTAL_DISPLAY_BARGAINS, totalDisplayBargains);
		model.addAttribute(NO_RESULTS_FOUND, noResultsFound);
		model.addAttribute(RESULTS_FOUND, resultsFound);

		return "bargains_new";
	}
	
	@GetMapping("/commented")
	public String listBargainsCommented(Model model, 
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(value = "ended", required = false) String ended,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {

		boolean noResultsFound = false;
		boolean resultsFound = false;
		
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		Page<Bargain> pageBargains = bargainService.getBargainsMostCommented(pageable, keyword);
		long totalBargains = pageBargains.getTotalElements();
		if(("on").equals(ended)) {
			pageBargains = bargainService.getBargainsNotClosedMostCommented(pageable, keyword, false);
		}
		long totalDisplayBargains = pageBargains.getTotalElements();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		if(keyword.length() > 0 && totalDisplayBargains == 0) {
			noResultsFound = true;			
		}
		
		if(keyword.length() > 0 && totalDisplayBargains > 0) {
			resultsFound = true;
		}
		
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(TITLE, keyword);
		model.addAttribute(BARGAINS, pageBargains);
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_PAGE, page);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(TOTAL_PAGES, pageBargains.getTotalPages());
		model.addAttribute(VOTE_DTO, new VoteDto());
		model.addAttribute(TODAY, LocalDate.now());
		model.addAttribute(CLOSED, ended);
		model.addAttribute(TOTAL_BARGAINS, totalBargains);
		model.addAttribute(TOTAL_DISPLAY_BARGAINS, totalDisplayBargains);
		model.addAttribute(NO_RESULTS_FOUND, noResultsFound);
		model.addAttribute(RESULTS_FOUND, resultsFound);
	
		return "bargains_commented";
	}
	
	@GetMapping("/{bargainCategory}")
	public String listBargainsByCategory(@PathVariable String bargainCategory, Model model, 
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(value = "ended", required = false) String ended,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {

		boolean noResultsFound = false;
		boolean resultsFound = false;
		
		Category category = converter.convert(bargainCategory);
		Category[] categories = Category.values();
		if(!Arrays.asList(categories).contains(category)) {
			return "redirect:/";
		}
		
		Sort sort = Sort.by("voteCount").descending().and(Sort.by(CREATED_AT));
		Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

		Page<Bargain> pageBargainsByCategory = bargainService.getAllBargainsByTitleLikeByCategory(pageable, keyword, category);
		long totalBargains = pageBargainsByCategory.getTotalElements();
		if(("on").equals(ended)) {
			pageBargainsByCategory = bargainService.getNotClosedBargainsByTitleLikeByCategory(pageable, keyword, category, false);
		}
		long totalDisplayBargains = pageBargainsByCategory.getTotalElements();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		if(keyword.length() > 0 && totalDisplayBargains == 0) {
			noResultsFound = true;			
		}
		
		if(keyword.length() > 0 && totalDisplayBargains > 0) {
			resultsFound = true;
		}
	
		model.addAttribute(BARGAIN_CATEGORY, category);
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(TITLE, keyword);
		model.addAttribute(BARGAINS, pageBargainsByCategory);
		model.addAttribute(TOTAL_BARGAINS, totalBargains);
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_PAGE, page);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(TOTAL_PAGES, pageBargainsByCategory.getTotalPages());
		model.addAttribute(VOTE_DTO, new VoteDto());
		model.addAttribute(TODAY, LocalDate.now());
		model.addAttribute(CLOSED, ended);
		model.addAttribute(TOTAL_DISPLAY_BARGAINS, totalDisplayBargains);
		model.addAttribute(NO_RESULTS_FOUND, noResultsFound);
		model.addAttribute(RESULTS_FOUND, resultsFound);
		
		return BARGAIN_CATEGORY;
	}

	@GetMapping("/{bargainCategory}/new")
	public String listNewBargainsByCategory(@PathVariable String bargainCategory, Model model, 
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(value = "ended", required = false) String ended,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {
		
		boolean noResultsFound = false;
		boolean resultsFound = false;

		Sort sort = Sort.by(CREATED_AT).descending();
		Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

		Category category = converter.convert(bargainCategory);
		Page<Bargain> pageBargainsByCategory = bargainService.getAllBargainsByTitleLikeByCategory(pageable, keyword, category);
		long totalBargains = pageBargainsByCategory.getTotalElements();
		if(("on").equals(ended)) {
			pageBargainsByCategory = bargainService.getNotClosedBargainsByTitleLikeByCategory(pageable, keyword, category, false);
		}
		long totalDisplayBargains = pageBargainsByCategory.getTotalElements();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		if(keyword.length() > 0 && totalDisplayBargains == 0) {
			noResultsFound = true;			
		}
		
		if(keyword.length() > 0 && totalDisplayBargains > 0) {
			resultsFound = true;
		}
	
		model.addAttribute(BARGAIN_CATEGORY, category);
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(TITLE, keyword);
		model.addAttribute(BARGAINS, pageBargainsByCategory);
		model.addAttribute(TOTAL_BARGAINS, totalBargains);
		model.addAttribute(TOTAL_DISPLAY_BARGAINS, totalDisplayBargains);
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_PAGE, page);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(TOTAL_PAGES, pageBargainsByCategory.getTotalPages());
		model.addAttribute(VOTE_DTO, new VoteDto());
		model.addAttribute(TODAY, LocalDate.now());	
		model.addAttribute(CLOSED, ended);
		model.addAttribute(NO_RESULTS_FOUND, noResultsFound);
		model.addAttribute(RESULTS_FOUND, resultsFound);
		
		return "category_new";
	}

	@GetMapping("/{bargainCategory}/commented")
	public String listMostCommentedBargainsByCategory(@PathVariable String bargainCategory, Model model, 
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(value = "ended", required = false) String ended,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {
		
		boolean noResultsFound = false;
		boolean resultsFound = false;

		Pageable pageable = PageRequest.of(page - 1, pageSize);

		Category category = converter.convert(bargainCategory);
		Page<Bargain> pageBargainsByCategory = bargainService.getBargainsMostCommentedByCategory(pageable, keyword, category);
		long totalBargains = pageBargainsByCategory.getTotalElements();
		if(("on").equals(ended)) {
			pageBargainsByCategory = bargainService.getBargainsNotClosedMostCommentedByCategory(pageable, keyword, category, false);
		}
		long totalDisplayBargains = pageBargainsByCategory.getTotalElements();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		if(keyword.length() > 0 && totalDisplayBargains == 0) {
			noResultsFound = true;			
		}
		
		if(keyword.length() > 0 && totalDisplayBargains > 0) {
			resultsFound = true;
		}
	
		model.addAttribute(BARGAIN_CATEGORY, category);
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(TITLE, keyword);
		model.addAttribute(BARGAINS, pageBargainsByCategory);
		model.addAttribute(TOTAL_BARGAINS, totalBargains);
		model.addAttribute(TOTAL_DISPLAY_BARGAINS, totalDisplayBargains);
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_PAGE, page);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(TOTAL_PAGES, pageBargainsByCategory.getTotalPages());
		model.addAttribute(VOTE_DTO, new VoteDto());
		model.addAttribute(TODAY, LocalDate.now());
		model.addAttribute(CLOSED, ended);
		model.addAttribute(NO_RESULTS_FOUND, noResultsFound);
		model.addAttribute(RESULTS_FOUND, resultsFound);
		
		return "category_commented";
	}
	
	@GetMapping("/shops/{shopId}")
	public String listShopBargains(@PathVariable Long shopId, Model model, 
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) throws ResourceNotFoundException {

		Sort sort = Sort.by(CREATED_AT).descending();
		Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
		
		Page<Bargain> pageBargains = bargainService.getBargainsByTitleLikeAndShopId(pageable, keyword, shopId);
		long totalBargains = pageBargains.getTotalElements();
		
		long totalDisplayBargains = pageBargains.getTotalElements();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
	
		Shop shop = shopService.getShopById(shopId);
		
		boolean isEmpty = bargainService.getAllBargains().isEmpty();	
		boolean noResultsFound = false;
		boolean resultsFound = false;
		
	 if(!isEmpty && keyword.length() > 0 && totalDisplayBargains == 0) {
				noResultsFound = true;		
		}
		
		if(!isEmpty && keyword.length() > 0 && totalDisplayBargains > 0) {
			resultsFound = true;
		}
		
		model.addAttribute("shop", shop);
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute(TITLE, keyword);
		model.addAttribute(BARGAINS, pageBargains);
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_PAGE, page);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(TOTAL_PAGES, pageBargains.getTotalPages());
		model.addAttribute(VOTE_DTO, new VoteDto());
		model.addAttribute(TODAY, LocalDate.now());	
		model.addAttribute(TOTAL_BARGAINS, totalBargains);
		model.addAttribute(TOTAL_DISPLAY_BARGAINS, totalDisplayBargains);
		model.addAttribute("isEmptyListOfBargains", isEmpty);
		model.addAttribute(NO_RESULTS_FOUND, noResultsFound);
		model.addAttribute(RESULTS_FOUND, resultsFound);
		
		return "shop_bargains";
	}
	
	@GetMapping("/bargains/{bargainId}")
	public String getBargainById(Model model, @PathVariable Long bargainId, 
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) throws ResourceNotFoundException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		Bargain bargain = bargainService.getBargainById(bargainId);
		Sort sort = Sort.by(CREATED_AT).ascending();
		Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
		Page<Comment> pageCommentsByBargainId = commentService.getCommentsByBargainId(pageable, bargainId);
		Category category = bargain.getCategory();

		model.addAttribute(BARGAIN_CATEGORY, category);
		model.addAttribute("commentDto", new CommentDto());
		model.addAttribute(LOGGED_USER, email);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
		model.addAttribute("totalComments", pageCommentsByBargainId.getTotalElements());
		model.addAttribute("pageComments", pageCommentsByBargainId);
		model.addAttribute("pageTotalComments", pageCommentsByBargainId.getSize());
		model.addAttribute(PAGE_SIZE, pageSize);
		model.addAttribute(CURRENT_PAGE, page);
		model.addAttribute(CURRENT_SIZE, pageable.getPageSize());
		model.addAttribute(TOTAL_PAGES, pageCommentsByBargainId.getTotalPages());
		model.addAttribute("bargain", bargain);
		model.addAttribute(VOTE_DTO, new VoteDto());		
		
		return "bargain";
	}

	@GetMapping("/bargains/add")
	public String showAddBargainForm(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		List<Shop> allShops = shopService.getAllShops();
		
		model.addAttribute(SHOPS, allShops);
		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));	
		model.addAttribute("bargain", new Bargain());
		model.addAttribute("bargainDto", new BargainDto());

		return "add_bargain_form";
	}

	@PostMapping("/bargains/add")
	@Transactional
	public String addBargain(@Valid @ModelAttribute("bargainDto") BargainDto bargainDto, 
			BindingResult bindingResult, Model model) throws ResourceNotFoundException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		if (bindingResult.hasErrors()) {
			List<Shop> allShops = shopService.getAllShops();
			
			model.addAttribute(SHOPS, allShops);
			model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
			return "add_bargain_form";
		}

		Bargain bargain = bargainService.bargainDtoToBargain(bargainDto);
		
		bargainService.addBargain(bargain);
		bargain.setUser(userService.findUserByEmail(email));	
		BargainPhoto bargainPhoto = bargainPhotoService.getBargainPhotoById(1L)
				.orElseThrow(ResourceNotFoundException::new);
		bargain.setBargainPhoto(bargainPhoto);		

		activityService.addActivity(bargain.getUser(), bargain.getCreatedAt(), bargain, ActivityType.BARGAIN);
		
		return REDIRECT_BARGAINS + bargain.getId();
	}

	@GetMapping("/bargains/{bargainId}/edit")
	public String showEditBargainForm(@PathVariable Long bargainId, Model model) throws ResourceNotFoundException {
			
		Bargain bargain = bargainService.getBargainById(bargainId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		if(userSecurity.isOwnerOrAdmin(bargain.getUser().getEmail(), email)) {
			List<Shop> allShops = shopService.getAllShops();
			
			BargainDto bargainDto = bargainService.bargainToBargainDto(bargain);
			model.addAttribute(SHOPS, allShops);
			model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
			model.addAttribute("bargainDto", bargainDto);
		} else {
			throw new ForbiddenException(YOU_DON_T_HAVE_PERMISSION_TO_DO_IT);
		}
		return "edit_bargain_form";
	}

	@PostMapping("/bargains/{bargainId}/edit")
	@Transactional
	public String editBargain(@PathVariable Long bargainId, 
			@Valid @ModelAttribute("bargainDto") BargainDto bargainDto, 
			BindingResult bindingResult, Model model) throws ResourceNotFoundException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		if (bindingResult.hasErrors()) {
			List<Shop> allShops = shopService.getAllShops();
			
			model.addAttribute(SHOPS, allShops);
			model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
			return "edit_bargain_form";
		}
			
		Bargain bargain = bargainService.getBargainById(bargainId);	
		bargain.setTitle(bargainDto.getTitle());
		bargain.setDescription(bargainDto.getDescription());
		bargain.setReducePrice(bargainDto.getReducePrice());
		bargain.setNormalPrice(bargainDto.getNormalPrice());
		bargain.setDelivery(bargainDto.getDelivery());
		bargain.setCoupon(bargainDto.getCoupon());
		bargain.setCategory(bargainDto.getCategory());
		bargain.setLink(bargainDto.getLink());
		bargain.setStartBargain(bargainDto.getStartBargain());
		bargain.setEndBargain(bargainDto.getEndBargain());
		bargain.setShop(bargainDto.getShop());
			
		return REDIRECT_BARGAINS + bargainId;
	}
	
	@GetMapping("/bargains/{bargainId}/photo/edit")
	public String showBargainPhoto(@PathVariable Long bargainId, Model model) throws ResourceNotFoundException {
		
		Bargain bargain = bargainService.getBargainById(bargainId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
			
		if(userSecurity.isOwnerOrAdmin(bargain.getUser().getEmail(), email)) {	
			model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
			model.addAttribute("photoFileDto", new PhotoFileDto());
		} else {
			throw new ForbiddenException("Access denied");
		}
		
		return "bargain-photo";
	}   
	
	@Transactional
	@RequestMapping("/bargains/{bargainId}/photo/edit")
	public String updatePhoto(@PathVariable Long bargainId, RedirectAttributes redirectAttributes,
			@Valid @ModelAttribute("photoFileDto") PhotoFileDto photoFileDto, 
			BindingResult result, Model model) throws IOException, ResourceNotFoundException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		Bargain bargain = bargainService.getBargainById(bargainId);
		
		if (result.hasErrors()) { 
    		model.addAttribute(CURRENT_USER, userService.findUserByEmail(email));
    		return "bargain-photo";
    	}
	    	
		BargainPhoto bargainPhoto = BargainPhoto.builder()
				.file(photoFileDto.getFileImage().getBytes())
				.filename(photoFileDto.getFileImage().getOriginalFilename())
				.contentType(photoFileDto.getFileImage().getContentType())
				.createdAt(LocalDate.now())
				.fileLength(photoFileDto.getFileImage().getSize())
				.build();
		bargainPhotoService.saveBargainPhoto(bargainPhoto);
		bargain.setBargainPhoto(bargainPhoto);
		redirectAttributes.addFlashAttribute("editedPhoto", "The photo has been edited successfully.");

		return REDIRECT_BARGAINS + bargainId; 	
	}

	@GetMapping("/bargains/{bargainId}/delete")
	@Transactional
	public String deleteBargainById(@PathVariable Long bargainId) throws ResourceNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		if(userSecurity.isOwnerOrAdmin(bargainService.getBargainById(bargainId).getUser().getEmail(), email)) {
			bargainService.deleteBargainById(bargainId);
		} else {
			throw new ForbiddenException(YOU_DON_T_HAVE_PERMISSION_TO_DO_IT);
		}
		return "redirect:/";
	}
	 
	@GetMapping("/bargains/{bargainId}/close")
	public String closeBargainById(@PathVariable Long bargainId) throws ResourceNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		if(userSecurity.isOwnerOrAdmin(bargainService.getBargainById(bargainId).getUser().getEmail(), email)) {
		bargainService.closeBargainById(bargainId);
		} else {
			throw new ForbiddenException(YOU_DON_T_HAVE_PERMISSION_TO_DO_IT);
		}
		return REDIRECT_BARGAINS + bargainId;
	}
	
	@GetMapping("/bargains/{bargainId}/open")
	public String openBargainById(@PathVariable Long bargainId) throws ResourceNotFoundException {  
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		if(userSecurity.isOwnerOrAdmin(bargainService.getBargainById(bargainId).getUser().getEmail(), email)) {
			bargainService.openBargainById(bargainId);
		} else {
			throw new ForbiddenException(YOU_DON_T_HAVE_PERMISSION_TO_DO_IT);
		}
			return REDIRECT_BARGAINS + bargainId;
		}
	
	  @GetMapping("bargains/{bargainId}/photo")
	    public void getImage(@PathVariable Long bargainId, HttpServletResponse response) throws Exception {
		    Bargain bargain = bargainService.getBargainById(bargainId);
		    BargainPhoto bargainPhoto = bargainPhotoService.getBargainPhotoById(bargain.getBargainPhoto().getId())
					.orElseThrow(ResourceNotFoundException::new);
	        byte[] bytes = bargainPhoto.getFile();
	        InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(bytes));
	        String mimeType = URLConnection.guessContentTypeFromStream(inputStream);
	        response.setContentType(mimeType);
	        OutputStream outputStream = response.getOutputStream();
	        outputStream.write(bytes);
	        outputStream.flush();
	        outputStream.close();
	    }

}	