package org.cl.servies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.cl.conf.Config;
import org.cl.model.ClassiferNode;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;
import org.cl.utils.Utils;

public class GetDF {
	public final static Map<Integer,Map<String,Map<String, Double>>> DF = new HashMap<Integer, Map<String,Map<String,Double>>>();//{LABEL:{CLASSFILENAME:{FETURE:DF}}}
	public static float DF_threshold = Config.DF_threshold;
	static boolean DF_FLAG = Config.DF_FLAG;//是否使用DF筛选特征
	static int[] LABELS = Config.LABELS;
	static int TRAIN_ID_SIZE = Config.TRAIN_ID_SIZE;//每类用户用于训练的ID数量。
	static String COMBINATION = Config.COMBINATION;
	static Map<ClassiferNode,Map<String,String>> classifer_user_map = null;
	static{
		classifer_user_map = GetUserFeature.classifer_user_map;
		if(classifer_user_map.size()==0){
			System.out.println("classifer_user_map.size = 0!please execute GetUserFeature.getUserFeatureMap()");
		}
	}
	/*-------------------------获取已经计算好的DF（每种train_id_size、每类label，特征的DF值都不相同）----------------------------------------*/
	public static void getDF(int fold_i) throws IOException{
		if(false==DF_FLAG)return;
		DF.clear();
		for(int li=1;li<=LABELS.length;li++){
			int labelid = LABELS[li-1];
			Map<String,Map<String, Double>> I_DF = new HashMap<String,Map<String, Double>>();
			if(classifer_user_map==null||classifer_user_map.size()==0)return;
			Iterator<ClassiferNode> it = classifer_user_map.keySet().iterator();
			while(it.hasNext()){
				ClassiferNode classifer = it.next();
				Map<String, Double> feature_chi_map = new HashMap<String, Double>();
				String classifername = classifer.getClassifer_name();
				feature_chi_map = ReadInfo.getCHIMap(Config.Public_Info+fold_i+"\\", TRAIN_ID_SIZE+"_"+COMBINATION+labelid+"_"+classifername+"df.txt",":",0,1,DF_threshold);
				if(feature_chi_map==null)return;//有些特征没有CHI值
				I_DF.put(classifername, feature_chi_map);
			}
			DF.put(labelid, I_DF);
		}
	}

	/*-------------------------计算DF（每种train_id_size、每类label，特征的DF值都不相同）----------------------------------------*/

	/**
	 * 由  训练用户 获取各类用户出现各特征的文档数，并根据DF计算公式得到各个特征对于各类用户的DF值
	 * DF = (N*(AD-CB)^2)/((A+C)*(B+D)*(A+B)*(C+D))
	 * @param fold_i
	 * @param train_id_size
	 * @param lable_map  {labelID:{classifer_name:{feature:在训练用户中出现的文档数}}}
	 * @throws IOException 
	 */
	public static void calculateDF(int fold_i,int train_id_size) throws IOException {
		//获取【每个类别】用户的【每个分类器】中【每个特征】在训练用户中出现的文档数
		Map<Integer,Map<String,Map<String, Integer>>> lable_map = getDF_Pre(fold_i,train_id_size);
		int[] LABELS = Config.LABELS;
		//计算【每个类别】用户的【每个分类器】中【每个特征】的DF值(即出现次数)
		for(int labelid : LABELS){//不同类别用户
			Map<String, Map<String, Integer>> classifer_map = lable_map.get(labelid);
			for(ClassiferNode classifer : classifer_user_map.keySet()){
				String classifer_name = classifer.getClassifer_name();
				Map<String, Integer> feature_DF_map = classifer_map.get(classifer_name);//得到当前类别用户文档中，各特征出现次数
				List<String> feature_DF_list = new ArrayList<String>();
				Utils.mapSortByValueInteger(feature_DF_list,feature_DF_map);
				SaveInfo.saveList(Config.Public_Info, fold_i+"\\"+train_id_size+"_"+COMBINATION+labelid+"_"+classifer_name+"df.txt", feature_DF_list,feature_DF_list.size());
			}
		}
	}
	/**
	 * 获取【每个类别】用户的【每个分类器】中【每个特征】在训练用户中出现的文档数
	 * @param fold_i
	 * @param train_id_size
	 * @return
	 * @throws IOException
	 */
	private static Map<Integer,Map<String,Map<String, Integer>>> getDF_Pre(int fold_i,int train_id_size) throws IOException {
		Map<Integer,Map<String,Map<String, Integer>>> lable_map = new HashMap<Integer,Map<String,Map<String, Integer>>>();
		int[] LABELS = Config.LABELS;
		for(int labelid : LABELS){//不同类别用户                              
			Map<String,Map<String, Integer>> classifer_map = new  HashMap<String,Map<String, Integer>>();
			Set<String> train_id_set = ReadInfo.getSet(Config.Public_Info+fold_i+"\\", train_id_size+"_"+labelid+"_trainingid.txt");	
			Iterator<Entry<ClassiferNode, Map<String, String>>> it = classifer_user_map.entrySet().iterator();//{classifer_name:{uid:feature}}
			while(it.hasNext()){
				Map<String, Integer> feature_map = new HashMap<String, Integer>();//一类用户的一个分类器中，<特征编号,该特征出现在该类多少用户微博中>
				Entry<ClassiferNode, Map<String, String>> classifer = it.next();
				Map<String, String> user_feature_map = classifer.getValue();
				for(String id : train_id_set){
					if(!user_feature_map.containsKey(id)||user_feature_map.get(id).length()==0){SaveInfo.option_log(id+"---没有特征----"+classifer.getKey());continue;}
					List<String> feature_list = Utils.stringFeaturetoList(user_feature_map.get(id));
					for(String feature:feature_list){
						String feature_index = feature.split(":")[0];
						Utils.putInMap(feature_map, feature_index, 1);//每个用户的文档中出现N次只计数一次
					}
				}
				classifer_map.put(classifer.getKey().getClassifer_name(), feature_map);
			}
			lable_map.put(labelid, classifer_map);
		}
		return lable_map;
	}



}
