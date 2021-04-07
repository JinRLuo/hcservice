package com.hcservice.domain.model;

import java.time.LocalDate;

public class Building {

    private Integer buildingId;

    private Integer buildingNum;

    private LocalDate buildTime;

    private String managerName;

    private String managerPhoneNum;

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public Integer getBuildingNum() {
        return buildingNum;
    }

    public void setBuildingNum(Integer buildingNum) {
        this.buildingNum = buildingNum;
    }

    public LocalDate getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(LocalDate buildTime) {
        this.buildTime = buildTime;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerPhoneNum() {
        return managerPhoneNum;
    }

    public void setManagerPhoneNum(String managerPhoneNum) {
        this.managerPhoneNum = managerPhoneNum;
    }
}
