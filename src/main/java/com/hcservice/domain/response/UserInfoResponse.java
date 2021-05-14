package com.hcservice.domain.response;

import com.hcservice.domain.model.HomeOwner;

import java.util.List;

public class UserInfoResponse {

    private Integer userId;

    private String userName;

    private String email;

    private String phoneNum;

    private String pictureUrl;

    private Integer status;

    private List<HomeOwnerInfoResponse> homeOwners;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<HomeOwnerInfoResponse> getHomeOwners() {
        return homeOwners;
    }

    public void setHomeOwners(List<HomeOwnerInfoResponse> homeOwners) {
        this.homeOwners = homeOwners;
    }
}
