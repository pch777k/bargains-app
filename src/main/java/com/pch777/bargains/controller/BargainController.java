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

import com.pch777.bargains.exception.ForbiddenException;
import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.ActivityType;
import com.pch777.bargains.model.Bargain;
import com.pch777.bargains.model.BargainDto;
import com.pch777.bargains.model.BargainPhoto;
import com.pch777.bargains.model.Category;
import com.pch777.bargains.model.Comment;
import com.pch777.bargains.model.CommentDto;
import com.pch777.bargains.model.PhotoFileDto;
import com.pch777.bargains.model.Shop;
import com.pch777.bargains.model.VoteDto;
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

		Sort sort = Sort.by("voteCount").descending().and(Sort.by("createdAt"));
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
		
		if(!isEmpty && keyword.length() > 0) {
			if(totalDisplayBargains == 0) {
				noResultsFound = true;
			}			
		}
		
		if(!isEmpty && keyword.length() > 0 && totalDisplayBargains > 0) {
			resultsFound = true;
		}
		
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("title", keyword);
		model.addAttribute("bargains", pageBargains);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentPage", page);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("totalPages", pageBargains.getTotalPages());
		model.addAttribute("voteDto", new VoteDto());
		model.addAttribute("today", LocalDate.now());	
		model.addAttribute("closed", ended);
		model.addAttribute("totalBargains", totalBargains);
		model.addAttribute("totalDisplayBargains", totalDisplayBargains);
		model.addAttribute("isEmptyListOfBargains", isEmpty);
		model.addAttribute("noResultsFound", noResultsFound);
		model.addAttribute("resultsFound", resultsFound);
		
		return "bargains";
	}
	
	@GetMapping("/new")
	public String listNewBargains(Model model, 
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(value = "ended", required = false) String ended,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {

		boolean noResultsFound = false;
		boolean resultsFound = false;
				
		Sort sort = Sort.by("createdAt").descending();
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

		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("title", keyword);
		model.addAttribute("bargains", pageBargains);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentPage", page);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("totalPages", pageBargains.getTotalPages());
		model.addAttribute("voteDto", new VoteDto());
		model.addAttribute("today", LocalDate.now());
		model.addAttribute("closed", ended);
		model.addAttribute("totalBargains", totalBargains);
		model.addAttribute("totalDisplayBargains", totalDisplayBargains);
		model.addAttribute("noResultsFound", noResultsFound);
		model.addAttribute("resultsFound", resultsFound);

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
		
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("title", keyword);
		model.addAttribute("bargains", pageBargains);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentPage", page);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("totalPages", pageBargains.getTotalPages());
		model.addAttribute("voteDto", new VoteDto());
		model.addAttribute("today", LocalDate.now());
		model.addAttribute("closed", ended);
		model.addAttribute("totalBargains", totalBargains);
		model.addAttribute("totalDisplayBargains", totalDisplayBargains);
		model.addAttribute("noResultsFound", noResultsFound);
		model.addAttribute("resultsFound", resultsFound);
	
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
		
		Sort sort = Sort.by("voteCount").descending().and(Sort.by("createdAt"));
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
	
		model.addAttribute("category", category);
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("title", keyword);
		model.addAttribute("bargains", pageBargainsByCategory);
		model.addAttribute("totalBargains", totalBargains);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentPage", page);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("totalPages", pageBargainsByCategory.getTotalPages());
		model.addAttribute("voteDto", new VoteDto());
		model.addAttribute("today", LocalDate.now());
		model.addAttribute("closed", ended);
		model.addAttribute("totalDisplayBargains", totalDisplayBargains);
		model.addAttribute("noResultsFound", noResultsFound);
		model.addAttribute("resultsFound", resultsFound);
		
		return "category";
	}

	@GetMapping("/{bargainCategory}/new")
	public String listNewBargainsByCategory(@PathVariable String bargainCategory, Model model, 
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(value = "ended", required = false) String ended,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) {
		
		boolean noResultsFound = false;
		boolean resultsFound = false;

		Sort sort = Sort.by("createdAt").descending();
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
	
		model.addAttribute("category", category);
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("title", keyword);
		model.addAttribute("bargains", pageBargainsByCategory);
		model.addAttribute("totalBargains", totalBargains);
		model.addAttribute("totalDisplayBargains", totalDisplayBargains);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentPage", page);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("totalPages", pageBargainsByCategory.getTotalPages());
		model.addAttribute("voteDto", new VoteDto());
		model.addAttribute("today", LocalDate.now());	
		model.addAttribute("closed", ended);
		model.addAttribute("noResultsFound", noResultsFound);
		model.addAttribute("resultsFound", resultsFound);
		
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
	
		model.addAttribute("category", category);
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("title", keyword);
		model.addAttribute("bargains", pageBargainsByCategory);
		model.addAttribute("totalBargains", totalBargains);
		model.addAttribute("totalDisplayBargains", totalDisplayBargains);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentPage", page);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("totalPages", pageBargainsByCategory.getTotalPages());
		model.addAttribute("voteDto", new VoteDto());
		model.addAttribute("today", LocalDate.now());
		model.addAttribute("closed", ended);
		model.addAttribute("noResultsFound", noResultsFound);
		model.addAttribute("resultsFound", resultsFound);
		
		return "category_commented";
	}
	
	@GetMapping("/shops/{shopId}")
	public String listShopBargains(@PathVariable Long shopId, Model model, 
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) throws ResourceNotFoundException {

		Sort sort = Sort.by("createdAt").descending();
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
		
		if(!isEmpty && keyword.length() > 0) {
			if(totalDisplayBargains == 0) {
				noResultsFound = true;
			}			
		}
		
		if(!isEmpty && keyword.length() > 0 && totalDisplayBargains > 0) {
			resultsFound = true;
		}
		
		model.addAttribute("shop", shop);
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("title", keyword);
		model.addAttribute("bargains", pageBargains);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentPage", page);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("totalPages", pageBargains.getTotalPages());
		model.addAttribute("voteDto", new VoteDto());
		model.addAttribute("today", LocalDate.now());	
		model.addAttribute("totalBargains", totalBargains);
		model.addAttribute("totalDisplayBargains", totalDisplayBargains);
		model.addAttribute("isEmptyListOfBargains", isEmpty);
		model.addAttribute("noResultsFound", noResultsFound);
		model.addAttribute("resultsFound", resultsFound);
		
		return "shop_bargains";
	}
	
	@GetMapping("/bargains/{bargainId}")
	public String getBargainById(Model model, @PathVariable Long bargainId, 
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) throws ResourceNotFoundException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		Bargain bargain = bargainService.getBargainById(bargainId);
		Sort sort = Sort.by("createdAt").ascending();
		Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
		Page<Comment> pageCommentsByBargainId = commentService.getCommentsByBargainId(pageable, bargainId);
		Category category = bargain.getCategory();

		model.addAttribute("category", category);
		model.addAttribute("commentDto", new CommentDto());
		model.addAttribute("loggedUser", email);
		model.addAttribute("currentUser", userService.findUserByEmail(email));
		model.addAttribute("totalComments", pageCommentsByBargainId.getTotalElements());
		model.addAttribute("pageComments", pageCommentsByBargainId);
		model.addAttribute("pageTotalComments", pageCommentsByBargainId.getSize());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentPage", page);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("totalPages", pageCommentsByBargainId.getTotalPages());
		model.addAttribute("bargain", bargain);
		model.addAttribute("voteDto", new VoteDto());		
		
		return "bargain";
	}

	@GetMapping("/bargains/add")
	public String showAddBargainForm(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		List<Shop> shops = shopService.getAllShops();
		
		model.addAttribute("shops", shops);
		model.addAttribute("currentUser", userService.findUserByEmail(email));	
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
			model.addAttribute("currentUser", userService.findUserByEmail(email));
			return "add_bargain_form";
		}

		Bargain bargain = bargainService.bargainDtoToBargain(bargainDto);
		
		bargainService.addBargain(bargain);
		bargain.setUser(userService.findUserByEmail(email));	
		BargainPhoto bargainPhoto = bargainPhotoService.getBargainPhotoById(1L)
				.orElseThrow(ResourceNotFoundException::new);
		bargain.setBargainPhoto(bargainPhoto);		

		activityService.addActivity(bargain.getUser(), bargain.getCreatedAt(), bargain, ActivityType.BARGAIN);
		
		return "redirect:/bargains/" + bargain.getId();
	}

	@GetMapping("/bargains/{bargainId}/edit")
	public String showEditBargainForm(@PathVariable Long bargainId, Model model) throws ResourceNotFoundException {
			
		Bargain bargain = bargainService.getBargainById(bargainId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		if(userSecurity.isOwnerOrAdmin(bargain.getUser().getEmail(), email)) {
			List<Shop> shops = shopService.getAllShops();
			
			BargainDto bargainDto = bargainService.bargainToBargainDto(bargain);
			model.addAttribute("shops", shops);
			model.addAttribute("currentUser", userService.findUserByEmail(email));
			model.addAttribute("bargainDto", bargainDto);
		} else {
			throw new ForbiddenException("You don't have permission to do it.");
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
			model.addAttribute("currentUser", userService.findUserByEmail(email));
			return "edit_bargain_form";
		}
			
		Bargain bargain = bargainService.getBargainById(bargainId);	
		bargain.setTitle(bargainDto.getTitle());
		bargain.setDescription(bargainDto.getDescription());
		bargain.setReducePrice(bargainDto.getReducePrice());
		bargain.setNormalPrice(bargainDto.getNormalPrice());
		bargain.setDelivery(bargainDto.getDelivery());
		bargain.setCoupon(bargainDto.getCoupon());
		bargain.setLink(bargainDto.getLink());
		bargain.setStartBargain(bargainDto.getStartBargain());
		bargain.setEndBargain(bargainDto.getEndBargain());
		bargain.setShop(bargainDto.getShop());
			
		return "redirect:/bargains/" + bargainId;
	}
	
	@GetMapping("/bargains/{bargainId}/photo/edit")
	public String showBargainPhoto(@PathVariable Long bargainId, Model model) throws ResourceNotFoundException {
		
		Bargain bargain = bargainService.getBargainById(bargainId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
			
		if(userSecurity.isOwnerOrAdmin(bargain.getUser().getEmail(), email)) {	
			model.addAttribute("currentUser", userService.findUserByEmail(email));
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
    		model.addAttribute("currentUser", userService.findUserByEmail(email));
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

		return "redirect:/bargains/" + bargainId; 	
	}

	@GetMapping("/bargains/{bargainId}/delete")
	@Transactional
	public String deleteBargainById(@PathVariable Long bargainId) throws ResourceNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		
		if(userSecurity.isOwnerOrAdmin(bargainService.getBargainById(bargainId).getUser().getEmail(), email)) {
			bargainService.deleteBargainById(bargainId);
		} else {
			throw new ForbiddenException("You don't have permission to do it.");
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
			throw new ForbiddenException("You don't have permission to do it.");
		}
		return "redirect:/bargains/" + bargainId;
	}
	
	@GetMapping("/bargains/{bargainId}/open")
	public String openBargainById(@PathVariable Long bargainId) throws ResourceNotFoundException {  
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		if(userSecurity.isOwnerOrAdmin(bargainService.getBargainById(bargainId).getUser().getEmail(), email)) {
			bargainService.openBargainById(bargainId);
		} else {
			throw new ForbiddenException("You don't have permission to do it.");
		}
			return "redirect:/bargains/" + bargainId;
		}
	
	  @GetMapping("bargains/{bargainId}/photo")
	    public void getImage(@PathVariable Long bargainId, HttpServletResponse response) throws Exception {
		    Bargain bargain = bargainService.getBargainById(bargainId);
		    BargainPhoto bargainPhoto = bargainPhotoService.getBargainPhotoById(bargain.getBargainPhoto().getId())
					.orElseThrow(() -> new ResourceNotFoundException());
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