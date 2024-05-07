package com.macro.mall.tiny.config;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/**
 * @author zhouzz
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "hazelcast")
public class HazelcastConfiguration {

    @Value("${hazelcast.network.port}")
    private Integer port;

    @Value("#{'${hazelcast.cluster.members}'.split(',')}")
    private List<String> cluster;

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setInstanceName("hazelcast-instance");
        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPort(port)
                .setPortAutoIncrement(false);
        JoinConfig joinConfig = networkConfig.getJoin();
        //禁用广播
        joinConfig.getMulticastConfig()
                .setEnabled(false);
        //启用TCP/IP
        joinConfig.getTcpIpConfig()
                .setEnabled(true)
                .setMembers(cluster);
        return Hazelcast.newHazelcastInstance(config);
    }


}
