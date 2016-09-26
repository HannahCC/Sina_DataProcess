package org.cl.conf;

import java.util.ArrayList;
import java.util.List;

public class Config {

	public static String RootPath = null; //每个数据集实验结果的根目录
	public static String SVMPath = null; //svm存放的目录
	public static String UserID = null; //该数据集的id-label
	public static String SrcPath_Root = null; //feature 来源
	public static String ResPath_Root = null; //classifier 存放目录
	public static String TrainTestID = null; // id 的划分 
	public static String ResPath = ResPath_Root; // 常变的结果目录

	public static void init(String rootPath) {
		RootPath = rootPath;
		SVMPath = rootPath + "src\\SVM\\";
		UserID = rootPath + "user_id\\";
		SrcPath_Root = rootPath + "features\\";
		ResPath_Root = rootPath + "classifiers\\";
		TrainTestID = ResPath_Root + "train_test_id\\";
		ResPath = ResPath_Root;
	}
	public static void init(String rootPath, int classnum, int foldnum) {
		init(rootPath);
		CLASS_NUMBER = classnum;
		CLASSES = new int[classnum];
		for(int i=0;i<classnum;i++){CLASSES[i] = i+1;}
		for (int labelid : LABELS) {
			COMBINATION += labelid;
		}
		COMBINATION += "_";
		
		FOLD = foldnum;
	}

	public static void init(String rootPath, int classnum, int foldnum, int train_size){
		init(rootPath, classnum, foldnum);
		TRAIN_ID_SIZE = train_size;
	}
	
	public static void init(String rootPath, int classnum, int foldnum, int[] train_size_arr){
		init(rootPath, classnum, foldnum);
		TRAIN_ID_SIZE_ARR = train_size_arr;
	}
	public static int CLASS_NUMBER = 0;
	public static int[] CLASSES = null;
	public static int FOLD = 0;
	public static int LABEL_NUMBER = 0;
	public static int[] LABELS = {1, 2};//类别
	public static String COMBINATION = "";// 获取CHI\IDF时用于匹配文件名使用的字符串
	public static int WEIBO_NUMBER = 0;// 微博级别的分类器，控制微博数量
	
	public static final int ID_NUMBER = 200;//每类用户的总数(1vsall时被使用)
	public static int TRAIN_ID_SIZE = 4; //4份数据为traindata
	public static final int LEARN_ID_SIZE = 1;
	public static int[] TRAIN_ID_SIZE_ARR = null;//每类用户用于训练的ID数量。
	
	public static final boolean LEARN_FLAG = false;
	public static final float CHI_threshold = 0.5f;//取CHI值排在前50%的特征
	public static final boolean CHI_FLAG = false;//是否使用CHI筛选特征
	public static final float DF_threshold = 0.6f;//取CHI值排在前50%的特征
	public static final boolean DF_FLAG = false;//是否使用DF筛选特征
	public static final boolean TFIDF_FLAG = false;//是否使用TFIDF表示特征,false时只用tf,true用tfidf
	
	public static final String CLASSIFIER_TYPE = "lg";
	public static final List<String> CLASSIFERS = new ArrayList<String>();

	static{
		

		//CLASSIFERS.add("Feature_Relation\\1635_Self+SFriAvgVec_skn5wcr100l100i15_3Train");
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
