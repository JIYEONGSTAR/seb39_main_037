package com.main.project.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.main.project.foodType.entity.FoodType;
import com.main.project.location.entity.Location;
import com.main.project.restaurant.entity.Restaurant;
import com.main.project.user.entity.WebUser;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponseDto {
    private long reviewId;
    private String Nickname;
    private String reviewTitle;
    private String reviewBody;
    private int view;
    private int thumbUp;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDateTime createdAt;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDateTime updatedAt;
    private String reviewImgUrl;
    private long restaurantId;
    private String restaurantName;
    private Integer tasteStar;
    private Integer facilityStar;
    private Integer priceStar;

    private long foodTypeId;
    private long locationId;
    private long userId;

    public void setUser(WebUser user) {
        this.userId = user.getUserId();
    }
    public void setRestaurant(Restaurant restaurant) {
        this.restaurantId = restaurant.getRestaurantId();
    }
    public void setFoodType(FoodType foodType) {
        this.foodTypeId = foodType.getFoodTypeId();
    }
    public void setLocation(Location location) {
        this.locationId = location.getLocationId();
    }
}
