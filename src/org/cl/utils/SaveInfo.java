package org.cl.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cl.conf.Config;
import org.cl.model.ResultNode;
public class SaveInfo {
	static StringBuffer buff = new StringBuffer();
	public static void saveResult(int classid, int train_size, int test_size) {
		String res = classid+"---"+train_size+"---"+test_size;
		System.out.println(res);
		buff.append(res+"\r\n");
	}
	public static void saveResult(String res) {
		System.out.println(res);
		buff.append(res+"\r\n");
	}
	public static void saveResult(String res_dir,String res_file) {
		try {
			File res = new File(res_dir+res_file);
			BufferedWriter resw = new BufferedWriter(new FileWriter(res,true));
			resw.write(buff.toString());
			resw.flush();
			resw.close();
			buff = new StringBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void saveResult(String res_dir,String res_file,StringBuffer additional_buff) {
		try {
			File res = new File(res_dir+res_file);
			BufferedWriter resw = new BufferedWriter(new FileWriter(res,true));
			resw.write(additional_buff.toString());
			resw.flush();
			resw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void data_writer(int lableid_index, Map<String,StringBuffer> id_feature,int type) {
		if(id_feature==null||id_feature.size()==0){
			saveResult("*****************there is no feature!!************************");
			return;
		}
		String data_filename = "testing_data.txt";String id_filename = "testing_id.txt";
		if(type==1){data_filename = "training_data.txt";id_filename = "training_id.txt";}
		if(type==2){data_filename = "testing_data_fake.txt";id_filename = "testing_id_fake.txt";}
		File f1 = new File(Config.ResPath+data_filename);
		File f2 = new File(Config.ResPath+id_filename);
		try {
			BufferedWriter w1 = new BufferedWriter(new FileWriter(f1,true));
			BufferedWriter w2 = new BufferedWriter(new FileWriter(f2,true));
			Iterator<Entry<String, StringBuffer>> it = id_feature.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, StringBuffer> entry = it.next();
				w1.write(lableid_index+"\t"+entry.getValue().toString()+"\r\n");
				w2.write(entry.getKey()+"\t"+lableid_index+"\r\n");
			}
			w1.flush();w1.close();
			w2.flush();w2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void id_writer(String filename, Set<String> id_set) {
		File f = new File(Config.ResPath_Root+filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for(String id : id_set){
				w.write(id+"\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@SafeVarargs
	public static void saveSets(String dirname,Set<String>...id_sets) throws IOException {
		mkdir(dirname);
		int i = 1;
		for(Set<String>id_set:id_sets){
			if(id_set.size()==0){continue;}
			File f = new File(dirname+"\\"+i+".txt");
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for(String id : id_set){
				w.write(id+"\r\n");
			}
			w.flush();
			w.close();
			i++;
		}
	}
	public static void result_writer(String dirname, String result_filename,String id_filename,Map<String,String> id_actual_res,Map<String,ResultNode> id_predict_res) {
		mkdir(dirname);
		File f1 = new File(dirname+"\\"+result_filename);
		File f2 = new File(dirname+"\\"+id_filename);
		try {
			BufferedWriter w1 = new BufferedWriter(new FileWriter(f1));
			BufferedWriter w2 = new BufferedWriter(new FileWriter(f2));
			for(Entry<String,ResultNode>res : id_predict_res.entrySet()){
				w1.write(res.getValue().toString(" ")+"\r\n");
				String id = res.getKey();
				w2.write(id+"\t"+id_actual_res.get(id)+"\r\n");
			}
			w1.flush();w1.close();
			w2.flush();w2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void result_writer(String dirname, String filename,List<String> predict_res) {
		mkdir(dirname);
		File f = new File(dirname+"\\"+filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for(String res : predict_res){
				w.write(res+"\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void saveMap(String dir, String filename, Map<Integer, Integer> feature_map) {
		File f = new File(dir+filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			Iterator<Entry<Integer, Integer>> it = feature_map.entrySet().iterator();
			while(it.hasNext()){
				Entry<Integer, Integer> entry = it.next();
				w.write(entry.getKey()+"\t"+entry.getValue()+"\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void saveList(String dir, String filename,List<String> list) {
		mkdir(dir);
		File f = new File(dir+filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for(String item : list){
				w.write(item+"\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveList(String dir, String filename,List<String> list, int size) {
		mkdir(dir);
		File f = new File(dir+filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			w.write(size+"\r\n");
			for(String item : list){
				w.write(item+"\r\n");
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
		File f = new File(dir+filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for(String item : id_set){
				w.write(item+"\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void saveSet(String dir, String filename, Set<String> set, int max) {
		mkdir(dir);
		File f = new File(dir+filename);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			int i = 0;
			for(String item : set){
				i++;
				w.write(item+"\r\n");
				if(i>=max)break;
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
		if(!dir_root1.exists())dir_root1.mkdir();
	}
	public static void fileCopy(File src,String des_dir){
		try {
			FileInputStream fi = new FileInputStream(src);
			FileChannel in = fi.getChannel();
			mkdir(des_dir);	
			File t = new File(des_dir+"\\"+src.getName());
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

	public static void fileCrop(String src_file,String des_dir) {
		try {
			File src = new File(src_file);
			FileInputStream fi = new FileInputStream(src);
			FileChannel in = fi.getChannel();
			mkdir(des_dir);	
			File t = new File(des_dir+"\\"+src.getName());
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

}
