package org.cl.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cl.conf.Config;
import org.cl.utils.ReadInfo;

public class GetDimensionTopic {
	static String srcPath = "D:\\Project_DataMinning\\Data\\Sina_res\\Sina_NLPIR400_Good\\";
	static String resPath = "D:\\Project_DataMinning\\DataProcessd\\Sina_GenderPre_400\\Simple_LFLDASrcTopic_train_2000_100_nvabg_AVG·1000\\";
	static int fold_i = 0;
	static final int d_start = 2;
	public static void main(String args[]) throws IOException {
		Map<Integer, String> topic_words = getTopicsWords(srcPath+"Feature_Src\\Src_DescriptionLFLDA_2000_100_300_0.6\\testLFLDA.topWords");
		for(int i=0;i<Config.FOLD;i++){
			fold_i = i;
			getDimensionTopic("\\Dim_avg.txt","\\Dim_topic.txt",topic_words);
			
		}
	}
	private static Map<Integer, String> getTopicsWords(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		Map<Integer,String> lines = new HashMap<Integer,String>();
		String line="";
		int i = 0;
		while((line=br.readLine())!=null)
		{
			if(!(line.equals(""))){
				String[] item = line.split("\\s",2);
				lines.put(i++,item[1]);//(2,土豪，我们做朋友吧)	
			}
		}
		br.close();
		return lines;
	}
	public static void getDimensionTopic(String dims_filename, String res_filename,Map<Integer, String> topic_words) throws IOException {
		List<String> dim_avg = ReadInfo.getList(resPath+fold_i, dims_filename);
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(resPath+fold_i+res_filename)));
		for(int i=0;i<20;i++){
			int topic_i = Integer.parseInt(dim_avg.get(i).split("\t")[0])-d_start;
			bw.write(dim_avg.get(i)+"\r\n");
			bw.write(topic_words.get(topic_i)+"\r\n");
		}
		bw.write("\r\n");
		for(int i=0;i<20;i++){
			int topic_i = Integer.parseInt(dim_avg.get(99-i).split("\t")[0])-d_start;
			bw.write(dim_avg.get(99-i)+"\r\n");
			bw.write(topic_words.get(topic_i)+"\r\n");
		}
		bw.flush();
		bw.close();
	}
}
