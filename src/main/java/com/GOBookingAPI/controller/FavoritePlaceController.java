package com.GOBookingAPI.controller;

import com.GOBookingAPI.payload.request.FavoritePlaceRequest;
import com.GOBookingAPI.services.FavoritePlaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController("favorite-places")
@Validated
public class FavoritePlaceController {

    @Autowired
    private FavoritePlaceService favoritePlaceService;

    @GetMapping("/favorite-places")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getListFavoritePlaces() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(favoritePlaceService.getByUser(email));
    }


    @PostMapping(value = "/favorite-places")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createFavoritePlace(@RequestBody @Valid FavoritePlaceRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(favoritePlaceService.create(request, email));
    }

    @DeleteMapping("/favorite-places/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> removeFavoritePlace(@PathVariable int id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        favoritePlaceService.removeById(id, email);
        return ResponseEntity.ok("success");
    }
}
