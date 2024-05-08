package com.defaulttenant.hotswap.domain.structure.connector.dingding; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Dept_order_listStructure {

    public Long dept_id;
    public Long order;
    public Long getDept_id() {
        return dept_id;
    } 

    public void setDept_id(Long dept_id) {
        this.dept_id = dept_id; 
    } 

    public Long getOrder() {
        return order;
    } 

    public void setOrder(Long order) {
        this.order = order; 
    } 


}
