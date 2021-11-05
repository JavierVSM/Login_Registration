package com.javier.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.javier.models.Song;
import com.javier.models.User;
import com.javier.services.UserService;

@Controller
public class UserController {
	private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
	@RequestMapping( value = "/", method = RequestMethod.GET )
	public String index() {
		return "index.jsp";
	}
    
    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "registrationPage.jsp";
    }
   
    @RequestMapping("/login")
    public String login() {
        return "loginPage.jsp";
    }
        
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
    	if (result.hasErrors()) {
			return "redirect:/registration";
		}
    	User varUser= userService.registerUser(user);
    	session.setAttribute("userId", varUser.getId());    	
		return "redirect:/home"; 
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(
    		@RequestParam("email") String email, 
    		@RequestParam("password") String password, 
    		Model model, HttpSession session) {
    	
    	boolean validation = userService.authenticateUser(email, password);
    	if (validation) {
    		User userVar = userService.findByEmail(email);
    		session.setAttribute( "userId", userVar.getId() );
    		return "redirect:/home";
    	}
		else {
			model.addAttribute ("error", "Invalid credentials.");
			return "loginPage.jsp"; //Se debe redirigir a un JSP para que se muestre el mensaje de error.
		
		}		
    }
    
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
    	Long userId = (Long)session.getAttribute("userId");
    	User userVarSession = userService.findUserById(userId);
    	model.addAttribute("user", userVarSession );
    	return "homePage.jsp";
    }
    
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
    	session.invalidate();
    	return "redirect:/";
    }

}
