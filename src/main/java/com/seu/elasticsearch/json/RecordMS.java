package com.seu.elasticsearch.json;

//民事案例类
public class RecordMS {
	private String DisputeName;
	private String registerDate;
	private String DisputeType;
	private String CourtThink;
	private String JudgeDecision;
	private String DisputeTag;
	private String personName;
	private String hospitalName;

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
	public RecordMS(String DisputeName,String registerDate, String DisputeType,String CourtThink,String JudgeDecision,String DisputeTag,String personName,String hospitalName){
		this.registerDate = registerDate;
		this.DisputeName = DisputeName;
		this.DisputeType = DisputeType;
		this.CourtThink = CourtThink;
		this.JudgeDecision = JudgeDecision;
		this.DisputeTag = DisputeTag;
		this.personName=personName;
		this.hospitalName=hospitalName;
	}
	
	//获取name长度，以便设置展示页面的标题字体大小
	public Integer getLength(){
		return DisputeName.length();
	}
	
	public void setRegisterDate(String registerDate){
		this.registerDate = registerDate;
	}
	public String getRegisterDate(){
		return registerDate;
	}
	
	public String getCourtThink() {
		return CourtThink;
	}
	public void setCourtThink(String CourtThink) {
		this.CourtThink = CourtThink;
	}
	public String getJudgeDecision() {
		return JudgeDecision;
	}
	public void setJudgeDecision(String JudgeDecision) {
		this.JudgeDecision = JudgeDecision;
	}
	public String getDisputeName() {
		return DisputeName;
	}
	public void setDisputeName(String DisputeName) {
		this.DisputeName = DisputeName;
	}
	public String getDisputeTag() {
		return DisputeTag;
	}
	public void setDisputeTag(String DisputeTag) {
		this.DisputeTag = DisputeTag;
	}
	public String getDisputeType() {
		return DisputeType;
	}
	public void setDisputeType(String DisputeType) {
		this.DisputeType = DisputeType;
	}

}
