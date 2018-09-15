package com.seu.elasticsearch.json;

public class SourceRecord {
	private String DisputeName;
	private String registerDate;//日期
	private String caseSource;
	private String Abstract;
	
	public void setRegisterDate(String registerDate){
		this.registerDate = registerDate;
	}
	public String getRegisterDate(){
		return registerDate;
	}
	
	public String getCaseSource() {
		return caseSource;
	}
	public void setCaseSource(String caseSource) {
		this.caseSource = caseSource;
	}
	
	public String getAbstract() {
		return Abstract;
	}
	public void setAbstract(String abstract1) {
		Abstract = abstract1;
	}
	public String getDisputeName() {
		return DisputeName;
	}
	public void setDisputeName(String disputeName) {
		DisputeName = disputeName;
	}
	
	public SourceRecord(String caseSource,String DisputeName,String registerDate,String Abstract){
		this.caseSource=caseSource;
		this.registerDate = registerDate;
		this.DisputeName=DisputeName;
		this.Abstract=Abstract;
	}

}
