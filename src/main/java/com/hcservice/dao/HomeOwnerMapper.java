package com.hcservice.dao;

import com.hcservice.domain.model.HomeOwner;

public interface HomeOwnerMapper {

    HomeOwner getHomeOwnerByOwnerId(Integer ownerId);

    int insertHomeOwner(HomeOwner homeOwner);

}
