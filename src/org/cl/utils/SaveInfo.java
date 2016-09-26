package org.cl.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cl.conf.Config;
import org.cl.model.ResultNode;

public class SaveInfo {
	static int flag = 1;
	static StringBuffer option_log_buff = new StringBuffer();
	static StringBuffer res_log_buff = new StringBuffer();

	public static void option_log(String res) {
		System.out.println(res);
		option_log_buff.append(res + "\r\n");
	}

	public static void res_log(String res, boolean isFlag) {
		if (!isFlag) {
			res_log_buff.append("\r\n" + res + "\r\n");
			flag = 1;
		} else if ((flag & 4) == 0) {
			res_log_buff.append(res + "\t");
			flag++;
		} else {
			res_log_buff.append(res + "\r\n");
			flag = 1;
		}
	}

	public static void log_buff_writer(String res_dir, String res_file,
			boolean isAppend) {
		try {
			File res = new File(res_dir + res_file);
			BufferedWriter resw = new BufferedWriter(new FileWriter(res,
					isAppend));
			resw.write(option_log_buff.toString());
			if (res_log_buff.length() > 0) {
				resw.write("/******************res_log_buff***************************************/\r\n");
				resw.write(res_log_buff.toString());
			}
			resw.flush();
			resw.close();
			option_log_buff = new StringBuffer();
			res_log_buff = new StringBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void data_writer(int labelid,
			Map<String, StringBuffer> id_feature, int type) {
		if (id_feature == null || id_feature.size() == 0) {
			option_log("*****************there is no feature!!************************");
			return;
		}
		String data_filename = "";
		String id_filename = "";
		if (type == 0) {
			data_filename = "testing_data.txt";
			id_filename = "testing_id.txt";
		} else if (type == 1) {
			data_filename = "training_data.txt";
			id_filename = "training_id.txt";
		} else if (type == 2) {
			data_filename = "learning_data.txt";
			id_filename = "learning_id.txt";
		} else {
			option_log("*****************data type is wrong!!************************");
			return;
		}
		File f1 = new File(Config.ResPath + data_filename);
		File f2 = new File(Config.ResPath + id_filename);
		try {
			boolean isAppend = labelid == Config.LABELS[0] ? false : true;
			BufferedWriter w1 = new BufferedWriter(new FileWriter(f1, isAppend));
			BufferedWriter w2 = new BufferedWriter(new FileWriter(f2, isAppend));
			Iterator<Entry<String, StringBuffer>> it = id_feature.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<String, StringBuffer> entry = it.next();
				w1.write(labelid + "\t" + entry.getValue().toString() + "\r\n");
				w2.write(entry.getKey() + "\t" + labelid + "\r\n");
			}
			w1.flush();
			w1.close();
			w2.flush();
			w2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void id_writer(String filename, Set<String> id_set) {
		File f = new File(filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for (String id : id_set) {
				w.write(id + "\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void id_writer(String filename, Set<String> id_set,
			int label, boolean append) {
		File f = new File(filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f, append));
			for (String id : id_set) {
				w.write(id + "\t" + label + "\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SafeVarargs
	public static void saveSets(String dirname, Set<String>... id_sets)
			throws IOException {
		mkdir(dirname);
		int i = 1;
		for (Set<String> id_set : id_sets) {
			if (id_set.size() == 0) {
				continue;
			}
			File f = new File(dirname + "\\" + i + ".txt");
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for (String id : id_set) {
				w.write(id + "\r\n");
			}
			w.flush();
			w.close();
			i++;
		}
	}

	public static void result_writer(String dirname, String result_filename,
			String id_filename, Map<String, String> id_actual_res,
			Map<String, ResultNode> id_predict_res) {
		mkdir(dirname);
		File f1 = new File(dirname + "\\" + result_filename);
		File f2 = new File(dirname + "\\" + id_filename);
		try {
			BufferedWriter w1 = new BufferedWriter(new FileWriter(f1));
			BufferedWriter w2 = new BufferedWriter(new FileWriter(f2));
			for (Entry<String, ResultNode> res : id_predict_res.entrySet()) {
				w1.write(res.getValue().toString(" ") + "\r\n");
				String id = res.getKey();
				w2.write(id + "\t" + id_actual_res.get(id) + "\r\n");
			}
			w1.flush();
			w1.close();
			w2.flush();
			w2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void result_writer(String dirname, String filename,
			List<String> predict_res) {
		mkdir(dirname);
		File f = new File(dirname + "\\" + filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for (String res : predict_res) {
				w.write(res + "\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveMap(String dir, String filename,
			Map<Integer, Integer> feature_map) {
		File f = new File(dir + filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			Iterator<Entry<Integer, Integer>> it = feature_map.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<Integer, Integer> entry = it.next();
				w.write(entry.getKey() + "\t" + entry.getValue() + "\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveList(String dir, String filename, List<String> list) {
		mkdir(dir);
		File f = new File(dir + filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for (String item : list) {
				w.write(item + "\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveList(String dir, String filename, List<String> list,
			int size) {
		mkdir(dir);
		File f = new File(dir + filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			w.write(size + "\r\n");
			for (String item : list) {
				w.write(item + "\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveSet(String dir, String filename, Set<String> id_set) {
		mkdir(dir);
		File f = new File(dir + filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for (String item : id_set) {
				w.write(item + "\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveSet(String dir, String filename, Set<String> set,
			int max) {
		mkdir(dir);
		File f = new File(dir + filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			int i = 0;
			for (String item : set) {
				i++;
				w.write(item + "\r\n");
				if (i >= max)
					break;
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void mkdir(String dir) {
		File dir_root1 = new File(dir);
		if (!dir_root1.exists())
			dir_root1.mkdir();
	}

	public static void fileCopy(File src, String des_dir) {
		try {
			FileInputStream fi = new FileInputStream(src);
			FileChannel in = fi.getChannel();
			mkdir(des_dir);
			File t = new File(des_dir + "\\" + src.getName());
			FileOutputStream fo = new FileOutputStream(t);
			FileChannel out = fo.getChannel();
			in.transferTo(0, in.size(), out);
			fi.close();
			in.close();
			fo.close();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void fileCrop(String src_file, String des_dir) {
		try {
			File src = new File(src_file);
			FileInputStream fi = new FileInputStream(src);
			FileChannel in = fi.getChannel();
			mkdir(des_dir);
			File t = new File(des_dir + "\\" + src.getName());
			FileOutputStream fo = new FileOutputStream(t);
			FileChannel out = fo.getChannel();
			in.transferTo(0, in.size(), out);
			fi.close();
			in.close();
			fo.close();
			out.close();
			src.delete();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void saveArrays(String filename, double[][] res) {
		File f = new File(filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for (double[] r : res) {
				w.write(Arrays.toString(r) + "\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void getClassCombine(String foldname, String classifier)
			throws IOException {
		String ResPath = Config.ResPath_Root;

		String combine_dir = ResPath + foldname + "combine_" + classifier;
		SaveInfo.mkdir(combine_dir);
		for (int f = 0; f < Config.FOLD; f++) {
			String combine_dir_fold = combine_dir + f + "/";
			SaveInfo.mkdir(combine_dir_fold);
			FileWriter fw = new FileWriter(combine_dir_fold
					+ "allClassType_result.txt");
			Map<Integer, Map<String, String>> label_id_resultlist = new HashMap<Integer, Map<String, String>>();
			Set<String> testidSet = null;
			for (int c : Config.CLASSES) {
				String test_id_file = ResPath + foldname + "/class_" + c + "/"
						+ Config.FOLD + "fold_all/" + f + "/test.txt";
				String result_file = ResPath + foldname + "/class_" + c + "/"
						+ classifier + f + "/";
				testidSet = ReadInfo.getSet(test_id_file, "\\s{1,}", 0);
				HashMap<String, String> IdResult_map = new HashMap<String, String>();
				List<String> classResult_list = ReadInfo.getneedlist(
						result_file + "/result_lg.txt", "\\s{1,}", 1);
				List<String> testid_list = ReadInfo.getneedlist(result_file
						+ "/testing_id.txt", "\\s{1,}", 0);
				if (testid_list.size() == classResult_list.size()) {
					for (int i = 0; i < testid_list.size(); i++) {
						IdResult_map.put(testid_list.get(i),
								classResult_list.get(i));
					}
				} else {
					System.out
							.println("the testid size doesn't equal result_lg size");
				}
				label_id_resultlist.put(c, IdResult_map);
			}

			for (String testid : testidSet) {
				StringBuffer sb = new StringBuffer();
				sb.append(testid + "\t");
				for (int c : Config.CLASSES) {
					sb.append(c + ":" + label_id_resultlist.get(c).get(testid)
							+ "\t");
				}
				fw.write(sb.toString() + "\r\n");
			}
			fw.flush();
			fw.close();
		}

	}
}
