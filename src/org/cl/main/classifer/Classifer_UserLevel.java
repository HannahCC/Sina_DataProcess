package org.cl.main.classifer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cl.conf.Config;
import org.cl.model.ClassNode;
import org.cl.model.ClassiferNode;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;
import org.cl.utils.Services;


public class Classifer_UserLevel {
	/**
	 * 对用户级别的特征进行处理，可以进行使用单个分类器或多个分类器对用户进行分类，可以改变各参数
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException
	 */
	static int fold = 5;
	static double CHI_threshold = 0.75;//取CHI值排在前50%的特征
	static String CHI_TYPE = "";//type=""则用chi作为标准，若type="b"则用tf作为筛选标准
	static boolean CHI_FLAG = false;//是否使用CHI筛选特征
	static boolean TFIDF_FLAG = false;//是否使用TFIDF表示特征,false时只用tf,true用tfidf
	static int[] labels = {1,2};
	static int[] train_id_size_arr = {400,350,300,250,200,150,100};
	static int train_id_size = 400;
	static String res_dir = "Simple_Description\\";
	public static void main(String[] args) throws IOException{
		List<String> classifers = new ArrayList<String>();
		
		/*Config.SrcPath_Root = args[0];
		Config.ResPath_Root = args[1];
		res_dir = args[2];*/
		Config.ResPath = Config.ResPath_Root+res_dir;
		SaveInfo.mkdir(Config.ResPath);
		Services.LEARN_FLAG = true;
		
		/*for(int i=3;i<args.length;i++){
			classifers.add(args[i]);
		}*/
		//classifers.add("Feature_UserInfo\\Tag");
		classifers.add("Feature_UserInfo\\Description");
		//classifers.add("Feature_UserInfo\\Tag_AvgVecIn18w");
		//classifers.add("Feature_UserInfo\\Description_AvgVecIn18w");
		//classifers.add("Feature_Relation\\Fri_Fol_Tag");
		//classifers.add("Feature_Relation\\Fri_Fol_Description");
		//classifers.add("Feature_Relation\\Line_vec_all");
		//classifers.add("Feature_Relation\\Line6_desc_tag_Conc_18w_vec_all");
		Map<ClassiferNode,Map<String,String>> classifer_user_map = ReadInfo.getMap(classifers,"_feature.txt");
		Services.classifer_user_map = classifer_user_map;
		/*-------普通情况，所有labels都进行比较-（默认CHI_threshold = 0.5;train_id_size=640）--------*/
		cross_validation(fold);
		/*---------------------------------比较不同chi取值的情况---------------------------------*/
		//allLabel_varCHI();
		/*---------------------------------diff_train_id_size--------------------------------*/
		//allLabel_varTrainSize();
		/*-----------------------------------1vs1--------------------------------------------*/
		//OnevsOne();
		/*-----------------------------------1vs1-varCHI-------------------------------------*/
		//OnevsOne_varCHI();
		/*-----------------------------------1vsall------------------------------------------*/
		//OnevsAll();

		SaveInfo.saveResult(Config.ResPath_Root+res_dir,"res.txt");
	}
	public static void cross_validation(int fold) throws IOException{
		for(int i=0;i<fold;i++){
			SaveInfo.saveResult("------------------------fold-"+i+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+i+"//";
			SaveInfo.mkdir(Config.ResPath);
			if(TFIDF_FLAG){
				Services.getIDF(i,train_id_size,0,"");
				Services.getIDF(i,train_id_size,1,"");
			}
			for(int li=1;li<=labels.length;li++){
				int labelid = labels[li-1];
				SaveInfo.saveResult("--------------label-"+labelid+"-------------");
				ClassNode classnode = Services.getTTID(i,train_id_size,labelid);
				if(CHI_FLAG)Services.getCHI(i,train_id_size,labelid,CHI_threshold,CHI_TYPE,"");
				Services.getTTData(li,classnode);
			}
		}
	}


	public static void OnevsOne() throws IOException {
		int k = 0;
		for(int i=0;i<labels.length;i++){
			int labelid1 = labels[i];
			for(int j=i+1;j<labels.length;j++){
				int labelid2 = labels[j];
				SaveInfo.saveResult("---------------------label-"+labelid1+"vs"+labelid2+"-------------------");
				Config.ResPath = Config.ResPath_Root+res_dir+k+"//";
				SaveInfo.mkdir(Config.ResPath);
				cross_validation(k+"//",labelid1,labelid2);
				k++;
			}
		}
	}

	public static void OnevsOne_varCHI() throws IOException {
		double[] chi_threshold = {0.5,0.55,0.6,0.65,0.7,0.75,0.8,0.85,0.9,0.95};
		int k = 0;
		for(double threshold : chi_threshold){
			CHI_threshold = threshold;
			SaveInfo.saveResult("------------------------threshold-"+threshold+"--------------------");
			SaveInfo.mkdir(Config.ResPath_Root+k);
			int m = 0;
			for(int i=0;i<labels.length;i++){
				int labelid1 = labels[i];
				for(int j=i+1;j<labels.length;j++){
					int labelid2 = labels[j];
					SaveInfo.saveResult("---------------------label-"+labelid1+"vs"+labelid2+"-------------------");
					SaveInfo.mkdir(Config.ResPath_Root+k+"//"+m+"//");
					cross_validation(k+"//"+m+"//",labelid1,labelid2);
					m++;
				}
			}
			k++;
		}
	}

	public static void OnevsAll() throws IOException {
		int k = 0;
		for(int i=0;i<labels.length;i++){
			int labelid1 = labels[i];
			int labelid2 = labelid1*10+labelid1;
			SaveInfo.saveResult("---------------------label-"+labelid1+"vs"+labelid2+"-------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+k+"//";
			SaveInfo.mkdir(Config.ResPath);
			cross_validation(k+"//",labelid1,labelid2);
			k++;
		}
	}

	public static void allLabel_varCHI() throws IOException {
		double[] chi_threshold = {0.1,0.15,0.2,0.25,0.3,0.35,0.4,0.45,0.5,0.55,0.6,0.65,0.7,0.75,0.8,0.85,0.9,0.95,1.0};
		int k = 0;
		for(double threshold : chi_threshold){
			CHI_threshold = threshold;
			SaveInfo.saveResult("------------------------threshold-"+threshold+"--------------------");
			SaveInfo.mkdir(Config.ResPath_Root+k);
			cross_validation(5,k);
			k++;
		}
	}

	public static void allLabel_varTrainSize() throws IOException {
		int k = 0;
		for(int size : train_id_size_arr){
			SaveInfo.saveResult("------------------------train_id_size-"+size+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+k+"//";
			SaveInfo.mkdir(Config.ResPath);
			train_id_size = size;
			cross_validation(fold,k);
			k++;
		}
	}

	/*----------------------------------比较试验-------------------------------------------*/
	private static void cross_validation(int fold,int k) throws IOException{
		for(int i=0;i<fold;i++){
			SaveInfo.saveResult("------------------------fold-"+i+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+k+"//"+i+"//";
			SaveInfo.mkdir(Config.ResPath);
			if(TFIDF_FLAG){
				Services.getIDF(i,train_id_size,0,"");
				Services.getIDF(i,train_id_size,1,"");
			}
			for(int li=1;li<=labels.length;li++){
				int labelid = labels[li-1];
				SaveInfo.saveResult("--------------label-"+labelid+"-------------");
				ClassNode classnode = Services.getTTID(i,train_id_size,labelid);
				Services.getCHI(i,train_id_size,labelid,CHI_threshold,CHI_TYPE,"");
				Services.getTTData(li,classnode);
			}
		}
	}

	private static void cross_validation(String dir,int ...labels) throws IOException{
		String combination = "";
		if(labels.length<4){
			for(int labelid : labels){
				combination+=labelid;
			}
			combination+="_";
		}
		for(int i=0;i<fold;i++){
			SaveInfo.saveResult("------------------------fold-"+i+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+dir+i+"//";
			SaveInfo.mkdir(Config.ResPath);
			if(TFIDF_FLAG){
				Services.getIDF(i,train_id_size,0,combination);
				Services.getIDF(i,train_id_size,1,combination);
			}
			for(int li=0;li<labels.length;li++){
				int labelid = labels[li];
				SaveInfo.saveResult("--------------label-"+labelid+"-------------");
				ClassNode classnode = Services.getTTID(i,train_id_size,labelid);
				Services.getCHI(i,train_id_size,labelid,CHI_threshold,CHI_TYPE,combination);
				Services.getTTData(li,classnode);
			}
		}
	}

}