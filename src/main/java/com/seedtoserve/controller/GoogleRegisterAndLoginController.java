package com.seedtoserve.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class GoogleRegisterAndLoginController {

	 /**
     * Redirect user to Google's OAuth2 login page.
     * Spring Security handles the actual OAuth flow.
	 * @throws java.io.IOException 
	 * @throws java.io.IOException 
     */
	
    @GetMapping("/oauth2/authorize/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }
    
}
