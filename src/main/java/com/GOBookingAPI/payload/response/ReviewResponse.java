package com.GOBookingAPI.payload.response;


import com.GOBookingAPI.entities.Review;
import com.GOBookingAPI.payload.request.ReviewRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class ReviewResponse extends ReviewRequest {

    private int id;

    private long createAt;

    public ReviewResponse(Review review) {
        this.bookingId = review.getBooking().getId();
        this.createAt = review.getCreateAt().getTime();
        this.id = review.getId();
        this.content = review.getContent();
        this.rating = review.getRating();
    }
}
