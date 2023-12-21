package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.entities.FavouritePlace;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.FavoritePlaceRequest;
import com.GOBookingAPI.repositories.FavoritePlaceRepository;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.FavoritePlaceService;
import com.GOBookingAPI.services.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoritePlaceServiceImpl implements FavoritePlaceService {

    @Autowired
    private MapService mapService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FavoritePlaceRepository favoritePlaceRepository;

    @Override
    public FavouritePlace create(FavoritePlaceRequest req, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
        String address = mapService.convertLocationToAddress(req.getLocation());
        FavouritePlace favouritePlace = new FavouritePlace();
        favouritePlace.setAddress(address);
        favouritePlace.setLocation(req.getLocation());
        favouritePlace.setCustomer(user.getCustomer());
        favoritePlaceRepository.save(favouritePlace);
        return favouritePlace;
    }

    @Override
    public List<FavouritePlace> getByUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
        return favoritePlaceRepository.findByCustomerId(user.getId());
    }

    @Override
    public void removeById(int id, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
        FavouritePlace removePlace = favoritePlaceRepository.findById(id).orElseThrow(() -> new NotFoundException("Khong tim thay favorite place: " + id));
        if (removePlace.getCustomer().getId() == user.getId())
            favoritePlaceRepository.deleteById(id);
        else
            throw new AccessDeniedException("Favorite place khong thuoc ve ban");
    }
}
