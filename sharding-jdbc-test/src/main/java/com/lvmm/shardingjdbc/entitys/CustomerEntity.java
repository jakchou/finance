package com.lvmm.shardingjdbc.entitys;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "etl_customer")
public class CustomerEntity {
    @Id
    @Column(name = "customer_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long customerId;
    @Column(name = "customer_name")
    private String customerName;

}
