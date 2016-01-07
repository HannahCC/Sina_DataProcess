package org.cl.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cl.utils.ReadInfo;
import org.cl.utils.Utils;

public class GetSrcFeatureDetail {
	/**
	 * 从指定文件中获取ID，得到对应ID的src列表，得到对应src的主题向量
	 */
	static String srcPath = "D:\\Project_DataMinning\\Data\\Sina_res\\Sina_NLPIR400_Good\\";
	static String resPath = "D:\\Project_DataMinning\\DataProcessd\\Sina_GenderPre_400\\Simple_LFLDASrcTopic_train_2000_100_nvabg_AVG·1000\\";
	static int fold_i = 0;

	public static void main(String[] args) throws IOException {

		Map<String, Map<String,Integer>> uid_src_map = new HashMap<String, Map<String,Integer>>();
		ReadInfo.getMapMap(srcPath+"WeibosSrc\\Src_map.txt",uid_src_map,"\t",":",0);
		Map<String, double[]> src_topic = new HashMap<String,double[]>();//获得每个src属于各个topic的可能性
		ReadInfo.getArrayMap(src_topic,100,srcPath+"Feature_SRC\\Src_topic_2000_100_LFLDA.txt","\t"," ",0,1);

		List<String> dim_avg = ReadInfo.getList(resPath+fold_i, "\\Dim_avg.txt");
		Set<Integer> female_dim = new HashSet<Integer>();
		Set<Integer> male_dim = new HashSet<Integer>();
		for(int i=0;i<20;i++){
			female_dim.add(Integer.parseInt(dim_avg.get(i).split("\t")[0]));
			male_dim.add(Integer.parseInt(dim_avg.get(99-i).split("\t")[0]));
		}

		List<String> id_list = ReadInfo.getList(resPath+fold_i, "\\result_lg_right.txt");
		File f = new File(resPath+fold_i+"\\result_lg_right_detail.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		for(String id_item : id_list){
			String id = id_item.split("\t",2)[0];
			bw.write(id+"\t"+id_item.split("\t\\*\\*\t", 3)[1]+"\r\n");
			String[] features = id_item.split("\t\\*\\*\t", 3)[2].split("\t");
			StringBuffer female_buffer = new StringBuffer();
			StringBuffer male_buffer = new StringBuffer();
			for(String fe : features){
				String[] i_v = fe.split(":");
				int i = Integer.parseInt(i_v[0]);
				if(female_dim.contains(i)){
					female_buffer.append(fe+"\t");
				}else if(male_dim.contains(i)){
					male_buffer.append(fe+"\t");
				}
			}
			bw.write(female_buffer.toString()+"\r\n");
			bw.write(male_buffer.toString()+"\r\n");


			Map<String,Integer> src_map = uid_src_map.get(id);
			List<String> src_list = new ArrayList<String>();
			Utils.mapSortByValueInteger(src_list, src_map);
			bw.write(Utils.lists_ToString(src_list)+"\r\n");
			for(String src : src_map.keySet()){
				double[] src_topic_arr = src_topic.get(src);
				if(src_topic_arr==null){
					bw.write(src+"\r\n");
				}else{
					bw.write(src+"\t"+Utils.array_ToString(src_topic_arr)+"\r\n");
				}
			}
			bw.write("\r\n");
		}
		bw.flush();
		bw.close();
	}
}
