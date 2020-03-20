package com.lvmm.shardingjdbc.entitys;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "etl_order")
public class OrderEntity {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderId;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "pay_amount")
    private BigDecimal payAmount;

    @OneToMany(mappedBy = "orderEntity")
    private List<OrderItemEntity> orderItems;

    @Override
    public String toString() {
        return "OrderEntity{" +
                "orderId=" + orderId +
                ", productId=" + productId +
                ", customerId=" + customerId +
                ", payAmount=" + payAmount +
                ", orderItems=" + orderItems +
                '}';
    }
}
