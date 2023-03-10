package com.mocha.shopwebsite.controllers;



import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import com.mocha.shopwebsite.data.Item;
import com.mocha.shopwebsite.data.User;
import com.mocha.shopwebsite.repositories.BasketRepository;
import com.mocha.shopwebsite.repositories.ItemRepository;
import com.mocha.shopwebsite.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
class ItemsControllerTest {

    @Mock
    private ItemsController underTest;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BasketRepository basketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;
    @Mock
    private Model model;

    @BeforeEach
    void setUp(){
        underTest = new ItemsController(itemRepository, basketRepository, userRepository);
    }
    @Test
    void canAddItemSubmit() {
        //Given
        User testUser = new User(2, "username");
        String username = testUser.getUsername();

        session.setAttribute(username, username);
        userRepository.save(testUser);
        Item item = new Item();

        //When
        underTest.addItemSubmit(item, model, session);

        //then
        ArgumentCaptor<Item> itemArgumentCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item capturedItem = itemArgumentCaptor.getValue();
        assertThat(capturedItem).isEqualTo(item);
    }
}