package com.mocha.shopwebsite.controllers;
import com.mocha.shopwebsite.data.User;
import com.mocha.shopwebsite.repositories.UserRepository;

import com.mocha.shopwebsite.utility.Helper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Handles the HTTP GET request for the /login mapping.
     * Responsible for displaying the login page, redirects to home if user is already logged in.
     * @param model Enables the ability to pass data dynamically to the template.
     * @param session the users current HTTPS session, used for accessing data related to the user account.
     * @return either a redirect to the home page, or the name of the sign-in Thymeleaf template.
     */
    @GetMapping("/login")
    public String showLogin(Model model, HttpSession session) {

        if(Helper.isLoggedIn(session)) {
            return "redirect:/home";
        }

        model.addAttribute("loginForm", new User());
        model.addAttribute("loggedIn", Helper.isLoggedIn(session));

        return "sign-in";
    }

    /**
     * Handles the HTTP POST request for the /login mapping.
     * Responsible for authenticating the user with the provided details in the login form.
     * @param user The user object instantiated when the form was submitted, using data from the form.
     * @param model Enables the ability to pass data dynamically to the template.
     * @param session the users current HTTPS session, used for modifying data related to the user account.
     * @return a redirect to the home page if the user successfully signs in, otherwise an error message.
     */
    @PostMapping("/login")
    public String login(@ModelAttribute(name="loginForm") User user, Model model, HttpSession session) {
        String username = user.getUsername();
        String pass = user.getPassword();

        User foundUser = userRepository.findUserByUsername(username);

        if(foundUser != null) {
            if(pass.equals(foundUser.getPassword())) {
                session.setAttribute("username", username);
                return "redirect:home";
            } else {
                model.addAttribute("error", "Invalid password");
                return "sign-in";
            }
        } else {
            model.addAttribute("error", "Invalid username");
            return "sign-in";
        }
    }

    /**
     * Handles the HTTP GET request for the /register mapping.
     * Displays the register webpage if the user is not signed in.
     * @param session the users current HTTP session, used for checking whether the user is signed in.
     * @param model used for passing data dynamically to the Thymeleaf template
     * @return either the name of either the register or home Thymeleaf templates
     */
    @GetMapping("/register")
    public String getRegisterPage(HttpSession session, Model model) {
        boolean loggedIn = session.getAttribute("username") != null;
        model.addAttribute("loggedIn", Helper.isLoggedIn(session));

        if(loggedIn) {
            return "home";
        }

        model.addAttribute("newUser", new User());
        return "register";
    }

    /**
     * Handles the HTTP POST request for the /register mapping.
     *
     * @param user
     * @param m
     * @param session
     * @return
     */
    @PostMapping("/register")
    public String registerAccount(@ModelAttribute User user, Model m, HttpSession session) {
        userRepository.save(user);
        session.setAttribute("username", user.getUsername());
        return "redirect:/home";
    }

    /**
     * Called when the user clicks the 'Sign Out' button on the home screen. Simply removes the user attribute from the
     * session and redirects to the home page.
     * @param session
     * @return
     */
    @GetMapping("/sign-out")
    public String signOut(HttpSession session) {
        session.removeAttribute("username");
        return "redirect:/home";
    }
}