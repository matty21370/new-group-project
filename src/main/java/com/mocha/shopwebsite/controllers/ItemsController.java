package com.mocha.shopwebsite.controllers;

import com.mocha.shopwebsite.data.*;


import com.mocha.shopwebsite.repositories.BasketRepository;
import com.mocha.shopwebsite.repositories.ItemRepository;
import com.mocha.shopwebsite.repositories.UserRepository;
import com.mocha.shopwebsite.utility.Helper;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
@CrossOrigin
@Controller
public class ItemsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemsController.class);

    private final ItemRepository itemRepository;
    private final BasketRepository basketRepository;
    private final UserRepository userRepository;

    public ItemsController(ItemRepository itemRepository,
                           BasketRepository basketRepository,
                           UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.basketRepository = basketRepository;
        this.userRepository = userRepository;
    }

    /**
     * Method returns all items in itemRepository to catalog.html page to be displayed on that page
     * @param model   the Model that is used to render dynamic data into the view
     * @param session the HttpSession of the request calling this interface
     * @return the catalog template name
     */

    @GetMapping("/items")
    public String showItemsPage(Model model, HttpSession session) {
        Iterable<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        model.addAttribute("loggedIn", Helper.isLoggedIn(session));
        return "catalog";
    }

    /**
     * Retrieves the add item page based upon access control
     * @param model   the Model that is used to render data into the view
     * @param session the HTTP session of the request calling this interface
     * @return the add item page template name or a redirect to the login if not authenticated
     */
    @GetMapping("/add")
    public String showAddItemPage(Model model, HttpSession session) {

        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }

        model.addAttribute("item", new Item());
        model.addAttribute("loggedIn", session.getAttribute("username") != null);
        return "add-listing";
    }

    /**
     * Adds an item object to itemRepository through JpaRepository method save().
     *
     * @param item    item object to be added
     * @param model   the Model that is used to render data into the view
     * @param session the HTTP session calling the method
     * @return redirects to /items URL which displays the catalog HTML template
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addItemSubmit(@ModelAttribute Item item, Model model, HttpSession session) {
        //model.addAttribute("item", item); // todo remove this line if it works without this
        item.setUserId(Helper.GetUser(session, userRepository).getId());
        LOGGER.info("User ID:{}", item.getUserId());
        itemRepository.save(item);

        return "redirect:/items";
    }

    /**
     * Finds item object in itemRepository through its unique identifier. Displays the 'detail_product.html' page
     * containing product details
     *
     * @param id      unique identifier of item required
     * @param model   the Model that is used to render data into the view
     * @param session the HTTP session that is calling this method
     * @return the template name that renders the HTML page for the item
     */
    @RequestMapping("/item")
    public String getItem(@RequestParam long id, Model model, HttpSession session) {
        Optional<Item> foundItem = itemRepository.findById(id);

        if (foundItem.isPresent()) {
            Item item = foundItem.get();
            model.addAttribute("item", item);
            LOGGER.info("Item name:{}", item.getName());
        }

        model.addAttribute("loggedIn", Helper.isLoggedIn(session));

        return "detail_product";
    }

    /**
     *Handles the GET request for the /itemsDelete URL. Displays the template for 'itemsDelete.HTML'
     * @param model   the Model that is used to render data into the view
     * @param session the HTTP session that is calling the method
     * @return the HTML template for the webpage
     */
    @GetMapping("/itemsDelete")
    public String showItemsDeletePage(Model model, HttpSession session) {
        Iterable<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);

        model.addAttribute("loggedIn", Helper.isLoggedIn(session));

        return "itemsDelete";
    }

    /**
     * Adds item object to itemRepository through JpaRepository method save()
     * @param items object to be added
     * @return a redirect to method responsible for handling requests to /list URL
     */
    @PostMapping("/saveItem")
    public String saveEmployee(@ModelAttribute Item items) {
        itemRepository.save(items);
        return "redirect:/list";
    }

    /**
     * Handles request for /showUpdateForm URL based on whether item is in repository.
     * @param id Unique identifier of item in items list.
     * @return detail_product.html || add-item-form.html
     */

    @GetMapping("/showUpdateForm")
    public ModelAndView showUpdateForm(@RequestParam Long id) {
        ModelAndView mav = new ModelAndView("add-item-form");
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty()) {
            return new ModelAndView("detail_product");
        }
        Item items = optionalItem.get();
        mav.addObject("items", items);
        return mav;
    }

    /**
     * Handles the HTML POST request for the /delete route.
     * Deletes item from itemRepository based on its unique identifier
     * @param id unique identifier of item to be deleted
     * @return redirect to /itemsDelete URL
     */

    @PostMapping("/delete")
    public String deleteItem(@RequestParam Long id) {
        itemRepository.deleteById(id);
        return "redirect:/itemsDelete";
    }

    /**
     * Handles the HTML POST request for the /update route.
     * Fetches an Item from ItemRepository using its unique ID and applies the updated values.
     * @param id        Unique identifier of item to be updated
     * @param string    Image URL string to update item Image property
     * @param stringone Name string to update item Name property
     * @return a redirect to /items provided successful update, otherwise returns the name of the error HTML template
     */

    @PostMapping("/update")
    public String updateItem(@RequestParam Long id, @RequestParam String string,
                             @RequestParam String stringone) {

        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            item.setName(stringone);
            item.setImage(string);
            itemRepository.save(item);
            return "redirect:/items";
        }

        return "error";
    }

    /**
     * Handles GET request for /addToBasket URL. Adds basket object to basket repository. Sets basket object id
     * property to be same as item ID.
     * @param id      unique identifier to set to Basket Object
     * @param session the HTTP session calling the method
     * @return redirects to /login URL|| /items URL
     */
    @GetMapping("/addToBasket")
    public String addToBasket(@RequestParam long id, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login";
        }

        Basket basket = new Basket();
        User user = userRepository.findUserByUsername(username);
        basket.setItemId(id);
        basket.setUserId(user.getId());
        LOGGER.info("Basket item ID:{}", basket.getItemId());
        basketRepository.save(basket);

        return "redirect:/items";
    }

    /**
     * Returns number of items in itemRepository
     * @return a value with number of items
     */
    @GetMapping("/records")
    @ResponseBody
    public long getNumberOfRecords() {
        return itemRepository.count();
    }

    /**
     * Handles the GET request for /checkout URL. Creates a list to add items based on a unique identifier supplied by
     * basket object.
     * adds item to list if available in repository.
     * @param model   the Model used to render data into the view
     * @param session the HTTP session calling the method
     * @return the checkout.HTML template
     */
    @GetMapping("/checkout")
    public String viewBasket(Model model, HttpSession session) {

        User user = getUser(session);
        List<Basket> orders = basketRepository.findByUserId(user.getId());
        List<Item> items = new ArrayList<>();

        for (Basket basket : orders) {
            Optional<Item> item = itemRepository.findById(basket.getId());
            item.ifPresent(items::add);
        }
        model.addAttribute("items", items);
        model.addAttribute("loggedIn", Helper.isLoggedIn(session));

        return "checkout";
    }

    private User getUser(HttpSession session) {
        return userRepository.findUserByUsername(
                (String) session.getAttribute("username"));
    }

    /**
     * Handles the HTTP POST request for checkoutOne URL. Responsible for deleting basket object from basketRepository
     * @param id unique identifier for Basket object to be deleted
     * @return the checkout.HTML template
     */
    @PostMapping("/checkoutOne")
    public String deleteItem(@RequestParam Integer id) {
        basketRepository.deleteById(id);
        return "checkout";
    }

    /**
     * Handles HTTP POST request for /checkingOut URL. Responsible for deleting all objects from basket repository
     * when checking out.
     * @return the checkingOut.HTML template.
     */
    @PostMapping("/checkingOut")
    public String checkingOut() {
        basketRepository.deleteAll();
        return "checkingOut";
    }
}
