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

public class GetCHI {
	public final static Map<Integer,Map<String,Map<String, Double>>> CHI = new HashMap<Integer, Map<String,Map<String,Double>>>();//{LABEL:{CLASSFILENAME:{FETURE:CHI}}}
	public static float CHI_threshold = Config.CHI_threshold;
	static boolean CHI_FLAG = Config.CHI_FLAG;//是否使用CHI筛选特征
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
	/*-------------------------获取已经计算好的CHI（每种train_id_size、每类label，特征的chi值都不相同）----------------------------------------*/

	public static void getCHI(int fold_i) throws IOException{
		if(false==CHI_FLAG)return;
		CHI.clear();
		for(int li=1;li<=LABELS.length;li++){
			int labelid = LABELS[li-1];
			Map<String,Map<String, Double>> I_CHI = new HashMap<String,Map<String, Double>>();
			if(classifer_user_map==null||classifer_user_map.size()==0)return;
			Iterator<ClassiferNode> it = classifer_user_map.keySet().iterator();
			while(it.hasNext()){
				ClassiferNode classifer = it.next();
				Map<String, Double> feature_chi_map = new HashMap<String, Double>();
				String classifername = classifer.getClassifer_name();
				feature_chi_map = ReadInfo.getCHIMap(Config.TrainTestID+fold_i+"\\", TRAIN_ID_SIZE+"_"+COMBINATION+labelid+"_"+classifername+"chi.txt",":",0,1,CHI_threshold);
				if(feature_chi_map==null)return;//有些特征没有CHI值
				I_CHI.put(classifername, feature_chi_map);
			}
			CHI.put(labelid, I_CHI);
		}
	}

	/*-------------------------计算CHI（每种train_id_size、每类label，特征的chi值都不相同）----------------------------------------*/

	/**
	 * 由  训练用户 获取各类用户出现各特征的文档数，并根据CHI计算公式得到各个特征对于各类用户的CHI值
	 * CHI = (N*(AD-CB)^2)/((A+C)*(B+D)*(A+B)*(C+D))
	 * @param fold_i
	 * @param train_id_size
	 * @param lable_map  {labelID:{classifer_name:{feature:在训练用户中出现的文档数}}}
	 * @throws IOException 
	 */
	public static void calculateCHI(int fold_i,int train_id_size) throws IOException {
		//获取【每个类别】用户的【每个分类器】中【每个特征】在训练用户中出现的文档数
		Map<Integer,Map<String,Map<String, Integer>>> lable_map = getCHI_Pre(fold_i,train_id_size);
		int[] LABELS = Config.LABELS;
		//计算【每个类别】用户的【每个分类器】中【每个特征】的CHI值
		for(int labelid : LABELS){//不同类别用户
			Map<String, Map<String, Integer>> classifer_map = lable_map.get(labelid);
			for(ClassiferNode classifer : classifer_user_map.keySet()){
				String classifer_name = classifer.getClassifer_name();
				Map<String, Integer> post_feature_map = classifer_map.get(classifer_name);//得到当前类别用户文档中，各特征出现次数
				Map<String, Integer> nega_feature_map = getNega_FeatureMap(labelid,classifer_name,lable_map);//得到非当前类别用户文档中，各特征出现次数
				Map<String, Double> feature_chi_map = new HashMap<String, Double>();
				NormalizeCHI(train_id_size,post_feature_map,nega_feature_map,feature_chi_map);
				List<String> feature_chi_list = new ArrayList<String>();
				Utils.mapSortByValueDouble(feature_chi_list,feature_chi_map,post_feature_map);
				SaveInfo.saveList(Config.TrainTestID, fold_i+"\\"+train_id_size+"_"+COMBINATION+labelid+"_"+classifer_name+"chi.txt", feature_chi_list,feature_chi_list.size());
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
	private static Map<Integer,Map<String,Map<String, Integer>>> getCHI_Pre(int fold_i,int train_id_size) throws IOException {
		Map<Integer,Map<String,Map<String, Integer>>> lable_map = new HashMap<Integer,Map<String,Map<String, Integer>>>();
		int[] LABELS = Config.LABELS;
		for(int labelid : LABELS){//不同类别用户                              
			Map<String,Map<String, Integer>> classifer_map = new  HashMap<String,Map<String, Integer>>();
			Set<String> train_id_set = ReadInfo.getSet(Config.TrainTestID+fold_i+"\\", train_id_size+"_"+labelid+"_trainingid.txt");	
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
	/**
	 * 获取【classifer分类器】的各表情在【非labelid类用户】中出现的文档数
	 * @param labelid
	 * @param classifer
	 * @param lable_map
	 * @return
	 */
	private static Map<String, Integer> getNega_FeatureMap(int labelid, String classifer, Map<Integer, Map<String, Map<String, Integer>>> lable_map) {
		Map<String, Integer> feature_map = new HashMap<String, Integer>();//<特征编号,该特征出现在多少用户微博中>
		Iterator<Integer> it = lable_map.keySet().iterator();
		while(it.hasNext()){
			int labelid_i = it.next();
			if(labelid_i==labelid)continue;
			Map<String, Integer> feture_map_i = lable_map.get(labelid_i).get(classifer);
			Utils.mergeMap(feature_map, feture_map_i);
		}
		return feature_map;
	}
	/**
	 * 计算正类用户的特征列表中出现的特征的CHI值
	 * A =使用t表情的c类用户数
	 * B =使用t表情的非c类用户数
	 * C =不使用t表情的c类用户数
	 * D =不使用t表情的非c类用户数
	 * CHI = (N*(AD-CB)^2)/((A+C)*(B+D)*(A+B)*(C+D))
	 *  ||(溢出) 
	 * TMP = A*D - C*B
	 * CHI = (N/(A+C))*(TMP/(B+D))*(TMP/(A+B))*(1/(C+D))
	 */
	private static void NormalizeCHI(int train_id_size,Map<String, Integer> post_feature_map,
			Map<String, Integer> nega_feature_map,Map<String, Double> feature_chi_map) {
		int[] LABELS = Config.LABELS;
		double N = train_id_size*LABELS.length;
		Iterator<Entry<String, Integer>> post_it = post_feature_map.entrySet().iterator();
		while(post_it.hasNext()){
			Entry<String, Integer> entry = post_it.next();
			String index = entry.getKey();
			int count = entry.getValue();
			double A = count;
			double C = train_id_size - A;
			double B = 0;
			if(nega_feature_map.containsKey(index)){B = nega_feature_map.get(index);}
			double D = N-train_id_size-B;
			if(A+C==0||B+D==0||A+B==0||C+D==0){
				feature_chi_map.put(index,0.0);
			}else{
				double TMP = A*D - C*B;
				double chi =(N/(A+C))*(TMP/(B+D))*(TMP/(A+B))*(1/(C+D));
				if(chi<0){
					System.out.println(chi+"error!!!!!!!!!!!");
				}
				feature_chi_map.put(index,chi);
			}
		}
	}
}
