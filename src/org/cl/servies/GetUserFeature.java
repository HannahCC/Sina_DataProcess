package org.cl.servies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cl.conf.Config;
import org.cl.model.ClassiferNode;
import org.cl.utils.SaveInfo;

public class GetUserFeature {

	public static final Map<ClassiferNode,Map<String,String>> classifer_user_map = new HashMap<ClassiferNode,Map<String, String>>();
	public static final List<String> classifers = Config.CLASSIFERS;

	
	public static void getUserFeatureMap() throws IOException {
		classifer_user_map.clear();
		for(String classifer : classifers){
			ClassiferNode classifernode = new ClassiferNode();
			classifernode.setClassifer_name(classifer);
			File f = new File(Config.SrcPath_Root+classifer+".feature");
			if(!f.exists()){
				System.out.println(classifernode.getClassifer_name()+"has no feature!");
				continue;
			}
			Map<String, String> res = new HashMap<String, String>();
			BufferedReader r = new BufferedReader(new FileReader(f));
			String line = "";
			while((line = r.readLine())!=null){
				if(line.equals("")||!line.contains(":"))continue;
				String[] item = line.split("\\s+",2);
				res.put(item[0],item[1]);
			}
			int size = getFeatureSize(classifernode.getClassifer_name());
			classifernode.setClassifer_size(size);
			SaveInfo.option_log(classifernode.getClassifer_name()+"SIZE---:"+classifernode.getClassifer_size());
			classifer_user_map.put(classifernode, res);
			r.close();
		}
	}

	public static void getUserFeatureMap(int trainSize, int fold) throws IOException {
		classifer_user_map.clear();
		for(String classifer : classifers){
			ClassiferNode classifernode = new ClassiferNode();
			classifernode.setClassifer_name(classifer);
			File f = new File(Config.SrcPath_Root+classifer+".feature.t"+trainSize+"f"+fold);
			if(!f.exists()){
				System.out.println(classifernode.getClassifer_name()+"has no feature!");
				continue;
			}
			Map<String, String> res = new HashMap<String, String>();
			BufferedReader r = new BufferedReader(new FileReader(f));
			String line = "";
			while((line = r.readLine())!=null){
				if(line.equals("")||!line.contains(":"))continue;
				String[] item = line.split("\\s",2);
				res.put(item[0],item[1]);
			}
			int size = getFeatureSize(classifernode.getClassifer_name());
			classifernode.setClassifer_size(size);
			SaveInfo.option_log(classifernode.getClassifer_name()+"SIZE---:"+classifernode.getClassifer_size());
			classifer_user_map.put(classifernode, res);
			r.close();
		}
	}
	
	public static int getFeatureSize(String classfier) throws IOException{
		int size = 0;
		/*File f = new File(Config.SrcPath_Root+"\\Config\\Feature_Size.txt");
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		while((line = r.readLine())!=null){
			if(!line.equals("")){
				String[] items = line.split("\t");
				if(items[0].equals(classfier)){size = Integer.parseInt(items[1]);break;}
			}
		}
		r.close();*/
		return size;
	}
	
	/*public static int getFeatureSize(String classfier) throws IOException{
		int size = 0;
		File f = new File(Config.SrcPath_Root+"\\Config\\Dict_"+classfier+".txt");
		if(!f.exists()){
			System.out.println(classfier+"has not dict.we can't get the feature_size!");
			return -1;
		}
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		while((line = r.readLine())!=null){
			if(!line.equals(""))size=Integer.parseInt(line.split("\t")[1]);
		}
		r.close();
		return size+1;//因为编号是从0开始，所以size+1
	}*/

}
