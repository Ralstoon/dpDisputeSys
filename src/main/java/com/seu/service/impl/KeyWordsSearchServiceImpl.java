package com.seu.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.domian.Disputecase;
import com.seu.elasticsearch.ESIndexSearcher;
import com.seu.elasticsearch.MyTransportClient;
import com.seu.elasticsearch.TitleSearch;
import com.seu.elasticsearch.json.SimpleRecord;
import com.seu.repository.DisputecaseRepository;
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
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import java.util.*;

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
    @Autowired
    private DisputecaseRepository disputecaseRepository;

    @Override
    public List<String> getByOneWord(String keywords,String type) throws Exception {
        Client client=myClient.getClient();
        type=type.trim();
        String param1=type+"_index";
        String param2=param1+"_type";
        SearchRequestBuilder requestbuilder=client.prepareSearch(param1).setTypes(param2);
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

    @Override
    public List<String> getByWords(String keywords,String type) throws Exception {
        type=type.trim();
        String param1=type+"_index";
        String param2=param1+"_type";
        String[] wordList=keywords.trim().split(",");
        Client client=myClient.getClient();
        SearchRequestBuilder requestbuilder=client.prepareSearch(param1).setTypes(param2);
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
                .setHighlighterPreTags("<b>")
                .setHighlighterPostTags("</b>").execute().actionGet();
        SearchHits hits=searchResponse.getHits();
        List<String> result=new ArrayList<>();
        for(int i=0;i<hits.getHits().length;++i){
            System.out.println(hits.getAt(i).getSourceAsString());
            result.add(hits.getAt(i).getSource().get("keyword").toString());
            System.out.println(hits.getAt(i).getHighlightFields().get("keyword").getFragments()[0].toString());
        }
        return result;
    }

    @Override
    public Map<String, Object> getSimilarCases(JSONArray keyWordList) {//todo: 修改医疗行为
//        /** 先获取关键词,包括科室、手术、疾病、症状 */
//        Set<String> keywords=new HashSet<>();
//        String caseId = "";
//        Disputecase currentCase=disputecaseRepository.findOne(caseId);
//        JSONArray medical_process=JSONArray.parseArray(currentCase.getMedicalProcess());//todo: 修改医疗行为
//        for(int i=0;i<medical_process.size();++i){
//            JSONObject obj=medical_process.getJSONObject(i);
//            JSONObject involvedInstitute=obj.getJSONObject("InvolvedInstitute");
//            /** 科室 */
//
//            keywords.add(involvedInstitute.getString("Department").trim());
//
//            //resultOfRegConflict列表获取
//            JSONArray resultOfRegConflict = obj.getJSONArray("resultOfRegConflict");
//
//
//            for (Object eachResultOfRegConflict: resultOfRegConflict){
//
//
//                keywords.add(((JSONObject)eachResultOfRegConflict).getString("test"));
//
//                keywords.add(((JSONObject)eachResultOfRegConflict).getString("added"));
//
//                keywords.add(((JSONObject)eachResultOfRegConflict).getString("medicine"));
//
//                keywords.add(((JSONObject)eachResultOfRegConflict).getString("syndrome"));
//
//                List<String> operationList = (List<String>) ((JSONObject)eachResultOfRegConflict).get("operation");
//                for (i=0;i<operationList.size();++i){
//                    keywords.add(operationList.get(i));
//                }
//
//                List<String> diseaseAfterList = (List<String>) ((JSONObject)eachResultOfRegConflict).get("diseaseAfter");
//                for (i=0;i<diseaseAfterList.size();++i){
//                    keywords.add(diseaseAfterList.get(i));
//                }
//
//                List<String> diseaseBeforeList = (List<String>) ((JSONObject)eachResultOfRegConflict).get("diseaseBefore");
//                for (i=0;i<diseaseBeforeList.size();++i){
//                    keywords.add(diseaseBeforeList.get(i));
//                }
//
//                List<String> defaultBehaviorList = (List<String>) ((JSONObject)eachResultOfRegConflict).get("defaultBehavior");
//                for (i=0;i<defaultBehaviorList.size();++i){
//                    keywords.add(defaultBehaviorList.get(i));
//                }
//
//                List<String> diseasesymptomAfterList = (List<String>) ((JSONObject)eachResultOfRegConflict).get("diseasesymptomAfter");
//                for (i=0;i<diseasesymptomAfterList.size();++i){
//                    keywords.add(diseasesymptomAfterList.get(i));
//                }
//                List<String> diseasesymptomBeforeList = (List<String>) ((JSONObject)eachResultOfRegConflict).get("diseasesymptomBefore");
//                for (i=0;i<diseasesymptomBeforeList.size();++i){
//                    keywords.add(diseasesymptomBeforeList.get(i));
//                }
//
//
//
//            }
//
//        }
        //获取关键词
        Set<String> keywords=new HashSet<>();

        for (int i = 0; i<keyWordList.size();i++){
            keywords.add(((JSONObject)keyWordList.get(i)).getString("value"));
        }

        ESIndexSearcher esIndexSearcher=new ESIndexSearcher();
        int len=keywords.size();
        String[] keyWords=new String[len];
        int index=0;
        for(String s:keywords)
            keyWords[index++]=s;
        Map<String,Object> results=new HashMap<>();
        /** 获取三类文书，并JSON格式保存在disputecase */
        try {
            List<SimpleRecord> result=esIndexSearcher.searchMS(keyWords);
            results.put("dissension_ms",result); //裁判文书
            result=esIndexSearcher.searchDX(keyWords);
            results.put("dissension_dx",result); //典型案例
            result=esIndexSearcher.search(keyWords);
            results.put("dissension",result); // 纠纷案例
//            /** 保存类案索引 */
//            currentCase.setRecommendedPaper(results.toString());
//            disputecaseRepository.save(currentCase);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return results;
        }
    }

    @Override
    public Object getCaseDetails(String caseName,String type) {
        TitleSearch titleSearch=new TitleSearch();
        Object result=null;
        try {
            if(type.trim()=="dissension_ms" || type.trim().equals("dissension_ms"))
                result=titleSearch.titleSearchMS(caseName);
            else if(type.trim()=="dissension_dx" || type.trim().equals("dissension_dx"))
                result=titleSearch.titleSearchDX(caseName);
            else
                result=titleSearch.titleSearch(caseName);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return result;
        }
    }
}
