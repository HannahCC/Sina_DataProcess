package org.cl.main.classifer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cl.cmd.Cmd_Predict;
import org.cl.cmd.Cmd_Train;
import org.cl.conf.Config;
import org.cl.model.ClassNode;
import org.cl.model.ClassiferNode;
import org.cl.model.ResultNode;
import org.cl.utils.GetResult;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;
import org.cl.utils.Services;
import org.cl.utils.Utils;


public class Classifer_TriTrainning {
	/**
	 * 对用户级别的特征进行处理，可以进行使用单个分类器或多个分类器对用户进行分类，可以改变各参数
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException
	 */
	static String type = "lg";
	static int increment = 150;//每次取出100个相同的结果
	static int iter_max = 5;//最多迭代iter_max次
	static int train_id_size_max = 700;//训练集最大达到该值结束迭代
	static int fold = 5;
	static double CHI_threshold = 0.75;//取CHI值排在前50%的特征
	static String CHI_TYPE = "";//type=""则用chi作为标准，若type="b"则用tf作为筛选标准
	static boolean CHI_FLAG = false;//是否使用CHI筛选特征
	static boolean TFIDF_FLAG = false;//是否使用TFIDF表示特征,false时只用tf,true用tfidf
	static int[] labels = {1,2};
	static int train_id_size = 400;
	static String[] classifers = null;
	static String res_dir = "TriTraining_Line_Description_Tag_Incre150_iter5\\";
	static StringBuffer log_buff = new StringBuffer();
	public static void main(String[] args) throws IOException{
		Config.ResPath = Config.ResPath_Root+res_dir;
		SaveInfo.mkdir(Config.ResPath);
		classifers = new String[]{
				"Feature_Relation\\Fri_Fol_Description",
				"Feature_Relation\\Fri_Fol_Tag",
				"Feature_Relation\\line_vec_all"
		};
		/*-------普通情况，所有labels都进行比较-（默认CHI_threshold = 0.5;train_id_size=640）--------*/
		cross_validation(fold);
		SaveInfo.saveResult(Config.ResPath_Root+res_dir,"res.txt");
		SaveInfo.saveResult(Config.ResPath_Root+res_dir,"res.txt",log_buff);

	}
	public static void cross_validation(int fold) throws IOException{
		double[] res_avg  = new double[3];
		for(int i=0;i<fold;i++){
			Config.ResPath = Config.ResPath_Root+res_dir+i;
			SaveInfo.mkdir(Config.ResPath);
			SaveInfo.saveResult("-----------------------fold-"+i+"--------------------");
			log_buff.append("-----------------------fold-"+i+"--------------------\r\n");
			//获取各classifer-label对应的训练、测试ID集合
			Map<Integer, ClassNode> label_map = get_train_test_id(i);
			//迭代训练预测过程，知道训练集数量达到
			int train_id_size = 200;int iter = 0;
			while(train_id_size<train_id_size_max&&iter<iter_max){
				Config.ResPath = Config.ResPath_Root+res_dir+i+"\\"+iter;
				SaveInfo.mkdir(Config.ResPath);
				SaveInfo.saveResult("-----------------------第"+iter+"次迭代-------------------");
				//获取训练、测试数据并进行训练和预测
				train_and_predict(i,iter,label_map);
				//获取各分类器的预测结果
				List<Map<String,String>> classifer_result_map_list = get_predict_result(i,iter);
				//更新下一轮的训练、测试ID集合(2个分类器结果相同)
				//update_train_test_id_2(classifer_label_map,classifer_result_map_list);
				//更新下一轮的训练、测试ID集合(3个分类器结果相同)
				train_id_size += update_train_test_id(label_map,classifer_result_map_list);
				iter++;
			}
			//迭代完成后，利用三个分类器的结果进行ensemble处理
			double[] res = ensemble(i,iter);
			for(int k=0;k<res.length;k++){
				res_avg[k] += res[k];
			}
		}
		//对精确度求评价
		for(int k=0;k<res_avg.length;k++){
			res_avg[k] = res_avg[k]/fold;
		}
		SaveInfo.saveResult("-----------------------最终预测结果-------------------");
		SaveInfo.saveResult(Config.ResPath_Root+res_dir+"\\final_result_"+type+"----accuracy average="+res_avg[0]);
		SaveInfo.saveResult(Config.ResPath_Root+res_dir+"\\final_result_"+type+"----MicroF1Score average="+res_avg[1]);
		SaveInfo.saveResult(Config.ResPath_Root+res_dir+"\\final_result_"+type+"----MacroF1Score average="+res_avg[2]);
	}
	private static double[] ensemble(int i,int iter) throws IOException {
		Config.ResPath = Config.ResPath_Root+res_dir+i+"\\"+(iter-1)+"\\";
		Map<String,String> id_actual_res = new TreeMap<String, String>();
		Map<String,ResultNode> id_predict_res = new TreeMap<String, ResultNode>();
		for(int j=0;j<classifers.length;j++){
			String classifer_name = classifers[j].split("\\\\")[1].intern();
			// testing_id.txt testing_data.txt result_lg.txt 同一行为同一个用户
			List<String> testing_id = ReadInfo.getList(Config.ResPath+"Simple_"+classifer_name,"\\testing_id.txt","\\s",0);
			GetResult.getActualRes(Config.ResPath+"Simple_"+classifer_name,"\\testing_id.txt",testing_id,id_actual_res,"\\s",1);
			GetResult.getPredictRes(Config.ResPath+"Simple_"+classifer_name,"\\result_"+type+".txt",testing_id,id_predict_res,1);
		}
		Config.ResPath = Config.ResPath_Root+res_dir+i;
		SaveInfo.result_writer(Config.ResPath,"\\final_result_"+type+".txt","final_testing_id.txt",id_actual_res,id_predict_res);
		double accuracy =  GetResult.getAccuracy(id_actual_res,id_predict_res);
		double microF1 = GetResult.getMicroF1Score(id_actual_res, id_predict_res);
		double macroF1 = GetResult.getMacroF1Score(id_actual_res, id_predict_res);
		return new double[]{accuracy,microF1,macroF1};
	}
	private static Map<Integer, ClassNode> get_train_test_id(int fold_i) throws IOException {
		/*Map<String, Map<Integer, ClassNode>> classifer_label_map = new HashMap<String, Map<Integer, ClassNode>>();
		for(String classifer : classifers){
			String classifer_name = classifer.split("\\\\")[1].intern();*/
		Map<Integer, ClassNode> label_map = new HashMap<Integer, ClassNode>();
		for(int li=1;li<=labels.length;li++){
			int labelid = labels[li-1];
			ClassNode classnode = Services.getTTID_TriTraining(fold_i,train_id_size,labelid);
			label_map.put(labelid, classnode);
		}
		return label_map;
		/*classifer_label_map.put(classifer_name, label_map);
		}
		return classifer_label_map;*/
	}
	/**
	 * 将classifer_result_map_list中两个分类器结果相同的ID加入到另一个分类器的训练集中
	 * @param classifer_label_map
	 * @param classifer_result_map_list
	 * @return
	 */
	private static int update_train_test_id_2(Map<String, Map<Integer, ClassNode>> classifer_label_map, List<Map<String, String>> classifer_result_map_list) {
		int train_id_size = 0;
		for(int j=0;j<classifers.length;j++){
			//读取令两个分类器中标记相同的ID
			Map<String, String> result_map1 = classifer_result_map_list.get((j+1)%3);
			Map<String, String> result_map2 = classifer_result_map_list.get((j+2)%3);
			Map<String, String> result_map = new HashMap<String, String>();
			for(String id : result_map1.keySet()){
				if(result_map2.containsKey(id)){   //UID:实际lable##预测label##预测置信度
					String[] result1 = result_map1.get(id).split("##");
					String[] result2 = result_map2.get(id).split("##");
					if(result1[1].equals(result2[1])){
						result_map.put(id, result1[0]+"##"+result1[1]+"##"+(Double.parseDouble(result1[2])+Double.parseDouble(result2[2])));
					}
				}
			}
			//将label相同的ID按照分数排序，选取前increment=100个
			List<String> increment_id_list = new ArrayList<String>();
			Utils.mapSortByValue(result_map,increment_id_list);

			//将前increment=100个，从原本的test_id_set中移除，放到train_id_set中
			String classifer_name = classifers[j].split("\\\\")[1].intern();
			Map<Integer, ClassNode> label_map = classifer_label_map.get(classifer_name);
			int num = 0;
			for(String id_item: increment_id_list){
				String[] items = id_item.split("##");
				String uid = items[0];
				int act_label_id = Integer.parseInt(items[1]);
				int pre_label_id = Integer.parseInt(items[2]);
				if(!label_map.get(pre_label_id).getTrainning_id_set().contains(uid)){
					label_map.get(pre_label_id).getTrainning_id_set().add(uid);
					label_map.get(act_label_id).getTesting_id_set().remove(uid);
					num++;
				}
				if(act_label_id!=pre_label_id){
					SaveInfo.saveResult(uid+"----act_label_id="+act_label_id+"---pre_label_id="+pre_label_id);
				}
				if(num==increment){
					break;
				}
			}
			int new_train_id_size = label_map.get(1).getTrainning_id_set().size()+label_map.get(2).getTrainning_id_set().size();
			train_id_size = train_id_size>new_train_id_size?train_id_size:new_train_id_size;
		}
		return train_id_size;
	}
	/**
	 * 将所有分类器结果相同的ID加入到各自的训练集中
	 * @param classifer_label_map
	 * @param classifer_result_map_list
	 * @return 
	 * @return
	 */
	private static int update_train_test_id(Map<Integer, ClassNode> label_map, List<Map<String, String>> classifer_result_map_list) {
		Map<String, String> result_map1 = classifer_result_map_list.get(0);
		Map<String, String> result_map2 = classifer_result_map_list.get(1);
		Map<String, String> result_map3 = classifer_result_map_list.get(2);
		Map<String, String> result_map = new HashMap<String, String>();
		for(String id : result_map1.keySet()){
			if(result_map2.containsKey(id)&&result_map3.containsKey(id)){// UID:实际lable##预测label##预测置信度
				String[] result1 = result_map1.get(id).split("##");
				String[] result2 = result_map2.get(id).split("##");
				String[] result3 = result_map3.get(id).split("##");
				if(result1[1].equals(result2[1])&&result1[1].equals(result3[1])){
					result_map.put(id, result1[0]+"##"+result1[1]+"##"+(Double.parseDouble(result1[2])+Double.parseDouble(result2[2])+Double.parseDouble(result3[2])));
				}
			}
		}
		//将label相同的ID按照分数排序，选取前increment=100个
		List<String> increment_id_list = new ArrayList<String>();
		Utils.mapSortByValue(result_map,increment_id_list);

		//将前increment个，从原本的test_id_set中移除，放到train_id_set中
		int increment_size = increment<increment_id_list.size()?increment:increment_id_list.size();
		for(int i=0;i<increment_size;i++){
			String id_item = increment_id_list.get(i);
			String[] items = id_item.split("##");
			String uid = items[0];
			int act_label_id = Integer.parseInt(items[1]);
			int pre_label_id = Integer.parseInt(items[2]);
			if(label_map.get(pre_label_id).getTrainning_id_set().contains(uid)){
				System.out.println("******************************************"+uid+"********************************");
			}
			label_map.get(pre_label_id).getTrainning_id_set().add(uid);
			label_map.get(act_label_id).getTesting_id_set_fake().remove(uid);
			if(act_label_id!=pre_label_id){
				SaveInfo.saveResult(uid+"----act_label_id="+act_label_id+"---pre_label_id="+pre_label_id);
			}
		}
		SaveInfo.saveResult("-------------------------add "+increment_size+ "id------------------------------");
		return increment_size;
	}

	private static List<Map<String, String>> get_predict_result(int i,int d) throws IOException {
		List<Map<String,String>> classifer_result_map_list = new ArrayList<Map<String,String>>();
		for(String classifer : classifers){
			String classifer_name = classifer.split("\\\\")[1].intern();
			String classifer_dir = "Simple_"+classifer_name+"\\".intern();
			Config.ResPath = Config.ResPath_Root+res_dir+i+"\\"+d+"\\"+classifer_dir;
			//得到预测结果       UID:实际lable##预测label##预测置信度
			Map<String, String> result_map = GetResult.getResult(Config.ResPath,"testing_id_fake","testing_data_fake","result_fake",type);
			classifer_result_map_list.add(result_map);
		}
		return classifer_result_map_list;
	}
	private static void train_and_predict(int i, int iter, Map<Integer, ClassNode> label_map) throws IOException {
		for(int k=0;k<classifers.length;k++){
			String classifer_name = classifers[k].split("\\\\")[1].intern();
			List<String> classifers_list = new ArrayList<String>();
			classifers_list.add(classifers[k]);
			Map<ClassiferNode,Map<String,String>> classifer_user_map = ReadInfo.getMap(classifers_list,"_feature.txt");
			Services.classifer_user_map = classifer_user_map;
			String classifer_dir = "Simple_"+classifer_name+"\\".intern();
			Config.ResPath = Config.ResPath_Root+res_dir+i+"\\"+iter+"\\"+classifer_dir;
			SaveInfo.mkdir(Config.ResPath);
			if(TFIDF_FLAG){
				Services.getIDF(i,train_id_size,0,"");
				Services.getIDF(i,train_id_size,1,"");
			}
			for(int li=1;li<=labels.length;li++){
				int labelid = labels[li-1];
				SaveInfo.saveResult("------------"+classifer_name+"--label-"+labelid+"-------------");
				if(CHI_FLAG)Services.getCHI(i,train_id_size,labelid,CHI_threshold,CHI_TYPE,"");
				ClassNode classnode = label_map.get(labelid);
				Services.getTTData(li,classnode);
			}
			//Train and Predict
			Cmd_Train.train(Config.ResPath,type,"training_data");
			double accuracy = Cmd_Predict.predict(Config.ResPath,type);//对unlabeled 进行预测
			log_buff.append(Config.ResPath+"----"+accuracy+"\r\n");
			Cmd_Predict.predict(Config.ResPath,"testing_data_fake","result_fake",type);//对实际ID进行预测
		}
	}
}
