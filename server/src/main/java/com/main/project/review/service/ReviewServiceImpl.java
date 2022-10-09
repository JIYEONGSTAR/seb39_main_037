package com.main.project.review.service;

import com.main.project.badge.service.BadgeServiceImpl;
import com.main.project.exception.BusinessLogicException;
import com.main.project.exception.ExceptionCode;
import com.main.project.restaurant.service.RestaurantServiceImpl;
import com.main.project.review.entity.Review;
import com.main.project.review.repository.ReviewRepository;
import com.main.project.user.entity.WebUser;
import com.main.project.user.repository.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService{

    ReviewRepository reviewRepository;
    UserService userService;
    RestaurantServiceImpl restaurantServiceImpl;

    BadgeServiceImpl badgeService;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserService userService, RestaurantServiceImpl restaurantServiceImpl, BadgeServiceImpl badgeService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.restaurantServiceImpl = restaurantServiceImpl;
        this.badgeService = badgeService;
    }

    public Review createReview(long userId, long restaurantId, Review review) {


        review.addWebUser(userService.checkUserByUserId(userId));
        review.addRestaurant(restaurantServiceImpl.findRestaurant(restaurantId));
        verifyReview(review);

        review.setReviewTitle(review.getReviewTitle());
        review.setReviewBody(review.getReviewBody());
        review.setTasteStar(review.getTasteStar());
        review.setFacilityStar(review.getFacilityStar());
        review.setPriceStar(review.getPriceStar());
        review.setReviewImgUrl(review.getReviewImgUrl());

//        badgeService.assignBadge(userId);//리뷰를 작성할 때마다 리뷰관련 뱃지 할당 조건을 체크하는 메서드
        //post review 시 해당 뱃지가 없다는 오류로 인해 주석처리 했음(pr전 해제)
        Review newReview = reviewRepository.save(review);
        badgeService.assignReviewBadge(userId);


        return newReview;
    }

    public Review updateReview(long reviewId, long userId, Review review) {

        //수정할 리뷰가 존재하는지 체크
        Review foundReview = findVerifiedReview(reviewId);
        //작성자와 회원이 동일한지 체크
        if(userId == foundReview.getWebUser().getUserId()){
            //수정할 사항(제목, 내용, 별점)이 존재하는지 체크

            Optional.ofNullable(review.getReviewTitle())
                    .ifPresent(foundReview::setReviewTitle);
            Optional.ofNullable(review.getReviewBody())
                    .ifPresent(foundReview::setReviewBody);
            Optional.ofNullable(review.getTasteStar())
                    .ifPresent(foundReview::setTasteStar);
            Optional.ofNullable(review.getFacilityStar())
                    .ifPresent(foundReview::setFacilityStar);
            Optional.ofNullable(review.getPriceStar())
                    .ifPresent(foundReview::setPriceStar);
            Optional.ofNullable(review.getReviewImgUrl())
                    .ifPresent(foundReview::setReviewImgUrl);

            return reviewRepository.save(foundReview);
        }else {
            new BusinessLogicException(ExceptionCode.WRITER_IS_NOT_MATCH);
        }
        return null;
    }

    public Review findReview(long reviewId) {

        return findVerifiedReview(reviewId);
    }

    public Page<Review> findAllReview(int page, int size) {

        return reviewRepository.findAll(PageRequest.of(page, size, Sort.by("reviewId").descending()));
    }
    public Page<Review> findRestaurantReview(long restaurantId, int page) {

        return reviewRepository.findByRestaurant(restaurantId, PageRequest.of(page, 10, Sort.by("reviewId").descending()));
    }
    public Page<Review> findLocationReview(long locationId, int page) {

        return reviewRepository.findByLocation(locationId, PageRequest.of(page, 10, Sort.by("reviewId").descending()));
    }
    public Page<Review> RestaurantReviewList(long restaurantId, int page){  // 평균 값 저장을 위해 생턴
        return reviewRepository.findByRestaurant(restaurantId, PageRequest.of(page, 10, Sort.by("reviewId").descending()));

    }
    public Page<Review> findUserReview(WebUser user, int page) {
        return reviewRepository.findByWebUser(user, PageRequest.of(page, 10, Sort.by("reviewId").descending()));
    }

    public void deleteReview(long reviewId) {

        Review review = findVerifiedReview(reviewId);
        reviewRepository.delete(review);

    }

    @Transactional
    public int updateView(long reviewId) {
        return reviewRepository.view(reviewId);
    }
    @Transactional
    public double aveTasteStar(long restaurantId) {return reviewRepository.avgTasteStar(restaurantId);}
    @Transactional
    public double aveFacilityStar(long restaurantId) {return reviewRepository.avgFacilityStar(restaurantId);}
    @Transactional
    public double avePriceStar(long restaurantId) {return reviewRepository.avgPriceStar(restaurantId);}
    private void verifyReview(Review review) {
//         회원이 존재하는지 확인
        userService.checkUserByUserId(review.getWebUser().getUserId());

//         식당이 존재하는지 확인
        restaurantServiceImpl.findRestaurant(review.getRestaurant().getRestaurantId());

//         음식이 존재하는지 확인 (비지니스 로직 구현)
    }

    public Review findVerifiedReview(long reviewId) {
        Optional<Review> review = reviewRepository.findByReviewId(reviewId);

        Review findReview = review.orElseThrow(() -> new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));

        return findReview;
    }

    @Transactional
    public Page<Review> search(String keyword, int page) { //리뷰 검색기능 구현

        return reviewRepository.findByReviewTitleContaining(keyword, PageRequest.of(page, 10, Sort.by("reviewId").descending()));
    }

}
