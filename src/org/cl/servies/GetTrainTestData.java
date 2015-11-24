package org.cl.servies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.cl.conf.Config;
import org.cl.model.ClassNode;
import org.cl.model.ClassiferNode;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;
import org.cl.utils.Utils;

public class GetTrainTestData {
	public static Map<String, Integer> classifers_weibo_map = null;


	static int[] LABELS = Config.LABELS;
	static int WEIBO_NUMBER = Config.WEIBO_NUMBER;//微博级别的分类器，控制微博数量
	static Map<Integer,Map<String,Map<String, Double>>> CHI = null;
	static Map<String,Map<String, Double>> train_IDF = null;
	static Map<String,Map<String, Double>> test_IDF = null;
	static Map<ClassiferNode,Map<String,String>> classifer_user_map = null;
	static{
		CHI = GetCHI.CHI;
		train_IDF = GetIDF.train_IDF;
		test_IDF = GetIDF.test_IDF;
		classifer_user_map = GetUserFeature.classifer_user_map;
		if(classifer_user_map.size()==0){
			System.out.println("classifer_user_map.size = 0!please execute GetUserFeature.getUserFeatureMap()");
		}
	}
	//获取classnode的训练和测试数据（训练用户和测试用户ID已知）
	public static void getTTData_UserLevel(Map<Integer, ClassNode> label_map) {
		for(int li=1;li<=LABELS.length;li++){
			int labelid = LABELS[li-1];
			ClassNode classnode = label_map.get(labelid);
			SaveInfo.saveResult("--------------label-"+labelid+"-------------");
			SaveInfo.saveResult("------------------------Getting Testing Data--------------------");
			getData_UserLevel(labelid,classnode.getTesting_id_set(),0);//获取测试数据
			getData_UserLevel(labelid,classnode.getTesting_id_set_fake(),2);//获取测试Fake数据
			SaveInfo.saveResult("------------------------Getting Trainning Data--------------------");
			getData_UserLevel(labelid,classnode.getTrainning_id_set(),1);//获取训练数据
			Set<String> learning_id = classnode.getLearning_id_set();
			if(learning_id!=null&&learning_id.size()>0){
				SaveInfo.saveResult("------------------------Getting Learning Data--------------------");
				getData_UserLevel(labelid,classnode.getLearning_id_set(),3);//获取学习数据
			}
		}
	}

	public static void getTTData_WeiboLevel(Map<Integer, ClassNode> label_map) throws IOException {
		for(int li=1;li<=LABELS.length;li++){
			int labelid = LABELS[li-1];
			ClassNode classnode = label_map.get(labelid);
			SaveInfo.saveResult("--------------label-"+labelid+"-------------");
			SaveInfo.saveResult("------------------------Getting Testing Data--------------------");
			getData_WeiboLevel(labelid,classnode.getTesting_id_set(),0);//获取测试数据
			SaveInfo.saveResult("------------------------Getting Trainning Data--------------------");
			getData_WeiboLevel(labelid,classnode.getTrainning_id_set(),1);//获取训练数据
			Set<String> learning_id = classnode.getLearning_id_set();
			if(learning_id!=null&&learning_id.size()>0){
				SaveInfo.saveResult("------------------------Getting Learning Data--------------------");
				getData_WeiboLevel(labelid,classnode.getLearning_id_set(),1);//获取学习数据
			}
		}
	}

	//获取id_set的classifers特征的数据（每个特征在一定编号范围内）type表示data类别，0表示测试数据，1表示训练数据, 2表示测试Fake数据,3表示学习数据
	private static void getData_UserLevel(int labelid, Set<String> id_set,int data_type) {
		if(id_set==null||id_set.size()==0)return;
		int start_index = 1;//每一类正常特征的起始编码值
		Map<String,StringBuffer> id_feature = new TreeMap<String,StringBuffer>();
		Iterator<Entry<ClassiferNode, Map<String, String>>> it = classifer_user_map.entrySet().iterator();
		while(it.hasNext()){
			Entry<ClassiferNode, Map<String, String>> classifer_entry = it.next();
			ClassiferNode classifer = classifer_entry.getKey();
			Map<String, String> feature_map = classifer_entry.getValue();
			String classifer_name = classifer.getClassifer_name();
			for(String id : id_set){
				if(!feature_map.containsKey(id)||feature_map.get(id).length()==0){SaveInfo.saveResult(id+"---没有特征----"+classifer_name);continue;}
				List<String> feature_list = Utils.stringFeaturetoList(feature_map.get(id));
				//进行特征选择，当未获取该Classifer的各特征的CHI时不会进行特征选择
				//训练用户才能使用CHI进行筛选，对测试用户若使用CHI，即隐式的使用了训练用户的信息。因为每次筛选时是根据测试用户类别选择所用的CHI列表，即我们提前知道了测试用户的类别。
				if(Config.CHI_FLAG==true&&data_type==1&&CHI!=null&&CHI.containsKey(labelid)&&CHI.get(labelid).containsKey(classifer_name)){
					Map<String, Double> CHI_map = CHI.get(labelid).get(classifer_name);
					getSelectedFeatureByCHI(CHI_map, feature_list);
				}
				//将各特征表示成tfidf值
				//getFrequency(feature_list);//将对应特征的值由原来的频次改为频率
				if(data_type==1){getTFIDF(classifer_name,feature_list,train_IDF);}
				else{getTFIDF(classifer_name,feature_list,test_IDF);}

				String reencoded_feature = Utils.reencode(feature_list,start_index);//从start_index开始重新编码，并按编码大小从小到大排序
				Utils.putInMap(id_feature, id, reencoded_feature);
			}
			start_index += classifer.getClassifer_size();
		}
		SaveInfo.data_writer(labelid,id_feature,data_type);
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


	private static void getSelectedFeatureByCHI(Map<String, Double> CHI_map,List<String> feature_list) {
		List<String> new_feature_list = new ArrayList<String>();
		for(String feature:feature_list){
			String index = feature.split(":")[0];
			if(CHI_map.containsKey(index)){new_feature_list.add(feature);}
		}
		feature_list.clear();
		feature_list.addAll(new_feature_list);
	}
	private static void getTFIDF(String classifer, List<String> feature_list,Map<String,Map<String, Double>> Classifier_IDF_map) {
		// feature_list里的item========1976:0.07153965785381027
		if(Config.TFIDF_FLAG==false||Classifier_IDF_map==null||!Classifier_IDF_map.containsKey(classifer))return;
		Map<String, Double> IDF = Classifier_IDF_map.get(classifer);
		/*
		if(type==0){IDF = }
		else if(type==1){IDF = train_IDF.get(classifer);}
		else{return;}*/
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
