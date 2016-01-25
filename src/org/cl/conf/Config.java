package org.cl.conf;

import java.util.ArrayList;
import java.util.List;

import org.cl.utils.SaveInfo;

public class Config {

	public static final String SrcPath_Root = "D:\\Project_DataMinning\\Data\\Sina_res\\Sina_NLPIR2223_GenderPre\\";
	public static final String ResDir = "D:\\Project_DataMinning\\DataProcessd\\";
	public static final String ResPath_Root = ResDir+"Sina_GenderPre_1635\\";
	public static final String Public_Info = ResPath_Root + "Public_Info_Rel\\"; //"UserTextNN_withID\\UserID\\";  //
	public static final String UserID = ResPath_Root + "UserID\\";
	
	public static final int FOLD = 5;
	public static int[] LABELS = {1,2};//类别
	public static final int WEIBO_NUMBER = 0;//微博级别的分类器，控制微博数量
	
	public static final int ID_NUMBER = 200;//每类用户的总数
	public static final int TRAIN_ID_SIZE = 4; //4份数据为traindata
	public static final int LEARN_ID_SIZE = 1;
	public static final int[] TRAIN_ID_SIZE_ARR = {4,3,2,1};//每类用户用于训练的ID数量。
	
	public static final boolean LEARN_FLAG = false;
	public static final float CHI_threshold = 0.5f;//取CHI值排在前50%的特征
	public static final boolean CHI_FLAG = false;//是否使用CHI筛选特征
	public static final float DF_threshold = 0.6f;//取CHI值排在前50%的特征
	public static final boolean DF_FLAG = false;//是否使用DF筛选特征
	public static final boolean TFIDF_FLAG = false;//是否使用TFIDF表示特征,false时只用tf,true用tfidf
	
	public static final String SVM_TYPE = "lg";
	public static final List<String> CLASSIFERS = new ArrayList<String>();

	public static String ResPath = ResPath_Root;
	public static String COMBINATION = "";//获取CHI\IDF时用于匹配文件名使用的字符串
	static{
		for(int labelid : LABELS){COMBINATION+=labelid;}
		COMBINATION+="_";
		SaveInfo.mkdir(ResPath_Root);
		
		/*CLASSIFERS.add("Feature_UserInfo\\TagAvgVec");
		//CLASSIFERS.add("Feature_Relation\\LFDMMVFriTopic");
		CLASSIFERS.add("Feature_SRC\\AppType2_BaiKeExpand25");
		CLASSIFERS.add("Feature_Relation\\VFriNewType_Expand25");*/
		//CLASSIFERS.add("Feature_UserInfo\\Tag");
		//CLASSIFERS.add("Feature_UserInfo\\1635_TagAvgVec");
		//CLASSIFERS.add("Feature_Src\\SrcNewType1");
		//CLASSIFERS.add("Feature_UserInfo\\ScreenNameAvgVec");
		//CLASSIFERS.add("Feature_UserInfo\\TagType");
		//CLASSIFERS.add("Feature_UserInfo\\Description");
		//CLASSIFERS.add("Feature_Relation\\Fri_Fol_Tag");
		//CLASSIFERS.add("Feature_Relation\\Fri_Fol_Description");
		//CLASSIFERS.add("Feature_Relation\\Line_vec_all");
		//CLASSIFERS.add("Feature_Relation\\Line6_desc_tag_Conc_18w_vec_all");
		//CLASSIFERS.add("Feature_Relation\\VFolType");
		//CLASSIFERS.add("Feature_Relation\\HLVFolNewType_Expand25");
		//CLASSIFERS.add("Feature_Relation\\LDAVFriTopic2000_100");
		//CLASSIFERS.add("Feature_Relation\\LDAVFolTopic");
		//CLASSIFERS.add("Feature_Relation\\LDAVFriFolTopic");
		//CLASSIFERS.add("Feature_Relation\\LFDMMVFriTopic");
		//CLASSIFERS.add("Feature_Relation\\LFDMMVFolTopic");
		//CLASSIFERS.add("Feature_Relation\\LFDMMVFriFolTopic");
		//CLASSIFERS.add("Feature_SRC\\SrcType2_Expand25");
		//CLASSIFERS.add("Feature_SRC\\LDASrcTopic2000_100");
		//CLASSIFERS.add("Feature_SRC\\LFLDASrcTopic");
		//CLASSIFERS.add("Feature_SRC\\LFDMMSrcTopic");
		//CLASSIFERS.add("Feature_SRC\\Src");
		//CLASSIFERS.add("Feature_SRC\\App");
		//CLASSIFERS.add("Feature_SRC\\LFLDAAppTopic2000_100_100_0.6");
		
	}
}
