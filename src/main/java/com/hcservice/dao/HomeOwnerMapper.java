package com.hcservice.dao;

import com.hcservice.domain.model.HomeOwner;

import java.util.List;

public interface HomeOwnerMapper {

    HomeOwner getHomeOwnerByOwnerId(Integer ownerId);

    int updateHomeOwnerStatusByOwnerId(Integer ownerId, Integer status);

    int insertHomeOwner(HomeOwner homeOwner);

    List<HomeOwner> getHomeOwnersByUserId(Integer userId);

    List<HomeOwner> getNewHomeOwner();

}
