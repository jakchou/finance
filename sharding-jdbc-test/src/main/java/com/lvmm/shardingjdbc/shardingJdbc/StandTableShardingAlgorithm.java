package com.lvmm.shardingjdbc.shardingJdbc;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class StandTableShardingAlgorithm  implements PreciseShardingAlgorithm<Long>, RangeShardingAlgorithm<Long> {


    /**
     * 范围匹配
     * @param tableNames
     * @param shardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> tableNames, RangeShardingValue<Long> shardingValue) {
        Set<String> result = new LinkedHashSet<>();
        if (Range.closed(0L, 1000L).encloses(shardingValue.getValueRange())) {
            for (String each : tableNames) {
                if (each.endsWith("0")) {
                    result.add(each);
                }
            }
        } else if (Range.closed(1001L, 2000L).encloses(shardingValue.getValueRange())) {
            for (String each : tableNames) {
                if (each.endsWith("1")) {
                    result.add(each);
                }
            }
        } else if (Range.closed(2001L, 3000L).encloses(shardingValue.getValueRange())) {
            for (String each : tableNames) {
                if (each.endsWith("2")) {
                    result.add(each);
                }
            }
        } else if (Range.closed(3001L, 4000L).encloses(shardingValue.getValueRange())) {
            for (String each : tableNames) {
                if (each.endsWith("3")) {
                    result.add(each);
                }
            }
        }  else if (Range.closed(4001L, 5000L).encloses(shardingValue.getValueRange())) {
            for (String each : tableNames) {
                if (each.endsWith("4")) {
                    result.add(each);
                }
            }
        } else if (Range.closed(5001L, 6000L).encloses(shardingValue.getValueRange())) {
            for (String each : tableNames) {
                if (each.endsWith("5")) {
                    result.add(each);
                }
            }
        } else if (Range.closed(6001L, 7000L).encloses(shardingValue.getValueRange())) {
            for (String each : tableNames) {
                if (each.endsWith("6")) {
                    result.add(each);
                }
            }
        } else if (Range.closed(7001L, 8000L).encloses(shardingValue.getValueRange())) {
            for (String each : tableNames) {
                if (each.endsWith("7")) {
                    result.add(each);
                }
            }
        } else if (Range.closed(8001L, 9000L).encloses(shardingValue.getValueRange())) {
            for (String each : tableNames) {
                if (each.endsWith("8")) {
                    result.add(each);
                }
            }
        } else if (Range.closed(9001L, 10000L).encloses(shardingValue.getValueRange())) {
            for (String each : tableNames) {
                if (each.endsWith("9")) {
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
     * @param tableNames
     * @param shardingValue
     * @return
     */
    @Override
    public String doSharding(Collection<String> tableNames, PreciseShardingValue<Long> shardingValue) {
        Long pre= shardingValue.getValue().longValue()/1000;
        //String pre=shardingValue.getValue().toString().substring(0,1);
        for (String each : tableNames) {
            if (each.endsWith(pre.toString())) {
                    return  each;

                }
            }
        throw new UnsupportedOperationException();
    }
}
