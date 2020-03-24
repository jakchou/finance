package com.lvmm.shardingjdbc.service.Imp;

import com.lvmm.shardingjdbc.entitys.OrderEntity;
import com.lvmm.shardingjdbc.respository.OrderRespository;
import com.lvmm.shardingjdbc.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderServiceImp implements OrderService {
    @Autowired
    private OrderRespository orderRespository;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public OrderEntity findByOrderId(Long orderId) {
        Optional<OrderEntity> order = orderRespository.findById(orderId);
        redisTemplate.opsForValue().set("username","zhouzhengzheng");
        System.out.println("hello");
        System.out.println(order);
        return null;
    }
}
