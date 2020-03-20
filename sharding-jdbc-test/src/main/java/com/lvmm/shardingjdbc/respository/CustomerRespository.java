package com.lvmm.shardingjdbc.respository;

import com.lvmm.shardingjdbc.entitys.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRespository extends JpaRepository<CustomerEntity,Long> , JpaSpecificationExecutor<CustomerEntity> {

}
