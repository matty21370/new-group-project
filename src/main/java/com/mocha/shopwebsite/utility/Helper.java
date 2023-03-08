package com.mocha.shopwebsite.utility;

import com.mocha.shopwebsite.data.User;
import com.mocha.shopwebsite.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class Helper {

    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("username") != null;
    }

    public static User GetUser(HttpSession session, UserRepository userRepository) {
        return userRepository.findUserByUsername((String )session.getAttribute("username"));
    }

}
