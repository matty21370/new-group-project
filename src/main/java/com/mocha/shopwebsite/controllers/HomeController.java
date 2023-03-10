package com.mocha.shopwebsite.controllers;

import com.mocha.shopwebsite.repositories.ItemRepository;
import com.mocha.shopwebsite.utility.Helper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 */
@Controller
public class HomeController {


    private final ItemRepository itemRepository;

    public HomeController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    /**
     * Handles the HTML GET request for the /home and / URLs. Responsible for displaying the 'home.html' webpage with
     * the appropriate authentication
     * @param model the Model that is used to render dynamic data into the view
     * @param session the HttpSession of the request calling this interface
     * @return the home template name
     */
    @GetMapping(value = {"/", "/home"})
    public String showHomePage(Model model, HttpSession session) {

        model.addAttribute("items", itemRepository.findAll());

        if(Helper.isLoggedIn(session)) {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("loggedIn", true);
        } else {
            model.addAttribute("loggedIn", false);
        }

        return "home";
    }

    /**
     * Handles the HTML GET request for the /account URL. Responsible for displaying the 'my_account.html' webpage given
     * the user has appropriate authentication.
     * @param model Acts as a link between this method and the HTML page, allowing data to be injected into the page
     *              dynamically.
     * @param session Stores user data throughout their session on the web app, such as their username.
     * @return The name of the template that renders the HTML page.
     */
    @GetMapping("/account")
    public String showAccountPage(Model model, HttpSession session) {
        if(Helper.isLoggedIn(session)) {
            return "redirect:/login";
        }

        model.addAttribute("loggedIn", true);
        return "my_account";
    }

    /**
     * Handles the HTML GET request for the /about URL. Responsible for displaying the about.html webpage
     * @param model the Model that is used to render data into the view
     * @param session the HTTP session of the request calling this interface
     * @return the HTML template for the about page
     */
    @GetMapping("/about")
    public String showAboutPage(Model model, HttpSession session) {
        model.addAttribute("loggedIn", Helper.isLoggedIn(session));

        return "about";
    }
}
