package com.lvmm.shardingjdbc.respository;

import com.lvmm.shardingjdbc.entitys.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRespository extends JpaRepository<OrderEntity,Long> , JpaSpecificationExecutor<OrderEntity> {

}
