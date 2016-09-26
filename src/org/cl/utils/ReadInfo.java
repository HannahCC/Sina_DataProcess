package org.cl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ReadInfo {
	public static void getSet(String dir,String filename,Set<String> res) throws IOException {
		File f = new File(dir+filename);
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			res.add(line);
		}
		r.close();
	}
	public static Set<String> getSet(String dir,String filename) throws IOException {
		Set<String> res = new HashSet<String>();
		File f = new File(dir+filename);
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			res.add(line);
		}
		r.close();
		return res;
	}
	public static Set<String> getSet(int type,String dir,String filename) throws IOException {
		Set<String> res = new HashSet<String>();
		File f = new File(dir+filename);
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		while((line = r.readLine())!=null){
			if(line.contains("-----"+type)){break;}
		}
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			if(line.contains("----"))break;
			res.add(line);
		}
		r.close();
		return res;
	}
	public static Set<String> getSet(String filename, String regex, int i) throws IOException {
		Set<String> res = new HashSet<String>();
		File f = new File(filename);
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			res.add(line.split(regex)[i]);
		}
		r.close();
		return res;
	}

	public static List<String> getList(String dir,String filename) throws IOException {
		List<String> res = new ArrayList<String>();
		File f = new File(dir+filename);
		if(!f.exists())return res;
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			res.add(line);
		}
		r.close();
		return res;
	}
	public static List<String> getList(String dir, String filename,String regex, int i) throws IOException {
		List<String> res = new ArrayList<String>();
		File f = new File(dir+filename);
		if(!f.exists())return res;
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			res.add(line.split(regex)[i]);
		}
		r.close();
		return res;
	}
	public static List<List<String>> getListList(String dir, String filename, int number_of_weibo) throws IOException {
		List<List<String>> res = new ArrayList<List<String>>();
		File f = new File(dir+filename);
		if(!f.exists())return res;
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		int size = 0;
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			List<String> resi = new ArrayList<String>();
			String[] items = line.split("\t");
			for(String item : items){resi.add(item);}
			res.add(resi);
			if((++size)>=number_of_weibo)break;
		}
		r.close();
		return res;
	}
	public static int getLineNum(String dir, String filename) throws IOException {
		int size = 0;
		File f = new File(dir+filename);
		if(!f.exists())return 0;
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			size++;
		}
		r.close();
		return size;
	}
	public static int getUserNum(String dir, String filename, String regex, int i) throws IOException {
		int size = 0;
		File f = new File(dir+filename);
		if(!f.exists())return 0;
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			String[]res = line.split(regex);
			if(res.length==i){
				size++;
			}
		}
		r.close();
		return size;
	}
	public static Map<String,String> getMap(String dir, String filename,String regex,int key,int value) throws IOException{
		File f=new File(dir+filename);
		if(!f.exists())return null;
		BufferedReader br = new BufferedReader(new FileReader(f));
		Map<String,String> lines = new HashMap<String,String>();
		String line="";
		while((line=br.readLine())!=null)
		{
			if(!(line.equals(""))){
				String[] item = line.split(regex);
				lines.put(item[key],item[value]);//(2,土豪，我们做朋友吧)	
			}
		}
		br.close();
		return lines;
	}

	public static void getMap(String filename, HashMap<String,String> map, String seperater,int spilttimes, int idx1, int idx2) throws IOException{
		File r=new File(filename);
		BufferedReader br=null;
		br=new BufferedReader(new FileReader(r));
		String line="";
		String[] spilts = null;
		while((line=br.readLine())!=null)
		{
			if(!(line.equals(""))){
				spilts = line.split(seperater,spilttimes);
				map.put(spilts[idx1], spilts[idx2]);
			}
		}
		br.close();
	}
	public static void getMapMap(String filename, Map<String, Map<String, Integer>> map_map,String regex1,String regex2,int key_index) throws IOException {
		File f1 = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(f1));
		String line;
		while((line = br.readLine())!=null){
			String[] items = line.split(regex1);
			Map<String, Integer> map = new HashMap<String, Integer>();
			for(int i=key_index+1;i<items.length;i++){
				String[] items2 = items[i].split(regex2);
				Utils.putInMap(map, items2[0], Integer.parseInt(items2[1]));
			}
			map_map.put(items[key_index], map);
		}
		br.close();
	}
	public static void getArrayMap(Map<String, double[]> array_map, int size, String filename,String regex1,String regex2,int i,int j) throws IOException {
		File f1 = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(f1));
		String line;
		while((line = br.readLine())!=null){
			double[] array = new double[size];
			String[] items = line.split(regex1);
			String[] item = items[j].split(regex2);
			for(int k=0;k<size;k++){array[k]=Double.parseDouble(item[k]);}
			array_map.put(items[i], array);
		}
		br.close();
	}
	public static Map<String,Double> getMapDouble(String dir, String filename,String regex,int key,int value) throws IOException{
		File f=new File(dir+filename);
		if(!f.exists())return null;
		BufferedReader br = new BufferedReader(new FileReader(f));
		Map<String,Double> lines = new HashMap<String,Double>();
		String line;
		while((line=br.readLine())!=null)
		{
			if(!(line.equals(""))){
				String[] item = line.split(regex);
				if(item.length>=2){
					lines.put(item[key],Double.parseDouble(item[value]));
				}else{
					System.out.println("This line can be parse corecctly:"+line);
				}
			}
		}
		br.close();
		return lines;
	}
	public static Map<String,Double> getCHIMap(String dir, String filename,String regex,int key,int value, double CHI_threshold) throws IOException{
		File f=new File(dir+filename);
		if(!f.exists())return null;
		BufferedReader br=new BufferedReader(new FileReader(f));
		String line=br.readLine();
		int size = (int) (Integer.parseInt(line)*CHI_threshold);//第一行存放特征的个数，整型
		int i = 0;
		Map<String,Double> lines = new HashMap<String,Double>();
		while((line=br.readLine())!=null)
		{
			if(!(line.equals(""))){
				String[] item = line.split(regex);
				lines.put(item[key],Double.parseDouble(item[value]));	
			}
			if((++i)==size)break;
		}
		br.close();
		return lines;
	}
	public static Map<String,String> getResultMap(String dir, String filename,String regex,int key,int value) throws IOException{
		File f=new File(dir+filename);
		if(!f.exists())return null;
		BufferedReader br = new BufferedReader(new FileReader(f));
		Map<String,String> lines = new HashMap<String,String>();
		String line="";
		while((line=br.readLine())!=null)
		{
			if(!(line.equals(""))){
				String[] item = line.split(regex);
				lines.put(item[key],item[value]);//(2,土豪，我们做朋友吧)	
			}
		}
		br.close();
		return lines;
	}
	public static Map<String,Set<String>> getLabelIdsMap(String dir,String fileName) throws IOException{
		 Map<String,Set<String>> labelIdsMap=new HashMap<String,Set<String>>();
		File f = new File(dir+fileName);
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			String[] elms=line.split("\\s{1,}");
			String id=elms[0].trim();
			String label=elms[1].trim();
			Set<String> res=labelIdsMap.get(label);
			if(res==null){
				res = new HashSet<String>();
			}
			res.add(id);
			labelIdsMap.put(label, res);
		}
		r.close();
		
		return labelIdsMap;
	}
	public static List<String> getneedlist(String filename){
		List<String> needList = new ArrayList<String>();
		File file = new File(filename);
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf-8");
			BufferedReader inOne = new BufferedReader(read);

			String s = null;
			while((s = inOne.readLine())!=null){
				needList.add(s);
			}
//			System.out.println("the size of file "+filename+"is "+needList.size());
			inOne.close();
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return needList;

	}
	public static List<String> getneedlist(String filename,String reg,int idx){
		List<String> needList = new ArrayList<String>();
		File file = new File(filename);
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf-8");
			BufferedReader inOne = new BufferedReader(read);

			String s = null;
			while((s = inOne.readLine())!=null){
				needList.add(s.split(reg)[idx]);
			}
//			System.out.println("the size of file "+filename+"is "+needList.size());
			inOne.close();
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return needList;

	}
}
