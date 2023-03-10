package com.mocha.shopwebsite.controllers;

import com.mocha.shopwebsite.data.Item;
import com.mocha.shopwebsite.repositories.ItemRepository;
import com.mocha.shopwebsite.utility.Helper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Controller
public class HomeController {

    private ItemRepository itemRepository;

    public HomeController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Shows homepage with session data if one is instanced
     * @param model Holds application data, session username and Logged in status
     * @param session Holds session data
     * @return home.html
     */
    @GetMapping(value = {"/", "/home"})
    public String showHomePage(Model model, HttpSession session) {

        //todo Cache result of itemRepository query
        List<Item> items = itemRepository.findAll();

        model.addAttribute("items", items.size() == 0 ? "No items found" : items);

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
     * @param model Acts as a link between the controller and the HTML page, allowing data to be injected into the page
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

    @GetMapping("/about")
    public String showAboutPage(Model model, HttpSession session) {
        model.addAttribute("loggedIn", Helper.isLoggedIn(session));

        return "about";
    }
}
