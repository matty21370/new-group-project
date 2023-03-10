package com.mocha.shopwebsite.repositories;

import com.mocha.shopwebsite.data.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

public interface ItemRepository extends JpaRepository<Item, Long> {

    long count();
}
