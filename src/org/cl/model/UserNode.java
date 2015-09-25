package org.cl.model;

public class UserNode {
	/*<person>
	  <id>11086</id> 
	  <sex>男</sex> 
	  <address>广东，广州</address> 
	  <fansNum>592</fansNum> 
	  <summary>做自己的梦，走自己的路。</summary> 
	  <wbNum>1051</wbNum> 
	  <gzNum>280</gzNum> 
	  <blog>http://www.yijee.com</blog> 
	  <edu>高中： 彭湃中学 (1999年)</edu> 
	  <work>公司： 新浪微博-Xweibo 地区：广东 ，广州 职位：web前端</work> 
	  <renZh>1</renZh> 
	  <brithday>魔羯座</brithday> 
	</person>*/
	
	String id ;
	String sex ; 
	String address;
	String fansNum;
	String summary ;
	String wbNum ;
	String gzNum ; 
	String blog ; 
	String edu ;
	String work ;
	String renZh ;
	String brithday;
	String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFansNum() {
		return fansNum;
	}
	public void setFansNum(String fansNum) {
		this.fansNum = fansNum;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getWbNum() {
		return wbNum;
	}
	public void setWbNum(String wbNum) {
		this.wbNum = wbNum;
	}
	public String getGzNum() {
		return gzNum;
	}
	public void setGzNum(String gzNum) {
		this.gzNum = gzNum;
	}
	public String getBlog() {
		return blog;
	}
	public void setBlog(String blog) {
		this.blog = blog;
	}
	public String getEdu() {
		return edu;
	}
	public void setEdu(String edu) {
		this.edu = edu;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getRenZh() {
		return renZh;
	}
	public void setRenZh(String renZh) {
		this.renZh = renZh;
	}
	public String getBrithday() {
		return brithday;
	}
	public void setBrithday(String brithday) {
		this.brithday = brithday;
	}
	
}
