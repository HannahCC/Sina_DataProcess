package org.cl.main.classifer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cl.conf.Config;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;
import org.cl.utils.Utils;

public class Decoder {

	/**
	 * 将Config.Public_Info中的<特征编号：chi值>解码成<特征：chi值>，方便数据分析
	 */
	static String TYPE = "trainidf";
	static String[] CLASSIFERS = {
		"Feature_Tag\\Tag",
		"Feature_UserInfo\\Description",
		/*"Feature_Relation\\FolTag",
		"Feature_Relation\\FolDescription",
		"Feature_Relation\\OFSTag",
		"Feature_Relation\\OFSDescription",*/
		"Feature_Textual\\Text"
	};
	public static void main(String args[]) throws IOException{
		SaveInfo.mkdir(Config.Public_Info+"Decoded\\");
		String combination = "";
		for(int labelid : Config.LABELS){combination+=labelid;}
		combination+="_";
		for(int i=0;i<Config.FOLD;i++){
			for(int labelid : Config.LABELS){
				for(String classifer : CLASSIFERS){
					String classifername = classifer.split("\\\\")[1];
					Map<String,Double> feature_map = ReadInfo.getMapDouble(Config.Public_Info+i+"\\", Config.TRAIN_ID_SIZE+"_"+combination+labelid+"_"+classifername+"_"+TYPE+".txt", ":", 0, 1);
					Map<String,String> feature_dict = ReadInfo.getMap(Config.SrcPath_Root+"Config\\","Dict_"+classifername+".txt","\t",1,0);
					List<String> feature_list = DecodeFeatureMap(feature_dict, feature_map);
					SaveInfo.saveList(Config.Public_Info+"Decoded\\"+i+"\\",  Config.TRAIN_ID_SIZE+"_"+combination+labelid+"_"+classifername+"_"+TYPE+".txt", feature_list);
				}
			}
		}
	}

	private static List<String> DecodeFeatureMap(Map<String,String> dict,Map<String, Double> feature_map) {
		Map<String, Double> feature_map_new = new HashMap<String,Double>();
		Iterator<Entry<String, Double>> it = feature_map.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Double> entry = it.next();
			String encode = entry.getKey();
			String decode = dict.get(encode);
			feature_map_new.put(decode, entry.getValue());
		}
		List<String> feature_list = new ArrayList<String>();
		Utils.mapSortByValue(feature_list, feature_map_new);
		return feature_list;
	}
}


