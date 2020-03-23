package com.lvmm.shardingjdbc.common;

public class CommonEnum {

    public static enum OrderStatus {

        New("新订单") , Common("正常单"), Cancel("取消单"),Complate("完成");

        private final String cnName;

         OrderStatus (String cnName) {
            this.cnName = cnName;
        }

        public String getCnName(){
            return this.cnName;
        }

    }



}
