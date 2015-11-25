package org.cl.servies;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.cl.conf.Config;
import org.cl.model.ClassNode;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;
import org.cl.utils.Utils;

public class GetTrainTestID {
	static int FOLD = Config.FOLD;
	static int[] LABELS = Config.LABELS;//类别
	static int ID_NUMBER = Config.ID_NUMBER;//每类ID数量
	static int TRAIN_ID_SIZE = Config.TRAIN_ID_SIZE;//每类用户用于训练的ID数量。
	static int[] TRAIN_ID_SIZE_ARR = Config.TRAIN_ID_SIZE_ARR;//每类用户用于训练的ID数量。
	static boolean LEARN_FLAG = Config.LEARN_FLAG;//控制是否载入学习数据
	/*-------------------------从已经分配好的训练、测试ID中得到相应的ID----------------------------------------*/
	public static Map<Integer, ClassNode> getTTID_TriTraining(int fold_i,float training_ratio,float learning_ratio) throws IOException {
		Map<Integer, ClassNode> label_map = new HashMap<Integer, ClassNode>();
		for(int li=0;li<LABELS.length;li++){
			int labelid = LABELS[li];
			Set<String> test_id_set = ReadInfo.getSet(Config.Public_Info,fold_i+"\\"+labelid+"_testingid.txt");
			Set<String> id_set = ReadInfo.getSet(Config.Public_Info,fold_i+"\\"+TRAIN_ID_SIZE+"_"+labelid+"_trainingid.txt");
			//将id_set按ratio比例分成两份分别作为训练数据和学习数据 training_ratio:1-training_ratio
			List<Set<String>> id_set_list1 = Utils.spilt(id_set, training_ratio);
			ClassNode classnode = new ClassNode(labelid,id_set_list1.get(0),test_id_set);
			//学习数据集中抽取一部分作为初始的学习数据子集
			List<Set<String>> id_set_list2 = Utils.spilt(id_set_list1.get(1), learning_ratio);
			classnode.setLearning_id_subset(id_set_list2.get(0));
			classnode.setLearning_id_set(id_set_list2.get(1));
			label_map.put(labelid, classnode);
		}
		return label_map;
	}
	//获取classnode的训练和测试ID
	public static Map<Integer, ClassNode> getTTID(int fold_i) throws IOException {
		Map<Integer, ClassNode> label_map = new HashMap<Integer, ClassNode>();
		for(int li=0;li<LABELS.length;li++){
			int labelid = LABELS[li];
			Set<String> train_id_set = ReadInfo.getSet(Config.Public_Info,fold_i+"\\"+TRAIN_ID_SIZE+"_"+labelid+"_trainingid.txt");
			Set<String> test_id_set = ReadInfo.getSet(Config.Public_Info,fold_i+"\\"+labelid+"_testingid.txt");
			ClassNode myclassNode = new ClassNode(labelid,train_id_set,test_id_set);
			if(LEARN_FLAG){
				Set<String> learn_id_subset = ReadInfo.getSet(Config.ResPath_Root,"UserID\\L"+labelid+".txt");
				myclassNode.setLearning_id_subset(learn_id_subset);
			}
			label_map.put(labelid, myclassNode);
		}
		return label_map;
	}
	/*-------------------------从UserID目录下获取所有ID，将其分配成FOLD组，分别作为训练、测试数据------------------------------*/

	/**
	 * 从USERID_ROOT目录下获取labelid类用户ID，分成FOLD组，得到FOLD组测试用户ID和训练用户ID。每组测试用户和训练用户不会产生交叉
	 * 每组训练用户可以获取不同规模，规模大小存储在TRAIN_ID_SIZE数组中。 （规模最大为ID_NUMBER/FOLD）(取较小规模的ID时，顺序抽取前size个)
	 * 测试用户ID命名规则：[labelid]_testingid.txt   
	 * 训练用户ID命名规则：[size]_[labelid]_trainingid.txt
	 * @param labelid
	 * @throws IOException 
	 */
	public static void setTrain_TestID() throws IOException {
		for(int labelid : LABELS){
			List<Set<String>> id_set_list = getIDSetList(labelid);//得到一类用户所有的ID，分为FOLD组，分别装在Set中。
			ClassNode classnode = new ClassNode(labelid,id_set_list);
			for(int i=0;i<FOLD;i++){  //每折使用其中1组作为测试，另外FOLD-1组作为训练
				SaveInfo.mkdir(Config.Public_Info+i);
				classnode.setTesting_id_set(i);
				SaveInfo.id_writer(Config.Public_Info+i+"\\"+labelid+"_testingid"+".txt",classnode.getTesting_id_set());

				//获取固定的size的数据作为训练数据
				classnode.setTrainning_id_set(i);
				SaveInfo.id_writer(Config.Public_Info+i+"\\"+TRAIN_ID_SIZE+"_"+labelid+"_trainingid.txt",classnode.getTrainning_id_set());

				//针对训练数据变化的实验，训练数据分别由1份、2份、3...FOLD-1份数据组成
				int id_size = id_set_list.get(0).size();
				for(int n=1;n<FOLD;n++){  
					classnode.setTrainning_id_set_byfold(i, n);
					SaveInfo.id_writer(Config.Public_Info+i+"\\"+(n*id_size)+"_"+labelid+"_trainingid.txt",classnode.getTrainning_id_set());
				}

				//针对训练数据变化的实验，训练数据从id_set_list选第i组以外的组中各抽取部分，凑齐size个作为训练ID
				for(int size : TRAIN_ID_SIZE_ARR){  
					classnode.setTrainning_id_set_bynum(i, size);
					SaveInfo.id_writer(Config.Public_Info+i+"\\"+size+"_"+labelid+"_trainingid.txt",classnode.getTrainning_id_set());
				}
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
	public static void setTrain_TestID_1vsall() throws IOException {
		for(int labelid : LABELS){
			List<Set<String>> id_set_list = getIDSetList_1vsall(labelid);//得到非label类用户所有的ID，分为FOLD组，分别装在Set中。
			ClassNode classnode = new ClassNode(labelid,id_set_list);
			labelid = labelid*10+labelid;
			for(int i=0;i<FOLD;i++){
				SaveInfo.mkdir(Config.Public_Info+i);
				classnode.setTesting_id_set(i);
				SaveInfo.id_writer(Config.Public_Info+i+"\\"+labelid+"_testingid"+".txt",classnode.getTesting_id_set());
				for(int size : TRAIN_ID_SIZE_ARR){  
					classnode.setTrainning_id_set_bynum(i, size);
					SaveInfo.id_writer(Config.Public_Info+i+"\\"+size+"_"+labelid+"_trainingid.txt",classnode.getTrainning_id_set());
				}
			}
		}
	}

	//从UserInfo.txt中根据用户属性获取TESTID
	public static void tmp_setTestID() throws IOException {
		File srcf = new File(Config.SrcPath_Root+"UserInfo0_MuteUser.txt");
		BufferedReader r = new BufferedReader(new FileReader(srcf));
		File resf1 = new File(Config.Public_Info+"0\\1_testingid.txt");
		BufferedWriter w1 = new BufferedWriter(new FileWriter(resf1));
		File resf2 = new File(Config.Public_Info+"0\\2_testingid.txt");
		BufferedWriter w2 = new BufferedWriter(new FileWriter(resf2));
		// 用户Id、 用户名、是否被选择、性别（男性1，女性0） 、训练还是测试（训练1，测试0）
		String line = null;
		while(null!=(line=r.readLine())){
			if(line.equals(""))continue;
			JSONObject json = JSONObject.fromObject(line);
			String id = json.getString("id");
			String gender = json.getString("gender");
			if(gender.equals("f")){//female
				w1.write(id+"\r\n");
			}else if(gender.equals("m")){//male
				w2.write(id+"\r\n");
			}else {
				System.out.println(line);
			}

		}
		r.close();
		w1.flush();w1.close();
		w2.flush();w2.close();
	}
	//从老师给定的文件中获取TrainOrTestID
	public static void tmp_setTrain_TestID(String ratio) throws IOException {
		File srcf = new File(Config.SrcPath_Root+"allfilenames_32wuser_gender_split_selected_"+ratio+".txt");
		BufferedReader r = new BufferedReader(new FileReader(srcf));
		File resf1 = new File(Config.Public_Info+"0\\1_testingid.txt");
		BufferedWriter w1 = new BufferedWriter(new FileWriter(resf1));
		File resf2 = new File(Config.Public_Info+"0\\2_testingid.txt");
		BufferedWriter w2 = new BufferedWriter(new FileWriter(resf2));
		File resf3 = new File(Config.Public_Info+"0\\400_1_trainingid.txt");
		BufferedWriter w3 = new BufferedWriter(new FileWriter(resf3));
		File resf4 = new File(Config.Public_Info+"0\\400_2_trainingid.txt");
		BufferedWriter w4 = new BufferedWriter(new FileWriter(resf4));

		// 用户Id、 用户名、是否被选择、性别（男性1，女性0） 、训练还是测试（训练1，测试0）
		String line = null;
		while(null!=(line=r.readLine())){
			String[] items = line.split("\\s");
			if(items[4].equals("0")){//test data
				if(items[3].equals("0")){//female
					w1.write(items[0]+"\r\n");
				}else if(items[3].equals("1")){//male
					w2.write(items[0]+"\r\n");
				}else {
					System.out.println(line);
				}
			}else if(items[4].equals("1")){//train data
				if(items[3].equals("0")){//female
					w3.write(items[0]+"\r\n");
				}else if(items[3].equals("1")){//male
					w4.write(items[0]+"\r\n");
				}else {
					System.out.println(line);
				}
			}else {
				System.out.println(line);
			}
		}
		r.close();
		w1.flush();w1.close();
		w2.flush();w2.close();
		w3.flush();w3.close();
		w4.flush();w4.close();

	}
	//从UserID_XX/1.txt中获取ID，根据一定比例获取TrainOrTestID
	public static void tmp_setTrain_TestID(float ratio) throws IOException{
		for(int labelid : LABELS){
			Set<String> id_set = ReadInfo.getSet(Config.UserID,labelid+".txt");
			List<Set<String>> id_set_list = Utils.spilt(id_set, ratio);
			SaveInfo.saveSet(Config.Public_Info, "0\\112_"+labelid+"_trainingid.txt", id_set_list.get(0));
			SaveInfo.saveSet(Config.Public_Info, "0\\"+labelid+"_testingid.txt", id_set_list.get(1));
		}
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
		Set<String> id_set = ReadInfo.getSet(Config.UserID,labelid+".txt");
		//id_set = Utils.subSet(id_set, ID_NUMBER);//从id_set中随机取id_number个数据作为新的id_set，最多取id_set.size()个
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
			Set<String> id_set = ReadInfo.getSet(Config.UserID,labelid_i+".txt");
			id_set = Utils.subSet(id_set, id_number_i);//从id_set中随机取id_number个数据作为新的id_set，最多取id_set.size()个
			//size += id_set.size(); //266/5*5=265                                                                                          
			List<Set<String>>id_set_list_i = Utils.spilt(id_set, FOLD);//将id_set分成fold组,每组id_size = 266/5 = 53
			Utils.merge(id_set_list, id_set_list_i);//将5组ID，分别加到id_set_list中，循环之后id_set_list就共有5组数据，每组数据来自不同labelid_i的用户
		}
		return id_set_list;
		//return new ClassNode(labelid,id_set_list,size);
	}

}
