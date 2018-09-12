package com.seu.service.impl;

import com.seu.elasticsearch.MyTransportClient;
import com.seu.service.KeyWordsSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName KeyWordsSearchServiceImpl
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/9 0009 下午 8:27
 * @Version 1.0
 **/

@Service
@Slf4j
public class KeyWordsSearchServiceImpl implements KeyWordsSearchService {
    @Autowired
    private MyTransportClient myClient;

    @Override
    public List<String> getByOneWord(String keywords) throws Exception {
        Client client=myClient.getClient();
        SearchRequestBuilder requestbuilder=client.prepareSearch("手术_index").setTypes("手术_index_type");
        SearchResponse searchResponse=requestbuilder.setQuery(QueryBuilders.matchPhraseQuery("keyword",keywords))
                .addHighlightedField("keyword")
                .setHighlighterPreTags("<1>")
                .setHighlighterPostTags("</1>")
//                .setHighlighterPreTags("<2>")
//                .setHighlighterPostTags("</2>")
                .execute().actionGet();

        SearchHits hits=searchResponse.getHits();
        List<String> result=new ArrayList<>();
        for(int i=0;i<hits.getHits().length;++i){
            System.out.println(hits.getAt(i).getSourceAsString());
            result.add(hits.getAt(i).getSource().get("keyword").toString());
            System.out.println(hits.getAt(i).getHighlightFields().get("keyword").getFragments()[0].toString());
        }
        return result;
    }

//    @Override
//    public List<String> getByWords(String keywords) throws Exception {
//        String[] wordList=keywords.trim().split(",");
//        Client client=myClient.getClient();
//        SearchRequestBuilder requestbuilder=client.prepareSearch("手术_index").setTypes("手术_index_type");
//        int size=wordList.length;
//        for(int i=0;i<size;++i){
//            String s=wordList[i];
//            requestbuilder.setQuery(QueryBuilders.matchPhraseQuery("keyword",s))
//                    .addHighlightedField("keyword")
//                    .setHighlighterPreTags("<"+(i+1)+">")
//                    .setHighlighterPostTags("</"+(i+1)+">");
//        }
//        SearchResponse searchResponse=requestbuilder.execute().actionGet();
//        SearchHits hits=searchResponse.getHits();
//        List<String> result=new ArrayList<>();
//        for(int i=0;i<hits.getHits().length;++i){
//            System.out.println(hits.getAt(i).getSourceAsString());
//            result.add(hits.getAt(i).getSource().get("keyword").toString());
//            System.out.println(hits.getAt(i).getHighlightFields().get("keyword").getFragments()[0].toString());
//        }
//        return result;
//    }


    @Override
    public List<String> getByWords(String keywords) throws Exception {
        String[] wordList=keywords.trim().split(",");
        Client client=myClient.getClient();
        SearchRequestBuilder requestbuilder=client.prepareSearch("手术_index").setTypes("手术_index_type");
        int size=wordList.length;
        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery();
        for(int i=0;i<size;++i){
            String s=wordList[i];
            boolQuery.should(QueryBuilders.matchPhraseQuery("keyword",s));
//            requestbuilder.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("keyword",s)));
//                    .addHighlightedField("keyword")
//                    .setHighlighterPreTags("<"+(i+1)+">")
//                    .setHighlighterPostTags("</"+(i+1)+">");
        }
        SearchResponse searchResponse=requestbuilder.setQuery(boolQuery).addHighlightedField("keyword")
                .setHighlighterPreTags("<1>")
                .setHighlighterPostTags("</1>").execute().actionGet();
        SearchHits hits=searchResponse.getHits();
        List<String> result=new ArrayList<>();
        for(int i=0;i<hits.getHits().length;++i){
            System.out.println(hits.getAt(i).getSourceAsString());
            result.add(hits.getAt(i).getSource().get("keyword").toString());
            System.out.println(hits.getAt(i).getHighlightFields().get("keyword").getFragments()[0].toString());
        }
        return result;
    }
}
