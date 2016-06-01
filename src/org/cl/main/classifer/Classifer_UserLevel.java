package org.cl.main.classifer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.cl.conf.Config;
import org.cl.model.ClassNode;
import org.cl.servies.GetCHI;
import org.cl.servies.GetDF;
import org.cl.servies.GetIDF;
import org.cl.servies.GetTrainTestData;
import org.cl.servies.GetTrainTestID;
import org.cl.servies.GetUserFeature;
import org.cl.utils.SaveInfo;

public class Classifer_UserLevel {
	/**
	 * 对用户级别的特征进行处理，可以进行使用单个分类器或多个分类器对用户进行分类，可以改变各参数
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException
	 */
	static String res_dir = "Simple_Self+SFriAvgVec_skn5wcr100l100i15_3Train-test\\";
	public static void main(String[] args) throws IOException{
		Config.TRAIN_ID_SIZE = Integer.parseInt(args[0]);
		res_dir = args[1];
		for(int i=2;i<args.length;i++){
			Config.CLASSIFERS.add(args[i]);
		}
		
		Config.ResPath = Config.ResPath_Root+res_dir;
		SaveInfo.mkdir(Config.ResPath);
		//GetUserFeature.getUserFeatureMap();
		/*-------普通情况，所有labels都进行比较-（默认CHI_threshold = 0.5;train_id_size=640）--------*/
		//cross_validation(Config.TRAIN_ID_SIZE,"");
		/*---------------------------------每折用户的特征不相同-----------------------------------*/
		cross_validation_dynamicFeature(Config.TRAIN_ID_SIZE,"");//Config.TRAIN_ID_SIZE
		/*---------------------------------比较不同chi取值的情况---------------------------------*/
		//allLabel_varCHI();
		/*---------------------------------diff_train_id_size--------------------------------*/
		//allLabel_varTrainSize();
		//allLabel_varTrainSize_dynamicFeature();
		/*-----------------------------------1vs1--------------------------------------------*/
		//OnevsOne();
		/*-----------------------------------1vs1-varCHI-------------------------------------*/
		//OnevsOne_varCHI();
		/*-----------------------------------1vsall------------------------------------------*/
		//OnevsAll();

		SaveInfo.log_buff_writer(Config.ResPath_Root+res_dir,"res.txt");
	}
	private static void cross_validation(int train_id_size, String dir) throws IOException{
		for(int i=0;i<Config.FOLD;i++){
			SaveInfo.option_log("------------------------fold-"+i+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+dir+i+"//";
			SaveInfo.mkdir(Config.ResPath);
			GetIDF.getIDF(i,0);
			GetIDF.getIDF(i,1);
			GetDF.getDF(i);
			GetCHI.getCHI(i);
			Map<Integer, ClassNode> label_map = GetTrainTestID.getTTID(i, train_id_size);
			GetTrainTestData.getTTData_UserLevel(label_map);
		}
	}
	
	private static void cross_validation_dynamicFeature(int train_id_size, String dir) throws IOException{
		for(int i=0;i<Config.FOLD;i++){
			SaveInfo.option_log("------------------------fold-"+i+"--------------------");
			GetUserFeature.getUserFeatureMap(i);
			Config.ResPath = Config.ResPath_Root+res_dir+dir+i+"//";
			SaveInfo.mkdir(Config.ResPath);
			GetIDF.getIDF(i,0);
			GetIDF.getIDF(i,1);
			GetDF.getDF(i);
			GetCHI.getCHI(i);
			Map<Integer, ClassNode> label_map = GetTrainTestID.getTTID(i, train_id_size);
			GetTrainTestData.getTTData_UserLevel(label_map);
		}
	}

	public static void OnevsOne() throws IOException {
		int k = 0;
		int[] labels_copy = Arrays.copyOf(Config.LABELS, Config.LABELS.length);
		Config.LABELS = null;
		Config.LABELS = new int[2];
		for(int i=0;i<labels_copy.length;i++){
			Config.LABELS[0] = labels_copy[i];
			for(int j=i+1;j<labels_copy.length;j++){
				Config.LABELS[1] = labels_copy[j];
				SaveInfo.option_log("---------------------label-"+Config.LABELS[0]+"vs"+Config.LABELS[1]+"-------------------");
				Config.ResPath = Config.ResPath_Root+res_dir+k+"//";
				SaveInfo.mkdir(Config.ResPath);
				cross_validation(Config.TRAIN_ID_SIZE, k+"//");
				k++;
			}
		}
	}

	public static void OnevsOne_varCHI() throws IOException {
		float[] chi_threshold = {0.5f,0.55f,0.6f,0.65f,0.7f,0.75f,0.8f,0.85f,0.9f,0.95f};
		int k = 0;
		int[] labels_copy = Arrays.copyOf(Config.LABELS, Config.LABELS.length);
		Config.LABELS = null;
		Config.LABELS = new int[2];
		for(float threshold : chi_threshold){
			GetCHI.CHI_threshold = threshold;
			SaveInfo.option_log("------------------------threshold-"+threshold+"--------------------");
			SaveInfo.mkdir(Config.ResPath_Root+k);
			int m = 0;
			for(int i=0;i<labels_copy.length;i++){
				Config.LABELS[0] = labels_copy[i];
				for(int j=i+1;j<labels_copy.length;j++){
					Config.LABELS[1] = labels_copy[j];
					SaveInfo.option_log("---------------------label-"+Config.LABELS[0]+"vs"+Config.LABELS[1]+"-------------------");
					Config.ResPath = Config.ResPath_Root+res_dir+k+"//"+m+"//";
					SaveInfo.mkdir(Config.ResPath);
					cross_validation(Config.TRAIN_ID_SIZE, k+"//"+m+"//");
					m++;
				}
			}
			k++;
		}
	}

	public static void OnevsAll() throws IOException {
		int k = 0;
		int[] labels_copy = Arrays.copyOf(Config.LABELS, Config.LABELS.length);
		Config.LABELS = null;
		Config.LABELS = new int[2];
		for(int i=0;i<labels_copy.length;i++){
			Config.LABELS[0] = labels_copy[i];
			Config.LABELS[1] = Config.LABELS[0]*10+Config.LABELS[0];
			SaveInfo.option_log("---------------------label-"+Config.LABELS[0]+"vs"+Config.LABELS[1]+"-------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+k+"//";
			SaveInfo.mkdir(Config.ResPath);
			cross_validation(Config.TRAIN_ID_SIZE, k+"//");	
			k++;
		}
	}

	public static void allLabel_varCHI() throws IOException {
		float[] chi_threshold = {0.1f,0.15f,0.2f,0.25f,0.3f,0.35f,0.4f,0.45f,0.5f,0.55f,0.6f,0.65f,0.7f,0.75f,0.8f,0.85f,0.9f,0.95f,1.0f};
		int k = 0;
		for(float threshold : chi_threshold){
			GetCHI.CHI_threshold = threshold;
			SaveInfo.option_log("------------------------threshold-"+threshold+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+k+"//";
			SaveInfo.mkdir(Config.ResPath);
			cross_validation(Config.TRAIN_ID_SIZE, k+"\\");
			k++;
		}
	}

	public static void allLabel_varTrainSize() throws IOException {
		int k = 0;
		for(int size : Config.TRAIN_ID_SIZE_ARR){
			SaveInfo.option_log("------------------------train_id_size-"+size+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+k+"//";
			SaveInfo.mkdir(Config.ResPath);
			cross_validation(size, k+"\\");
			k++;
		}
	}
	
	public static void allLabel_varTrainSize_dynamicFeature() throws IOException {
		int k = 0;
		for(int size : Config.TRAIN_ID_SIZE_ARR){
			SaveInfo.option_log("------------------------train_id_size-"+size+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+k+"//";
			SaveInfo.mkdir(Config.ResPath);
			cross_validation_dynamicFeature(size, k+"\\");
			k++;
		}
	}
}