package com.defaulttenant.hotswap.domain.structure.connector.dingding; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Leader_in_deptStructure {

    public Boolean leader;
    public Long dept_id;
    public Boolean getLeader() {
        return leader;
    } 

    public void setLeader(Boolean leader) {
        this.leader = leader; 
    } 

    public Long getDept_id() {
        return dept_id;
    } 

    public void setDept_id(Long dept_id) {
        this.dept_id = dept_id; 
    } 


}
