package com.hcservice.dao;

import com.hcservice.domain.model.Room;

import java.util.List;

public interface RoomMapper {

    Room getRoomByNum(Integer buildingNum, Integer roomNum);

    Room getRoomById(Integer roomId);

    List<Room> getRoomsByUserId(Integer userId);

    List<Room> getRoomsBySearch(Integer buildingNum, Integer roomNum);

}
