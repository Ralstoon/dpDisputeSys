package com.seu.elasticsearch.json;

public class SimpleRecord {
	private String Abstract;
	private String registerDate;
	private String DisputeName;


	public String getRegisterDate(){
		return registerDate;
	}
	public void setRegisterDate(String registerDate){
		this.registerDate = registerDate;
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

	public SimpleRecord(String DisputeName,String registerDate,String Abstract){
		this.Abstract=Abstract;
		this.registerDate = registerDate;
		this.DisputeName=DisputeName;
	}

}
