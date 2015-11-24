package org.cl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
		List<String> res = new LinkedList<String>();
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

}
