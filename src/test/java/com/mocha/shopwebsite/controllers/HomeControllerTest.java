package com.mocha.shopwebsite.controllers;

import com.mocha.shopwebsite.data.Item;
import com.mocha.shopwebsite.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @MockBean ItemRepository itemRepository;

    @Autowired
    private MockMvc mvc;

    @Test
    public void showHomePageNotLoggedIn() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();

        mvc.perform(get("/").session(mockHttpSession)
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("loggedIn", false));
    }

    @Test
    public void showHomePageLoggedIn() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("username", "test");

        mvc.perform(get("/").session(mockHttpSession).accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("loggedIn", true))
                .andExpect(model().attribute("username", "test"));
    }

//    @Test
//    public void showHomePageNoItems() throws Exception {
//        MockHttpSession mockHttpSession = new MockHttpSession();
//        mockHttpSession.setAttribute("username", "test");
//
//        Iterable<Item> items = new ArrayList<>();
//        mockHttpSession.setAttribute("items", items);
//
//        mvc.perform(get("/").session(mockHttpSession).accept(MediaType.TEXT_HTML))
//                .andExpect(status().isOk())
//                .andExpect(view().name("home"))
//                .andExpect(model().attribute("items".length(), 0));
//    }

}
