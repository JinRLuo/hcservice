package com.hcservice.domain.model;

public class Room {

    private Integer roomId;

    private Integer roomNum;

    private Integer status;

    private Building building;

    private Integer buildingNum;

    private Integer area;

    private HomeOwner owner;

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(Integer roomNum) {
        this.roomNum = roomNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public Integer getBuildingNum() {
        return buildingNum;
    }

    public void setBuildingNum(Integer buildingNum) {
        this.buildingNum = buildingNum;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public HomeOwner getOwner() {
        return owner;
    }

    public void setOwner(HomeOwner owner) {
        this.owner = owner;
    }
}
