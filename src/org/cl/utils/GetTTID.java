package org.cl.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import org.cl.conf.Config;

public class GetTTID {
	private static String PUBLIC_INFO_ROOT = "Public_Info_Test82\\";//Public_Info_Behaviour\\   Public_Info_Style\\
	private static String USERID_ROOT = "UserID_Test\\";//UserID_Behaviour\\   Public_Info_Style\\

	//从UserInfo.txt中根据用户属性获取TESTID
	public static void tmp_getTestID() throws IOException {
		File srcf = new File(Config.SrcPath_Root+"UserInfo0_MuteUser.txt");
		BufferedReader r = new BufferedReader(new FileReader(srcf));
		File resf1 = new File(Config.ResPath_Root+PUBLIC_INFO_ROOT+"0\\1_testingid.txt");
		BufferedWriter w1 = new BufferedWriter(new FileWriter(resf1));
		File resf2 = new File(Config.ResPath_Root+PUBLIC_INFO_ROOT+"0\\2_testingid.txt");
		BufferedWriter w2 = new BufferedWriter(new FileWriter(resf2));
		// 用户Id、 用户名、是否被选择、性别（男性1，女性0） 、训练还是测试（训练1，测试0）
		String line = null;
		while(null!=(line=r.readLine())){
			if(line.equals(""))continue;
			JSONObject json = JSONObject.fromObject(line);
			String id = json.getString("id");
			String gender = json.getString("gender");
			if(gender.equals("f")){//female
				w1.write(id+"\r\n");
			}else if(gender.equals("m")){//male
				w2.write(id+"\r\n");
			}else {
				System.out.println(line);
			}

		}
		r.close();
		w1.flush();w1.close();
		w2.flush();w2.close();
	}
	//从老师给定的文件中获取TrainOrTestID
	public static void tmp_getTrain_TestID(String bili) throws IOException {
		File srcf = new File(Config.SrcPath_Root+"allfilenames_32wuser_gender_split_selected_"+bili+".txt");
		BufferedReader r = new BufferedReader(new FileReader(srcf));
		File resf1 = new File(Config.ResPath_Root+PUBLIC_INFO_ROOT+"0\\1_testingid.txt");
		BufferedWriter w1 = new BufferedWriter(new FileWriter(resf1));
		File resf2 = new File(Config.ResPath_Root+PUBLIC_INFO_ROOT+"0\\2_testingid.txt");
		BufferedWriter w2 = new BufferedWriter(new FileWriter(resf2));
		File resf3 = new File(Config.ResPath_Root+PUBLIC_INFO_ROOT+"0\\400_1_trainingid.txt");
		BufferedWriter w3 = new BufferedWriter(new FileWriter(resf3));
		File resf4 = new File(Config.ResPath_Root+PUBLIC_INFO_ROOT+"0\\400_2_trainingid.txt");
		BufferedWriter w4 = new BufferedWriter(new FileWriter(resf4));

		// 用户Id、 用户名、是否被选择、性别（男性1，女性0） 、训练还是测试（训练1，测试0）
		String line = null;
		while(null!=(line=r.readLine())){
			String[] items = line.split("\\s");
			if(items[4].equals("0")){//test data
				if(items[3].equals("0")){//female
					w1.write(items[0]+"\r\n");
				}else if(items[3].equals("1")){//male
					w2.write(items[0]+"\r\n");
				}else {
					System.out.println(line);
				}
			}else if(items[4].equals("1")){//train data
				if(items[3].equals("0")){//female
					w3.write(items[0]+"\r\n");
				}else if(items[3].equals("1")){//male
					w4.write(items[0]+"\r\n");
				}else {
					System.out.println(line);
				}
			}else {
				System.out.println(line);
			}
		}
		r.close();
		w1.flush();w1.close();
		w2.flush();w2.close();
		w3.flush();w3.close();
		w4.flush();w4.close();

	}
	//从UserID_XX/1.txt中获取ID，根据一定比例获取TrainOrTestID
	public static void tmp_getTrain_TestID(int[] LABELS,double ratio) throws IOException{
		for(int labelid : LABELS){
			Set<String> id_set = ReadInfo.getSet(Config.ResPath_Root,USERID_ROOT+labelid+".txt");
			List<Set<String>> id_set_list = Utils.spilt(id_set, ratio);
			SaveInfo.saveSet(Config.ResPath_Root, PUBLIC_INFO_ROOT+"0\\112_"+labelid+"_trainingid.txt", id_set_list.get(0));
			SaveInfo.saveSet(Config.ResPath_Root, PUBLIC_INFO_ROOT+"0\\"+labelid+"_testingid.txt", id_set_list.get(1));
		}
	}
}
