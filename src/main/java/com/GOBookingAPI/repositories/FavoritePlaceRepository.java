package com.GOBookingAPI.repositories;

import com.GOBookingAPI.entities.FavouritePlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoritePlaceRepository extends JpaRepository<FavouritePlace, Integer> {

    List<FavouritePlace> findByCustomerId(int uid);
}
