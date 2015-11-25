package org.cl.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

public class Utils {
	static DecimalFormat df = new DecimalFormat("0.0000000000");
	//将id_Set按比例ratio分成两个id_set 
	public static List<Set<String>> spilt(Set<String> id_set, float ratio) {
		int num = id_set.size();
		int i_num = (int) (num*ratio);
		List<Set<String>> id_set_list = new ArrayList<Set<String>>();
		Set<String> id_set_1 = new TreeSet<String>();
		Set<String> id_set_2 = new TreeSet<String>();
		int i=0;
		Iterator<String> it = id_set.iterator();
		while(it.hasNext()){
			String id = it.next();
			if(i<i_num){
				id_set_1.add(id);
			}else{
				id_set_2.add(id);
			}
			i++;
		}
		id_set_list.add(id_set_1);
		id_set_list.add(id_set_2);
		return id_set_list;
	}
	//将id_Set分成两个id_set,第一个number个
	public static List<Set<String>> spilt(int number, Set<String> id_set) {
		List<Set<String>> id_set_list = new ArrayList<Set<String>>();
		Set<String> id_set_1 = new TreeSet<String>();
		Set<String> id_set_2 = new TreeSet<String>();
		int i=0;
		Iterator<String> it = id_set.iterator();
		while(it.hasNext()){
			String id = it.next();
			if(i<number){
				id_set_1.add(id);
			}else{
				id_set_2.add(id);
			}
			i++;
		}
		id_set_list.add(id_set_1);
		id_set_list.add(id_set_2);
		return id_set_list;
	}
	public static List<Set<String>> spilt(Set<String> id_set, int fold) {
		int num = id_set.size();
		int i_num = num/fold;
		List<Set<String>> id_set_list = new ArrayList<Set<String>>();
		Set<String> id_set_i = new TreeSet<String>();
		int i=0;
		Iterator<String> it = id_set.iterator();
		while(it.hasNext()){
			String id = it.next();
			if(i<i_num||id_set_list.size()+1==fold){
				id_set_i.add(id);
			}else if(id_set_list.size()<fold){
				id_set_list.add(id_set_i);
				id_set_i = new TreeSet<String>();
				i=0;
				id_set_i.add(id);
			}
			i++;
		}
		id_set_list.add(id_set_i);
		return id_set_list;
	}
	//从数据集id_set中选取count个数据作为子集
	public static Set<String> subSet(Set<String> id_set,int count) {
		Set<String> new_set = new TreeSet<String>();
		int n = id_set.size();
		if(count>=n){return id_set;}
		for(String id : id_set){
			new_set.add(id);
			if(new_set.size()==count)break;
		}
		return new_set;
	}

	//从n个数中，取m个随机数
	public static Set<Integer> getRandomNumer(int n,int m) {
		Set<Integer> number = new TreeSet<Integer>();
		if(m>n/2){//如从10个数中取8个随机数，转化为从10个数中取2个
			for(int i=0;i<n;i++){number.add(i);}
			Random r = new Random();
			while(number.size()>m){
				number.remove(Math.abs(r.nextInt())%n);
			}
		}else{
			Random r = new Random();
			while(number.size()<=m){
				number.add(Math.abs(r.nextInt())%n);
			}
		}
		return number;
	}


	//将feature_list中编码相同的term合并起来，并按编码值从小到大排序
	//term : 675:1	804:4	849:1	
	public static void merge(List<String> feature_list, int stasEncode) {
		Map<Integer,Integer> feature_map = new TreeMap<Integer,Integer>();
		for(String term : feature_list){//term : 675:1	804:1	849:1	
			String[] vertor = term.split("\\s");
			for(String v : vertor){
				int index = Integer.parseInt(v.split(":")[0]);
				int count = Integer.parseInt(v.split(":")[1]);
				int newindex = index+stasEncode;
				if(feature_map.containsKey(newindex)){feature_map.put(newindex, feature_map.get(newindex)+count);}
				else{feature_map.put(newindex, count);}
			}
		}
		feature_list.clear();
		Iterator<Entry<Integer, Integer>> it = feature_map.entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, Integer> entry = it.next();
			feature_list.add(entry.getKey()+":"+entry.getValue()+"\\s");
		}
	}

	public static String reencode(List<String> feature_list, int start_index){
		StringBuffer sb = new StringBuffer();
		for(String term : feature_list){//term : 675:1
			int index = Integer.parseInt(term.split(":")[0]);
			double count = Double.parseDouble(term.split(":")[1]);
			int newindex = index+start_index;
			sb.append(newindex+":"+count+"\t");
		}
		return sb.toString();
	}
	public static List<String> stringFeaturetoList(String feature) {
		List<String> feature_list = new ArrayList<String>();
		String[] vertor = feature.split("\\s");
		for(String v : vertor){
			int index = Integer.parseInt(v.split(":")[0]);
			double count = Double.parseDouble(v.split(":")[1]);
			feature_list.add(index+":"+count);
		}
		return feature_list;
	}
	//得到group_size的全排列，如group_size={1,2} ，结果为{{0,0}，{0,1}，{0,2}，{1,0}，{1,1}，{1,2}}
	public static int[][] recursion_getFullArray(int[] group_size) {
		int group_num = group_size.length;
		int[][] fullArray = null;
		if(group_num==1){
			fullArray = new int[group_size[0]][group_num];
			for(int i=0;i<group_size[0];i++){
				fullArray[i][0] = i;
			}
		}else{
			int[] new_group_size = getSubArray(group_size,1,group_num-1);//得到后面n-1个元素的全排列
			int[][] subfullArray = recursion_getFullArray(new_group_size);
			fullArray = new int[group_size[0]*subfullArray.length][group_num];
			for(int i=0;i<group_size[0];i++){
				for(int j=0;j<subfullArray.length;j++){
					fullArray[i*subfullArray.length+j] = mergeArray(i,subfullArray[j]);
				}
			}
		}
		return fullArray;
	}
	//将元素value分别于array组合，如value=1，array={{0,0}，{0,1}，{1,0}，{1,1}} ==》{{1,0,0}，{1,0,1}，{1,1,0}，{1,1,1}}
	public static int[] mergeArray(int value, int[] array) {
		int len = array.length+1;
		int[] new_array = new int[len];
		new_array[0] = value;
		for(int i=1;i<len;i++){
			new_array[i] = array[i-1];
		}
		return new_array;
	}
	//得到group_size中由第i个元素开始，长度为length的子元素集合
	public static int[] getSubArray(int[] group_size,int s,int length) {
		int[] new_group_size = new int[length];
		for(int i=0;i<length;i++){
			new_group_size[i] = group_size[i+s];
		}
		return new_group_size;
	}
	//将n个特征list按顺序转化为String
	@SafeVarargs
	public static String lists_ToString(List<String> ... lists) {
		StringBuffer str = new StringBuffer();
		for(List<String> list : lists){
			if(list==null||list.size()==0)continue;
			for(String emo : list){
				str.append(emo);//emo特征向量自带"\\s"
			}
		}
		return str.toString();
	}
	public static String array_ToString(int[] array) {
		StringBuffer str = new StringBuffer();
		for(int i : array){
			str.append("_"+i);
		}
		return str.toString();
	}
	public static String map_toString(int lableid_index, Map<String, StringBuffer> map) {
		if(map==null||map.size()==0)return null;
		StringBuffer str = new StringBuffer();
		Iterator<Entry<String, StringBuffer>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, StringBuffer> entry = it.next();
			str.append(lableid_index+"\\s"+entry.getValue().toString()+"\r\n");
		}
		return str.toString();
	}
	public static void fileCopy(File s,int year, String string) throws IOException{
		FileInputStream fi = new FileInputStream(s);
		FileChannel in = fi.getChannel();

		File dir_birth = new File("E:\\Working\\Project_DataObtain\\Sina_res\\619843_Brithday\\"+year+"\\");
		if(!dir_birth.exists()){dir_birth.mkdir();}
		File t = new File("E:\\Working\\Project_DataObtain\\Sina_res\\619843_Brithday\\"+year+"\\"+s.getName());
		FileOutputStream fo = new FileOutputStream(t);
		FileChannel out = fo.getChannel();
		in.transferTo(0, in.size(), out);
		fi.close();
		in.close();
		fo.close();
		out.close();
	}

	public static void mergeMap(Map<String, Integer> map,Map<String, Integer> new_map) {
		Iterator<Entry<String, Integer>> it = new_map.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Integer> entry = it.next();
			String key = entry.getKey();
			int value = entry.getValue();
			if(map.containsKey(key)){map.put(key, map.get(key)+value);}
			else{map.put(key,value);}
		}
	}
	public static void mergeDict(Map<String, Integer> dict,Map<String, Integer> dict2) {
		Iterator<Entry<String, Integer>> it = dict2.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Integer> entry = it.next();
			dict.put(entry.getKey(),entry.getValue());
		}
	}

	public static void mapSortByValueInteger(List<String> list,Map<String, Integer> map) {
		List<Entry<String,Integer>> list_tmp = new ArrayList<Entry<String,Integer>>(map.entrySet());
		Collections.sort(list_tmp,new Comparator<Entry<String,Integer>>(){
			public int compare(Entry<String,Integer> arg0,Entry<String,Integer> arg1) {
				double r = arg1.getValue()-arg0.getValue();
				if(r>0)return 1;
				else if(r<0)return -1;
				else return 0;
			}
		});
		for(Entry<String,Integer> entry : list_tmp){
			String item = entry.getKey()+":"+entry.getValue();
			list.add(item);
		}
	}
	//按降序排列
	public static void mapSortByValue(List<String> list,Map<String,Double> map) {
		List<Entry<String,Double>> list_tmp = new ArrayList<Entry<String,Double>>(map.entrySet());
		Collections.sort(list_tmp,new Comparator<Entry<String,Double>>(){
			public int compare(Entry<String,Double> arg0,Entry<String,Double> arg1) {
				double r = arg1.getValue()-arg0.getValue();
				if(r>0)return 1;
				else if(r<0)return -1;
				else return 0;
			}
		});
		for(Entry<String,Double> entry : list_tmp){
			String item = entry.getKey()+":"+entry.getValue();
			list.add(item);
		}
	}
	//按降序排列
	public static void mapSortByValue(List<String> list,Map<String,Double> map,final Map<String, Integer> additional_map) {
		List<Entry<String,Double>> list_tmp = new ArrayList<Entry<String,Double>>(map.entrySet());
		Collections.sort(list_tmp,new Comparator<Entry<String,Double>>(){
			public int compare(Entry<String,Double> arg0,Entry<String,Double> arg1) {
				double r = arg1.getValue()-arg0.getValue();
				if(r>0)return 1;
				else if(r<0)return -1;
				else{
					int r2 = 0;
					if(additional_map.containsKey(arg0.getKey())&&additional_map.containsKey(arg1.getKey())){
						r2 = additional_map.get(arg0.getKey()) - additional_map.get(arg1.getKey());
					}
					if(r2>0)return 1;
					else if(r2<0)return -1;
					else{
						return arg0.getKey().compareTo(arg1.getKey());
					}
				}
			}
		});
		for(Entry<String,Double> entry : list_tmp){
			String item = entry.getKey()+":"+df.format(entry.getValue());
			list.add(item);
		}
	}
	public static void mapSortByValue(Map<String, Double> map,double threshold) {
		List<Entry<String,Double>> list_tmp = new ArrayList<Entry<String,Double>>(map.entrySet());
		Collections.sort(list_tmp,new Comparator<Entry<String,Double>>(){
			public int compare(Entry<String,Double> arg0,Entry<String,Double> arg1) {
				double r = arg1.getValue()-arg0.getValue();
				if(r>0)return 1;
				else if(r<0)return -1;
				else return 0;
			}
		});
		map.clear();
		int size = (int) (list_tmp.size()*threshold);
		for(int i=0;i<size;i++){
			map.put(list_tmp.get(i).getKey(), list_tmp.get(i).getValue());
		}
	}
	//专门用于对预测结果排序  
	public static void mapSortByValue(Map<String, String> map, List<String> res_list) {
		List<Entry<String,String>> list_tmp = new ArrayList<Entry<String,String>>(map.entrySet());
		Collections.sort(list_tmp,new Comparator<Entry<String,String>>(){
			public int compare(Entry<String,String> arg0,Entry<String,String> arg1) {
				double arg0_value = Double.parseDouble(arg0.getValue().split("##")[2]);
				double arg1_value = Double.parseDouble(arg1.getValue().split("##")[2]);
				double r = arg1_value-arg0_value;
				if(r>0)return 1;
				else if(r<0)return -1;
				else return 0;
			}
		});
		//map.clear();
		for(int i=0;i<list_tmp.size();i++){
			String uid = list_tmp.get(i).getKey();
			String res = list_tmp.get(i).getValue();
			res_list.add(uid+"##"+res);
		}
	}
	public static Set<String> union(Set<String> id_set_1, Set<String> id_set_2) {
		Set<String> id_set = new HashSet<String>();
		for(String id :id_set_1){
			id_set.add(id);
		}
		for(String id :id_set_2){
			id_set.add(id);
		}
		return id_set;
	}
	public static Set<String> intersection(Set<String> id_set_1,Set<String> id_set_2) {
		Set<String> id_set = new HashSet<String>();
		for(String id :id_set_1){
			if(id_set_2.contains(id))id_set.add(id);
		}
		return id_set;
	}
	public static int getMax(double[] res_list) {
		double max = -1;int max_index = -1;int j = 0;
		for(double res : res_list){
			if(max<res){max_index = j;max = res;}
			j++;
		}
		return max_index+1;
	}
	public static int getMax(List<Double> res_list) {
		double max = -1;int max_index = -1;int j = 0;
		for(double res : res_list){
			if(max<res){max_index = j;max = res;}
			j++;
		}
		return max_index;
	}
	public static int getMax(String[] item, int s, int length) {
		double max = -1;int max_index = -1;
		for(int i=s;i<=length-s;i++){
			double res = Double.parseDouble(item[i]);
			if(max<res){max_index = i;max = res;}
		}
		return max_index;
	}
	public static void merge(List<Set<String>> id_set_list,List<Set<String>> id_set_list_i) {
		int len = id_set_list_i.size();
		for(int i=0;i<len;i++){
			Set<String> id_set_i = id_set_list_i.get(i);
			if(id_set_list.size()<=i){
				Set<String> id_set = new TreeSet<String>();
				for(String id : id_set_i){
					id_set.add(id);
				}
				id_set_list.add(id_set);
			}else{
				Set<String> id_set = id_set_list.get(i);
				for(String id : id_set_i){
					id_set.add(id);
				}
			}
		}
	}

	public static void putInMap( Map<Integer, Integer> map,int index,int value) {
		if(map.containsKey(index)){map.put(index,map.get(index)+value);
		}else{map.put(index,value);}
	}
	public static void putInMap( Map<Integer, Double> map,int index,Double value) {
		if(map.containsKey(index)){map.put(index,map.get(index)+value);
		}else{map.put(index,value);}
	}
	public static void putInMap( Map<String, Integer> map,String index,int value) {
		if(map.containsKey(index)){map.put(index,map.get(index)+value);
		}else{map.put(index,value);}
	}
	public static void putInMap( Map<String, Double> map,String index,Double value) {
		if(map.containsKey(index)){map.put(index,map.get(index)+value);
		}else{map.put(index,value);}
	}
	public static void putInMap(Map<String, StringBuffer> map,String id, String string) {
		if(map.containsKey(id)){map.put(id, map.get(id).append(string));}
		else{map.put(id, new StringBuffer(string));}
	}

}
