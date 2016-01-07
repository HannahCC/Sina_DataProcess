package org.cl.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FindSameBadId {

	/**
	 * 找到几个BadID文件中的相同部分
	 */
	static List<String> Bad_Id_one = new ArrayList<String>();
	static List<String> Bad_Id_two = new ArrayList<String>();
	static List<String> Bad_Id_three = new ArrayList<String>();
	static List<String> Bad_Id_four = new ArrayList<String>();
	static List<String> Bad_Id_five = new ArrayList<String>();
	
	static List<String> Bad_Id_same = new ArrayList<String>();
	public static void main(String[] args) {
		String Id_one_file = "E:\\ThreeTypeOfUser\\GroupClassification\\FiveFolds\\ProfileFeatures\\res\\fivepartdata\\FivePartsTest_another\\ALL\\BadData\\BadIdList.txt";
		String Id_two_file = "E:\\ThreeTypeOfUser\\GroupClassification\\FiveFolds\\ProfileFeatures\\res\\fivepartdata\\FivePartsTest_another\\Not1\\BadData\\BadIdList.txt";
		String Id_three_file = "E:\\ThreeTypeOfUser\\GroupClassification\\FiveFolds\\ProfileFeatures\\res\\fivepartdata\\FivePartsTest_another\\Not2\\BadData\\BadIdList.txt";
		String Id_four_file = "E:\\ThreeTypeOfUser\\GroupClassification\\FiveFolds\\ProfileFeatures\\res\\fivepartdata\\FivePartsTest_another\\Not3\\BadData\\BadIdList.txt";
		String Id_five_file = "E:\\ThreeTypeOfUser\\GroupClassification\\FiveFolds\\ProfileFeatures\\res\\fivepartdata\\FivePartsTest_another\\Not4\\BadData\\BadIdList.txt";
		
		
		Bad_Id_one = getneedlist(Id_one_file);
		Bad_Id_two = getneedlist(Id_two_file);
		Bad_Id_three = getneedlist(Id_three_file);
		Bad_Id_four = getneedlist(Id_four_file);
		Bad_Id_five = getneedlist(Id_five_file);
		
		String tempid = null;
		for(int i = 0; i < Bad_Id_one.size(); i++){
			tempid = Bad_Id_one.get(i); 
			if(Bad_Id_two.contains(tempid)){
				if(Bad_Id_three.contains(tempid)){
					if(Bad_Id_four.contains(tempid)){
						if(Bad_Id_five.contains(tempid)){
							Bad_Id_same.add(tempid);
						}
					}
				}
			}
		}
		
		try {
			save(Bad_Id_same,"Bad_Id_same");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static List<String> getneedlist(String filename){
		List<String> needList = new ArrayList<String>();
		File file1 = new File(filename);
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file1),"utf-8");
			BufferedReader inOne = new BufferedReader(read);

			String s = null;
			while((s = inOne.readLine())!=null){
				needList.add(s);
			}
			System.out.println("the size of file "+filename+"is "+needList.size());
			inOne.close();
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return needList;

	}
	
	private static void save(List<String> list,String saveName) throws IOException {
		// TODO Auto-generated method stub
		if(list!=null&&list.size()>0){
			File file = new File("E:\\ThreeTypeOfUser\\GroupClassification\\FiveFolds\\ProfileFeatures\\res\\fivepartdata\\FivePartsTest_another\\Bad_Id_same\\"+saveName+".txt");
			
File fileDir=new File("E:\\ThreeTypeOfUser\\GroupClassification\\FiveFolds\\ProfileFeatures\\res\\fivepartdata\\FivePartsTest_another\\Bad_Id_same\\");
			if(!fileDir.exists()) fileDir.mkdir();

			//	System.out.println("E:\\\\"+list+".txt");
			OutputStreamWriter out1=new OutputStreamWriter(new FileOutputStream(file),"utf-8");
			BufferedWriter outOne = new BufferedWriter(out1);
			for(String str :list){
				outOne.write(str+"\r\n");
			}

			outOne.flush();
			outOne.close();
		}
	}
}
