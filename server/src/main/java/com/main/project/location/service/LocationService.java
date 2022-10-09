package com.main.project.location.service;


import com.main.project.location.dto.PostDto;
import com.main.project.location.entity.City;
import com.main.project.location.entity.Location;
import com.main.project.location.entity.State;

import java.util.List;

public interface LocationService {

    public List<State> selectState();
    public List<Location> selectCity(State state);
    public State createState(State state);  //관리자용
    public City createCity(City city);   //관리자용
    public Location createLocation(PostDto.LocationPostDto locationPostDto);  //관리자용
    public Location findLocation(long locationId);
    public void deleteState(long stateId);  //관리자용
    public void deleteCity(long cityId);    //관리자용
    public State foundState(String stateName);
    public City foundCity(String cityName);
    public Location findByLocation(State state, City city);

}
