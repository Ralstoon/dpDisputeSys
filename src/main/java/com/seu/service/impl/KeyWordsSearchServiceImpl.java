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
    public Map<String, Object> getSimilarCases(String caseId) {
        /** 先获取关键词,包括科室、手术、疾病、症状 */
        Set<String> keywords=new HashSet<>();
        Disputecase currentCase=disputecaseRepository.getOne(caseId);
        JSONArray medical_process=JSONArray.parseArray(currentCase.getMedicalProcess());
        for(int i=0;i<medical_process.size();++i){
            JSONObject obj=medical_process.getJSONObject(i);
            JSONArray involvedInstitute=obj.getJSONArray("InvolvedInstitute");
            /** 科室 */
            for(int j=0;j<involvedInstitute.size();++j){
                JSONObject institute=involvedInstitute.getJSONObject(j);
                keywords.add(institute.getString("Department").trim());
            }
            /** 疾病，症状 */
            JSONArray diseaseList=obj.getJSONArray("DiseaseListAfter");
            for(int j=0;j<diseaseList.size();++j) {
                JSONObject disease=diseaseList.getJSONObject(j);
                keywords.add(disease.getString("DiseaseName").trim());
            }
            diseaseList=obj.getJSONArray("DiseaseListBefore");
            for(int j=0;j<diseaseList.size();++j) {
                JSONObject disease=diseaseList.getJSONObject(j);
                keywords.add(disease.getString("DiseaseName").trim());
            }
            keywords.add(obj.getString("DiseasesymptomAfter"));
            keywords.add(obj.getString("DiseasesymptomBefore"));
            /** 手术 */
            JSONObject resultList=obj.getJSONObject("ResultList");
            if(!resultList.getJSONObject("operator").isEmpty()){
                JSONObject operator=resultList.getJSONObject("operator");
                keywords.add(operator.getString("operation").trim());
            }
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
            results.put("裁判文书",result);
            result=esIndexSearcher.searchDX(keyWords);
            results.put("典型案例",result);
            result=esIndexSearcher.search(keyWords);
            results.put("纠纷调解",result);
            /** 保存类案索引 */
            currentCase.setRecommendedPaper(results.toString());
            disputecaseRepository.save(currentCase);
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
            if(type.trim()=="裁判文书" || type.trim().equals("裁判文书"))
                result=titleSearch.titleSearchMS(caseName);
            else if(type.trim()=="典型案例" || type.trim().equals("典型案例"))
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
