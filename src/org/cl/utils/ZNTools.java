package org.cl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

public class ZNTools {
	public static List<String> getneedlist(String filename) {
		List<String> needList = new ArrayList<String>();
		File file = new File(filename);
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), "utf-8");
			BufferedReader inOne = new BufferedReader(read);

			String s = null;
			while ((s = inOne.readLine()) != null) {
				if(needList.contains(s))continue;
				needList.add(s);
			}
			// System.out.println("the size of file "+filename+"is "+needList.size());
			inOne.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return needList;

	}

	public static List<String> getneedlist(String filename, String reg, int idx) {
		List<String> needList = new ArrayList<String>();
		File file = new File(filename);
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), "utf-8");
			BufferedReader inOne = new BufferedReader(read);

			String s = null;
			while ((s = inOne.readLine()) != null) {
				needList.add(s.split(reg)[idx]);
			}
			// System.out.println("the size of file "+filename+"is "+needList.size());
			inOne.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return needList;

	}

	public static void getlist(String filename, ArrayList<String> list) {
		File file = new File(filename);
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), "utf-8");
			BufferedReader inOne = new BufferedReader(read);

			String s = null;
			while ((s = inOne.readLine()) != null) {
				list.add(s);
			}
			inOne.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void mkdirs(String filename) {
		File dir = new File(filename);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static void getMap(String filename, HashMap<String, String> map,
			String keys, String values) throws IOException {
		File r = new File(filename);
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(r));
		String line = "";
		while ((line = br.readLine()) != null) {
			if (!(line.equals(""))) {
				JSONObject json = JSONObject.fromObject(line);
				String key = json.getString(keys);
				String value = json.getString(values);
				map.put(key, value);
			}
		}
		br.close();
	}

	public static void getSet(String filename, Set<String> set)
			throws IOException {
		File r = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(r));
		String line = "";
		while ((line = br.readLine()) != null) {
			if (!(line.equals(""))) {
				set.add(line);
			}
		}
		br.close();
	}

	public static void getSet(String filename, HashSet<String> set,
			String seperater, int idx) throws IOException {
		File r = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(r));
		String line = "";
		while ((line = br.readLine()) != null) {
			if (!(line.equals(""))) {
				set.add(line.split(seperater)[idx]);
			}
		}
		br.close();
	}

	public static HashSet<String> getSet(String filename, String seperater,
			int idx) throws IOException {
		HashSet<String> set = new HashSet<String>();
		File r = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(r));
		String line = "";
		while ((line = br.readLine()) != null) {
			if (!(line.equals(""))) {
				set.add(line.split(seperater)[idx]);
			}
		}
		br.close();
		return set;
	}

	public static HashSet<String> getSet(String filename) throws IOException {
		HashSet<String> set = new HashSet<String>();
		File r = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(r));
		String line = "";
		while ((line = br.readLine()) != null) {
			if (!(line.equals(""))) {
				set.add(line);
			}
		}
		br.close();
		return set;
	}

	public static void getMap(String filename, HashMap<String, String> map,
			String seperater) throws IOException {
		File r = new File(filename);
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(r));
		String line = "";
		String[] spilts = null;
		while ((line = br.readLine()) != null) {
			if (!(line.equals(""))) {
				spilts = line.split(seperater, 2);
				map.put(spilts[0], spilts[1]);
			}
		}
		br.close();
	}

	public static void getMap(String filename, HashMap<String, String> map,
			String seperater, int spilttimes, int idx1, int idx2)
			throws IOException {
		File r = new File(filename);
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(r));
		String line = "";
		String[] spilts = null;
		while ((line = br.readLine()) != null) {
			if (!(line.equals(""))) {
				spilts = line.split(seperater, spilttimes);
				map.put(spilts[idx1], spilts[idx2]);
			}
		}
		br.close();
	}

	public static void saveList(String filename, ArrayList<String> list)
			throws IOException {
		FileWriter fw = new FileWriter(filename);
		for (String s : list) {
			fw.write(s + "\r\n");
		}
		fw.flush();
		fw.close();

	}

	public static void saveSet(String filename, HashSet<String> set)
			throws IOException {
		FileWriter fw = new FileWriter(filename);
		for (String s : set) {
			fw.write(s + "\r\n");
		}
		fw.flush();
		fw.close();

	}
}
