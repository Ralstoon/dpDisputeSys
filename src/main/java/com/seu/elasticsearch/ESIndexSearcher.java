package com.seu.elasticsearch;



/*
 * 调用es的查询函数做关键词检索，网站大部分的检索都是在这里实现的
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.search.MultiMatchQuery.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import com.seu.elasticsearch.json.*;
import com.hankcs.hanlp.HanLP;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;


public class ESIndexSearcher {
    // excel表格案例，即纠纷案例
    private static Map<String, SimpleRecord> minshiMap = new HashMap<String, SimpleRecord>();

    public List<SimpleRecord> search(String[] keyword) throws Exception {
        int length = keyword.length;
        String[] word = new String[10];
        // 初始化为“”
        for (int i = 0; i < 10; i++) {
            word[i] = "";
        }
        for (int i = 0; i < length; i++)
            keyword[i] = java.net.URLDecoder.decode(keyword[i], "utf-8");
        Set<String> set = new LinkedHashSet<String>();
        for (String s : keyword) {
            set.add(s);
        }
        // 把set加入到word
        String[] wordTemp = set.toArray(new String[] {});
        for (int i = 0; i < 10; i++) {
            if (i < wordTemp.length)
                word[i] = wordTemp[i];
        }
        List<SimpleRecord> recordList = queryProcess(word);// 通过关键词搜索
        return recordList;// 返回List对象
    }

    // 纠纷案例检索
    public List<SimpleRecord> queryProcess(String[] word) throws Exception {
        // map里的关键词在字段里查找

        List<SimpleRecord> recordList = new ArrayList<SimpleRecord>();
        Client client = new MyTransportClient().getClient();
        List<MultiMatchQueryBuilder> queryList = new ArrayList<MultiMatchQueryBuilder>();
        // word里的查询
        for (String s : word) {
            if (!s.equals(""))
                queryList.add(QueryBuilders.multiMatchQuery(s, "disputeName",
                        "Abstract", "disputeResolution", "agreement"));
        }
        // 构造最终查询
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (int i = 0; i < queryList.size(); i++)
            query = query.should(queryList.get(i));
        SearchResponse response = client
                .prepareSearch("dissension_index")
                .setTypes("dissensiontexts")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(
                        QueryBuilders.boolQuery().minimumNumberShouldMatch(1)
                                .should(query)).setSize(10).setExplain(true)
                .execute().actionGet();
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSource();
            SimpleRecord r = new SimpleRecord(
                    map.get("disputeName").toString(), map.get("acceptDate")
                    .toString(), map.get("Abstract").toString());
            recordList.add(r);
        }
        client.close();
        // 去重
        Set<String> uniclist = new LinkedHashSet<String>();
        for (SimpleRecord sr : recordList) {
            uniclist.add(sr.getDisputeName());
        }
        List<SimpleRecord> res = new ArrayList<SimpleRecord>();

        for (String title : uniclist) {
            for (SimpleRecord sr : recordList) {
                if (sr.getDisputeName().equals(title)) {
                    res.add(sr);
                    break;
                }
            }
        }
        return res;
    }

    // 典型案例查询
    public List<SimpleRecord> searchDX(String[] keyword) throws Exception {
        String[] word = new String[10];
        for (int i = 0; i < keyword.length; i++)
            keyword[i] = java.net.URLDecoder.decode(keyword[i], "utf-8");
        for (int i = 0; i < 10; i++) {
            if (i < keyword.length)
                word[i] = keyword[i];
            else
                word[i] = "";
        }
        List<SimpleRecord> recordDXList = DXqueryProcess(word);
        return recordDXList;
    }

    // 典型案例查询
    public List<SimpleRecord> DXqueryProcess(String[] word) throws Exception {
        List<SimpleRecord> recordDXList = new ArrayList<SimpleRecord>();
        Client client = new MyTransportClient().getClient();
        MultiMatchQueryBuilder queryA = QueryBuilders.multiMatchQuery(word[0],
                "DisputeName", "DisputeType", "DisputeLocation", "Institution",
                "StaffName", "Abstract", "DisputeResolution", "comment",
                "RelateLaw", "DisputeTag");
        MultiMatchQueryBuilder queryB = QueryBuilders.multiMatchQuery(word[1],
                "DisputeName", "DisputeType", "DisputeLocation", "Institution",
                "StaffName", "Abstract", "DisputeResolution", "comment",
                "RelateLaw", "DisputeTag");
        MultiMatchQueryBuilder queryC = QueryBuilders.multiMatchQuery(word[2],
                "DisputeName", "DisputeType", "DisputeLocation", "Institution",
                "StaffName", "Abstract", "DisputeResolution", "comment",
                "RelateLaw", "DisputeTag");
        MultiMatchQueryBuilder queryD = QueryBuilders.multiMatchQuery(word[3],
                "DisputeName", "DisputeType", "DisputeLocation", "Institution",
                "StaffName", "Abstract", "DisputeResolution", "comment",
                "RelateLaw", "DisputeTag");
        MultiMatchQueryBuilder queryE = QueryBuilders.multiMatchQuery(word[4],
                "DisputeName", "DisputeType", "DisputeLocation", "Institution",
                "StaffName", "Abstract", "DisputeResolution", "comment",
                "RelateLaw", "DisputeTag");
        MultiMatchQueryBuilder queryF = QueryBuilders.multiMatchQuery(word[5],
                "DisputeName", "DisputeType", "DisputeLocation", "Institution",
                "StaffName", "Abstract", "DisputeResolution", "comment",
                "RelateLaw", "DisputeTag");
        MultiMatchQueryBuilder queryG = QueryBuilders.multiMatchQuery(word[6],
                "DisputeName", "DisputeType", "DisputeLocation", "Institution",
                "StaffName", "Abstract", "DisputeResolution", "comment",
                "RelateLaw", "DisputeTag");
        MultiMatchQueryBuilder queryH = QueryBuilders.multiMatchQuery(word[7],
                "DisputeName", "DisputeType", "DisputeLocation", "Institution",
                "StaffName", "Abstract", "DisputeResolution", "comment",
                "RelateLaw", "DisputeTag");
        MultiMatchQueryBuilder queryI = QueryBuilders.multiMatchQuery(word[8],
                "DisputeName", "DisputeType", "DisputeLocation", "Institution",
                "StaffName", "Abstract", "DisputeResolution", "comment",
                "RelateLaw", "DisputeTag");
        MultiMatchQueryBuilder queryJ = QueryBuilders.multiMatchQuery(word[9],
                "DisputeName", "DisputeType", "DisputeLocation", "Institution",
                "StaffName", "Abstract", "DisputeResolution", "comment",
                "RelateLaw", "DisputeTag");
        MultiMatchQueryBuilder queryOp = QueryBuilders.multiMatchQuery("江苏",
                "DisputeName", "DisputeType", "DisputeLocation", "Institution",
                "StaffName", "Abstract", "DisputeResolution", "comment",
                "RelateLaw", "DisputeTag");
        SearchResponse response = client
                .prepareSearch("dissension_dx_index")
                .setTypes("dissension_dxtexts")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(
                        QueryBuilders.boolQuery().minimumNumberShouldMatch(1)
                                .should(queryA).boost(0).should(queryOp)
                                .boost(1).should(queryB).boost(1)
                                .should(queryC).boost(2).should(queryD)
                                .boost(3).should(queryE).boost(4)
                                .should(queryF).boost(5).should(queryG)
                                .boost(6).should(queryH).boost(7)
                                .should(queryI).boost(8).should(queryJ)
                                .boost(9)).setSize(10).setExplain(true)
                .execute().actionGet();
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSource();
            // System.out.println(map);
            SimpleRecord r = new SimpleRecord(
                    map.get("DisputeName").toString(), "201602121", map.get(
                    "Abstract").toString());
            recordDXList.add(r);
        }
        client.close();
        return recordDXList;
    }

    // 民事案例查询
    public List<SimpleRecord> searchMS(String[] keyword) throws Exception {
        String[] word = new String[10];
        for (int i = 0; i < keyword.length; i++)
            keyword[i] = java.net.URLDecoder.decode(keyword[i], "utf-8");
        for (int i = 0; i < 10; i++) {
            if (i < keyword.length)
                word[i] = keyword[i];
            else
                word[i] = "";
        }
        List<SimpleRecord> recordMSList = DXqueryProcessMS(word);

        return recordMSList;
    }

    // 用于医患纠纷_用户入口的检索任务
    public List<SimpleRecord> searchMS(List<String> keyword) throws Exception {
        String[] word = new String[keyword.size()];
        for (int i = 0; i < word.length; i++)
            word[i] = java.net.URLDecoder.decode(keyword.get(i), "utf-8");
        List<SimpleRecord> recordMSList = DXqueryProcessMS(word);

        return recordMSList;
    }

    public static void main(String[] args) throws Exception{
        ESIndexSearcher esIndexSearcher=new ESIndexSearcher();
        /** 民事案例 */
        List<SimpleRecord> simpleRecordList=esIndexSearcher.searchMS(new String[]{"骨科"});
        /**  */
        System.out.println(simpleRecordList);
    }


    public List<SimpleRecord> DXqueryProcessMS(String[] word)
            throws Exception {
        List<SimpleRecord> recordMSList = new ArrayList<SimpleRecord>();
        Client client =new MyTransportClient().getClient();
        List<MultiMatchQueryBuilder> queryList = new ArrayList<MultiMatchQueryBuilder>();
        // word里的查询
        for (String s : word) {
            queryList.add(QueryBuilders.multiMatchQuery(s, "CourtThink"));
        }
        // 构造最终查询
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (int i = 0; i < queryList.size(); i++)
            query = query.should(queryList.get(i));
        SearchResponse response = client
                .prepareSearch("dissension_ms_index")
                .setTypes("dissension_mstexts")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(
                        QueryBuilders.boolQuery().minimumNumberShouldMatch(1)
                                .should(query)).setSize(10).setExplain(true)
                .execute().actionGet();
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSource();
            // System.out.println(map);
            List<String> AbstractList = HanLP.extractSummary(
                    map.get("JudgeDecision").toString(), 8);
            String Abstract = "";
            for (String s : AbstractList)
                Abstract += s;
            Abstract = Abstract.replaceAll("</p>", "").replaceAll("<p>", "")
                    .replaceAll("&nbsp", "");
            // 时间没有提取出来，统一使用20162121代替
            SimpleRecord r = new SimpleRecord(
                    map.get("DisputeName").toString(), "201602121", Abstract);
            String title = map.get("DisputeName").toString();
            // 特殊案例特殊处理，可以删掉
            if (title.contains("岳淑清"))
                continue;
            recordMSList.add(r);
        }
        client.close();
        // 保存检索结果
        Map<String, SimpleRecord> recordMap = new HashMap<String, SimpleRecord>();
        for (SimpleRecord s : recordMSList) {
            recordMap.put(s.getDisputeName(), s);
        }
        // 如果有元素，先清空
        if (minshiMap.size() > 0)
            minshiMap.clear();
        minshiMap.putAll(recordMap);
        return recordMSList;
    }

    // 结果中查询，用于用户入口的快捷查询
    public List<SimpleRecord> queryInResult(List<String> keyword)
            throws Exception {
        String[] word = new String[keyword.size()];

        for (int i = 0; i < word.length; i++)
            word[i] = java.net.URLDecoder.decode(keyword.get(i), "utf-8");
        List<SimpleRecord> recordMSList = new ArrayList<SimpleRecord>();
        Client client = new MyTransportClient().getClient();
        List<MultiMatchQueryBuilder> queryList = new ArrayList<MultiMatchQueryBuilder>();
        for (String s : word) {
            queryList.add(QueryBuilders.multiMatchQuery(s, "CourtThink"));
        }
        // 构造最终查询
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (int i = 0; i < queryList.size(); i++)
            query = query.should(queryList.get(i));
        SearchResponse response = client
                .prepareSearch("dissension_ms_index")
                .setTypes("dissension_mstexts")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(
                        QueryBuilders.boolQuery().minimumNumberShouldMatch(1)
                                .should(query)).setSize(10).setExplain(true)
                .execute().actionGet();
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSource();
            List<String> AbstractList = HanLP.extractSummary(
                    map.get("JudgeDecision").toString(), 8);
            String Abstract = "";
            for (String s : AbstractList)
                Abstract += s;
            Abstract = Abstract.replaceAll("</p>", "").replaceAll("<p>", "")
                    .replaceAll("&nbsp", "");
            SimpleRecord r = new SimpleRecord(
                    map.get("DisputeName").toString(), "201602121", Abstract);
            recordMSList.add(r);
        }
        client.close();
        Collection<SimpleRecord> former = minshiMap.values();
        List<SimpleRecord> v = new ArrayList<SimpleRecord>(former);
        Map<String, SimpleRecord> recordMap = new HashMap<String, SimpleRecord>();
        for (SimpleRecord s : recordMSList) {
            recordMap.put(s.getDisputeName(), s);
        }
        // 和之前的搜索结果取交集,有待测试
        Map<String, SimpleRecord> recordResult = new HashMap<String, SimpleRecord>();
        if (minshiMap.size() > 0) {
            Iterator iter = minshiMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry en = (Entry) iter.next();
                if (recordMap.containsKey(en.getKey())) {
                    recordResult.put(en.getKey().toString(),
                            (SimpleRecord) en.getValue());
                }
            }
        } else {
            recordResult = recordMap;
        }
        Collection<SimpleRecord> valueCollection = recordResult.values();
        List<SimpleRecord> valueList = new ArrayList<SimpleRecord>(
                valueCollection);
        return valueList;
    }

    // 用于根据申请人名字查询是否有对应案例
    public List<SourceRecord> queryByPersonName(String name) throws Exception {
        List<SourceRecord> recordList = new ArrayList<SourceRecord>();
        Client client = new MyTransportClient().getClient();
        // 纠纷案例
        MatchQueryBuilder query = QueryBuilders.matchQuery("personName", name);
        SearchResponse response = client.prepareSearch("dissension_index")
                .setTypes("dissensiontexts")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.boolQuery().must(query)).setSize(10)
                .setExplain(true).execute().actionGet();
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSource();
            SourceRecord r = new SourceRecord("1", map.get("disputeName")
                    .toString(), map.get("acceptDate").toString(), map.get(
                    "Abstract").toString());
            String temp = map.get("personName").toString();
            CharSequence s = name;
            if (temp.contains(s))
                recordList.add(r);
        }
        // 裁判文书
        MatchQueryBuilder queryA = QueryBuilders
                .matchQuery("DisputeName", name);
        SearchResponse response2 = client.prepareSearch("dissension_ms_index")
                .setTypes("dissension_mstexts")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.boolQuery().must(queryA)).setSize(5)
                .setExplain(true).execute().actionGet();
        SearchHit[] hits2 = response2.getHits().getHits();
        for (SearchHit hit : hits2) {
            Map<String, Object> map = hit.getSource();
            List<String> AbstractList = HanLP.extractSummary(
                    map.get("JudgeDecision").toString(), 8);
            String Abstract = "";
            for (String s : AbstractList)
                Abstract += s;
            Abstract = Abstract.replaceAll("</p>", "").replaceAll("<p>", "")
                    .replaceAll("&nbsp", "");
            SourceRecord r = new SourceRecord("3", map.get("DisputeName")
                    .toString(), "201602121", Abstract);
            String temp = map.get("DisputeName").toString();
            CharSequence s = name;
            if (temp.contains(s))
                recordList.add(r);
        }
        client.close();
        return recordList;
    }

    // 若有多名申请人
    public List<SourceRecord> queryByPersonName(List<String> personList)
            throws Exception {
        // personList为一个当事人名字List
        List<SourceRecord> recordList = new ArrayList<SourceRecord>();
        for (int i = 0; i < personList.size(); i++) {
            if (!personList.get(i).equals("")) {// 不为空
                List<SourceRecord> perList = queryByPersonName(personList
                        .get(i));
                for (SourceRecord s : perList)
                    recordList.add(s);
            }
        }
        return recordList;
    }

    // 根据医院名字查询相关案例
    public List<SourceRecord> queryByHospital(String hospitalname)
            throws Exception {
        List<SourceRecord> recordList = new ArrayList<SourceRecord>();
        Client client = new MyTransportClient().getClient();
        MatchQueryBuilder queryA = QueryBuilders.matchQuery("hospitalName",
                hospitalname);
        SearchResponse response = client.prepareSearch("dissension_index")
                .setTypes("dissensiontexts")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.boolQuery().must(queryA)).setSize(10)
                .setExplain(true).execute().actionGet();
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSource();
            SourceRecord r = new SourceRecord("1", map.get("disputeName")
                    .toString(), map.get("acceptDate").toString(), map.get(
                    "Abstract").toString());
            String temp = map.get("hospitalName").toString();
            CharSequence s = hospitalname;
            if (temp.contains(s))
                recordList.add(r);
        }
        // 裁判文书
        SearchResponse response2 = client.prepareSearch("dissension_ms_index")
                .setTypes("dissension_mstexts")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.boolQuery().must(queryA)).setSize(10)
                .setExplain(true).execute().actionGet();
        SearchHit[] hits2 = response2.getHits().getHits();
        for (SearchHit hit : hits2) {
            Map<String, Object> map = hit.getSource();
            List<String> AbstractList = HanLP.extractSummary(
                    map.get("JudgeDecision").toString(), 8);
            String Abstract = "";
            for (String s : AbstractList)
                Abstract += s;
            Abstract = Abstract.replaceAll("</p>", "").replaceAll("<p>", "")
                    .replaceAll("&nbsp", "");
            SourceRecord r = new SourceRecord("3", map.get("DisputeName")
                    .toString(), map.get("registerDate").toString(), Abstract);
            String temp = map.get("hospitalName").toString();
            CharSequence s = hospitalname;
            if (temp.contains(s))
                recordList.add(r);
        }
        client.close();
        return recordList;
    }

    // 根据医院、科室等信息查询案例
    public List<SourceRecord> queryByHospital(String hospitalname,
                                              String office, String basictype, String disputelevel)
            throws Exception {
        List<SourceRecord> recordList = new ArrayList<SourceRecord>();
        Client client = new MyTransportClient().getClient();
        MatchQueryBuilder queryA = QueryBuilders.matchQuery("hospitalName",
                hospitalname);
        MatchQueryBuilder queryB = QueryBuilders.matchQuery("Abstract",
                basictype);
        MatchQueryBuilder queryC = QueryBuilders.matchQuery("disputeDegree",
                disputelevel);
        SearchResponse response = client
                .prepareSearch("dissension_index")
                .setTypes("dissensiontexts")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(
                        QueryBuilders.boolQuery().must(queryA).must(queryB)
                                .must(queryC)).setSize(10).setExplain(true)
                .execute().actionGet();
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSource();
            SourceRecord r = new SourceRecord("1", map.get("disputeName")
                    .toString(), map.get("acceptDate").toString(), map.get(
                    "Abstract").toString());
            String temp = map.get("hospitalName").toString();
            CharSequence s = hospitalname;
            if (temp.contains(s))
                recordList.add(r);
        }
        client.close();
        return recordList;
    }

    // 按时间段查找历史案例
    public List<HistoryCase> searchByDate(String firstDate, String secondDate)
            throws Exception {

        List<HistoryCase> result = new ArrayList<HistoryCase>();
        Client client =new MyTransportClient().getClient();
        String[] firsttemp = firstDate.split("/");
        String[] secondtemp = secondDate.split("/");
        int first = 0;
        int second = 0;
        if ((firsttemp.length == 3) && (secondtemp.length == 3)) {
            String firstAfter = firsttemp[2] + firsttemp[0] + firsttemp[1];
            String secondAfter = secondtemp[2] + secondtemp[0] + secondtemp[1];
            first = Integer.parseInt(firstAfter);
            second = Integer.parseInt(secondAfter);
        }
        RangeQueryBuilder queryDateA = QueryBuilders.rangeQuery("registerDate")
                .gte(first).lte(second);
        SearchResponse response = client.prepareSearch("historycase_index")
                .setTypes("historycasetexts")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.boolQuery().must(queryDateA))
                .setExplain(true).execute().actionGet();
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSource();
            HistoryCase r = new HistoryCase(map.get("litigant").toString(), map
                    .get("litigantID").toString(), map.get("applicant")
                    .toString(), map.get("applicantID").toString(), map.get(
                    "agent").toString(), map.get("agentID").toString(), map
                    .get("hospital").toString(), map.get("disputeName")
                    .toString(), (int) map.get("registerDate"), map.get(
                    "briefExplanation").toString(), map.get("disputeArea")
                    .toString(), map.get("disputeType").toString(), map.get(
                    "disease").toString(), map.get("medicalTreatment")
                    .toString(), map.get("fault").toString(), map.get("result")
                    .toString(), map.get("appeal").toString(), map.get(
                    "otherExplanation").toString(), map.get("manageType")
                    .toString(), map.get("consequence").toString());
            result.add(r);
        }
        client.close();
        return result;
    }

    // 按当事人姓名查找历史案例
    public List<HistoryCase> searchByLitigant(String name) throws Exception {
        List<HistoryCase> result = new ArrayList<HistoryCase>();
        Client client =new MyTransportClient().getClient();
        MatchQueryBuilder queryname = QueryBuilders
                .matchQuery("litigant", name);
        SearchResponse response = client.prepareSearch("historycase_index")
                .setTypes("historycasetexts")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.boolQuery().must(queryname))
                .setExplain(true).setSize(10).execute().actionGet();
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSource();
            HistoryCase r = new HistoryCase(map.get("litigant").toString(), map
                    .get("litigantID").toString(), map.get("applicant")
                    .toString(), map.get("applicantID").toString(), map.get(
                    "agent").toString(), map.get("agentID").toString(), map
                    .get("hospital").toString(), map.get("disputeName")
                    .toString(), (int) map.get("registerDate"), map.get(
                    "briefExplanation").toString(), map.get("disputeArea")
                    .toString(), map.get("disputeType").toString(), map.get(
                    "disease").toString(), map.get("medicalTreatment")
                    .toString(), map.get("fault").toString(), map.get("result")
                    .toString(), map.get("appeal").toString(), map.get(
                    "otherExplanation").toString(), map.get("manageType")
                    .toString(), map.get("consequence").toString());
            result.add(r);
        }
        client.close();
        return result;
    }

    public ResultVO getESList(Integer page,Integer size,String type){
        String indices=null;
        String types=null;
        if(type.trim().equals("裁判文书")){
            indices="dissension_ms_index";
            types="dissension_mstexts";
        }else if(type.trim().equals("典型案例")){
            indices="dissension_dx_index";
            types="dissension_dxtexts";
        }else if(type.trim().equals("纠纷案例")){
            indices="dissension_index";
            types="dissensiontexts";
        }else{
            return ResultVOUtil.ReturnBack(501,"文书类型错误，必须为[裁判文书、典型案例和纠纷案例]其中一种");
        }
        try {
            Client client=new MyTransportClient().getClient();
            SearchResponse response = client
                    .prepareSearch(indices)
                    .setTypes(types)
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(
                            QueryBuilders.matchAllQuery()).addSort("_id", SortOrder.ASC).setFrom(page).setSize(size).setExplain(true)
                    .execute().actionGet();
            SearchHits searchHits=response.getHits();
            JSONArray res=JSONArray.parseArray("[]");
            for(SearchHit hit:searchHits){
                Map<String,Object> sourceAsMap=hit.getSource();
                JSONObject obj=JSONObject.parseObject("{}");
                if(type.trim().equals("纠纷案例")){
                    obj.put("disputeName",sourceAsMap.get("disputeName"));
                    obj.put("disputeType",sourceAsMap.get("disputeType"));
                }else{
                    obj.put("disputeName",sourceAsMap.get("DisputeName"));
                    obj.put("disputeType",sourceAsMap.get("DisputeType"));
                }
                res.add(obj);
            }
            return ResultVOUtil.ReturnBack(res,200,"success");
        }catch (Exception e){
            e.printStackTrace();
            return ResultVOUtil.ReturnBack(501,"查询出错");
        }
    }
}

