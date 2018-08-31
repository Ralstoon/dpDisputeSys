package com.seu.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * @ClassName MyTransportClient
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/27 22:11
 * @Version 1.0
 **/

@Component
public class MyTransportClient {
    public Client getClient() throws Exception{
        Settings settings=Settings.settingsBuilder()
                            .put("cluster.name",ESConstants.clusterName)
                            .put("client.transport.sniff",true).build();

        Client client=TransportClient.builder()
                                    .settings(settings)
                                    .build()
                                    .addTransportAddress(
                                            new InetSocketTransportAddress(
                                                    InetAddress.getByName(ESConstants.IP),9300
                                            )
                                    );  //http请求是9200，客户端是9300，看yml配置
        return client;
    }

}
