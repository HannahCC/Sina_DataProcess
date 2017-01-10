package org.cl.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cl.conf.Config;

public class GetUserLabel {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void getUserLabel(String feature) throws IOException {
		// int[] classType = {1,2,3,4,5};
		int fold = Config.FOLD;
		HashMap<String, String> user_Labelcount_map = new HashMap<String, String>();
		ReadInfo.getMap(Config.UserID + "userLabel.txt", user_Labelcount_map,
				"\t\t", 3, 0, 1);
		getUserPredictLabel(user_Labelcount_map, feature, fold);
		HashMap<String, String> user_Label_map = new HashMap<String, String>();
		ReadInfo.getMap(Config.UserID + "userLabel.txt", user_Label_map,
				"\t\t", 2, 0, 1);
		getUserTrueLabel(user_Label_map, feature, fold);

	}

	public static void getUserLabel(int[] classType, String sourcePath,
			String outfile) throws IOException {
		HashMap<Integer, Set<String>> label_idset_map = new HashMap<Integer, Set<String>>();
		Set<String> allId = new HashSet<String>();
		for (int c : classType) {
			String filename = sourcePath + c + ".txt";
			Set<String> idset = ReadInfo.getSet("", filename);
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

	public static void getUserPredictLabel(
			HashMap<String, String> user_Labelcount_map, String feature,
			int fold) throws IOException {
		for (int f = 0; f < fold; f++) {
			String resPath = Config.ResPath_Root;
			// SaveInfo.mkdir(resPath + feature + f + "/");
			String filename = resPath + feature + f
					+ "/allClassType_result.txt";
			List<String> pre_result_list = ReadInfo.getneedlist(filename);
			String[] spilt = null;
			String[] pros = null;

			FileWriter fw = new FileWriter(resPath + feature + f
					+ "/predict_result.txt");
			for (String s : pre_result_list) {
				StringBuffer sb = new StringBuffer();

				HashMap<String, Double> result_map = new HashMap<String, Double>();
				spilt = s.split("\t");
				sb.append(spilt[0] + "\t\t");
				for (int i = 1; i < spilt.length; i++) {
					pros = spilt[i].split(":", 2);
					result_map.put(pros[0], Double.parseDouble(pros[1]));
				}
				List<Map.Entry<String, Double>> list_Map = new ArrayList<Map.Entry<String, Double>>(
						result_map.entrySet());
				Collections.sort(list_Map,
						new Comparator<Map.Entry<String, Double>>() {
							public int compare(Map.Entry<String, Double> o1,
									Map.Entry<String, Double> o2) {
								if (o2.getValue() != null
										&& o1.getValue() != null) {
									return o2.getValue().compareTo(
											o1.getValue());
								} else {
									return -1;
								}

							}
						});
				int classcount = Integer.parseInt(user_Labelcount_map
						.get(spilt[0]));
				sb.append(classcount + "\t\t");
				for (int n = 0; n < classcount; n++) {
					sb.append(list_Map.get(n).getKey() + "\t");
				}

				fw.write(sb.toString() + "\r\n");

			}
			fw.flush();
			fw.close();
		}
	}

	public static void getUserTrueLabel(HashMap<String, String> user_Label_map,
			String feature, int fold) throws IOException {
		String resPath = Config.ResPath_Root;
		for (int f = 0; f < fold; f++) {
			// SaveInfo.mkdir(resPath + feature + f + "/");
			String filename = resPath + feature + f
					+ "/allClassType_result.txt";
			List<String> pre_result_list = ReadInfo.getneedlist(filename);
			String[] spilt = null;

			FileWriter fw = new FileWriter(resPath + feature + f
					+ "/true_result.txt");
			for (String s : pre_result_list) {
				StringBuffer sb = new StringBuffer();
				spilt = s.split("\t");
				sb.append(spilt[0] + "\t\t");
				sb.append(user_Label_map.get(spilt[0]));
				fw.write(sb.toString() + "\r\n");

			}
			fw.flush();
			fw.close();
		}
	}

}
