package com.lvmm.shardingjdbc.service.Imp;

import com.lvmm.shardingjdbc.entitys.OrderEntity;
import com.lvmm.shardingjdbc.respository.OrderRespository;
import com.lvmm.shardingjdbc.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImp implements OrderService {
    @Autowired
    private OrderRespository orderRespository;
    @Override
    public OrderEntity findByOrderId() {
        OrderEntity order= orderRespository.getOne(1001L);
        System.out.println(order);
        return order;
    }
}
