package org.cl.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cl.conf.Config;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;
import org.cl.utils.Utils;

public class GetDimensionAvg {
	static String Path = "D:\\Project_DataMinning\\DataProcessd\\Sina_GenderPre_400\\Simple_LFLDASrcTopic_train_2000_100_nvabg_AVG·1000\\";
	static int fold_i = 0;
	static final int dim = 100;
	static final int d_start = 2;
	public static void main(String args[]) throws IOException {
		for(int i=0;i<Config.FOLD;i++){
			fold_i = i;
			getDimensionAvg();
		}
	}
	public static void getDimensionAvg() throws IOException {
		List<String> data_list = ReadInfo.getList(Path+fold_i, "\\result_lg_right.txt");
		int[] k = new int[Config.LABELS.length];
		double[][] avg = new double[Config.LABELS.length][dim];
		for(String data : data_list){
			String[] items = data.split("\t\\*\\*\t",3);
			int label = Integer.parseInt(items[0].split("\t")[1]);
			int label_i = Utils.indexOfArray(Config.LABELS, label);

			String[] features = items[2].split("\t");
			for(String f : features){
				String[] i_v = f.split(":");
				int i = Integer.parseInt(i_v[0])-d_start;
				double v = Double.parseDouble(i_v[1]);
				avg[label_i][i] = (avg[label_i][i] * k[label_i] + v)/(k[label_i]+1);	
			}
			k[label_i]++;
		}
		System.out.println(Arrays.toString(k));
		double[] cha = new double[dim];
		//二维的时候才这样
		Map<String, Double> dim_cha = new HashMap<String, Double>();
		for(int i=0;i<dim;i++){
			cha[i] = avg[0][i]-avg[1][i];
			dim_cha.put((i+d_start)+"\t"+avg[0][i]+"\t"+avg[1][i], cha[i]);
		}
		List<String> res=  new ArrayList<String>();
		Utils.mapSortByValueDouble(res, dim_cha);
		SaveInfo.saveList(Path+fold_i,"\\Dim_avg.txt", res);
	}
}
