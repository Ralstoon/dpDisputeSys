package com.seu.elasticsearch.json;

public class HistoryCase {
	private String litigant;//当事人
	private String litigantID;
	private String applicant;//申请人
	private String applicantID;
	private String agent;//代理人
	private String agentID;
	private String hospital;
	private String disputeName;//案例标题
	private int registerDate;//录入时间
	private String briefExplanation;//简要情况说明
	private String disputeArea;//纠纷区域
	private String disputeType;//纠纷类型
	private String disease;//疾病名称
	private String medicalTreatment;//医疗行为
	private String fault;//可能过失
	private String result;//伤害结果
	private String appeal;//诉求
	private String otherExplanation;//其他情况说明
	private String manageType;//管理责任类型
	private String consequence;//造成后果
	
	//返回标题长度
	public int getLength(){
		return disputeName.length();
	}
	
	
	public HistoryCase(String litigant,String litigantID,String applicant,String applicantID,
			String agent, String agentID,String hospital,String disputeName,int registerDate
			,String briefExplanation,String disputeArea,String disputeType,String disease,String medicalTreatment,String fault,
			String result,String appeal,String otherExplanation,String manageType,String consequence){
		this.litigant = litigant;
		this.litigantID = litigantID;
		this.applicant = applicant;
		this.applicantID = applicantID;
		this.agent = agent;
		this.agentID = agentID;
		this.hospital = hospital;
		this.disputeName = disputeName;
		this.registerDate = registerDate;
		this.briefExplanation = briefExplanation;
		this.disputeType = disputeType;
		this.disputeArea = disputeArea;
		this.disease = disease;
		this.medicalTreatment = medicalTreatment;
		this.fault = fault;
		this.result = result;
		this.appeal = appeal;
		this.otherExplanation = otherExplanation;
		this.manageType = manageType;
		this.consequence = consequence;
	}
	

	/*public HistoryCase(String litigant,String litigantID,String applicant,String applicantID,
			String agent, String agentID,String hospital,String disputeName,String registerDate
			,String briefExplanation,String disputeArea,String disputeType,
			String result,String appeal,String otherExplanation){
		this.litigant = litigant;
		this.litigantID = litigantID;
		this.applicant = applicant;
		this.applicantID = applicantID;
		this.agent = agent;
		this.agentID = agentID;
		this.hospital = hospital;
		this.disputeName = disputeName;
		this.registerDate = registerDate;
		this.briefExplanation = briefExplanation;
		this.disputeType = disputeType;
		this.disputeArea = disputeArea;
		//this.disease = disease;
		//this.medicalTreatment = medicalTreatment;
		//this.fault = fault;
		this.result = result;
		this.appeal = appeal;
		this.otherExplanation = otherExplanation;
	}*/
	

	public String getLitigant() {
		return litigant;
	}
	public void setLitigant(String litigant) {
		this.litigant = litigant;
	}
	public String getLitigantID() {
		return litigantID;
	}
	public void setLitigantID(String litigantID) {
		this.litigantID = litigantID;
	}

	public String getApplicant() {
		return applicant;
	}
	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}
	public String getApplicantID() {
		return applicantID;
	}
	public void setApplicantID(String applicantID) {
		this.applicantID = applicantID;
	}
	
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		
	}
	public String getAgentID() {
		return agentID;
	}
	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}
	
	
	public String getHospital() {
		return hospital;
	}
	public void setHospital(String hospital) {
		this.hospital = hospital;
	}
	public String getDisputeName() {
		return disputeName;
	}
	public void setDisputeName(String disputeName) {
		this.disputeName = disputeName;
	}
	public int getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(int registerDate) {
		this.registerDate = registerDate;
	}
	

	public String getBriefExplanation() {
		return briefExplanation;
	}
	public void setBriefExplanation(String briefExplanation) {
		this.briefExplanation = briefExplanation;
	}
	
	 public String getDisputeType(){
		 return disputeType;
	 }
	 
	 public void setDisputeType(String disputeType){
		 this.disputeType = disputeType;
	 }
	 
	 public String getDisputeArea(){
		 return disputeArea;
	 }
	 
	 public void setDisputeArea(String disputeArea){
		 this.disputeArea = disputeArea;
	 }
	 
	 
	public String getDisease() {
		return disease;
	}
	public void setDisease(String disease) {
		this.disease = disease;
	}
	public String getMedicalTreatment() {
		return medicalTreatment;
	}
	public void setMedicalTreatment(String medicalTreatment) {
		this.medicalTreatment = medicalTreatment;
	}
	
	public String getFault() {
		return fault;
	}
	public void setFault(String fault) {
		this.fault = fault;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getAppeal() {
		return appeal;
	}
	public void setAppeal(String appeal) {
		this.appeal = appeal;
	}
	
	public String getOtherExplanation() {
		return otherExplanation;
	}
	public void setOtherExplanation(String otherExplanation) {
		this.otherExplanation = otherExplanation;
	}
	
	public void setManageType(String manageType){
		this.manageType = manageType;
	}
	
	public String getManageType(){
		return manageType;
	}
	
	public void setConsequence(String consequence){
		this.consequence = consequence;
	}
	
	public String getConsequence(){
		return consequence;
	}
	
	
	}
