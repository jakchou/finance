package com.lvmm.shardingjdbc.es.service;

import com.lvmm.shardingjdbc.entitys.OrderEntity;

public interface ESOrderService {
    public void sycnOrder(OrderEntity order);
}
