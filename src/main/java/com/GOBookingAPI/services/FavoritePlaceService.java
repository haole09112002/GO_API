package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.FavouritePlace;
import com.GOBookingAPI.payload.request.FavoritePlaceRequest;

import java.util.List;

public interface FavoritePlaceService {

    FavouritePlace create(FavoritePlaceRequest req, String email);

    List<FavouritePlace> getByUser(String email);

    void removeById(int id, String email);
}
