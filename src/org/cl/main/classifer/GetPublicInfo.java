package org.cl.main.classifer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.cl.conf.Config;
import org.cl.model.ClassNode;
import org.cl.model.ClassiferNode;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;
import org.cl.utils.Utils;

public class GetPublicInfo {
	static String PUBLIC_INFO = "Public_Info_Rel\\";//Public_Info_Behaviour\\   Public_Info_Style\\
	static String USERID = "UserID\\";//UserID_Behaviour\\ 
	static DecimalFormat CHIDF = new DecimalFormat("0.0000000000");
	static int FOLD = 5;
	static int WEIBO_NUMBER = 100;
	static int[] LABELS = {1,2};//类别
	static int ID_NUMBER = 500;//每类ID数量
	static int[] TRAIN_ID_SIZE = {400,350,300,250,200,150,100};//每类用户用于训练的ID数量。
	static int TEST_ID_SIZE = ID_NUMBER/FOLD;//每类用户用于测试的ID数量。
	
	static Map<ClassiferNode,Map<String,String>> CLASSIFERS_USER_MAP = null;
	static String[] CLASSIFERS = {"Feature_Tag\\Tag",
		"Feature_UserInfo\\Description",
		"Feature_Relation\\FolTag",
		"Feature_Relation\\FolDescription",
		"Feature_Relation\\OFSTag",
		"Feature_Relation\\OFSDescription",
		//"Feature_Relation\\FolType",
		//"Feature_Relation\\FriFolType"};
		/*,"Feature_SRC\\AppType"//,"Feature_SRC\\MobileType"
		,"Feature_Style\\Acronym","Feature_Style\\Buzz","Feature_Style\\Emotion","Feature_Style\\Modal","Feature_Style\\Punt",*/
		"Feature_Textual\\Text"/*,"Feature_Textual\\POS"*/
	};

	public static void main(String args[]) throws IOException{
		SaveInfo.mkdir(Config.ResPath_Root);
		SaveInfo.mkdir(Config.ResPath_Root+PUBLIC_INFO);
		SaveInfo.mkdir(Config.ResPath_Root+USERID);
		
		/*在特殊情况下获取训练和测试用户的ID
		GetTTID.tmp_getTrain_TestID(LABELS,0.8);
		GetTTID.tmp_getTrain_TestID("82");
		GetTTID.tmp_getTestID();*/
		/*----获取各情况下UserID---------*/
		for(int labelid : LABELS){
			//获取UserId
			//getUserID("UserID\\",labelid);//获取每类用户ID各ID_NUMBER个放在USERID_ROOT目录下
			//getUserIDWeibonumOverN(labelid);//获取每类CLASSFIERS特征都有的ID（通过getUserID()得到的ID）,且微博数超过WEIBO_NUMBER的用户,主要用户获取WeiboLevel特征时控制特征个数
		}
		
		/*----获取各情况下的测试、训练ID组---------*/
		for(int labelid : LABELS){
			getTrain_TestID(labelid);//从筛选过的ID中获取测试和训练用户ID组,得到[labelid]_testingid.txt， [size]_[labelid]_trainingid.txt存放在PUBLIC_INFO_ROOT/FOLD_i下
			//getTrain_TestID_1vsall(labelid);//获取除labelid以外的类用户的ID组成的800个ID，分成5组
		}
		
		/*----获取LABELS中所有类用户区分时的idf和chi------*/
		/*for(int i=0;i<FOLD;i++){
			for(int size : TRAIN_ID_SIZE){//不同train_size
				CLASSIFERS_USER_MAP = ReadInfo.getMap(CLASSIFERS,"_feature.txt");
				//getTF(i,size,1);//获取每个特征在每一类用户出现的总次数tf，并将各特征按tf降序排列，存储在[train_id_size]_[labelid]_[CLASSFIERS]tf.txt 或者 [train_id_size]_[非labelid]_[labelid]_[CLASSFIERS]tf.txt 
				//getCHI(i,size,getCHI_Pre(i,size));//根据chi求解公式求出每一类用户，每个特征的chi值，并将各特征按chi降序排列，存储在[train_id_size]_[labelid]_[CLASSFIERS]chi.txt 或者 [train_id_size]_[非labelid]_[labelid]_[CLASSFIERS]chi.txt 
				//getCHI_B(i,size);//将各特征按照出现在某类用户微博中的频次降序排列，存储在[train_id_size]_[labelid]_[CLASSFIERS]chib.txt中
				//getIDF(i,size,0);//根据各特征出现在测试用户文档集中的文档数，得到该特征的idf值，存储在[train_id_size]_[combination]_[classifername]_[typename]idf.txt中
				//getIDF(i,size,1);//根据各特征出现在训练用户文档集中的文档数，得到该特征的idf值
			}
		}
		*/
		/*----获取1vs1的idf和chi------*/
		/*for(int i=0;i<FOLD;i++){
			LABELS = new int[2];
			for(int k=1;k<=4;k++){
				LABELS[0] = k;
				for(int j=k+1;j<=4;j++){
					LABELS[1] = j;
					getCHI(i,640,getCHI_Pre(i,640));
					getIDF(i,640,0);
					getIDF(i,640,1);
				}
			}
		}*/
		/*----获取1vsall的idf和chi------*/
		/*for(int i=0;i<FOLD;i++){
			LABELS = new int[2];
			for(int k=1;k<=4;k++){
				LABELS[0] = k;
				LABELS[1] = k*10+k;
				getCHI(i,640,getCHI_Pre(i,640));
				getIDF(i,640,0);
				getIDF(i,640,1);
			}
		}*/
	}
	/*-------------------------------------------TF------------------------------------------------------*/
	//获取【每个类别】用户的【每个分类器】中【每个特征】在训练用户中出现的文档数
	public static void getTF(int fold_i,int train_id_size,int type) throws IOException {
		String combination = "";
		if(LABELS.length<4){
			for(int labelid : LABELS){
				combination+=labelid;
			}
			combination+="_";
		}
		String typename = "";
		if(type==0){typename="test";}
		else if(type==1){typename="train";}


		for(int labelid : LABELS){//不同类别用户                              
			Set<String> id_set = null;
			if(type==0){id_set = ReadInfo.getSet(Config.ResPath_Root+PUBLIC_INFO+fold_i+"\\", labelid+"_testingid.txt");	}
			else if(type==1){id_set = ReadInfo.getSet(Config.ResPath_Root+PUBLIC_INFO+fold_i+"\\", train_id_size+"_"+labelid+"_trainingid.txt");}
			
			Iterator<Entry<ClassiferNode, Map<String, String>>> it = CLASSIFERS_USER_MAP.entrySet().iterator();
			while(it.hasNext()){//不同特征中
				Map<String, Double> feature_map = new HashMap<String, Double>();//一类用户的一个分类器中，<特征编号,该特征出现在该类用户微博中多少次数>
				Entry<ClassiferNode, Map<String, String>> classifer = it.next();
				Map<String, String> user_feature_map = classifer.getValue();
				for(String id : id_set){
					if(!user_feature_map.containsKey(id)||user_feature_map.get(id).length()==0){SaveInfo.saveResult(id+"---没有特征----"+classifer.getKey());continue;}
					List<String> feature_list = Utils.stringFeaturetoList(user_feature_map.get(id));
					for(String feature:feature_list){
						String feature_index = feature.split(":")[0];
						double feature_count = Double.parseDouble(feature.split(":")[1]);
						Utils.putInMap(feature_map, feature_index, feature_count);//每个用户的文档中出现N次计数N次
					}
				}
				List<String> feature_list = new ArrayList<String>();
				Utils.mapSortByValue(feature_list, feature_map);
				SaveInfo.saveList(Config.ResPath_Root, PUBLIC_INFO+fold_i+"\\"+train_id_size+"_"+combination+labelid+"_"+classifer.getKey().getClassifer_name()+"_"+typename+"tf.txt", feature_list);
			}
		}
	}
	/*-------------------------------------------IDF------------------------------------------------------*/
	//type为1是文档集为训练用户文档集，type为0是文档集为测试用户文档集
	/**
	 * 根据各特征出现在文档集中的文档数，得到该特征的idf值（特征的区分能力，值越大，区分能力越好）
	 * @param fold_i
	 * @param train_id_size
	 * @param type
	 * @throws IOException 
	 */
	public static void getIDF(int fold_i,int train_id_size, int type) throws IOException {
		String typename = "";
		String combination = "";
		if(LABELS.length<4){
			for(int labelid : LABELS){
				combination+=labelid;
			}
			combination+="_";
		}
		Set<String> id_set = new TreeSet<String>();
		for(int labelid:LABELS){
			if(type==0){typename="test";ReadInfo.getSet(Config.ResPath_Root+PUBLIC_INFO+fold_i+"\\",labelid+"_testingid.txt",id_set);}
			else if(type==1){typename="train";ReadInfo.getSet(Config.ResPath_Root+PUBLIC_INFO+fold_i+"\\", train_id_size+"_"+labelid+"_trainingid.txt",id_set);}
		}

		double N = id_set.size();
		for(ClassiferNode classifer : CLASSIFERS_USER_MAP.keySet()){//不同分类器，如表情 <1000：11> 
			Map<String, String> user_feature_map = CLASSIFERS_USER_MAP.get(classifer);
			//获取【该分类器】中【每个特征】在文档集中出现的文档数
			Map<String, Integer> feature_map = new HashMap<String, Integer>();//<特征编号,该特征出现在多少训练用户的微博中>
			for(String id:id_set){//不同用户
				if(!user_feature_map.containsKey(id)||user_feature_map.get(id).length()==0){SaveInfo.saveResult(id+"---没有特征----"+classifer);continue;}
				List<String> feature_list = Utils.stringFeaturetoList(user_feature_map.get(id));//ReadInfo.getList(Config.SrcPath_Root,classifer.getClassifer_name()+"\\"+id+".txt","\t",0);
				for(String feature:feature_list){
					String feature_index = feature.split(":")[0];
					if(feature_map.containsKey(feature_index)){feature_map.put(feature_index, feature_map.get(feature_index)+1);
					}else{feature_map.put(feature_index, 1);}
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
			Utils.mapSortByValue(feature_idf_list, feature_idf_map);
			SaveInfo.saveList(Config.ResPath_Root, PUBLIC_INFO+fold_i+"\\"+train_id_size+"_"+combination+classifer.getClassifer_name()+"_"+typename+"idf.txt", feature_idf_list);
		}
	}
	/*-------------------------------------------CHI------------------------------------------------------*/
	/**
	 * 由  训练用户 获取各类用户出现各特征的文档数，并根据CHI计算公式得到各个特征对于各类用户的CHI值
	 * @param fold_i
	 * @param train_id_size
	 * @param lable_map
	 */
	public static void getCHI(int fold_i,int train_id_size,Map<Integer, Map<String, Map<String, Integer>>> lable_map) {
		String combination = "";
		if(LABELS.length<4){
			for(int labelid : LABELS){
				combination+=labelid;
			}
			combination+="_";
		}
		for(int labelid : LABELS){//不同类别用户
			Map<String, Map<String, Integer>> classifer_map = lable_map.get(labelid);
			for(ClassiferNode classifer : CLASSIFERS_USER_MAP.keySet()){
				String classifer_name = classifer.getClassifer_name();
				Map<String, Integer> post_feature_map = classifer_map.get(classifer_name);//得到当前类别用户文档中，各特征出现次数
				Map<String, Integer> nega_feature_map = getNega_FeatureMap(labelid,classifer_name,lable_map);//得到非当前类别用户文档中，各特征出现次数
				Map<String, Double> feature_chi_map = new HashMap<String, Double>();
				NormalizeCHIOfPost(train_id_size,post_feature_map,nega_feature_map,feature_chi_map);
				NormalizeCHIOfNega(train_id_size,post_feature_map,nega_feature_map,feature_chi_map);
				List<String> feature_chi_list = new ArrayList<String>();
				Utils.mapSortByValue(feature_chi_list, feature_chi_map,post_feature_map,CHIDF);
				SaveInfo.saveList(Config.ResPath_Root, PUBLIC_INFO+fold_i+"\\"+train_id_size+"_"+combination+labelid+"_"+classifer_name+"chi.txt", feature_chi_list,feature_chi_list.size());
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
	public static Map<Integer,Map<String,Map<String, Integer>>> getCHI_Pre(int fold_i,int train_id_size) throws IOException {
		Map<Integer,Map<String,Map<String, Integer>>> lable_map = new HashMap<Integer,Map<String,Map<String, Integer>>>();
		for(int labelid : LABELS){//不同类别用户                              
			Map<String,Map<String, Integer>> classifer_map = new  HashMap<String,Map<String, Integer>>();
			Set<String> train_id_set = ReadInfo.getSet(Config.ResPath_Root+PUBLIC_INFO+fold_i+"\\", train_id_size+"_"+labelid+"_trainingid.txt");	
			Iterator<Entry<ClassiferNode, Map<String, String>>> it = CLASSIFERS_USER_MAP.entrySet().iterator();
			while(it.hasNext()){
				Map<String, Integer> feature_map = new HashMap<String, Integer>();//一类用户的一个分类器中，<特征编号,该特征出现在该类多少用户微博中>
				Entry<ClassiferNode, Map<String, String>> classifer = it.next();
				Map<String, String> user_feature_map = classifer.getValue();
				for(String id : train_id_set){
					if(!user_feature_map.containsKey(id)||user_feature_map.get(id).length()==0){SaveInfo.saveResult(id+"---没有特征----"+classifer.getKey());continue;}
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
	private static void NormalizeCHIOfNega(int train_id_size,
			Map<String, Integer> post_feature_map,
			Map<String, Integer> nega_feature_map,
			Map<String, Double> feature_chi_map) {
		Iterator<Entry<String, Integer>> nega_it = nega_feature_map.entrySet().iterator();
		while(nega_it.hasNext()){
			Entry<String, Integer> entry = nega_it.next();
			String index = entry.getKey();
			if(feature_chi_map.containsKey(index))continue;//如果已经存在，说明正类中存在该特征，已经计算过其chi值
			int count = entry.getValue();
			double N = train_id_size*LABELS.length;
			double A = 0;
			double C = train_id_size;
			double B = count;
			double D = N-train_id_size-B;
			if(A+C==0||B+D==0||A+B==0||C+D==0){
				feature_chi_map.put(index,0.0);}
			else{
				double TMP = A*D - C*B;
				double chi =(N/(A+C))*(TMP/(B+D))*(TMP/(A+B))*(1/(C+D));
				if(chi<0){
					System.out.println(chi+"error!!!!!!!!!!!");
				}
				feature_chi_map.put(index, chi);
			}
		}
	}
	/**
	 * A =使用t表情的c类用户数
	 * B =使用t表情的非c类用户数
	 * C =不使用t表情的c类用户数
	 * D =不使用t表情的非c类用户数
	 */
	private static void NormalizeCHIOfPost(int train_id_size,Map<String, Integer> post_feature_map,
			Map<String, Integer> nega_feature_map,Map<String, Double> feature_chi_map) {
		Iterator<Entry<String, Integer>> post_it = post_feature_map.entrySet().iterator();
		while(post_it.hasNext()){
			Entry<String, Integer> entry = post_it.next();
			String index = entry.getKey();
			int count = entry.getValue();
			double N = train_id_size*LABELS.length;
			double A = count;
			double C = train_id_size - A;
			double B = 0;
			if(nega_feature_map.containsKey(index)){B = nega_feature_map.get(index);}
			double D = N-train_id_size-B;
			if(A+C==0||B+D==0||A+B==0||C+D==0){
				feature_chi_map.put(index,0.0);}
			else{
				double TMP = A*D - C*B;
				double chi =(N/(A+C))*(TMP/(B+D))*(TMP/(A+B))*(1/(C+D));
				if(chi<0){
					System.out.println(chi+"error!!!!!!!!!!!");
				}
				feature_chi_map.put(index, chi);
			}
		}
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
	/*-------------------------------------------CHI-B-----------------------------------------------------*/
	public static void getCHI_B(int fold_i, int train_id_size) throws IOException {
		String combination = "";
		if(LABELS.length<4){
			for(int labelid : LABELS){
				combination+=labelid;
			}
			combination+="_";
		}
		for(int labelid : LABELS){//不同类别用户                              
			Set<String> train_id_set = ReadInfo.getSet(Config.ResPath_Root+PUBLIC_INFO+fold_i+"\\", train_id_size+"_"+labelid+"_trainingid.txt");
			Iterator<Entry<ClassiferNode, Map<String, String>>> it = CLASSIFERS_USER_MAP.entrySet().iterator();
			while(it.hasNext()){
				Map<String, Integer> feature_map = new HashMap<String, Integer>();//一类用户的一个分类器中，<特征编号,该特征出现在该类多少用户微博中>
				Entry<ClassiferNode, Map<String, String>> classifer = it.next();
				Map<String, String> user_feature_map = classifer.getValue();
				for(String id : train_id_set){
					if(!user_feature_map.containsKey(id)||user_feature_map.get(id).length()==0){SaveInfo.saveResult(id+"---没有特征----"+classifer.getKey());continue;}
					List<String> feature_list = Utils.stringFeaturetoList(user_feature_map.get(id));
					for(String feature:feature_list){
						String feature_index = feature.split(":")[0];
						if(feature_map.containsKey(feature_index)){feature_map.put(feature_index, feature_map.get(feature_index)+1);
						}else{feature_map.put(feature_index, 1);}
					}
				}
				List<String> feature_list = new ArrayList<String>();
				Utils.mapSortByValueInteger(feature_list, feature_map);
				SaveInfo.saveList(Config.ResPath_Root, PUBLIC_INFO+fold_i+"\\"+train_id_size+"_"+combination+labelid+"_"+classifer.getKey().getClassifer_name()+"chib.txt", feature_list,feature_list.size());
			}
		}
	}
	/*----------------------------------------- Get Train_TestID--------------------------------------------------*/
	/**
	 * 从USERID_ROOT目录下获取labelid类用户ID，分成FOLD组，得到FOLD组测试用户ID和训练用户ID。每组测试用户和训练用户不会产生交叉
	 * 每组训练用户可以获取不同规模，规模大小存储在TRAIN_ID_SIZE数组中。 （规模最大为ID_NUMBER/FOLD）(取较小规模的ID时，顺序抽取前size个)
	 * 测试用户ID命名规则：[labelid]_testingid.txt   
	 * 训练用户ID命名规则：[size]_[labelid]_trainingid.txt
	 * @param labelid
	 * @throws IOException 
	 */
	public static void getTrain_TestID(int labelid) throws IOException {
		List<Set<String>> id_set_list = getIDSetList(labelid);//得到一类用户所有的ID，分为FOLD组，分别装在Set中。
		ClassNode classnode = new ClassNode(labelid,id_set_list);
		for(int i=0;i<FOLD;i++){  //每折使用其中1组作为测试，另外FOLD-1组作为训练
			SaveInfo.mkdir(Config.ResPath_Root+PUBLIC_INFO+i);
			classnode.setTesting_id_set(i);
			SaveInfo.id_writer(PUBLIC_INFO+i+"\\"+labelid+"_testingid"+".txt",classnode.getTesting_id_set());

			/*classnode.setTrainning_id_set(i);
			SaveInfo.id_writer(PUBLIC_INFO+i+"\\400_"+labelid+"_trainingid.txt",classnode.getTrainning_id_set());
			*/ 
			
			/*for(int n=1;n<FOLD;n++){  
				classnode.setTrainning_id_set_byfold(i, n);
				SaveInfo.id_writer(PUBLIC_INFO+i+"\\"+n+"00_"+labelid+"_trainingid.txt",classnode.getTrainning_id_set());
			}*/
			
			for(int size : TRAIN_ID_SIZE){  
				classnode.setTrainning_id_set_bynum(i, size);
				SaveInfo.id_writer(PUBLIC_INFO+i+"\\"+size+"_"+labelid+"_trainingid.txt",classnode.getTrainning_id_set());
			}
		}
	}
	/**
	 * 获取label类用户FOLD组测试用户ID和训练用户ID
	 * 每组训练用户可以获取不同规模，规模大小存储在TRAIN_ID_SIZE数组中。
	 * 测试用户ID命名规则：[labelid*10+labelid]_testingid.txt   如:11_testingid.txt
	 * 训练用户ID命名规则：[size]_[labelid*10+labelid]_trainingid.txt
	 * @param labelid
	 * @throws IOException 
	 */
	public static void getTrain_TestID_1vsall(int labelid) throws IOException {
		List<Set<String>> id_set_list = getIDSetList_1vsall(labelid);//得到非label类用户所有的ID，分为FOLD组，分别装在Set中。
		ClassNode classnode = new ClassNode(labelid,id_set_list);
		//ClassNode classnode = getIDSetList_1vsall(labelid);//得到的5组非label类用户ID
		labelid = labelid*10+labelid;
		for(int i=0;i<FOLD;i++){
			SaveInfo.mkdir(Config.ResPath_Root+PUBLIC_INFO+i);
			classnode.setTesting_id_set(i);
			SaveInfo.id_writer(PUBLIC_INFO+i+"\\"+labelid+"_testingid"+".txt",classnode.getTesting_id_set());
			for(int size : TRAIN_ID_SIZE){  
				classnode.setTrainning_id_set_bynum(i, size);
				SaveInfo.id_writer(PUBLIC_INFO+i+"\\"+size+"_"+labelid+"_trainingid.txt",classnode.getTrainning_id_set());
			}
		}
	}
	
	/*----------------------------------------UserID---------------------------------------------------------*/
	/**
	 * 从源数据文件中获取CLASSFIERS各个特征都有的UserID，一共ID_NUMBER个存放到USERID_ROOT目录下
	 * @param labelid
	 * @throws IOException 
	 */
	public static void getUserID(String srcDir,int labelid) throws IOException {
		Set<String> id_set = ReadInfo.getSet(Config.SrcPath_Root+srcDir,labelid+".txt");
		/*for(String classifer :CLASSIFERS){
			id_set = IdFilter(id_set,classifer);
		}*/
		System.out.println(labelid+"----"+id_set.size());
		SaveInfo.saveSet(Config.ResPath_Root+USERID,labelid+".txt",id_set,ID_NUMBER);
	}

	/**
	 * 获取USERID_ROOT中各类用户的ID，筛选出微博数超过WEIBO_NUMBER的用户，一共ID_NUMBER个存放到USERID_ROOT目录下
	 * @param labelid
	 * @throws IOException 
	 */
	public static void getUserIDWeibonumOverN(int labelid) throws IOException{
		List<String> idlist = ReadInfo.getList(Config.ResPath_Root+USERID,labelid+".txt");//各类特征都有的ID
		Set<String> newidlist = new HashSet<String>();
		for(String id :idlist){
			int size = ReadInfo.getNum(Config.SrcPath_Root, "WeibosCon\\"+id+".txt");
			if(size>=WEIBO_NUMBER){
				newidlist.add(id);
			}
		}
		System.out.println(newidlist.size());
		SaveInfo.saveSet(Config.ResPath_Root+USERID, labelid+".txt", newidlist,ID_NUMBER);
	}
	/*----------------------------------------UTILS---------------------------------------------------------*/
	/**
	 * 将id_set中不出现在feature目录下的id删除
	 * @param id_set
	 * @param feature
	 * @return
	 * @throws IOException 
	 */
	private static Set<String> IdFilter(Set<String> id_set, String feature) throws IOException {
		File f=new File(Config.SrcPath_Root+feature+"_feature.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = "";
		Set<String> new_id_set = new HashSet<String>();
		while((line = br.readLine())!=null){
			String id = line.split("\t")[0];
			if(id_set.contains(id))new_id_set.add(id);
		}
		br.close();
		System.out.println(new_id_set.size());
		return new_id_set;
	}
	/**
	 * 从Config.ResPath_Root/USERID_ROOT/labelid.txt中选取ID_NUMBER个ID，分成FOLD组
	 * @param labelid
	 * @param id_number
	 * @param fold
	 * @return
	 * @throws IOException 
	 */
	private static List<Set<String>> getIDSetList(int labelid) throws IOException {
		Set<String> id_set = ReadInfo.getSet(Config.ResPath_Root,USERID+labelid+".txt");
		//id_set = Utils.subSet(id_set, ID_NUMBER);//从id_set中随机取id_number个数据作为新的id_set，最多取id_set.size()个
		int size = id_set.size();
		List<Set<String>>id_set_list = Utils.spilt(id_set, FOLD);//将id_set分成fold组
		return id_set_list;
	}
	/**
	 * 从不是labelid类的各类用户中分别抽取一部分，组成非label类用户，同样分成FOLD组，存储在ClassNode中
	 * @param labelid
	 * @return
	 * @throws IOException 
	 */
	private static List<Set<String>> getIDSetList_1vsall(int labelid) throws IOException {
		int id_number_i = ID_NUMBER/(LABELS.length-1);// 800/3 = 266;
		//int size = 0;
		List<Set<String>>id_set_list = new ArrayList<Set<String>>();
		for(int labelid_i : LABELS){
			if(labelid_i==labelid){continue;}
			Set<String> id_set = ReadInfo.getSet(Config.ResPath_Root,USERID+labelid_i+".txt");
			id_set = Utils.subSet(id_set, id_number_i);//从id_set中随机取id_number个数据作为新的id_set，最多取id_set.size()个
			//size += id_set.size(); //266/5*5=265                                                                                          
			List<Set<String>>id_set_list_i = Utils.spilt(id_set, FOLD);//将id_set分成fold组,每组id_size = 266/5 = 53
			Utils.merge(id_set_list, id_set_list_i);//将5组ID，分别加到id_set_list中，循环之后id_set_list就共有5组数据，每组数据来自不同labelid_i的用户
		}
		return id_set_list;
		//return new ClassNode(labelid,id_set_list,size);
	}

}
