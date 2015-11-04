package org.cl.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.cl.conf.Config;
import org.cl.model.ClassNode;
import org.cl.model.ClassiferNode;

public class Services {
	//public static int fold_i = -1;
	//public static int train_id_size = 0;
	public static int WEIBO_NUMBER = 0;//微博级别的分类器，控制微博数量
	public static boolean LEARN_FLAG = false;//控制是否载入学习数据
	public static Map<String, Integer> classifers_weibo_map = null;
	public static Map<ClassiferNode,Map<String,String>> classifer_user_map = null;
	public static Map<String,Map<String, Double>> CHI = null;
	public static Map<String,Map<String, Double>> train_IDF = null;
	public static Map<String,Map<String, Double>> test_IDF = null;
	//获取classnode的训练和测试ID
	public static ClassNode getTTID(int fold_i, int train_id_size, int labelid) throws IOException {
		Set<String> train_id_set = ReadInfo.getSet(Config.Public_Info,fold_i+"\\"+train_id_size+"_"+labelid+"_trainingid.txt");
		Set<String> test_id_set = ReadInfo.getSet(Config.Public_Info,fold_i+"\\"+labelid+"_testingid.txt");
		ClassNode myclassNode = new ClassNode(labelid,train_id_set,test_id_set);
		if(LEARN_FLAG){
			Set<String> learn_id_set = ReadInfo.getSet(Config.ResPath_Root,"UserID\\L"+labelid+".txt");
			myclassNode.setLearning_id_set(learn_id_set);
		}
		return myclassNode;
	}

	public static ClassNode getTTID_TriTraining(int fold_i, int train_id_size, int labelid) throws IOException {
		Set<String> test_id_set = ReadInfo.getSet(Config.Public_Info,fold_i+"\\"+labelid+"_testingid.txt");
		Set<String> id_set = ReadInfo.getSet(Config.Public_Info,fold_i+"\\"+train_id_size+"_"+labelid+"_trainingid.txt");
		List<Set<String>> id_set_list = Utils.spilt(id_set, 0.25);
		return new ClassNode(labelid,id_set_list.get(0),test_id_set,id_set_list.get(1));
	}

	public static void getTestID_TriTraining(int fold_i, ClassNode classnode) throws IOException {
		int labelid = classnode.getClassid();
		Set<String> test_id_set = ReadInfo.getSet(Config.Public_Info,fold_i+"\\"+labelid+"_testingid.txt");
		classnode.setTesting_id_set(test_id_set);
	}
	//type=""则用chi作为标准，若type="b"则用tf作为筛选标准
	public static void getCHI(int fold_i, int train_id_size, int lableid, double CHI_threshold,String type,String combination) throws IOException{
		CHI = new HashMap<String,Map<String, Double>>();
		if(classifer_user_map==null||classifer_user_map.size()==0)return;
		Iterator<ClassiferNode> it = classifer_user_map.keySet().iterator();
		while(it.hasNext()){
			ClassiferNode classifer = it.next();
			Map<String, Double> feature_chi_map = new HashMap<String, Double>();
			String classifername = classifer.getClassifer_name();
			feature_chi_map = ReadInfo.getCHIMap(Config.Public_Info+fold_i+"\\", train_id_size+"_"+combination+lableid+"_"+classifername+"chi"+type+".txt",":",0,1,CHI_threshold);
			if(feature_chi_map==null)return;//有些特征没有CHI值
			CHI.put(classifername, feature_chi_map);
		}
	}
	//获取各类别中各特征的idf值 ,type为1是文档集为训练用户文档集，type为0是文档集为测试用户文档集
	public static void getIDF(int fold_i, int train_id_size, int type,String combination) throws IOException{
		Map<String, Map<String, Double>> IDF  = null;
		String typename = null;
		if(type==0){IDF = test_IDF;typename = "test";}
		else if(type==1){IDF = train_IDF;typename = "train";}
		IDF = new HashMap<String,Map<String, Double>>();
		if(classifer_user_map==null||classifer_user_map.size()==0)return;
		Iterator<ClassiferNode> it = classifer_user_map.keySet().iterator();
		while(it.hasNext()){
			ClassiferNode classifer = it.next();
			Map<String, Double> feature_idf_map = new HashMap<String, Double>();
			String classifername = classifer.getClassifer_name();
			feature_idf_map = ReadInfo.getMapDouble(Config.Public_Info+fold_i+"\\", train_id_size+"_"+combination+classifername+"_"+typename+"idf.txt",":",0,1);
			if(feature_idf_map==null)return;//有些特征没有IDF值
			IDF.put(classifername, feature_idf_map);
		}
	}

	//获取classnode的训练和测试数据（训练用户和测试用户ID已知）
	public static void getTTData(int lableid_index,ClassNode classnode) {
		//获取具体数据      
		SaveInfo.saveResult("------------------------Getting Testing Data--------------------");
		getData_UserLevel(lableid_index,classnode.getTesting_id_set(),0);//获取测试数据
		getData_UserLevel(lableid_index,classnode.getTesting_id_set_fake(),2);//获取测试Fake数据
		SaveInfo.saveResult("------------------------Getting Trainning Data--------------------");
		getData_UserLevel(lableid_index,classnode.getTrainning_id_set(),1);//获取训练数据
		if(LEARN_FLAG){
			SaveInfo.saveResult("------------------------Getting Learning Data--------------------");
			getData_UserLevel(lableid_index,classnode.getLearning_id_set(),3);//获取学习数据
		}
	}

	public static void getTTData_Behavior(int lableid_index,ClassNode classnode) throws IOException {
		//获取具体数据      
		SaveInfo.saveResult("------------------------Getting Testing Data--------------------");
		getData_WeiboLevel(lableid_index,classnode.getTesting_id_set(),0);//获取测试数据
		SaveInfo.saveResult("------------------------Getting Trainning Data--------------------");
		getData_WeiboLevel(lableid_index,classnode.getTrainning_id_set(),1);//获取训练数据
		if(LEARN_FLAG){
			SaveInfo.saveResult("------------------------Getting Learning Data--------------------");
			getData_WeiboLevel(lableid_index,classnode.getLearning_id_set(),1);//获取学习数据
		}
	}

	/*-------------------------------------Private-----------------------------------------------------------------*/
	//获取id_set的classifers特征的数据（每个特征在一定编号范围内）type表示data类别，0表示测试数据，1表示训练数据, 2表示测试Fake数据,3表示学习数据
	private static void getData_UserLevel(int lableid_index, Set<String> id_set,int type) {
		if(id_set==null||id_set.size()==0)return;
		int start_index = 1;//每一类正常特征的起始编码值
		Map<String,StringBuffer> id_feature = new TreeMap<String,StringBuffer>();
		Iterator<Entry<ClassiferNode, Map<String, String>>> it = classifer_user_map.entrySet().iterator();
		while(it.hasNext()){
			Entry<ClassiferNode, Map<String, String>> classifer = it.next();
			Map<String, String> feature_map = classifer.getValue();
			for(String id : id_set){
				if(!feature_map.containsKey(id)||feature_map.get(id).length()==0){SaveInfo.saveResult(id+"---没有特征----"+classifer.getKey());continue;}
				List<String> feature_list = Utils.stringFeaturetoList(feature_map.get(id));
				//进行特征选择，当未获取该Classifer的各特征的CHI时不会进行特征选择
				if(type==1){//训练用户才能使用CHI进行筛选，对测试用户若使用CHI，即隐式的使用了训练用户的信息。因为每次筛选时是根据测试用户类别选择所用的CHI列表，即我们提前知道了测试用户的类别。
					getSelectedFeatureByCHI(classifer.getKey().getClassifer_name(),feature_list);
				}
				//将各特征表示成tfidf值
				//getFrequency(feature_list);//将对应特征的值由原来的频次改为频率
				getTFIDF(classifer.getKey().getClassifer_name(),feature_list,type);

				String reencoded_feature = Utils.reencode(feature_list,start_index);//从start_index开始重新编码，并按编码大小从小到大排序
				Utils.putInMap(id_feature, id, reencoded_feature);
			}
			start_index += classifer.getKey().getClassifer_size();
		}
		SaveInfo.data_writer(lableid_index,id_feature,type);
	}

	private static void getData_WeiboLevel(int lableid_index,Set<String> id_set, int type) throws IOException {
		if(id_set==null||id_set.size()==0)return;
		int normalEncode = 1;//每一类正常特征的起始编码值
		Map<String,StringBuffer> id_feature = new TreeMap<String,StringBuffer>();
		Iterator<Entry<String, Integer>> it = classifers_weibo_map.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Integer> classifer = it.next();
			for(String id:id_set){//得到一个ID的所有classifers特征的数据
				List<List<String>> feature_list_list = ReadInfo.getListList(Config.SrcPath_Root,classifer.getKey()+"\\"+id+".txt",WEIBO_NUMBER);
				if(feature_list_list.size()==0){System.out.println(id+"---NO"+classifer);continue;}
				int i = 0;//用户每条微博作为一个向量
				for(List<String> feature_list:feature_list_list){
					String id_i = id + (i++);
					Utils.reencode(feature_list,normalEncode);//从normalEncode开始重新编码，并按编码大小从小到大排序
					if(id_feature.containsKey(id_i)){id_feature.put(id_i, id_feature.get(id_i).append(Utils.lists_ToString(feature_list)));}
					else{id_feature.put(id_i, new StringBuffer(Utils.lists_ToString(feature_list)));}
				}
			}
			normalEncode += classifer.getValue();
		}
		SaveInfo.data_writer(lableid_index,id_feature,type);
	}


	private static void getSelectedFeatureByCHI(String classifer,List<String> feature_list) {
		if(CHI==null||!CHI.containsKey(classifer))return;
		Map<String, Double> CHI_map = CHI.get(classifer);//[111:0.345,...]
		List<String> new_feature_list = new ArrayList<String>();
		for(String feature:feature_list){
			String index = feature.split(":")[0];
			if(CHI_map.containsKey(index)){new_feature_list.add(feature);}
		}
		feature_list.clear();
		feature_list.addAll(new_feature_list);
	}
	private static void getTFIDF(String classifer, List<String> feature_list,int type) {
		// feature_list里的item========1976:0.07153965785381027
		if(train_IDF==null||!train_IDF.containsKey(classifer))return;
		Map<String, Double> IDF = null;
		if(type==0){IDF = test_IDF.get(classifer);}
		else if(type==1){IDF = train_IDF.get(classifer);}
		else{return;}
		List<String> new_feature_list = new ArrayList<String>();
		for(String item : feature_list){
			String feature = item.split(":")[0];
			double tf = Double.parseDouble(item.split(":")[1]);
			double idf = IDF.get(feature);
			double tfidf = tf*idf;
			new_feature_list.add(feature+":"+tfidf);
		}
		feature_list.clear();
		feature_list.addAll(new_feature_list);
	}
	/*private static void getFrequency(List<String> feature_list) {
		int sum  = 0;
		for(String feature : feature_list){
			sum+=Integer.parseInt(feature.split(":")[1]);
		}
		List<String> new_feature_list = new ArrayList<String>();
		for(String feature : feature_list){
			String index = feature.split(":")[0];
			int count = Integer.parseInt(feature.split(":")[1]);
			double tf = count/(double)sum;
			new_feature_list.add(index+":"+tf);
		}
		feature_list.clear();
		feature_list.addAll(new_feature_list);
	}*/

}
