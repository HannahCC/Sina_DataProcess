package org.cl.main.classifer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cl.conf.Config;
import org.cl.model.ClassNode;
import org.cl.servies.Cmd_Predict;
import org.cl.servies.Cmd_Train;
import org.cl.servies.GetCHI;
import org.cl.servies.GetIDF;
import org.cl.servies.GetResult;
import org.cl.servies.GetTrainTestData;
import org.cl.servies.GetTrainTestID;
import org.cl.servies.GetUserFeature;
import org.cl.utils.SaveInfo;
import org.cl.utils.Utils;


public class Classifer_TriTrainning {
	/**
	 * 对用户级别的特征进行处理，可以进行使用单个分类器或多个分类器对用户进行分类，可以改变各参数
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException
	 */
	static float training_ratio = 1/4f;//将原训练数据中training_ratio份作为初始训练数据，其余作为初始学习数据
	static float learning_ratio = 1/3f;//从学习数据集U中取learning_ratio份作为初始学习数据子集U'
	static int increment = 10;//不同的更新数据集方法，其意义不一样
	static int iter_max = 10;//最多迭代iter_max次
	static boolean STOP_FLAG = false;//迭代次数达到iter_max、unlabeled集中没有数据可用、会置为true
	static String res_dir = "TriTraining_Line_Tag_Incre10_iter10_update2\\";
	static String[] classifers = null;
	public static void main(String[] args) throws IOException{
		Config.ResPath = Config.ResPath_Root+res_dir;
		SaveInfo.mkdir(Config.ResPath);
		classifers = new String[]{
				//"Feature_Relation\\Fri_Fol_Description",
				"Feature_Relation\\Fri_Fol_Tag",
				"Feature_Relation\\line_vec_all"
		};
		/*-------普通情况，所有labels都进行比较-（默认CHI_threshold = 0.5;train_id_size=640）--------*/
		cross_validation();
		SaveInfo.log_buff_writer(Config.ResPath_Root+res_dir,"res.txt");

	}
	public static void cross_validation() throws IOException{
		double[] acc_avg = new double[classifers.length];
		//double esm_acc_avg  = 0.0;
		for(int i=0;i<Config.FOLD;i++){
			Config.ResPath = Config.ResPath_Root+res_dir+i;
			SaveInfo.mkdir(Config.ResPath);
			SaveInfo.option_log("-----------------------fold-"+i+"--------------------");
			SaveInfo.res_log("-----------------------fold-"+i+"--------------------",false);
			//获取各classifer-label对应的训练、测试ID集合(确定测试集，初始的训练集，初始的假测试集)
			Map<Integer, ClassNode> label_map = GetTrainTestID.getTTID_TriTraining(i,training_ratio,learning_ratio);
			//迭代训练预测过程，直到训练集数量达到
			int iter = 0;
			STOP_FLAG = false;
			while(!STOP_FLAG){
				Config.ResPath = Config.ResPath_Root+res_dir+i+"\\"+iter;
				SaveInfo.mkdir(Config.ResPath);
				SaveInfo.option_log("-----------------------第"+iter+"次迭代-------------------");
				//获取训练、测试数据并进行训练和预测
				train_and_predict(i,iter,label_map,true);
				//获取各分类器的预测结果
				Map<String, Map<String,String>> classifer_result_maps = get_predict_result(i,iter);
				//更新下一轮的训练、测试ID集合
				//update_train_test_id1(label_map,classifer_result_maps);
				update_train_test_id2(label_map,classifer_result_maps);
				iter++;
				if(iter>=iter_max){
					STOP_FLAG=true;
				}
			}
			Config.ResPath = Config.ResPath_Root+res_dir+i+"\\"+iter;
			SaveInfo.mkdir(Config.ResPath);
			SaveInfo.option_log("-----------------------第"+iter+"次迭代(最终)-------------------");
			double[] accuracy = train_and_predict(i,iter,label_map,false);
			//迭代完后，将最后一次迭代的结果作为最终结果
			for(int j=0;j<classifers.length;j++){
				acc_avg[j]+=accuracy[j];
			}
			//迭代完成后，利用三个分类器的结果进行ensemble处理,将essemble作为最终结果
			/*String[] classifers_name = new String[classifers.length];
			for(int j=0;j<classifers.length;j++){
				classifers_name[j] = classifers[j].split("\\\\")[1].intern();
			}
			esm_acc_avg += GetResult.getEssembleResult("", res_dir+i+"\\"+(iter-1)+"\\", res_dir+i+"\\", "",classifers_name);
			 */
		}

		SaveInfo.option_log("-----------------------最终预测结果-------------------");
		for(int j=0;j<classifers.length;j++){
			acc_avg[j]=acc_avg[j]/(float)Config.FOLD;
			SaveInfo.option_log(classifers[j]+"----accuracy average="+acc_avg[j]);
			SaveInfo.res_log(classifers[j]+"----accuracy average="+acc_avg[j],false);
		}

		/*esm_acc_avg = esm_acc_avg/Config.FOLD;
		SaveInfo.option_log("-----------------------集成最终预测结果-------------------");
		SaveInfo.option_log(Config.ResPath_Root+res_dir+"\\final_result_"+Config.SVM_TYPE+"----accuracy average="+esm_acc_avg);
		SaveInfo.res_log(Config.ResPath_Root+res_dir+"\\final_result_"+Config.SVM_TYPE+"----accuracy average="+esm_acc_avg);
		 */
	}
	private static double[] train_and_predict(int i, int iter, Map<Integer, ClassNode> label_map, boolean isPredictLearning) throws IOException {
		double[] accuracy = new double[classifers.length];
		for(int j=0;j<classifers.length;j++){
			String classifer_dir = "Simple_"+classifers[j].split("\\\\")[1]+"\\".intern();
			Config.ResPath = Config.ResPath_Root+res_dir+i+"\\"+iter+"\\"+classifer_dir;
			SaveInfo.mkdir(Config.ResPath);
			GetUserFeature.classifers.clear();
			GetUserFeature.classifer_user_map.clear();
			GetUserFeature.classifers.add(classifers[j]);
			GetUserFeature.getUserFeatureMap();
			GetIDF.getIDF(i,0);
			GetIDF.getIDF(i,1);
			GetCHI.getCHI(i);
			GetTrainTestData.getTTData_UserLevel(label_map);
			//Train and Predict
			Cmd_Train.train(Config.ResPath,"training_data");
			accuracy[j] = Cmd_Predict.predict(Config.ResPath,"testing_data","result");//对实际ID进行预测
			if(isPredictLearning)Cmd_Predict.predict(Config.ResPath,"learning_data","result_learning");//对unlabeled 进行预测
		}
		return accuracy;
	}
	private static Map<String, Map<String,String>> get_predict_result(int i,int d) throws IOException {
		Map<String, Map<String,String>> classifer_result_maps = new HashMap<String, Map<String,String>>();
		for(String classifer : classifers){
			String classifer_name = classifer.split("\\\\")[1].intern();
			String classifer_dir = "Simple_"+classifer_name+"\\".intern();
			Config.ResPath = Config.ResPath_Root+res_dir+i+"\\"+d+"\\"+classifer_dir;
			//得到预测结果       UID:实际lable##预测label##预测置信度
			Map<String, String> result_map = GetResult.getResult(Config.ResPath,"learning_id","learning_data","result_learning");
			classifer_result_maps.put(classifer_name, result_map);
		}
		return classifer_result_maps;
	}
	/**
	 * 将所有分类器结果相同、置信度最高的   最多increment个   ID加入到训练集中,使用预测label,而不是实际lable来作为它在训练集中的label
	 * @param classifer_label_map
	 * @param classifer_result_map_list
	 * @return 
	 * @return
	 */
	static void update_train_test_id1(Map<Integer, ClassNode> label_map, Map<String, Map<String,String>> classifer_result_maps) {
		Map<String, String> result_map1 = null;
		Map<String, String> result_map2 = null;
		Iterator<Entry<String, Map<String, String>>> it = classifer_result_maps.entrySet().iterator();
		while(it.hasNext()){
			//读取令两个分类器中标记相同的ID
			result_map1 = it.next().getValue();
			while(it.hasNext()){
				result_map2 = it.next().getValue();
				Map<String, String> result_map = new HashMap<String, String>();
				for(String id : result_map1.keySet()){
					if(result_map2.containsKey(id)){   //UID:实际lable##预测label##预测置信度
						String[] result1 = result_map1.get(id).split("##");
						String[] result2 = result_map2.get(id).split("##");
						if(result1[1].equals(result2[1])){//与此前分类器对该ID的预测结果相同，更新置信度为N个分类器的置信度之和
							result_map.put(id, result1[0]+"##"+result1[1]+"##"+(Double.parseDouble(result1[2])+Double.parseDouble(result2[2])));
						}
					}
				}
				result_map1 = result_map;
			}
		}
		//将label相同的ID按照分数排序，选取前increment=100个
		List<String> increment_id_list = new ArrayList<String>();
		Utils.mapSortByValue(result_map1,increment_id_list);

		//将前increment个，从原本的learn_id_subset中移除，放到train_id_set中
		int increment_size = increment<increment_id_list.size()?increment:increment_id_list.size();
		Map<Integer, Integer> label_increment_size = new HashMap<Integer, Integer>();
		for(int i=0;i<increment_size;i++){
			String id_item = increment_id_list.get(i);
			String[] items = id_item.split("##");
			String uid = items[0];
			int act_label_id = Integer.parseInt(items[1]);
			int pre_label_id = Integer.parseInt(items[2]);
			Utils.putInMap(label_increment_size, act_label_id, 1);
			if(label_map.get(pre_label_id).getTrainning_id_set().contains(uid)){
				System.out.println("**********wrong:test_id-"+uid+"was in train id set********************************");
			}
			label_map.get(pre_label_id).getTrainning_id_set().add(uid);
			label_map.get(act_label_id).getLearning_id_subset().remove(uid);
			if(act_label_id!=pre_label_id){
				SaveInfo.option_log(uid+"----act_label_id="+act_label_id+"---pre_label_id="+pre_label_id);
			}
		}
		//从learn_id_set中加入同样个数的case到learn_id_subset中（当learn_id_set中不够数据时，停止迭代）
		for(Integer label : label_increment_size.keySet()){
			if(label_map.get(label).getLearning_id_set().size()==0){
				STOP_FLAG = true;
				break;
			}
			List<Set<String>> id_set_list = Utils.spilt(label_increment_size.get(label), label_map.get(label).getLearning_id_set());
			label_map.get(label).getLearning_id_subset().addAll(id_set_list.get(0));
			label_map.get(label).setLearning_id_set(id_set_list.get(1));
		}
		SaveInfo.option_log("-------------------------add "+increment_size+ "id------------------------------");
	}
	/**
	 * 从各自分类器中选择置信度最高的case，每个  分类器  每类取前increment个（最多共有classifer.size*label.size*increment个，但分类器之间很多相同的ID）
	 * 使用预测label,而不是实际lable来作为它在训练集中的label
	 * 若不同的分类器对同一个用户判断的置信度都较高，但预测类别不一样时，选择相信置信度较高的结果
	 * @param label_map
	 * @param classifer_result_maps
	 * @return
	 */
	static void update_train_test_id2(Map<Integer, ClassNode> label_map,Map<String, Map<String, String>> classifer_result_maps) {
		//将每个分类器的结果按置信度从大到小排序，存放在increment_id_list中
		//对每个分类器的increment_id_list，各个类别各取出前increment个，存放在最终要被使用的increment_id_map中，并保持对该ID的预测结果为 不同分类器中置信度高的结果
		Map<String,String> increment_id_map = new HashMap<String,String>();
		Iterator<Entry<String, Map<String, String>>> it1 = classifer_result_maps.entrySet().iterator();
		while(it1.hasNext()){
			Map<String, String> result_map = it1.next().getValue();
			List<String> increment_id_list = new ArrayList<String>();
			Utils.mapSortByValue(result_map,increment_id_list);
			Map<Integer, Integer> pre_label_increment_size = new HashMap<Integer, Integer>();
			for(String id_res: increment_id_list){
				String[] items = id_res.split("##");//UID##实际lable##预测label##预测置信度
				String id = items[0];
				int pre_label_id = Integer.parseInt(items[2]);
				float support = Float.parseFloat(items[3]);
				Utils.putInMap(pre_label_increment_size, pre_label_id, 1);//记录从list中每类分别取了多少个ID
				//一个ID是否加入increment_id_map对应label的map中，有以下几种情况
				//1.此前该ID已经存在map中，策略，更新其support为最大值
				if(increment_id_map.containsKey(id)){//label_increment_id_map对应label的map中已经存在该ID
					String[] result = increment_id_map.get(id).split("##");//UID##实际lable##预测label##预测置信度
					if(Integer.parseInt(result[2])!=pre_label_id){//不同的分类器对同一个用户判断的置信度都较高，但预测类别不一样时，选择相信置信度较高的结果	
						if(Float.parseFloat(result[3])<support){
							increment_id_map.put(id, id_res);
						}
					}
				}//2.ID还不存在与map中，若该分类器取出的pre_label_id类结果数量还没超过increment，直接加入
				else if(pre_label_increment_size.get(pre_label_id)<=increment){//每个 分类器  每类最多取前increment个
					increment_id_map.put(id, id_res);
				}else{//2.ID还不存在与map中，但该分类器取出的pre_label_id类结果数量已经超过increment，不加入，
					//判断当前分类器的结果列表是否各类已经取到了increment个,若已经取到，则退出循环
					int label_size = 0;
					for(Entry<Integer, Integer> entry: pre_label_increment_size.entrySet()){
						if(entry.getValue()==increment){
							label_size++;
						}
					}
					if(label_size==pre_label_increment_size.size()){break;}
				}
			}
		}

		//将前increment个，从原本的learn_id_subset中移除，放到train_id_set中,
		//再从learn_id_set中加入同样个数的case到learn_id_subset中（当learn_id_set中不够数据时，停止迭代）
		Map<Integer, Integer> label_increment_size = new HashMap<Integer, Integer>();
		for(Entry<String, String> id_res_entry : increment_id_map.entrySet()){
			String res_item = id_res_entry.getValue();
			String[] items = res_item.split("##");
			String uid = items[0];
			int act_label_id = Integer.parseInt(items[1]);
			int pre_label_id = Integer.parseInt(items[2]);
			Utils.putInMap(label_increment_size, act_label_id, 1);
			if(label_map.get(pre_label_id).getTrainning_id_set().contains(uid)){
				System.out.println("**********wrong:test_id-"+uid+"was in train id set********************************");
			}
			label_map.get(pre_label_id).getTrainning_id_set().add(uid);
			label_map.get(act_label_id).getLearning_id_subset().remove(uid);
			if(act_label_id!=pre_label_id){
				SaveInfo.option_log(uid+"----act_label_id="+act_label_id+"---pre_label_id="+pre_label_id);
			}
		}
		//从learn_id_set中加入同样个数的case到learn_id_subset中（当learn_id_set中不够数据时，停止迭代）
		for(Integer label : label_increment_size.keySet()){
			if(label_map.get(label).getLearning_id_set().size()==0){
				STOP_FLAG = true;
				break;
			}
			List<Set<String>> id_set_list = Utils.spilt(label_increment_size.get(label), label_map.get(label).getLearning_id_set());
			label_map.get(label).getLearning_id_subset().addAll(id_set_list.get(0));
			label_map.get(label).setLearning_id_set(id_set_list.get(1));
		}
		SaveInfo.option_log("-------------------------add "+label_increment_size.size()+ "id------------------------------");
	}
}
