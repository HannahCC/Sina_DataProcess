package org.cl.conf;

import org.cl.utils.SaveInfo;

public class Config {

	public static final String SrcPath_Root = "D:\\Project_DataMinning\\Data\\Sina_res\\Sina_NLPIR1000_GenderPre\\mute_fake\\";
	public static final String ResDir = "D:\\Project_DataMinning\\DataProcessd\\";
	public static final String ResPath_Root = ResDir+"Sina_GenderPre_mute_fake1000\\";
	public static String Public_Info = ResPath_Root + "Public_Info_Rel\\";
	public static String UserID = ResPath_Root + "UserID\\";
	public static String ResPath = ResPath_Root;

	public static int FOLD = 5;
	public static int[] LABELS = {1,2};//类别
	public static String COMBINATION = "";//获取CHI\IDF时用于匹配文件名使用的字符串
	public static int WEIBO_NUMBER = 0;//微博级别的分类器，控制微博数量
	
	public static int ID_NUMBER = 500;//每类用户的总数
	public static int TRAIN_ID_SIZE = 400;
	public static int[] TRAIN_ID_SIZE_ARR = {400,350,300,250,200,150,100};//每类用户用于训练的ID数量。
	
	public static boolean LEARN_FLAG = false;
	public static float CHI_threshold = 0.5f;//取CHI值排在前50%的特征
	public static boolean CHI_FLAG = false;//是否使用CHI筛选特征
	public static boolean TFIDF_FLAG = false;//是否使用TFIDF表示特征,false时只用tf,true用tfidf
	
	public static String SVM_TYPE = "lg";

	
	static{
		for(int labelid : LABELS){COMBINATION+=labelid;}
		COMBINATION+="_";
		SaveInfo.mkdir(ResPath_Root);
	}
}
