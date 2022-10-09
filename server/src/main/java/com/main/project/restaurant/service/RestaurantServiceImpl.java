package com.main.project.restaurant.service;

import com.main.project.exception.BusinessLogicException;
import com.main.project.exception.ExceptionCode;
import com.main.project.food.entity.Food;
import com.main.project.food.repository.FoodRepository;
import com.main.project.foodType.entity.FoodType;
import com.main.project.foodType.repository.FoodTypeRepository;
import com.main.project.foodType.service.FoodTypeServiceImpl;
import com.main.project.location.entity.Location;
import com.main.project.location.repository.CityRepository;
import com.main.project.location.repository.StateRepository;
import com.main.project.location.service.LocationServiceImpl;
import com.main.project.naver.GeoTrans;
import com.main.project.naver.NaverClient;
import com.main.project.naver.SearchLocalReq;
import com.main.project.restaurant.dto.RestaurantDto;
import com.main.project.restaurant.entity.Restaurant;
import com.main.project.restaurant.entity.RestaurantFood;
import com.main.project.restaurant.repository.RestaurantRepository;
import com.main.project.restaurant.repository.RestaurantFoodRepository;
import com.main.project.review.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.*;
@Service
public class RestaurantServiceImpl implements RestaurantService{
    private final NaverClient naverClient;
    private final RestaurantRepository restaurantRepository;
    ReviewRepository reviewRepository;
    LocationServiceImpl locationService;
    StateRepository stateRepository;

    FoodTypeRepository foodTypeRepository;
    CityRepository cityRepository;
    FoodTypeServiceImpl foodTypeService;
    RestaurantFoodRepository restaurantFoodRepository;
    FoodRepository foodRepository;


    public RestaurantServiceImpl(NaverClient naverClient, RestaurantRepository restaurantRepository, ReviewRepository reviewRepository, LocationServiceImpl locationService, StateRepository stateRepository, FoodTypeRepository foodTypeRepository, CityRepository cityRepository, FoodTypeServiceImpl foodTypeService, RestaurantFoodRepository restaurantFoodRepository, FoodRepository foodRepository) {
        this.naverClient = naverClient;
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
        this.locationService = locationService;
        this.stateRepository = stateRepository;
        this.foodTypeRepository = foodTypeRepository;
        this.cityRepository = cityRepository;
        this.foodTypeService = foodTypeService;
        this.restaurantFoodRepository = restaurantFoodRepository;
        this.foodRepository = foodRepository;
    }

    public Page<Restaurant> searchApi(String query){

        // 지역 검색 (var - 키워드는 지역 변수 타입 추론을 허용)
        var searchLocalReq = new SearchLocalReq();
        searchLocalReq.setQuery(query);

        var searchLocalRes = naverClient.localSearch(searchLocalReq);
        var result = new RestaurantDto();

        if(searchLocalRes.getTotal() > 0) {

            var localItem = searchLocalRes.getItems();

            for(var test: localItem){
                GeoTrans geo = new GeoTrans(0, test.getMapx(), test.getMapy()); //katech(네이버 제공 위치) -> 위경도 변환
                String editted= test.getTitle().replace("<b>" , "").replace("</b>","");
                String neweditted = null;
                if(editted.charAt(editted.length() - 2) == ' ' ){
                    neweditted = editted.substring(0, editted.length() - 1);
                }
                else{
                    neweditted = editted;
                }
                result.setRestaurantName(neweditted);

                String category  = test.getCategory().split(">",4)[0];
                    if(category.equals("음식점")){
                        result.setCategory(test.getCategory().split(">",4)[1]);
                    }else{
                        result.setCategory(category);
                    }
                String resState = test.getAddress().split(" ", 4)[0];
                String resCity = test.getAddress().split(" ", 4)[1];
                Location resLocation = locationService.findByLocation(stateRepository.findByStateName(resState), cityRepository.findByCityName(resCity));
                result.setLocationId(resLocation.getLocationId());
                result.setAddress(test.getAddress());
                result.setMapx(geo.outpt_x);
                result.setMapy(geo.outpt_y);
                System.out.println("test X: " + geo.outpt_x + ", test Y: " + geo.outpt_y);
                var restaurant = dtoToEntity(result);
                restaurant.setLocation(resLocation);

                List<FoodType> foodTypes = foodTypeRepository.findAll();

                for(FoodType foodType : foodTypes){
                   if( restaurant.getCategory().contains(foodType.getTypeName())) {
                       restaurant.setFoodType(foodType);
                   }
                }


                restaurantRepository.save(restaurant);

                RestaurantFood restaurantFood = new RestaurantFood();
                restaurantFood.setRestaurant(restaurant);
                String a =query.split(" ",2)[1];
                Food foundFood = foodRepository.findByFoodName(a).get();
                restaurantFood.setFood(foundFood);
                restaurantFood.setLocation(resLocation);
                restaurantFoodRepository.save(restaurantFood);
            }

        }
        return findAll(0,5);

    }

    private Restaurant dtoToEntity(RestaurantDto restaurantDto) {
        var restaurant = new Restaurant();
        restaurant.setRestaurantId(restaurantDto.getRestaurantId());
        restaurant.setRestaurantName(restaurantDto.getRestaurantName());
        restaurant.setCategory(restaurantDto.getCategory());
        restaurant.setAddress(restaurantDto.getAddress());
        restaurant.setMapx(restaurantDto.getMapx());
        restaurant.setMapy(restaurantDto.getMapy());

        return restaurant;
    }
    public Restaurant updateRestaurant(long restaurantId, String foodTypeName) { // 타입 등록시 지역 자동 추가
        Restaurant findRestaurant = findRestaurant(restaurantId);
        findRestaurant.addFoodType(foodTypeService.findFoodType(foodTypeName));
        return restaurantRepository.save(findRestaurant);
    }

    public Page<Restaurant> findAll(int page, int size) {
        return restaurantRepository.findAll(PageRequest.of(page, size, Sort.by("restaurantId").descending()));
    }
    public Restaurant findRestaurant(long restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        Restaurant foundRestaurant = restaurant.orElseThrow(() -> new BusinessLogicException(ExceptionCode.RESTAURANT_NOT_FOUND));
        return foundRestaurant;
    }

    @Transactional
    public Page<Restaurant> search(String title, int page) { //리뷰 검색기능 구현

        Page<Restaurant> restaurants =  restaurantRepository.findByRestaurantNameContaining(title, PageRequest.of(page, 10, Sort.by("restaurantId").descending()));
        return  restaurants;
    }
    public Page<Restaurant> findLocationRestaurant(long locationId, int page) {

        return restaurantRepository.findByLocation(locationId, PageRequest.of(page, 10, Sort.by("restaurantId").descending()));
    }

    public void delete(long restaurantId) {
        restaurantRepository.deleteById(restaurantId);
    }

}