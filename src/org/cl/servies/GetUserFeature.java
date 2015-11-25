package org.cl.servies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cl.conf.Config;
import org.cl.model.ClassiferNode;
import org.cl.utils.SaveInfo;

public class GetUserFeature {

	public static Map<ClassiferNode,Map<String,String>> classifer_user_map = new HashMap<ClassiferNode,Map<String, String>>();
	public static List<String> classifers = new ArrayList<String>();

	static{
		//classifers.add("Feature_UserInfo\\Tag");
		////classifers.add("Feature_UserInfo\\Description");
		//classifers.add("Feature_UserInfo\\Tag_AvgVecIn18w");
		//classifers.add("Feature_UserInfo\\Description_AvgVecIn18w");
		classifers.add("Feature_Relation\\Fri_Fol_Tag");
		//classifers.add("Feature_Relation\\Fri_Fol_Description");
		//classifers.add("Feature_Relation\\Line_vec_all");
		//classifers.add("Feature_Relation\\Line6_desc_tag_Conc_18w_vec_all");
		//classifers.add("Feature_Relation\\FolType");
		//classifers.add("Feature_Relation\\FriFolType");
		//classifers.add("Feature_SRC\\AppType");
	}
	
	
	public static void getUserFeatureMap() throws IOException {
		for(String classifer : classifers){
			ClassiferNode classifernode = new ClassiferNode();
			classifernode.setClassifer_name(classifer.split("\\\\")[1]);
			File f = new File(Config.SrcPath_Root+classifer+"_feature.txt");
			if(!f.exists()){
				System.out.println(classifernode.getClassifer_name()+"has no feature!");
				continue;
			}
			Map<String, String> res = new HashMap<String, String>();
			BufferedReader r = new BufferedReader(new FileReader(f));
			String line = "";
			while((line = r.readLine())!=null){
				if(line.equals("")||!line.contains(":"))continue;
				String uid = line.split("\\s")[0];
				res.put(uid,line.replace(uid, "").trim());
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
	}

}