package com.lvmm.shardingjdbc.respository;

import com.lvmm.shardingjdbc.entitys.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRespository extends JpaRepository<UserEntity,Long> , JpaSpecificationExecutor<UserEntity> {

    public List<UserEntity> findAll();
}
