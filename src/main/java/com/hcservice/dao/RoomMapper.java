package com.hcservice.dao;

import com.hcservice.domain.model.Room;

public interface RoomMapper {

    Room getRoomByNum(Integer buildingNum, Integer roomNum);

    Room getRoomById(Integer roomId);

}
