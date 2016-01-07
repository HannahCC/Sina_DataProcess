package org.cl.servies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.cl.conf.Config;
import org.cl.model.ClassiferNode;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;
import org.cl.utils.Utils;

public class GetIDF {
	public static final Map<String,Map<String, Double>> train_IDF = new HashMap<String,Map<String, Double>>();
	public static final Map<String,Map<String, Double>> test_IDF = new HashMap<String,Map<String, Double>>();
	static final boolean TFIDF_FLAG = Config.TFIDF_FLAG;//是否使用TFIDF表示特征,false时只用tf,true用tfidf
	static final int[] LABELS = Config.LABELS;
	static final int TRAIN_ID_SIZE = Config.TRAIN_ID_SIZE;//每类用户用于训练的ID数量。
	static final String COMBINATION = Config.COMBINATION;
	static Map<ClassiferNode,Map<String,String>> classifer_user_map = null;
	static{
		classifer_user_map = GetUserFeature.classifer_user_map;
		if(classifer_user_map.size()==0){
			System.out.println("classifer_user_map.size = 0!please execute GetUserFeature.getUserFeatureMap()");
		}
	}
	//获取各类别中各特征的idf值 ,type为1是文档集为训练用户文档集，type为0是文档集为测试用户文档集
	public static void getIDF(int fold_i, int type) throws IOException{
		if(false==TFIDF_FLAG)return;
		Map<String, Map<String, Double>> IDF  = null;
		String typename = null;
		if(type==0){IDF = test_IDF;typename = "test";}
		else if(type==1){IDF = train_IDF;typename = "train";}
		IDF.clear();
		if(classifer_user_map==null||classifer_user_map.size()==0)return;
		Iterator<ClassiferNode> it = classifer_user_map.keySet().iterator();
		while(it.hasNext()){
			ClassiferNode classifer = it.next();
			Map<String, Double> feature_idf_map = new HashMap<String, Double>();
			String classifername = classifer.getClassifer_name();
			feature_idf_map = ReadInfo.getMapDouble(Config.Public_Info+fold_i+"\\", TRAIN_ID_SIZE+"_"+COMBINATION+classifername+"_"+typename+"idf.txt",":",0,1);
			if(feature_idf_map==null)return;//有些特征没有IDF值
			IDF.put(classifername, feature_idf_map);
		}
	}
	
	/*-------------------------------------------计算IDF------------------------------------------------------*/
	//type为1是文档集为训练用户文档集，type为0是文档集为测试用户文档集
	/**
	 * 根据各特征出现在文档集中的文档数，得到该特征的idf值（特征的区分能力，值越大，区分能力越好）
	 * idf = log(|D|/|j:ti∈dj|)其中，D表示用户总数；|j:ti∈dj|表示出现表情i的用户数目.(一个词在所有用户文档都出现，即idf=0)
	 * @param fold_i
	 * @param train_id_size
	 * @param type
	 * @throws IOException 
	 */
	public static void calculateIDF(int fold_i,int train_id_size, int data_type) throws IOException {
		String typename = "";
		Set<String> id_set = new TreeSet<String>();
		for(int labelid:LABELS){
			if(data_type==0){typename="test";ReadInfo.getSet(Config.Public_Info+fold_i+"\\",labelid+"_testingid.txt",id_set);}
			else if(data_type==1){typename="train";ReadInfo.getSet(Config.Public_Info+fold_i+"\\", train_id_size+"_"+labelid+"_trainingid.txt",id_set);}
		}

		double N = id_set.size();
		for(ClassiferNode classifer : classifer_user_map.keySet()){//不同分类器，如表情 <1000：11> 
			Map<String, String> user_feature_map = classifer_user_map.get(classifer);
			//获取【该分类器】中【每个特征】在文档集中出现的文档数
			Map<String, Integer> feature_map = new HashMap<String, Integer>();//<特征编号,该特征出现在多少训练用户的微博中>
			for(String id:id_set){//不同用户
				if(!user_feature_map.containsKey(id)||user_feature_map.get(id).length()==0){SaveInfo.option_log(id+"---没有特征----"+classifer);continue;}
				List<String> feature_list = Utils.stringFeaturetoList(user_feature_map.get(id));//ReadInfo.getList(Config.SrcPath_Root,classifer.getClassifer_name()+"\\"+id+".txt","\t",0);
				for(String feature:feature_list){
					String feature_index = feature.split(":")[0];
					Utils.putInMap(feature_map, feature_index, 1);//每个用户的文档中出现N次只计数一次
				}
			}
			//求每个特征的idf值 idf = log(N/J) 其中N为文档总数，J为特征出现的文档数
			Map<String, Double> feature_idf_map = new HashMap<String, Double>();
			Iterator<Entry<String, Integer>> it = feature_map.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, Integer> entry = it.next();
				String index = entry.getKey();
				double J = entry.getValue();
				double idf = Math.log(N/J);
				feature_idf_map.put(index, idf);
			}
			List<String> feature_idf_list = new ArrayList<String>();
			Utils.mapSortByValueDouble(feature_idf_list, feature_idf_map);
			SaveInfo.saveList(Config.Public_Info,fold_i+"\\"+train_id_size+"_"+COMBINATION+classifer.getClassifer_name()+"_"+typename+"idf.txt", feature_idf_list);
		}
	}
}
