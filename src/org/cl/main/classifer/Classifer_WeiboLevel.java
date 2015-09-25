package org.cl.main.classifer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.cl.conf.Config;
import org.cl.model.ClassNode;
import org.cl.utils.SaveInfo;
import org.cl.utils.Services;


public class Classifer_WeiboLevel {
	/**
	 * 对微博级别的特征进行处理，每条微博作为一个向量来训练分类器。
	 * 可以进行使用单个分类器或多个分类器对用户进行分类，可以改变各参数
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException
	 */
	static int fold = 5;
	static int[] labels = {1,2,3,4};
	static int[] train_id_size = {480,320,160,80,40,20,40};
	static int number_of_weibo = 100;//每个用户获取前100个微博的行为特征
	static String res_dir = "Simple_vecAll\\";
	public static void main(String[] args) throws IOException{
		Config.ResPath = res_dir;
		SaveInfo.mkdir(Config.ResPath);
		Map<String,Integer> classfiers = new HashMap<String,Integer>();
		classfiers.put("Feature_Behaviour", 14);
		Services.classifers_weibo_map = classfiers;
		Services.train_id_size = 480;
		Services.number_of_weibo = number_of_weibo;
		/*---------------------------------diff_train_id_size---------------------------------*/
		/*int k = 0;
		for(int size : train_id_size){
			SaveInfo.saveResult("------------------------train_id_size-"+size+"--------------------");
			Config.ResPath = Config.ResPath_Root+k+"//";
			SaveInfo.mkdir(Config.ResPath);
			Services.train_id_size = size;
			cross_validation(fold,k);
			k++;
		}
		*/
		cross_validation(fold);
		SaveInfo.saveResult(Config.ResPath_Root+res_dir,"res.txt");
	}
	
	
	public static void cross_validation(int fold) throws IOException{
		for(int i=0;i<fold;i++){
			SaveInfo.saveResult("------------------------fold-"+i+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+i+"//";
			SaveInfo.mkdir(Config.ResPath);
			Services.fold_i = i;
			for(int li=0;li<labels.length;li++){
				int labelid = labels[li];
				SaveInfo.saveResult("--------------label-"+labelid+"-------------");
				ClassNode classnode = Services.getTTID(labelid);
				Services.getTTData_Behavior(li,classnode);
			}
		}
	}
	public static void cross_validation(int fold,int k) throws IOException{
		for(int i=0;i<fold;i++){
			SaveInfo.saveResult("------------------------fold-"+i+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+k+"//"+i+"//";
			SaveInfo.mkdir(Config.ResPath);
			Services.fold_i = i;
			for(int li=0;li<labels.length;li++){
				int labelid = labels[li];
				SaveInfo.saveResult("--------------label-"+labelid+"-------------");
				ClassNode classnode = Services.getTTID(labelid);
				Services.getTTData_Behavior(li,classnode);
			}
		}
	}
}