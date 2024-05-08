CREATE TABLE `entity2` (
`id` bigint NOT NULL COMMENT '主键',
`created_time` datetime NULL COMMENT '创建时间',
`updated_time` datetime NULL COMMENT '更新时间',
`created_by` varchar(256) NULL COMMENT '创建者',
`updated_by` varchar(256) NULL COMMENT '更新者',
PRIMARY KEY (`id`)
);

