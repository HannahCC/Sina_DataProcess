package org.cl.main.classifer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.cl.conf.Config;
import org.cl.utils.ZNTools;

public class GetPublicInfoZN {

	public static void main(String args[]) throws IOException {
		Config.init(args[0]);
		Config.isDel = Boolean.parseBoolean(args[1]);
		Config.LABEL_NUMBER = Integer.parseInt(args[2]);// label个数
		Config.FOLD = Integer.parseInt(args[3]);
		System.out.println("Config.RootPath : " + Config.RootPath);
		System.out.println("Config.LABEL_NUMBER : " + Config.LABEL_NUMBER);
		System.out.println("Config.FOLD : " + Config.FOLD);
		int testPercent = Integer.parseInt(args[4]);
		System.out.println("testPercent : " + testPercent);
		System.out.print("trainPercent : ");
		int n = args.length - 5;
		int[] trainPercent = new int[n];// 使用训练数据百分比
		for (int i = 0; i < n; i++) {
			trainPercent[i] = Integer.parseInt(args[5 + i]);
			System.out.print(trainPercent[i] + "\t");
		}
		System.out.println();
		/*
		 * Config.sourcePath = "/home/zps/experiment/"; Config.FOLD = 10; int
		 * labelnum = 5;//label个数 int[] trainPercent =
		 * {10,20,30,40,50,60,70,80,90};//使用训练数据百分比
		 */
		// 因为假设预先知道每个测试数据所属类别数目,getUserLabelFile 用于得到所有用户所属类别个数以及所属类别
		getUserLabelFile(Config.LABEL_NUMBER);
		System.out.println("Get User label File done.");
		// 得到每个类别的五折划分
		GetClassedFolds(Config.LABEL_NUMBER, testPercent, trainPercent);
		System.out.println("Get Classed Folds done.");
	}

	public static void getUserLabelFile(int labelnum) throws IOException {
		String outfile = Config.UserID + "userLabel.txt";
		int allclassType = labelnum;
		int[] classType = new int[allclassType];
		for (int i = 1; i <= allclassType; i++) {
			classType[i - 1] = i;
		}
		getUserLabel(classType, Config.UserID, outfile);

	}

	public static void getUserLabel(int[] classType, String sourcePath,
			String outfile) throws IOException {
		HashMap<Integer, HashSet<String>> label_idset_map = new HashMap<Integer, HashSet<String>>();
		HashSet<String> allId = new HashSet<String>();
		for (int c : classType) {
			String filename = sourcePath + c + ".txt";
			HashSet<String> idset = ZNTools.getSet(filename);
			allId.addAll(idset);
			label_idset_map.put(c, idset);
		}
		FileWriter fw = new FileWriter(outfile);
		for (String id : allId) {
			StringBuffer sb = new StringBuffer();
			sb.append(id + "\t\t");
			HashSet<Integer> classSet = new HashSet<Integer>();
			for (int c : classType) {
				if (label_idset_map.get(c).contains(id)) {
					classSet.add(c);
				}
			}
			sb.append(classSet.size() + "\t\t");
			for (int tc : classSet) {
				sb.append(tc + "\t");
			}

			fw.write(sb.toString() + "\r\n");

		}
		fw.flush();
		fw.close();

	}

	public static void GetClassedFolds(int labelnum, int testPercent,
			int[] trainPercent) throws IOException {
		// TODO Auto-generated method stub
		String savePath = Config.ResPath_Root;
		int foldnum = Config.FOLD;
		// getMaxfolds(Config.UserID, labelnum, foldnum, savePath);
		getfoldsTestFixed(Config.UserID, labelnum, foldnum, testPercent,
				trainPercent, savePath);

		getPublicInfoMainTrainImBalanced(labelnum, trainPercent, foldnum,
				savePath);

	}

	public static void getPublicInfoMainTrainImBalanced(int labelnum,
			int[] trainpercent, int foldnum, String savePath)
			throws IOException {
		HashSet<Integer> neglabel = new HashSet<Integer>();
		for (int i = 1; i <= labelnum; i++) {
			neglabel.add(i);
		}
		for (int tp : trainpercent) {
			for (int j = 1; j <= labelnum; j++) {
				neglabel.remove(j);
				getPublicInfo_TrainImbalanced(j, neglabel, savePath + "(train_"
						+ tp + "percent)" + foldnum + "fold_all/", foldnum,
						savePath + "(train_" + tp + "percent)/class_" + j + "/"
								+ foldnum + "fold_all/");
				neglabel.add(j);
			}
		}
	}

	public static void getPublicInfoMain() throws IOException {
		HashSet<Integer> neglabel = new HashSet<Integer>();
		for (int i = 1; i < 6; i++) {
			neglabel.add(i);
		}
		int[] trainparts = { 1, 2, 3, 4 };
		for (int tp : trainparts) {
			String sourcePath = "E:/Flickr/(train_" + tp + "part)5fold_all/";
			for (int j = 1; j < 6; j++) {
				neglabel.remove(j);
				getPublicInfo(j, neglabel, sourcePath, 5,
						"E:/Flickr/Experiments/dest/" + "(train_" + tp
								+ "part)class_" + j + "_5fold_all/");
				neglabel.add(j);
			}
		}
	}

	public static void getMaxfolds(String classSourcePath, int labelnum,
			int foldnum, String savePath) throws IOException {
		String sourcePath = classSourcePath;
		int label = labelnum;
		for (int i = 1; i <= label; i++) {
			divideListIntoParts(foldnum, sourcePath + i + ".txt", savePath
					+ foldnum + "fold_all/", "" + i);
		}
	}

	public static void getfoldsTestFixed(String sourcePath, int labelnum,
			int foldnum, int testpercent, int[] trainpercent, String savePath)
			throws IOException {
		for (int i = 1; i <= labelnum; i++) {
			divideListIntoPartsTestFixed(foldnum, sourcePath + i + ".txt",
					savePath, i, testpercent, trainpercent);
		}

	}

	public static void divideListIntoPartsTestFixed(int part, String filename,
			String savePath, int fileindex, int testpercent, int[] trainpercent)
			throws IOException {
		List<String> list = ZNTools.getneedlist(filename);
		int sum = list.size();
		int testsize = (int) ((float) testpercent / 100 * sum);
		for (int i = 0; i < part; i++) {
			Random rnd = new Random(i + 1);
			Collections.shuffle(list, rnd);
			HashSet<String> testids = new HashSet<String>();
			for (int j = 0; j < testsize; j++) {
				testids.add(list.get(j));
			}
			for (int tp : trainpercent) {
				String dir = savePath + "/(train_" + tp + "percent)" + part
						+ "fold_all/" + i + "/";
				ZNTools.mkdirs(dir);
				int trainSize = (int) ((float) tp / 100 * sum) + 1;
				HashSet<String> trainids = new HashSet<String>();
				for (int k = 0; k < trainSize; k++) {
					if (k + testsize >= sum)
						break;
					trainids.add(list.get(k + testsize));

				}
				ZNTools.saveSet(dir + fileindex + "_testids.txt", testids);
				ZNTools.saveSet(dir + fileindex + "_trainids.txt", trainids);
			}
		}
	}

	public static void divideListIntoParts(int part, String filename,
			String savePath, String fileindex) throws IOException {
		List<String> list = ZNTools.getneedlist(filename);
		Random rnd = new Random(2);
		Collections.shuffle(list, rnd);
		int partsize = list.size() / part;
		for (int i = 0; i < part; i++) {
			ZNTools.mkdirs(savePath + i + "/");
			HashSet<String> trainids = new HashSet<String>();
			HashSet<String> testids = new HashSet<String>();
			for (int j = i * partsize; j < (i + 1) * partsize; j++) {
				testids.add(list.get(j));
			}
			for (int k = 0; k < partsize * part; k++) {
				if (!testids.contains(list.get(k))
						&& trainids.size() < part * partsize) {
					trainids.add(list.get(k));
				}
			}
			ZNTools.saveSet(savePath + i + "/" + fileindex + "_testids.txt",
					testids);
			ZNTools.saveSet(savePath + i + "/" + fileindex + "_trainids.txt",
					trainids);
		}
	}

	public static void getPublicInfo(int poslabel, HashSet<Integer> neglabel,
			String sourcePath, int fold, String savePath) throws IOException {
		for (int i = 0; i < fold; i++) {
			ArrayList<String> posTrainids = new ArrayList<String>();
			ZNTools.getlist(sourcePath + i + "/" + poslabel + "_trainids.txt",
					posTrainids);
			HashSet<String> posTrainidSet_removeTest = new HashSet<String>();

			ArrayList<String> posTestids = new ArrayList<String>();
			ZNTools.getlist(sourcePath + i + "/" + poslabel + "_testids.txt",
					posTestids);

			ArrayList<String> negTrainids_balance = new ArrayList<String>();
			ArrayList<String> negTestids = new ArrayList<String>();

			for (int label : neglabel) {
				ZNTools.getlist(sourcePath + i + "/" + label + "_testids.txt",
						negTestids);
			}
			HashSet<String> allTestidSet = new HashSet<String>();
			allTestidSet.addAll(negTestids);
			allTestidSet.addAll(posTestids);
			for (String pt : posTrainids) {
				if (!allTestidSet.contains(pt)) {
					posTrainidSet_removeTest.add(pt);
				}
			}
			for (int label : neglabel) {
				ArrayList<String> negTrainids = new ArrayList<String>();
				ZNTools.getlist(sourcePath + i + "/" + label + "_trainids.txt",
						negTrainids);
				Collections.shuffle(negTrainids);
				int n = 0;
				for (int j = 0; n < posTrainidSet_removeTest.size()
						/ neglabel.size(); j++) {
					if (!allTestidSet.contains(negTrainids.get(j))) {
						negTrainids_balance.add(negTrainids.get(j));
						n++;
					}
				}
			}

			ZNTools.mkdirs(savePath + i + "/");
			FileWriter fw = new FileWriter(savePath + i + "/train.txt");
			FileWriter fw2 = new FileWriter(savePath + i + "/test.txt");
			for (String ptrain : posTrainidSet_removeTest) {
				fw.write(ptrain + "\t" + "1" + "\r\n");
			}
			for (String ntrain : negTrainids_balance) {
				fw.write(ntrain + "\t" + "2" + "\r\n");
			}

			for (String ptest : posTestids) {
				fw2.write(ptest + "\t" + "1" + "\r\n");
			}
			for (String ntest : negTestids) {
				fw2.write(ntest + "\t" + "2" + "\r\n");
			}

			fw.flush();
			fw.close();
			fw2.flush();
			fw2.close();

		}
	}

	public static void getPublicInfo_TrainImbalanced(int poslabel,
			HashSet<Integer> neglabel, String sourcePath, int fold,
			String savePath) throws IOException {
		for (int i = 0; i < fold; i++) {
			Set<String> posTrainids = new HashSet<String>();
			ZNTools.getSet(sourcePath + i + "/" + poslabel + "_trainids.txt",
					posTrainids);
			Set<String> posTestids = new HashSet<String>();
			ZNTools.getSet(sourcePath + i + "/" + poslabel + "_testids.txt",
					posTestids);

			Set<String> negTrainids = new HashSet<String>();
			Set<String> negTestids = new HashSet<String>();
			for (int label : neglabel) {
				ZNTools.getSet(sourcePath + i + "/" + label + "_testids.txt",
						negTestids);
			}
			for (int label : neglabel) {
				ZNTools.getSet(sourcePath + i + "/" + label + "_trainids.txt",
						negTrainids);
			}

			for (String id : posTrainids) {
				if (negTrainids.contains(id)) {
					negTrainids.remove(id);
				}
			}
			if (Config.isDel) {
				for (String id : negTestids) {
					if (posTrainids.contains(id)) {
						posTrainids.remove(id);
					}
					if (negTrainids.contains(id)) {
						negTrainids.remove(id);
					}
				}

				for (String id : posTestids) {
					if (posTrainids.contains(id)) {
						posTrainids.remove(id);
					}
					if (negTrainids.contains(id)) {
						negTrainids.remove(id);
					}
				}
			}
			ZNTools.mkdirs(savePath + i + "/");
			FileWriter fw = new FileWriter(savePath + i + "/train.txt");
			FileWriter fw2 = new FileWriter(savePath + i + "/test.txt");
			for (String ptrain : posTrainids) {
				fw.write(ptrain + "\t" + "1" + "\r\n");
			}
			for (String ntrain : negTrainids) {
				fw.write(ntrain + "\t" + "2" + "\r\n");
			}

			for (String ptest : posTestids) {
				fw2.write(ptest + "\t" + "1" + "\r\n");
			}
			for (String ntest : negTestids) {
				fw2.write(ntest + "\t" + "2" + "\r\n");
			}

			fw.flush();
			fw.close();
			fw2.flush();
			fw2.close();

		}
	}

}
