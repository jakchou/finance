package com.lvmm.shardingjdbc.entitys;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "age")
    private int age;
}
