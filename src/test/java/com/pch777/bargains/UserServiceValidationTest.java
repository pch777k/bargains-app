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
import com.pch777.bargains.security.UserSecurity;
import com.pch777.bargains.service.ActivityService;
import com.pch777.bargains.service.BargainService;
import com.pch777.bargains.service.CommentService;
import com.pch777.bargains.service.UserPhotoService;
import com.pch777.bargains.service.UserService;
import com.pch777.bargains.service.VoteService;

@SpringBootTest
class UserServiceValidationTest {
	@Autowired
	private UserService userService;
	@Autowired
	private UserPhotoService userPhotoService;
	@Autowired
	private BargainService bargainService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private VoteService voteService;
	@Autowired
	private ActivityService activityService;
	@Autowired
	private UserSecurity userSecurity;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

private MockMvc mockMvc;
    
    @BeforeEach
    void setup(){
        this.mockMvc = MockMvcBuilders
          .standaloneSetup(new AppController(userService,userPhotoService, bargainService, commentService, voteService,activityService,
    	userSecurity, bCryptPasswordEncoder)).build();
    }
    
    @Test
    void givenMatchingPassword_whenPostRegisterUser_thenOk() 
      throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
          .post("/process_register")
          .accept(MediaType.TEXT_HTML)
          .param("nickname", "joe")
          .param("email", "joe@demomail.com")   
          .param("password", "pass")
          .param("confirmPassword", "pass"))
          .andExpect(model().errorCount(0))
          .andExpect(status().isOk());
    }
    
    @Test
    void givenNotMatchingPassword_whenPostRegisterUser_thenOk() 
      throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
          .post("/process_register")
          .accept(MediaType.TEXT_HTML)
          .param("nickname", "joe")
          .param("email", "joe@demomail.com")   
          .param("password", "pass")
          .param("confirmPassword", "pass1"))
          .andExpect(model().errorCount(1))
          .andExpect(status().isOk());
    }
    
    @Test
    void givenTooShortPassword_whenPostRegisterUser_thenOk() 
      throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
          .post("/process_register")
          .accept(MediaType.TEXT_HTML)
          .param("nickname", "joe")
          .param("email", "joe@demomail.com")   
          .param("password", "pa")
          .param("confirmPassword", "pa"))
          .andExpect(model().errorCount(1))
          .andExpect(status().isOk());
    }
    
    @Test
    void givenEmptyUser_whenPostRegisterUser_thenOk() 
      throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
          .post("/process_register")
          .accept(MediaType.TEXT_HTML)
          .param("nickname", "")
          .param("email", "joe@demomail.com")   
          .param("password", "pass")
          .param("confirmPassword", "pass"))
          .andExpect(model().errorCount(1))
          .andExpect(status().isOk());
    }
    
    @Test
    void givenBlankUser_whenPostRegisterUser_thenOk() 
      throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
          .post("/process_register")
          .accept(MediaType.TEXT_HTML)
          .param("nickname", "  ")
          .param("email", "joe@demomail.com")   
          .param("password", "pass")
          .param("confirmPassword", "pass"))
          .andExpect(model().errorCount(1))
          .andExpect(status().isOk());
    }
    
    @Test
    void givenEmptyEmail_whenPostRegisterUser_thenOk() 
      throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
          .post("/process_register")
          .accept(MediaType.TEXT_HTML)
          .param("nickname", "joe")
          .param("email", "")   
          .param("password", "pass")
          .param("confirmPassword", "pass"))
          .andExpect(model().errorCount(2))
          .andExpect(status().isOk());
    }
    
    @Test
    void givenNotValidEmail_whenPostRegisterUser_thenOk() 
      throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
          .post("/process_register")
          .accept(MediaType.TEXT_HTML)
          .param("nickname", "joe")
          .param("email", "joe@com")   
          .param("password", "pass")
          .param("confirmPassword", "pass"))
          .andExpect(model().errorCount(1))
          .andExpect(status().isOk());
    }
    
   
	
}
