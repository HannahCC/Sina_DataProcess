package org.cl.main.classifer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.cl.conf.Config;
import org.cl.model.ClassNode;
import org.cl.servies.GetCHI;
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
	static String res_dir = "Simple_Tag\\";
	public static void main(String[] args) throws IOException{
		/*Config.SrcPath_Root = args[0];
		Config.ResPath_Root = args[1];
		res_dir = args[2];*/
		Config.ResPath = Config.ResPath_Root+res_dir;
		SaveInfo.mkdir(Config.ResPath);
		GetUserFeature.getUserFeatureMap();
		/*-------普通情况，所有labels都进行比较-（默认CHI_threshold = 0.5;train_id_size=640）--------*/
		cross_validation("");
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
	private static void cross_validation(String dir) throws IOException{
		for(int i=0;i<Config.FOLD;i++){
			SaveInfo.saveResult("------------------------fold-"+i+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+dir+i+"//";
			SaveInfo.mkdir(Config.ResPath);
			GetIDF.getIDF(i,0);
			GetIDF.getIDF(i,1);
			GetCHI.getCHI(i);
			Map<Integer, ClassNode> label_map = GetTrainTestID.getTTID(i);
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
				SaveInfo.saveResult("---------------------label-"+Config.LABELS[0]+"vs"+Config.LABELS[1]+"-------------------");
				Config.ResPath = Config.ResPath_Root+res_dir+k+"//";
				SaveInfo.mkdir(Config.ResPath);
				cross_validation(k+"//");
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
			SaveInfo.saveResult("------------------------threshold-"+threshold+"--------------------");
			SaveInfo.mkdir(Config.ResPath_Root+k);
			int m = 0;
			for(int i=0;i<labels_copy.length;i++){
				Config.LABELS[0] = labels_copy[i];
				for(int j=i+1;j<labels_copy.length;j++){
					Config.LABELS[1] = labels_copy[j];
					SaveInfo.saveResult("---------------------label-"+Config.LABELS[0]+"vs"+Config.LABELS[1]+"-------------------");
					Config.ResPath = Config.ResPath_Root+res_dir+k+"//"+m+"//";
					SaveInfo.mkdir(Config.ResPath);
					cross_validation(k+"//"+m+"//");
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
			SaveInfo.saveResult("---------------------label-"+Config.LABELS[0]+"vs"+Config.LABELS[1]+"-------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+k+"//";
			SaveInfo.mkdir(Config.ResPath);
			cross_validation(k+"//");	
			k++;
		}
	}

	public static void allLabel_varCHI() throws IOException {
		float[] chi_threshold = {0.1f,0.15f,0.2f,0.25f,0.3f,0.35f,0.4f,0.45f,0.5f,0.55f,0.6f,0.65f,0.7f,0.75f,0.8f,0.85f,0.9f,0.95f,1.0f};
		int k = 0;
		for(float threshold : chi_threshold){
			GetCHI.CHI_threshold = threshold;
			SaveInfo.saveResult("------------------------threshold-"+threshold+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+k+"//";
			SaveInfo.mkdir(Config.ResPath);
			cross_validation(k+"\\");
			k++;
		}
	}

	public static void allLabel_varTrainSize() throws IOException {
		int k = 0;
		for(int size : Config.TRAIN_ID_SIZE_ARR){
			SaveInfo.saveResult("------------------------train_id_size-"+size+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+k+"//";
			SaveInfo.mkdir(Config.ResPath);
			cross_validation(k+"\\");
			k++;
		}
	}

}