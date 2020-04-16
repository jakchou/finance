package com.lvmm.shardingjdbc.service.Imp;

import com.lvmm.shardingjdbc.common.CommonEnum;
import com.lvmm.shardingjdbc.entitys.OrderEntity;
import com.lvmm.shardingjdbc.entitys.OrderItemEntity;
import com.lvmm.shardingjdbc.respository.OrderItemRespository;
import com.lvmm.shardingjdbc.respository.OrderRespository;
import com.lvmm.shardingjdbc.service.FileService;
import com.lvmm.shardingjdbc.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImp implements OrderService {
    @Autowired
    private OrderRespository orderRespository;
    @Autowired
    private OrderItemRespository itemRespository;
    @Autowired
    private FileService fileService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public OrderEntity findByOrderId(Long orderId) {
        Optional<OrderEntity> order = orderRespository.findById(orderId);
        //redisTemplate.opsForValue().set("username","zhouzhengzheng");
        System.out.println("hello");
        System.out.println(order);
        return null;
    }

    @Override
    public void saveOrder(Long orderId) {
        OrderEntity order=new OrderEntity();
        order.setOrderId(orderId);
        order.setCustomerId(1001L);
        order.setCustomerId(1001L);
        order.setOrder_status(CommonEnum.OrderStatus.Common);
        order.setPayAmount(BigDecimal.valueOf(100));
        order.setProductId(1001L);
        OrderItemEntity item=new OrderItemEntity();
        item.setOrderEntity(order);
        item.setOrderItemId(orderId*10+1);
        item.setPayAmount(BigDecimal.valueOf(100));
        List<OrderItemEntity> items=new ArrayList<OrderItemEntity>();
        order.setOrderItems(items);
        orderRespository.save(order);
        itemRespository.save(item);
        fileService.saveFIle();



    }
}
