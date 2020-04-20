package com.lvmm.shardingjdbc.es.service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lvmm.shardingjdbc.config.ESUtil;
import com.lvmm.shardingjdbc.entitys.OrderEntity;
import com.lvmm.shardingjdbc.es.service.ESOrderService;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ESOrderServiceImp implements ESOrderService {
    @Autowired
    private RestHighLevelClient client;

    @Override
    public void sycnOrder(OrderEntity order) {
        try {
            ESUtil.cereateIndex(client,order);
        }catch (Exception e){

        }


    }
}
