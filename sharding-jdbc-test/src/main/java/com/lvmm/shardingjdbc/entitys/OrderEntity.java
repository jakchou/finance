package com.lvmm.shardingjdbc.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lvmm.shardingjdbc.common.CommonEnum;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
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

    @Enumerated(EnumType.STRING )
    @Column(name = "order_status")
    private CommonEnum.OrderStatus order_status;

    @Column(name = "create_time")
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private Date createTime;

    @JsonIgnore
    @NotFound(action= NotFoundAction.IGNORE)
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
