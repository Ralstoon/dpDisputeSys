package com.seu.elasticsearch.json;

public class RecordDX {
	
	private String DisputeName;
	private String registerDate;
	private String DisputeType;
	private String DisputeLocation;
	private String Institution;
	private String StaffName;
	private String Abstract;
	private String DisputeResolution;
	private String comment;
	private String RelateLaw;
	private String DisputeTag;
	
	//获取name长度，以便设置展示页面的标题字体大小
		public Integer getLength(){
			return DisputeName.length();
		}
		

	public String getDisputeLocation() {
		return DisputeLocation;
	}
	public void setDisputeLocation(String DisputeLocation) {
		this.DisputeLocation = DisputeLocation;
	}
	public String getDisputeName() {
		return DisputeName;
	}
	public void setDisputeName(String DisputeName) {
		this.DisputeName = DisputeName;
	}
	public void setRegisterDate(String registerDate){
		this.registerDate = registerDate;
	}
	public String getRegisterDate(){
		return registerDate;
	}
	public String getInstitution() {
		return Institution;
	}
	public void setInstitution(String Institution) {
		this.Institution = Institution;
	}
	public String getStaffName() {
		return StaffName;
	}
	public void setStaffName(String StaffName) {
		this.StaffName = StaffName;
	}
	public String getAbstract() {
		return Abstract;
	}
	public void setAbstract(String Abstract) {
		this.Abstract = Abstract;
	}
	public String getDisputeResolution() {
		return DisputeResolution;
	}
	public void setDisputeResolution(String DisputeResolution) {
		this.DisputeResolution = DisputeResolution;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getRelateLaw() {
		return RelateLaw;
	}
	public void setRelateLaw(String RelateLaw) {
		this.RelateLaw = RelateLaw;
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
	
	
	public RecordDX(String DisputeName, String registerDate, String DisputeType, String DisputeLocation, String Institution, String StaffName, String Abstract, String DisputeResolution,String comment,
			String RelateLaw,String DisputeTag){
		this.registerDate = registerDate;
		this.DisputeResolution=DisputeResolution;
		this.Abstract=Abstract;
		this.StaffName=StaffName;
		this.DisputeTag=DisputeTag;
		this.comment=comment;
		this.RelateLaw=RelateLaw;
		this.Institution=Institution;
		this.DisputeType=DisputeType;
		this.DisputeLocation=DisputeLocation;
		this.DisputeName=DisputeName;
	}
}

