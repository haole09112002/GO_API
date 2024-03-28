package com.GOBookingAPI.repositories.projection;

public interface StatisticsReviewBaseProjection {
	Object getDate();
	int getCount();
	int getFiveStar();
	int getFourStar();
	int getThreeStar();
	int getTwoStar();
	int getOneStar();
}
