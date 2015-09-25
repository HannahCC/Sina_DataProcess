package org.cl.main.temp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cl.conf.Config;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;

public class FormatResult {

	public static void main(String args[]) throws IOException{
		
		List<String> id_list = new ArrayList<String>();
		//用户Id、 用户名、是否被选择、性别（男性1，女性0） 、训练还是测试（训练1，测试0）
		File f = new File(Config.SrcPath_Root+"allfilenames_32wuser_gender_split_selected_73.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = null;
		while(null!=(line = br.readLine())){
			if(!"".equals(line)){
				String[] items = line.split("\\s");
				String id = items[0];
				String type = items[4];
				if(type.equals("0")){//testing
					id_list.add(id);
				}
			}
		}
		br.close();
		
		formatter(id_list,"pred_fol_des_73");
		formatter(id_list,"pred_fol_tag_73");
		formatter(id_list,"pred_ofs_des_73");
		formatter(id_list,"pred_ofs_tag_73");
		
		
	}

	private static void formatter(List<String> id_list, String result_file) throws IOException {
		List<String> result_list = ReadInfo.getList(Config.ResPath+"ResultsOfFriends\\", result_file);
		List<String> id_result_list = mergeList(id_list,result_list);
		SaveInfo.saveList(Config.ResPath, "result_"+result_file+".txt", id_result_list);
	}

	private static List<String> mergeList(List<String> id_list,List<String> result_list) {
		List<String> id_result_list = new ArrayList<String>();
		for(int i=0;i<id_list.size();i++){
			String[] result_ = result_list.get(i).split("\\s");
			String result = result_[2]+" "+result_[1];//这一批结果的顺序是倒过来的
			String id_result = id_list.get(i)+" "+result;
			id_result_list.add(id_result);
		}
		return id_result_list;
	}
}
