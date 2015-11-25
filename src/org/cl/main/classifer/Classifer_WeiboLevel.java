package org.cl.main.classifer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.cl.conf.Config;
import org.cl.model.ClassNode;
import org.cl.servies.Cmd_Train;
import org.cl.servies.GetTrainTestData;
import org.cl.servies.GetTrainTestID;
import org.cl.utils.SaveInfo;


public class Classifer_WeiboLevel {
	/**
	 * 对微博级别的特征进行处理，每条微博作为一个向量来训练分类器。
	 * 可以进行使用单个分类器或多个分类器对用户进行分类，可以改变各参数
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException
	 */
	static String res_dir = "Simple_vecAll\\";
	public static void main(String[] args) throws IOException{
		Config.ResPath = res_dir;
		SaveInfo.mkdir(Config.ResPath);
		Map<String,Integer> classfiers = new HashMap<String,Integer>();
		classfiers.put("Feature_Behaviour", 14);
		GetTrainTestData.classifers_weibo_map = classfiers;
		/*---------------------------------diff_train_id_size---------------------------------*/
		/*int k = 0;
		for(int size : train_id_size){
			SaveInfo.saveResult("------------------------train_id_size-"+size+"--------------------");
			Config.ResPath = Config.ResPath_Root+k+"//";
			SaveInfo.mkdir(Config.ResPath);
			Services.train_id_size = size;
			cross_validation(k+"//");
			k++;
		}
		*/
		cross_validation("");
		SaveInfo.log_buff_writer(Config.ResPath_Root+res_dir,"res.txt");
	}
	
	
	public static void cross_validation(String dir) throws IOException{
		for(int i=0;i<Config.FOLD;i++){
			SaveInfo.option_log("------------------------fold-"+i+"--------------------");
			Config.ResPath = Config.ResPath_Root+res_dir+dir+i+"//";
			SaveInfo.mkdir(Config.ResPath);
			Map<Integer, ClassNode> label_map = GetTrainTestID.getTTID(i);
			GetTrainTestData.getTTData_WeiboLevel(label_map);
			Cmd_Train.train(Config.ResPath,"training_data");
		}
	}
}