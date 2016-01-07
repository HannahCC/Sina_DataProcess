package org.cl.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class getFeatureVisible {

	/**
	 *将svm输入文件表示为中文可视的文件
	 */
	static List<String> featurelist = new ArrayList<String>();
	static List<String> featurename = new ArrayList<String>();
	static Map<String,String> featureToNameMap = new HashMap<String,String>();
	
	public static void main(String[] args) {
	featurelist = getlist("E:\\ThreeTypeOfUser\\GroupClassification\\FiveFolds\\ProfileFeatures\\src\\Feature_Relation\\personalized_feature.txt");
    System.out.println("the size of featurelist is"+featurelist.size());
    featurename = getlist("E:\\ThreeTypeOfUser\\GroupClassification\\FiveFolds\\ProfileFeatures\\featurelist.txt");
    String[] arr = null;
    for(int i = 0; i < featurename.size(); i++){
    	arr = featurename.get(i).split("\t");
    	featureToNameMap.put(arr[1], arr[0]);
    	
    } 
    String[] idAndFeature = null;
    String[] features = null;
    String id = null;
    try {
		FileWriter fw = new FileWriter("E:\\ThreeTypeOfUser\\GroupClassification\\FiveFolds\\ProfileFeatures\\src\\Feature_Relation\\decode\\allfeatures_format_feature_decode.txt");
		for(int j = 0; j < featurelist.size(); j++){
		StringBuffer sbResult = new StringBuffer();
		idAndFeature = featurelist.get(j).split("\t",2);
		id = idAndFeature[0];
		sbResult.append(id+"\t");
		features = idAndFeature[1].split("\t");
		for(int n = 0; n < features.length; n++){
			String[] eachfeature = features[n].split(":");
			sbResult.append(featureToNameMap.get(eachfeature[0])+":"+eachfeature[1]+"\t");
		}
		
		sbResult.append("\r\n");
		String result = sbResult.toString();
		fw.write(result);
		}
		
		fw.flush();
		fw.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	private static List<String> getlist(String filename){
		List<String> needlist = new ArrayList<String>();
		File file = new File(filename);
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf-8");
			BufferedReader inOne = new BufferedReader(read);
			
			String s = "";
			while((s = inOne.readLine())!=null){
				needlist.add(s);
			}
			System.out.println("the list of "+filename+"is "+needlist.size());
			inOne.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return needlist; 
		
		
	}

}
