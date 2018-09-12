package com.seu.elasticsearch;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

/**
 * @ClassName ESIndex
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/27 21:35
 * @Version 1.0
 **/
public class ESIndex {

    public void searchESIndex(String indexName,String typeName) throws Exception{
        Client client=new MyTransportClient().getClient();

        SearchRequestBuilder requestbuilder=client.prepareSearch(indexName).setTypes(typeName);
        SearchResponse searchResponse=requestbuilder.setQuery(QueryBuilders.matchPhraseQuery("keywords","关键词"))
                .setFrom(0).setSize(10).setExplain(true).execute().actionGet();

        SearchHits hits=searchResponse.getHits();
        for(int i=0;i<hits.getHits().length;++i){
            System.out.println(hits.getHits()[i].getSourceAsString());
        }
    }

    // 创建科室-->手术索引，添加映射
    public void createESIndex(String indexName) throws Exception{
        String mappingName=indexName+"_type";
        Client client=new MyTransportClient().getClient();
//        String indexName="operation_index";

        // 若索引存在，则先删除索引
        if(client.admin().indices().prepareExists(indexName).get().isExists()){
            client.admin().indices().prepareDelete(indexName).get();
        }
        // 创建映射
//        String mapping="{"+mappingName+":{\"properties\":"
//                + "{\"keyword\" : { \"type\" : \"string\", \"index\" : \"analyzed\",\"analyzer\" : \"ik_max_word\", \"store\" : \"true\" },"
//                + "\"operations\" : { \"type\" : \"string\", \"index\" : \"no\", \"store\" : \"true\"}}}}";
        String mapping="{"+mappingName+":{\"properties\":"
                + "{\"keyword\" : { \"type\" : \"string\", \"index\" : \"analyzed\",\"analyzer\" : \"jieba_index\",\"search_analyzer\" : \"jieba_search\", \"store\" : \"true\" }}}}";

        // 向ES添加索引和对应的映射
        client.admin().indices().prepareCreate(indexName).addMapping(mappingName,mapping).get();
        client.close();
    }

    public static void main(String[] args){
        try{
            new ESIndex().createESIndex("手术2_index");
//            new ESIndex().searchESIndex("operation_index","operation_index_type");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
