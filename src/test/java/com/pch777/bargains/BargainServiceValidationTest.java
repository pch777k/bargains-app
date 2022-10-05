package com.pch777.bargains;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.pch777.bargains.controller.AppController;
import com.pch777.bargains.controller.BargainController;
import com.pch777.bargains.security.UserSecurity;
import com.pch777.bargains.service.ActivityService;
import com.pch777.bargains.service.BargainPhotoService;
import com.pch777.bargains.service.BargainService;
import com.pch777.bargains.service.CommentService;
import com.pch777.bargains.service.ShopService;
import com.pch777.bargains.service.UserPhotoService;
import com.pch777.bargains.service.UserService;
import com.pch777.bargains.service.VoteService;
import com.pch777.bargains.utility.StringToEnumConverter;

@SpringBootTest
class BargainServiceValidationTest {
	@Autowired
	private BargainService bargainService;
	@Autowired
	private BargainPhotoService bargainPhotoService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private UserService userService;
	@Autowired
	private ActivityService activityService;
	@Autowired
	private UserSecurity userSecurity;
	@Autowired
	private StringToEnumConverter converter;

private MockMvc mockMvc;
    
    @BeforeEach
    public void setup(){
        this.mockMvc = MockMvcBuilders
          .standaloneSetup(new BargainController(bargainService,bargainPhotoService, shopService, commentService, userService,activityService,
    	userSecurity, converter)).build();
    }
    
    @Test
    void givenValidBargain_whenPostAddBargain_thenOk() 
      throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
          .post("/bargains/add")
          .accept(MediaType.TEXT_HTML)
          .param("title", "bargain")
          .param("description", "lorem ipsum")   
          .param("reducePrice", "10")
          .param("normalPrice", "15")
          .param("delivery", "0")  
          .param("coupon", "sale")
          .param("link", "link")   
          .param("closed", "false")
          .param("startBargain", "")
          .param("endBargain", "")
          .param("category", "FASHION")
          .param("shop", ""))
          .andExpect(model().errorCount(0))
          .andExpect(status().isOk());
    }
    
    
   
	
}
