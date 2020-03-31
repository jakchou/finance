package com.lvmm.shardingjdbc.service;

import com.lvmm.shardingjdbc.entitys.OrderEntity;

public interface OrderService {

    OrderEntity findByOrderId(Long orderId);

    void saveOrder(Long orderId);
}
