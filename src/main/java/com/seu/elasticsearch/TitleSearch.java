package com.seu.elasticsearch;

/*
 * 用于搜索结果全文展示部分，根据标题搜索案例
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.search.MultiMatchQuery.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import com.seu.elasticsearch.json.*;

public class TitleSearch {
	public RecordMS titleSearchMS(String title) throws Exception {
		Client client =new MyTransportClient().getClient();
		String a = java.net.URLDecoder.decode(title, "utf-8");
		List<RecordMS> recordList = new ArrayList<RecordMS>();
		MatchQueryBuilder qb = QueryBuilders.matchQuery("DisputeName", a);
		SearchResponse response = client.prepareSearch("dissension_ms_index")
				.setTypes("dissension_mstexts")
				.setSearchType(SearchType.QUERY_AND_FETCH).setQuery(qb)
				.setSize(1).setExplain(true).execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		for (SearchHit hit : hits) {
			Map<String, Object> map = hit.getSource();
			// System.out.println(map);
			RecordMS r = new RecordMS(map.get("DisputeName").toString(),
					"201602121", map.get("DisputeType").toString(), map.get(
							"CourtThink").toString(), map.get("JudgeDecision")
							.toString(), map.get("DisputeTag").toString(), map
							.get("personName").toString(), map.get(
							"hospitalName").toString());
			recordList.add(r);
		}
		client.close();
		if (!recordList.isEmpty()) {
			String word = a.trim();
			boolean judge = recordList.get(0).getDisputeName().trim()
					.equals(word);
			if (judge)
				return recordList.get(0);
		}
		return null;
	}

	public DocumentBean titleSearch(String title) throws Exception {
		Client client =new MyTransportClient().getClient();
		String a = java.net.URLDecoder.decode(title, "utf-8");
		List<DocumentBean> recordList = new ArrayList<DocumentBean>();
		MatchQueryBuilder qb = QueryBuilders.matchQuery("disputeName", a);
		SearchResponse response = client.prepareSearch("dissension_index")
				.setTypes("dissensiontexts")
				.setSearchType(SearchType.QUERY_AND_FETCH).setQuery(qb)
				.setSize(1).setExplain(true).execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		for (SearchHit hit : hits) {
			Map<String, Object> map = hit.getSource();
			DocumentBean r = new DocumentBean(map.get("disputeNO").toString(),
					map.get("district").toString(), map.get("disputeName")
							.toString(), map.get("evaluation").toString(), map
							.get("fierceDegree").toString(), map.get(
							"disputePerson").toString(), map
							.get("compensation").toString(), map.get(
							"disputeDegree").toString(), map.get("acceptDate")
							.toString(), map.get("disputeOrigin").toString(),
					map.get("disputeType").toString(), map.get("disputeArea")
							.toString(), map.get("disputeLocation").toString(),
					map.get("Abstract").toString(), map
							.get("disputeResolution").toString(), map.get(
							"resolutionDate").toString(), map
							.get("institution").toString(), map
							.get("staffName").toString(), map.get("agreement")
							.toString(), map.get("disputeTag").toString(), map
							.get("personName").toString(), map.get(
							"hospitalName").toString());
			recordList.add(r);
		}
		client.close();
		if (!recordList.isEmpty()) {
			String word = a.trim();
			boolean judge = recordList.get(0).getDisputeName().trim()
					.equals(word);
			if (judge)
				return recordList.get(0);
		}
		return null;
	}

	public RecordDX titleSearchDX(String title) throws Exception {
		Client client =new MyTransportClient().getClient();
		String a = java.net.URLDecoder.decode(title, "utf-8");
		List<RecordDX> recordList = new ArrayList<RecordDX>();
		MatchQueryBuilder qb = QueryBuilders.matchQuery("DisputeName", a);
		SearchResponse response = client.prepareSearch("dissension_dx_index")
				.setTypes("dissension_dxtexts")
				.setSearchType(SearchType.QUERY_AND_FETCH).setQuery(qb)
				.setSize(1).setExplain(true).execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		for (SearchHit hit : hits) {
			Map<String, Object> map = hit.getSource();
			RecordDX r = new RecordDX(map.get("DisputeName").toString(),
					"201602121", map.get("DisputeType").toString(), map.get(
							"DisputeLocation").toString(), map.get(
							"Institution").toString(), map.get("StaffName")
							.toString(), map.get("Abstract").toString(), map
							.get("DisputeResolution").toString(), map.get(
							"comment").toString(), map.get("RelateLaw")
							.toString(), map.get("DisputeTag").toString());
			recordList.add(r);
		}
		client.close();
		if (!recordList.isEmpty()) {
			String word = a.trim();
			boolean judge = recordList.get(0).getDisputeName().trim()
					.equals(word);
			if (judge)
				return recordList.get(0);
		}
		return null;
	}

	// 历史案例标题搜索
	public HistoryCase titleSearchHC(String title) throws Exception {
		Client client =new MyTransportClient().getClient();
		String a = java.net.URLDecoder.decode(title, "utf-8");
		List<HistoryCase> recordList = new ArrayList<HistoryCase>();
		MatchQueryBuilder qb = QueryBuilders.matchQuery("disputeName", a);
		SearchResponse response = client.prepareSearch("historycase_index")
				.setTypes("historycasetexts")
				.setSearchType(SearchType.QUERY_AND_FETCH).setQuery(qb)
				.setSize(1).setExplain(true).execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		for (SearchHit hit : hits) {
			Map<String, Object> map = hit.getSource();
			// System.out.println(map);
			HistoryCase r = new HistoryCase(map.get("litigant").toString(), map
					.get("litigantID").toString(), map.get("applicant")
					.toString(), map.get("applicantID").toString(), map.get(
					"agent").toString(), map.get("agentID").toString(), map
					.get("hospital").toString(), map.get("disputeName")
					.toString(), 201602121, map.get("briefExplanation")
					.toString(), map.get("disputeArea").toString(), map.get(
					"disputeType").toString(), map.get("disease").toString(),
					map.get("medicalTreatment").toString(), map.get("fault")
							.toString(), map.get("result").toString(), map.get(
							"appeal").toString(), map.get("otherExplanation")
							.toString(), map.get("manageType").toString(), map
							.get("consequence").toString());
			recordList.add(r);
		}
		client.close();
		if (!recordList.isEmpty()) {
			String word = a.trim();
			boolean judge = recordList.get(0).getDisputeName().trim()
					.equals(word);
			if (judge)
				return recordList.get(0);
		}
		return null;
	}
}
