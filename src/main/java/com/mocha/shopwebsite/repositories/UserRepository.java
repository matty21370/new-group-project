package com.mocha.shopwebsite.repositories;
import com.mocha.shopwebsite.data.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends CrudRepository<User, Integer> {

    User findUserByUsername(String username);

}