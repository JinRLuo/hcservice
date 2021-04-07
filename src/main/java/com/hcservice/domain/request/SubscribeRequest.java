package com.hcservice.domain.request;

import java.time.LocalDateTime;

public class SubscribeRequest {

    private String visitTime;

    private Integer buildingNum;

    private Integer roomNum;

    private String visitingReason;

    private String visitorName;

    private String phoneNum;

    private Integer peopleCount;

    private String remark;

    public String getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(String visitTime) {
        this.visitTime = visitTime;
    }

    public Integer getBuildingNum() {
        return buildingNum;
    }

    public void setBuildingNum(Integer buildingNum) {
        this.buildingNum = buildingNum;
    }

    public Integer getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(Integer roomNum) {
        this.roomNum = roomNum;
    }

    public String getVisitingReason() {
        return visitingReason;
    }

    public void setVisitingReason(String visitingReason) {
        this.visitingReason = visitingReason;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Integer getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(Integer peopleCount) {
        this.peopleCount = peopleCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
