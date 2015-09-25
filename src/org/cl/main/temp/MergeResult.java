package org.cl.main.temp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cl.conf.Config;
import org.cl.model.ResultNode;
import org.cl.utils.GetResult;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;

public class MergeResult {

	/**
	 * 将该程序得到的结果与老师给其他特征的预测结果进行合并，查看效果  Tag_Description_Word和Name
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException{
		Map<String,String> id_actual_res = new TreeMap<String, String>();
		getActualRes(Config.ResPath_Root+"\\Public_Info_73\\0\\","1_testingid.txt",id_actual_res,1);
		getActualRes(Config.ResPath_Root+"\\Public_Info_73\\0\\","2_testingid.txt",id_actual_res,2);

		for(int i=1;i<10;i++){
			double self_weight = 1-0.1*i;
			double ofs_weight = 1;
			double fol_weigth = 1;
			mergeResult(id_actual_res,self_weight,ofs_weight,fol_weigth);
		}


	}
	private static void mergeResult(Map<String, String> id_actual_res,
			double self_weight, double ofs_weight, double fol_weigth) throws IOException {

		Map<String,ResultNode> id_predict_res = new TreeMap<String, ResultNode>();
		List<String> testing_id = ReadInfo.getList(Config.ResPath_Root,"\\MutiClassifer_Tag_Description_Word\\final_result_id_0.txt","\\s",0);
		GetResult.getPredictRes(Config.ResPath_Root,"\\MutiClassifer_Tag_Description_Word\\final_result0_lg.txt",testing_id,id_predict_res,self_weight);
		getPredictRes(Config.ResPath_Root,"\\result_namech_1.txt",id_predict_res,self_weight);
		getPredictRes(Config.ResPath_Root,"\\result_pred_ofs_des_73.txt",id_predict_res,ofs_weight);
		getPredictRes(Config.ResPath_Root,"\\result_pred_ofs_tag_73.txt",id_predict_res,ofs_weight);
		//getPredictRes(Config.ResPath_Root,"\\result_pred_fol_des_73.txt",id_predict_res,fol_weigth);                             
		//getPredictRes(Config.ResPath_Root,"\\result_pred_fol_tag_73.txt",id_predict_res,fol_weigth);
		String result_filename = "final_result_"+self_weight+"_"+ofs_weight;//+"_"+fol_weigth;
		SaveInfo.result_writer(Config.ResPath_Root+"MutiClassifer_Tag_Description_Word_ScreenName_Fri_des_tag\\",result_filename+".txt","final_result_id.txt",id_actual_res,id_predict_res);
		double accuracy =  GetResult.getAccuracy(id_actual_res,id_predict_res);
		SaveInfo.saveResult(Config.ResPath_Root+"MutiClassifer_Tag_Description_Word_ScreenName_Fri_des_tag\\"+result_filename+"----"+accuracy);

	}
	/**
	 * 与老师的结果进行合并
	 * @param dir   预测结果所在目录
	 * @param filename   预测结果存放的文件
	 * @param id_predict_res  存放合并后的预测值
	 * @param weigth   该结果的权值
	 * @throws IOException
	 */
	private static void getPredictRes(String dir, String filename,Map<String, ResultNode> id_predict_res,double weigth) throws IOException {
		File f = new File(dir+filename);
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			String[] items = line.split("\\s");
			String id = items[0];
			if(id_predict_res.containsKey(id)){
				id_predict_res.get(id).accumulate_results(items,weigth,1,2);//与之前的结果进行累加
			}else {
				ResultNode res = new ResultNode(items,1,2);
				id_predict_res.put(id,res);
			}
		}
		r.close();
	}
	/**
	 * 
	 * @param dir   预测结果所在目录
	 * @param filename   预测结果存放的文件
	 * @param id_actual_res   存放id对应的实际label
	 * @param label	  这个文件对应的label
	 * @throws IOException
	 */
	private static void getActualRes(String dir, String filename, Map<String,String> id_actual_res, int label) throws IOException {
		File f = new File(dir+filename);
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			id_actual_res.put(line,label+"");
		}
		r.close();
	}

}
