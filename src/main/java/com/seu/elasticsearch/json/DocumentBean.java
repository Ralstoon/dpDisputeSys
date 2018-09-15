package com.seu.elasticsearch.json;


/**
 * 文书类，存放相关信息（与搭建索引有重要联系）
 */

public class DocumentBean {
	//获取name长度，以便设置展示页面的标题字体大小
		public Integer getLength(){
			return disputeName.length();
		}
		
	
	public String getDisputeNO() {
		return disputeNO;
	}


	public void setDisputeNO(String disputeNO) {
		this.disputeNO = disputeNO;
	}


	public String getDisputePerson() {
		return disputePerson;
	}


	public void setDisputePerson(String disputePerson) {
		this.disputePerson = disputePerson;
	}


	public String getCompensation() {
		return compensation;
	}


	public void setCompensation(String compensation) {
		this.compensation = compensation;
	}

	 private String disputeNO;
	 private String district; //区域
	 private String disputeName; //纠纷名称
	 private String evaluation; //案情评估
	 private String fierceDegree; //激烈程度
	 private String disputePerson;
	 private String compensation; //涉及金额
	 private String disputeDegree; //纠纷等级
	 private String acceptDate; //受理时间
	 private String disputeOrigin; //案件来源
	 private String disputeType; //纠纷类型
	 private String disputeArea; //纠纷区域
	 private String disputeLocation; //发生地点
	 private String Abstract; //简要情况
	 private String disputeResolution; //调解结果
	 private String resolutionDate; //调解成功时间
	 private String institution; //责任单位
	 private String staffName; //调解员
	 private String agreement; //达成协议内容
	 private String disputeTag; //标签
	 
	public String getPersonName() {
		return personName;
	}


	public void setPersonName(String personName) {
		this.personName = personName;
	}



	public String getHospitalName() {
		return hospitalName;
	}


	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}


	private String personName;//当事人姓名
	private String hospitalName;//医院名称
	 
	 
	 
	 
	 public DocumentBean(String disputeNO,String district,String disputeName, String evaluation,String fierceDegree,
			 String disputePerson,String compensation,  String disputeDegree,String acceptDate,String disputeOrigin,String disputeType,
			 String disputeArea,String disputeLocation,String Abstract, String disputeResolution, String resolutionDate,
			 String institution, String staffName, String agreement, String disputeTag,String personName,String hospitalName){
				 this.disputeNO=disputeNO;
				 this.district = district;
				 this.disputeName=disputeName;
				 this.evaluation = evaluation;
				 this.fierceDegree = fierceDegree;
				 this.disputePerson = disputePerson;
				 this.compensation = compensation;
				 this.disputeDegree = disputeDegree;
				 this.acceptDate = acceptDate;
				 this.disputeOrigin = disputeOrigin;
				 this.disputeType = disputeType;
				 this.disputeArea = disputeArea;
				 this.disputeLocation =disputeLocation;
				 this.Abstract = Abstract;
				 this.disputeResolution = disputeResolution;
				 this.resolutionDate = resolutionDate;
				 this.institution = institution;
				 this.staffName = staffName;
				 this.agreement = agreement;
				 this.disputeTag = disputeTag;
				 this.personName=personName;
				 this.hospitalName=hospitalName;
			 
			 }
	 
	 
	 public String getDistrict(){
		 return district;
	 }
	 
	 public void setDistrict(String district){
		 this.district = district;
	 }
	 
	 public String getDisputeName(){
		 return disputeName;
	 }
	 
	 public void setDisputeName(String dn){
		 disputeName = dn;
	 }
	 
	 public String getEvaluation(){
		 return evaluation;
	 }
	 
	 public void setEvaluation(String evaluation){
		 this.evaluation = evaluation;
	 }
	 
	 public String getFierceDegree(){
		 return fierceDegree;
	 }
	 
	 public void setFierceDegree(String fierceDegree){
		 this.fierceDegree = fierceDegree;
	 }
	 
	 public String getDisputeDegree(){
		 return disputeDegree;
	 }
	 
	 public void setDisputeDegree(String rank){
		disputeDegree = rank;
	 }
	
	 public String getAcceptDate(){
		 return acceptDate;
	 }
	 
	 public void setAcceptDate(String date){
		 acceptDate = date;
	 }
	 
	 public String getDisputeOrigin(){
		 return disputeOrigin;
	 }
	 
	 public void setDisputeOrigin(String source){
		 disputeOrigin = source;
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
	 
	 public String getDisputeLocation(){
		 return disputeLocation;
	 }
	 
	 public void setDisputeLocation(String location){
		 disputeLocation = location;
	 }
	 
	 public String getAbstract(){
		 return Abstract;
	 }
	 
	 public void setAbstract(String Abstract){
		 this.Abstract = Abstract;
	 }
	 
	 public String getDisputeResolution(){
		 return disputeResolution;
	 }
	 
	 public void setDisputeResolution(String result){
		 disputeResolution = result;
	 }
	 
	 public String getResolutionDate(){
		 return resolutionDate;
	 }
	 
	 public void setResolutionDate(String date){
		 resolutionDate = date;
	 }
			
	 public String getInstitution(){
		 return institution;
	 }
	 
	 public void setInstitution(String institution){
		 this.institution = institution;
	 }
	
	 public String getStaffName(){
		 return staffName;
	 }
	 
	 public void setStaffName(String staffName){
		 this.staffName = staffName;
	 }
	 
	 public String getAgreement(){
		 return agreement;
	 }
	 
	 public void setAgreement(String agreement){
		 this.agreement = agreement;
	 }
	 
	 public String getDisputeTag(){
		 return disputeTag;
	 }
	 
	 public void setDisputeTag(String tag){
		 disputeTag = tag;
	 }
	 

}
