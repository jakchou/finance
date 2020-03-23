package com.lvmm.shardingjdbc.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "etl_order_item")
public class OrderItemEntity {
    @Id
    @Column(name = "order_item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderItemId;
    @Column(name = "pay_amount")
    private BigDecimal payAmount;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity orderEntity;

    @Override
    public String toString() {
        return "OrderItemEntity{" +
                "orderItemId=" + orderItemId +
                ", payAmount=" + payAmount +

                '}';
    }
}
