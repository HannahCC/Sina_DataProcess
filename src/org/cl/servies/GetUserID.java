package org.cl.servies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cl.conf.Config;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;

public class GetUserID {
	static int[] LABELS = Config.LABELS;//类别
	static int WEIBO_NUMBER = Config.WEIBO_NUMBER;
	static int ID_NUMBER = Config.ID_NUMBER;//每类ID数量
	static List<String> CLASSIFERS = new ArrayList<String>();
	static{
		SaveInfo.mkdir(Config.UserID);
		CLASSIFERS.add("Feature_UserInfo\\Description");
		CLASSIFERS.add("Feature_Tag\\Tag");
		CLASSIFERS.add("Feature_UserInfo\\Description");
	}
	/**
	 * 从源数据文件中获取CLASSFIERS各个特征都有的UserID，一共ID_NUMBER个存放到USERID_ROOT目录下
	 * @param labelid
	 * @throws IOException 
	 */
	public static void setUserID() throws IOException {
		for(int labelid : LABELS){
			Set<String> id_set = ReadInfo.getSet(Config.UserID,labelid+".txt");
			for(String classifer :CLASSIFERS){
				id_set = IdFilter(id_set,classifer);
			}
			System.out.println(labelid+"----"+id_set.size());
			SaveInfo.saveSet(Config.UserID,labelid+".txt",id_set,ID_NUMBER);
		}
	}
	/**
	 * 获取USERID_ROOT中各类用户的ID，筛选出微博数超过WEIBO_NUMBER的用户，一共ID_NUMBER个存放到USERID_ROOT目录下
	 * @param labelid
	 * @throws IOException 
	 */
	public static void setUserIDWeibonumOverN() throws IOException{
		for(int labelid : LABELS){
			List<String> idlist = ReadInfo.getList(Config.UserID,labelid+".txt");//各类特征都有的ID
			Set<String> newidlist = new HashSet<String>();
			for(String id :idlist){
				int size = ReadInfo.getLineNum(Config.SrcPath_Root, "WeibosCon\\"+id+".txt");
				if(size>=WEIBO_NUMBER){
					newidlist.add(id);
				}
			}
			System.out.println(newidlist.size());
			SaveInfo.saveSet(Config.UserID, labelid+".txt", newidlist,ID_NUMBER);
		}
	}
	/**
	 * 将id_set中不出现在feature目录下的id删除
	 * @param id_set
	 * @param feature
	 * @return
	 * @throws IOException 
	 */
	private static Set<String> IdFilter(Set<String> id_set, String feature) throws IOException {
		File f=new File(Config.SrcPath_Root+feature+"_feature.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = "";
		Set<String> new_id_set = new HashSet<String>();
		while((line = br.readLine())!=null){
			String id = line.split("\t")[0];
			if(id_set.contains(id))new_id_set.add(id);
		}
		br.close();
		System.out.println(new_id_set.size());
		return new_id_set;
	}
}
