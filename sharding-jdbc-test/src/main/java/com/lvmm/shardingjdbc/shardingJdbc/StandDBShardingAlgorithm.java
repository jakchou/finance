package com.lvmm.shardingjdbc.shardingJdbc;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.core.strategy.route.standard.StandardShardingStrategy;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class StandDBShardingAlgorithm implements PreciseShardingAlgorithm<Long>, RangeShardingAlgorithm<Long>  {


    /**
     * 范围匹配
     * @param databaseNames
     * @param shardingValueRange
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> databaseNames, RangeShardingValue<Long> shardingValueRange) {
        Set<String> result = new LinkedHashSet<>();
        if (Range.closed(1L, 5000L).encloses(shardingValueRange.getValueRange())) {
            for (String each : databaseNames) {
                if (each.endsWith("0")) {
                    result.add(each);
                }
            }
        } else if (Range.closed(50001L, 999999L).encloses(shardingValueRange.getValueRange())) {
            for (String each : databaseNames) {
                if (each.endsWith("1")) {
                    result.add(each);
                }
            }
        } else {
            throw new UnsupportedOperationException();
        }
        return result;
    }


    /**
     * 精确匹配匹配
     * @param databaseNames
     * @param shardingValueRange
     * @return
     */
    @Override
    public String doSharding(Collection<String> databaseNames, PreciseShardingValue<Long> shardingValueRange) {
        if (shardingValueRange.getValue()<5000L) {
            for (String each : databaseNames) {
                return  each;
            }
        } else if (shardingValueRange.getValue()>=5000L) {
            for (String each : databaseNames) {
                if (each.endsWith("1")) {
                    return  each;
                }
            }
        }
        throw new UnsupportedOperationException();
    }
}
