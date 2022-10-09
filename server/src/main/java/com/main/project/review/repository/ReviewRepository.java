package com.main.project.review.repository;

import com.main.project.review.entity.Review;
import com.main.project.user.entity.WebUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Modifying
    @Query("UPDATE Review r SET r.view = r.view + 1 WHERE r.reviewId = :reviewId")
    int view (long reviewId);

    @Query("SELECT r FROM Review r WHERE r.restaurant.id = :restaurantId")
    Page<Review> findByRestaurant(long restaurantId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.restaurant.location.id = :locationId")
    Page<Review> findByLocation(long locationId, Pageable pageable);
    @Query("SELECT r fROM Review r WHERE r.reviewTitle LIKE %:title%")
    Page<Review> findByReviewTitleContaining(@Param("title") String title, Pageable pageable); // containing 추가, 검색기능 구현
    Page<Review> findByWebUser(WebUser user, Pageable pageable);
    @Query("SELECT AVG(tasteStar) FROM Review r WHERE r.restaurant.id = :restaurantId")
    double avgTasteStar(long restaurantId); //별점 평균 구현
    @Query("SELECT AVG(facilityStar) FROM Review r WHERE r.restaurant.id = :restaurantId")
    double avgFacilityStar(long restaurantId); //별점 평균 구현
    @Query("SELECT AVG(priceStar) FROM Review r WHERE r.restaurant.id = :restaurantId")
    double avgPriceStar(long restaurantId); //별점 평균 구현

    Optional<Review> findByReviewId(long reviewId);
}
