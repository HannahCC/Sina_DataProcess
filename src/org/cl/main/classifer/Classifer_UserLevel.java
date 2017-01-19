package org.cl.main.classifer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.cl.conf.Config;
import org.cl.model.ClassNode;
import org.cl.servies.GetCHI;
import org.cl.servies.GetDF;
import org.cl.servies.GetIDF;
import org.cl.servies.GetTrainTestData;
import org.cl.servies.GetTrainTestID;
import org.cl.servies.GetUserFeature;
import org.cl.utils.GetUserLabel;
import org.cl.utils.SaveInfo;

public class Classifer_UserLevel {
	/**
	 * 对用户级别的特征进行处理，可以进行使用单个分类器或多个分类器对用户进行分类，可以改变各参数
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	static String res_dir = "";
	static int start_class = -1;

	public static void main(String[] args) throws IOException {
		System.out
				.println("----------------------------------------------------------");
		int classnum = Integer.parseInt(args[1]);// label个数
		start_class = Integer.parseInt(args[2]);
		int foldnum = Integer.parseInt(args[3]);
		System.out.println("Config.RootPath : " + args[0]);
		System.out.println("Config.CLASS_NUMBER : " + classnum);
		System.out.println("Config.FOLD : " + foldnum);
		System.out.print("trainPercent : ");
		int idx = 4;
		int train_size_num = Integer.parseInt(args[idx]);
		int[] train_size_arr = new int[train_size_num];// 使用训练数据百分比
		for (int i = 0; i < train_size_num; i++) {
			train_size_arr[i] = Integer.parseInt(args[idx + i + 1]);
			System.out.print(train_size_arr[i] + " ");
		}
		System.out.println();
		System.out.print("features : ");
		idx = idx + train_size_num + 1;
		int featurenum = Integer.parseInt(args[idx]);
		String[] features = new String[featurenum];
		for (int i = 0; i < featurenum; i++) {
			features[i] = args[idx + i + 1];
			System.out.print(features[i] + " ");
		}
		System.out.println();

		/*
		 * int labelnum = 2;// label个数 int foldnum = 10; int[] train_size_arr =
		 * { 10, 20, 30, 40, 50, 60, 70, 80, 90 };// 使用训练数据百分比 String[] features
		 * = { "line" };// 特征文件名 int featurenum = features.length;
		 */

		Config.init(args[0], classnum, foldnum, train_size_arr);
		// Config.init("D:/Project_DataMinning/DataProcessd/test/",
		// labelnum,foldnum, train_size);
		/*
		 * Config.init("D:\\Project_DataMinning\\DataProcessd\\test\\",
		 * labelnum, foldnum, train_size_arr);
		 */
		for (int i = 0; i < featurenum; i++) {
			String feature = features[i];
			Config.CLASSIFERS.add(feature);
			res_dir += feature;
			if (i != featurenum - 1) {
				res_dir += "+";
			}
		}

		/*
		 * Config.ResPath = Config.ResPath_Root + res_dir;
		 * SaveInfo.mkdir(Config.ResPath);
		 */

		GetUserFeature.getUserFeatureMap();// 每折特征文件一致
		/*-------普通情况，所有labels都进行比较-（默认CHI_threshold = 0.5;train_id_size=640）--------*/
		// cross_validation(Config.TRAIN_ID_SIZE,"");
		/*---------------------------------每折用户的特征不相同-----------------------------------*/
		// cross_validation_dynamicFeature(Config.TRAIN_ID_SIZE,"");//Config.TRAIN_ID_SIZE
		/*---------------------------------比较不同chi取值的情况---------------------------------*/
		// allLabel_varCHI();
		/*---------------------------------diff_train_id_size--------------------------------*/
		// allLabel_varTrainSize();
		// allLabel_varTrainSize_dynamicFeature();
		/*-----------------------------------1vs1--------------------------------------------*/
		// OnevsOne();
		/*-----------------------------------1vs1-varCHI-------------------------------------*/
		// OnevsOne_varCHI();
		/*-----------------------------------1vsall------------------------------------------*/
		// OnevsAll();
		/*
		 * SaveInfo.log_buff_writer(Config.ResPath_Root, res_dir + "_res.txt",
		 * false);
		 */
		/*-----------------------------------1vsall_diff_train_id_size----------------------*/
		oneVsAll_varTrainSize();

		System.out
				.println("----------------------------------------------------------");
	}

	private static void cross_validation(int train_id_size, String dir)
			throws IOException {
		for (int i = 0; i < Config.FOLD; i++) {
			SaveInfo.option_log("------------------------fold-" + i
					+ "--------------------");
			Config.ResPath = Config.ResPath_Root + res_dir + "/" + dir + i
					+ "/";
			SaveInfo.mkdir(Config.ResPath);
			GetIDF.getIDF(i, 0);
			GetIDF.getIDF(i, 1);
			GetDF.getDF(i);
			GetCHI.getCHI(i);
			Map<Integer, ClassNode> label_map = GetTrainTestID.getTTID(i,
					train_id_size);
			GetTrainTestData.getTTData_UserLevel(label_map);
		}
	}

	// 配合珍妮的id划分程序写的
	private static void cross_validation_mutiClassifier(int train_id_size,
			int class_i) throws IOException {
		String classifier_path = Config.ResPath_Root + "(train_"
				+ train_id_size + "percent)" + "\\class_" + class_i + "\\"
				+ res_dir + "\\";
		SaveInfo.mkdir(classifier_path);
		for (int i = 0; i < Config.FOLD; i++) {
			SaveInfo.option_log("------------------------------------fold-" + i
					+ "--------------------");
			// GetUserFeature.getUserFeatureMap(train_id_size, i);// 每折的特征文件不一样
			Config.ResPath = classifier_path + i + "\\";
			SaveInfo.mkdir(Config.ResPath);
			Map<Integer, ClassNode> label_map = GetTrainTestID.getTTID(
					train_id_size, class_i, i);
			GetTrainTestData.getTTData_UserLevel(label_map);
		}
		Config.ResPath = classifier_path;
		Predict.run(res_dir + "_res.txt", 1, 1, 1, 0.01);

	}

	private static void cross_validation_dynamicFeature(int train_id_size,
			String dir) throws IOException {
		for (int i = 0; i < Config.FOLD; i++) {
			SaveInfo.option_log("------------------------fold-" + i
					+ "--------------------");
			GetUserFeature.getUserFeatureMap(train_id_size, i);
			Config.ResPath = Config.ResPath_Root + res_dir + "/" + dir + i
					+ "/";
			SaveInfo.mkdir(Config.ResPath);
			GetIDF.getIDF(i, 0);
			GetIDF.getIDF(i, 1);
			GetDF.getDF(i);
			GetCHI.getCHI(i);
			Map<Integer, ClassNode> label_map = GetTrainTestID.getTTID(i,
					train_id_size);
			GetTrainTestData.getTTData_UserLevel(label_map);
		}
	}

	public static void OnevsOne() throws IOException {
		int k = 0;
		int[] labels_copy = Arrays.copyOf(Config.LABELS, Config.LABELS.length);
		Config.LABELS = null;
		Config.LABELS = new int[2];
		for (int i = 0; i < labels_copy.length; i++) {
			Config.LABELS[0] = labels_copy[i];
			for (int j = i + 1; j < labels_copy.length; j++) {
				Config.LABELS[1] = labels_copy[j];
				SaveInfo.option_log("---------------------label-"
						+ Config.LABELS[0] + "vs" + Config.LABELS[1]
						+ "-------------------");
				Config.ResPath = Config.ResPath_Root + res_dir + "/" + k + "/";
				SaveInfo.mkdir(Config.ResPath);
				cross_validation(Config.TRAIN_ID_SIZE, k + "/");
				k++;
			}
		}
	}

	public static void OnevsOne_varCHI() throws IOException {
		float[] chi_threshold = { 0.5f, 0.55f, 0.6f, 0.65f, 0.7f, 0.75f, 0.8f,
				0.85f, 0.9f, 0.95f };
		int k = 0;
		int[] labels_copy = Arrays.copyOf(Config.LABELS, Config.LABELS.length);
		Config.LABELS = null;
		Config.LABELS = new int[2];
		for (float threshold : chi_threshold) {
			GetCHI.CHI_threshold = threshold;
			SaveInfo.option_log("------------------------threshold-"
					+ threshold + "--------------------");
			SaveInfo.mkdir(Config.ResPath_Root + k);
			int m = 0;
			for (int i = 0; i < labels_copy.length; i++) {
				Config.LABELS[0] = labels_copy[i];
				for (int j = i + 1; j < labels_copy.length; j++) {
					Config.LABELS[1] = labels_copy[j];
					SaveInfo.option_log("---------------------label-"
							+ Config.LABELS[0] + "vs" + Config.LABELS[1]
							+ "-------------------");
					Config.ResPath = Config.ResPath_Root + res_dir + "/" + k
							+ "/" + m + "/";
					SaveInfo.mkdir(Config.ResPath);
					cross_validation(Config.TRAIN_ID_SIZE, k + "/" + m + "/");
					m++;
				}
			}
			k++;
		}
	}

	public static void OnevsAll() throws IOException {
		int k = 0;
		int[] labels_copy = Arrays.copyOf(Config.LABELS, Config.LABELS.length);
		Config.LABELS = null;
		Config.LABELS = new int[2];
		for (int i = 0; i < labels_copy.length; i++) {
			Config.LABELS[0] = labels_copy[i];
			Config.LABELS[1] = Config.LABELS[0] * 10 + Config.LABELS[0];
			SaveInfo.option_log("---------------------label-"
					+ Config.LABELS[0] + "vs" + Config.LABELS[1]
					+ "-------------------");
			Config.ResPath = Config.ResPath_Root + res_dir + "/" + k + "/";
			SaveInfo.mkdir(Config.ResPath);
			cross_validation(Config.TRAIN_ID_SIZE, k + "/");
			k++;
		}
	}

	public static void allLabel_varCHI() throws IOException {
		float[] chi_threshold = { 0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.35f, 0.4f,
				0.45f, 0.5f, 0.55f, 0.6f, 0.65f, 0.7f, 0.75f, 0.8f, 0.85f,
				0.9f, 0.95f, 1.0f };
		int k = 0;
		for (float threshold : chi_threshold) {
			GetCHI.CHI_threshold = threshold;
			SaveInfo.option_log("------------------------threshold-"
					+ threshold + "--------------------");
			Config.ResPath = Config.ResPath_Root + res_dir + "/" + k + "/";
			SaveInfo.mkdir(Config.ResPath);
			cross_validation(Config.TRAIN_ID_SIZE, k + "/");
			k++;
		}
	}

	public static void allLabel_varTrainSize() throws IOException {
		int k = 0;
		for (int size : Config.TRAIN_ID_SIZE_ARR) {
			SaveInfo.option_log("------------------------train_id_size-" + size
					+ "--------------------");
			Config.ResPath = Config.ResPath_Root + res_dir + "/" + k + "/";
			SaveInfo.mkdir(Config.ResPath);
			cross_validation(size, k + "/");
			k++;
		}
	}

	public static void allLabel_varTrainSize_dynamicFeature()
			throws IOException {
		int k = 0;
		for (int size : Config.TRAIN_ID_SIZE_ARR) {
			SaveInfo.option_log("------------------------train_id_size-" + size
					+ "--------------------");
			Config.ResPath = Config.ResPath_Root + res_dir + "/" + k + "/";
			SaveInfo.mkdir(Config.ResPath);
			cross_validation_dynamicFeature(size, k + "/");
			k++;
		}
	}

	public static void oneVsAll_varTrainSize() throws IOException {
		for (int size : Config.TRAIN_ID_SIZE_ARR) {
			SaveInfo.option_log("------------------------train_id_size-" + size
					+ "--------------------");
			for (int class_i : Config.CLASSES) {
				if (class_i < start_class)
					continue;
				SaveInfo.option_log("-----------------------------class_i-"
						+ class_i + "-----------------");
				cross_validation_mutiClassifier(size, class_i);
			}
			String foldname = "(train_" + size + "percent)/";
			SaveInfo.getClassCombine(foldname, res_dir + "/");
			GetUserLabel.getUserLabel(foldname + "combine_" + res_dir + "/");
			Config.deleteFile(size, res_dir);
		}
	}
}