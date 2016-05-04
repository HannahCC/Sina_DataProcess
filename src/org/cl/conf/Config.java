package org.cl.conf;

import java.util.ArrayList;
import java.util.List;

import org.cl.utils.SaveInfo;

public class Config {
	public static final String ResDir = "D:\\Project_DataMinning\\DataProcessd\\";

	//public static int[] LABELS = {1,2,3,4,5};//类别
	/*public static final String SrcPath_Root = "E:\\DataSource\\Youtube\\";//Sina_NLPIRandTHUext1000_Mute_GenderPre\\";//
	public static final String ResPath_Root = ResDir+"Youtube_11677\\";//"Sina_GenderPre_mute1000\\";//"Sina_AgePre_1535\\";////"Sina_AgePre_1161\\";//
	*/
	/*public static final String SrcPath_Root = "E:\\DataSource\\Flickr\\";//Sina_NLPIRandTHUext1000_Mute_GenderPre\\";//
	public static final String ResPath_Root = ResDir+"Flickr_42316\\";//"Sina_GenderPre_mute1000\\";//"Sina_AgePre_1535\\";////"Sina_AgePre_1161\\";//
	*/
	public static final String SrcPath_Root = "D:\\Project_DataMinning\\Data\\Sina_res\\Sina_NLPIR2223_GenderPre\\";//Sina_NLPIRandTHUext1000_Mute_GenderPre\\";//
	public static final String ResPath_Root = ResDir+"Sina_GenderPre_1635\\";//UserTextNN\\//"Sina_GenderPre_mute1000\\";//"Sina_AgePre_1535\\";////"Sina_AgePre_1161\\";//
	
	public static final String Public_Info = ResPath_Root + "Public_Info_Rel\\"; //"UserTextNNNotValidated\\UserID\\";  ////
	public static final String UserID = SrcPath_Root + "UserID\\";
	public static final String UserID_Suffix = ".txt";//"_old2_new.txt";
	
	public static final int FOLD = 5;
	public static int[] LABELS = {1,2};//类别
	public static final int WEIBO_NUMBER = 0;//微博级别的分类器，控制微博数量
	
	public static final int ID_NUMBER = 200;//每类用户的总数(1vsall时被使用)
	public static final int TRAIN_ID_SIZE = 4; //4份数据为traindata
	public static final int LEARN_ID_SIZE = 1;
	public static final int[] TRAIN_ID_SIZE_ARR = {4,3,2,1};//每类用户用于训练的ID数量。
	
	public static final boolean LEARN_FLAG = false;
	public static final float CHI_threshold = 0.5f;//取CHI值排在前50%的特征
	public static final boolean CHI_FLAG = false;//是否使用CHI筛选特征
	public static final float DF_threshold = 0.6f;//取CHI值排在前50%的特征
	public static final boolean DF_FLAG = false;//是否使用DF筛选特征
	public static final boolean TFIDF_FLAG = false;//是否使用TFIDF表示特征,false时只用tf,true用tfidf
	
	public static final String CLASSIFIER_TYPE = "lg";
	public static final List<String> CLASSIFERS = new ArrayList<String>();

	public static String ResPath = ResPath_Root;
	public static String COMBINATION = "";//获取CHI\IDF时用于匹配文件名使用的字符串
	static{
		for(int labelid : LABELS){COMBINATION+=labelid;}
		COMBINATION+="_";
		SaveInfo.mkdir(ResPath_Root);

		CLASSIFERS.add("Feature_Relation\\1635_FriAvgVec_skn10wc200l100i15_Train");
		//CLASSIFERS.add("Feature_Relation\\FriVec_Lines10n5d1281st");
		//CLASSIFERS.add("Feature_Relation\\FriVec_Lines10n5d1282nd");
		//CLASSIFERS.add("Feature_UserInfo\\Tag");
		//CLASSIFERS.add("Feature_UserInfo\\1635_Win8_L100_TagAvgVec");
		//CLASSIFERS.add("Feature_Relation\\VFri");
		//CLASSIFERS.add("Feature_Relation\\VFolType");
		//CLASSIFERS.add("Feature_Relation\\FriTagAvgVec");
		//CLASSIFERS.add("Feature_Relation\\FriAvgVec");
		//CLASSIFERS.add("Feature_SRC\\Src");
		//CLASSIFERS.add("Feature_SRC\\SrcAvgVec_Baike");
		
	}
}
