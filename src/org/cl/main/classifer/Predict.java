package org.cl.main.classifer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cl.cmd.Cmd_Predict;
import org.cl.cmd.Cmd_Train;
import org.cl.conf.Config;
import org.cl.model.ResultNode;
import org.cl.utils.GetResult;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;


public class Predict {
	public static boolean isAverage = true;
	public static String type = "lg";


	public static void main(String args[]) throws InterruptedException, IOException{
		/*String[] classifers = {"Simple_vec1st_sample10_d128_line","Simple_vec2nd_sample10_d128_line","Simple_vecAll_sample10_d128_line",
				"Simple_vec1st_sample10000_d128_line","Simple_vec2nd_sample10000_d128_line","Simple_vecAll_sample10000_d128_line",
				"Simple_vec1st_sample10000_d256_line","Simple_vec2nd_sample10000_d256_line","Simple_vecAll_sample10000_d256_line",
				"Simple_Tag","Simple_Tag_AvgVecIn18w","Simple_Tag_ConcVecIn18w","Simple_Tag_AvgVecIn200w","Simple_Tag_ConcVecIn200w",
				"Simple_Fri_Tag","Simple_Fol_Tag","Simple_Fri_Fol_Tag","Simple_Fri_Tag_withself","Simple_Fol_Tag_withself","Simple_Fri_Fol_Tag_withself",
				"Simple_Description","Simple_Description_AvgVecIn18w","Simple_Description_ConcVecIn18w","Simple_Description_AvgVecIn200w","Simple_Description_ConcVecIn200w",
				"Simple_Fri_Description","Simple_Fol_Description","Simple_Fri_Fol_Description","Simple_Fri_Description_withself","Simple_Fol_Description_withself","Simple_Fri_Fol_Description_withself",
				"Simple_vecAll_sample10000_d128_tag_200w","Simple_vecAll_sample10000_d128_desc_200w","Simple_vecAll_sample10000_d128_desc_tag_200w",
				"Simple_vecAll_sample10000_d128_line_desc_tag_200w","Simple_vecAll_sample10000_d128_line1.5_desc_tag_200w","Simple_vecAll_sample10000_d128_line2_desc_tag_200w",
				"Simple_vecAll_sample10000_d128_tag_18w","Simple_vecAll_sample10000_d128_desc_18w","Simple_vecAll_sample10000_d128_desc_tag_18w",
				"Simple_vecAll_sample10000_d128_line_desc_tag_18w","Simple_vecAll_sample10000_d128_line1.5_desc_tag_18w","Simple_vecAll_sample10000_d128_line2_desc_tag_18w",
		};*/
		/*String[] classifers = {"Simple_Tag","Simple_Fri_Tag","Simple_Fol_Tag","Simple_Fri_Fol_Tag","Simple_Fri_Tag_withself","Simple_Fol_Tag_withself","Simple_Fri_Fol_Tag_withself",
		"Simple_Description","Simple_Fri_Description","Simple_Fol_Description","Simple_Fri_Fol_Description","Simple_Fri_Description_withself","Simple_Fol_Description_withself","Simple_Fri_Fol_Description_withself",
		};*/
		/*String[] classifers = {"Simple_vecAll_sample10000_d128_line4_desc_tag_avg_18w","Simple_vecAll_sample10000_d128_line4_desc_tag_conc_18w",
		"Simple_vecAll_sample10000_d128_line5_desc_tag_avg_18w","Simple_vecAll_sample10000_d128_line5_desc_tag_conc_18w",
		"Simple_vecAll_sample10000_d128_line6_desc_tag_avg_18w","Simple_vecAll_sample10000_d128_line6_desc_tag_conc_18w"
		};*/
		String[] classifers = {"Simple_vecAll_sample10000_d128_line9_desc_tag_avg_18w"};
		for(String classifer : classifers){
		/*Config.ResPath_Root = args[0];
		type = args[1];
		for(int i=2;i<args.length;i++){
			String classifer = args[i];*/
			
			Config.ResPath = Config.ResPath_Root + classifer +"\\"; 
			singleTrain("",0,5);
			singlePredict("",0,5);
			singleGetF1("",0,5);
			//singleGetUserNoFeature("",0,5);
			/*doubleTrain("",0,4,0,5);
			doublePredict("",0,4,0,5);
			doubleGetF1("",0,4,0,5);*/
			//doubleGetUserNoFeature("",0,19,0,5);
			/*tribleTrain(0,10,0,6,0,5);
			triblePredict(0,10,0,6,0,5);
			tribleGetF1(0,10,0,6,0,5);
			tribleGetUserNoFeature(0,10,0,6,0,5);*/
			SaveInfo.saveResult(Config.ResPath,"res.txt");
		}
	}
	public static void singleTrain(String dir, int s, int n) {
		for(int i=0;i<n;i++){//train
			String path = Config.ResPath+dir+(i+s)+"\\";
			Cmd_Train.train(path,type);
		}
	}	
	public static void singlePredict(String dir, int s, int n){
		double average = 0;
		for(int i=0;i<n;i++){//train
			String path = Config.ResPath+dir+(i+s)+"\\";
			double accuracy = Cmd_Predict.predict(path,type);
			average+=accuracy;
		}
		if(isAverage){
			average /= n;
			SaveInfo.saveResult(type+" accuracy average="+average);
		}
	}
	public static void singleGetF1(String dir, int s, int n) throws IOException {
		double average1 = 0, average2 = 0;
		for(int i=0;i<n;i++){//train
			String path = Config.ResPath+dir+(i+s)+"\\";
			Map<String,String> id_actual_res = new TreeMap<String, String>();
			Map<String,ResultNode> id_predict_res = new TreeMap<String, ResultNode>();
			// testing_id.txt testing_data.txt result_lg.txt 同一行为同一个用户
			List<String> testing_id = ReadInfo.getList(path,"\\testing_id.txt","\\s",0);
			GetResult.getActualRes(path,"\\testing_id.txt",testing_id,id_actual_res,"\\s",1);
			GetResult.getPredictRes(path,"\\result_"+type+".txt",testing_id,id_predict_res,1);
			double micro_f1 = GetResult.getMicroF1Score(id_actual_res, id_predict_res);
			double macro_f1 = GetResult.getMacroF1Score(id_actual_res, id_predict_res);
			//SaveInfo.saveResult(Config.ResPath+dir+"---"+type+" MicroF1Score ="+micro_f1);
			//SaveInfo.saveResult(Config.ResPath+dir+"---"+type+" MacroF1Score ="+macro_f1);
			average1 += micro_f1;
			average2 += macro_f1;
		}
		if(isAverage){
			average1 /= n;
			average2 /= n;
			SaveInfo.saveResult(Config.ResPath+dir+"---"+type+" MicroF1Score average="+average1);
			SaveInfo.saveResult(Config.ResPath+dir+"---"+type+" MacroF1Score average="+average2);
		}
	}
	public static void singleGetUserNoFeature(String dir,int s, int n) throws IOException {
		int sum = 0;
		for(int i=0;i<n;i++){
			String path = Config.ResPath_Root+dir+(i+s)+"\\";
			int num = ReadInfo.getUserNum(path, "training_data.txt", "\t", 1);
			num += ReadInfo.getUserNum(path, "testing_data.txt", "\t", 1);
			sum+= num;
		}
		double avg = sum/(double)5;
		SaveInfo.saveResult("User has no feature average="+avg);
	}

	public static void doubleTrain(String dir, int s1, int n1, int s2, int n2){
		for(int i=0;i<n1;i++){//train
			singleTrain(dir+(i+s1)+"\\",s2,n2);
		}
	}
	public static void doublePredict(String dir, int s1, int n1, int s2, int n2) {
		for(int i=0;i<n1;i++){//predict
			singlePredict(dir+(i+s1)+"\\",s2,n2);
		}
	}
	public static void doubleGetUserNoFeature(String dir, int s1, int n1, int s2, int n2) throws IOException {
		for(int i=0;i<n1;i++){
			singleGetUserNoFeature(dir+(i+s1)+"\\",s2,n2);
		}
	}
	public static void doubleGetF1(String dir, int s1, int n1, int s2, int n2) throws IOException {
		for(int i=0;i<n1;i++){
			singleGetF1(dir+(i+s1)+"\\",s2,n2);
		}
	}
	public static void tribleTrain(int s1, int n1, int s2, int n2,int s3,int n3){
		for(int i=0;i<n1;i++){//train
			doubleTrain((i+s1)+"\\",s2,n2,s3,n3);
		}
	}
	public static void triblePredict(int s1, int n1, int s2, int n2,int s3,int n3) {
		for(int i=0;i<n1;i++){//predict
			doublePredict((i+s1)+"\\",s2,n2,s3,n3);
		}
	}
	public static void tribleGetUserNoFeature(int s1, int n1, int s2, int n2,int s3,int n3) throws IOException {
		for(int i=0;i<n1;i++){//predict
			doubleGetUserNoFeature((i+s1)+"\\",s2,n2,s3,n3);
		}
	}
	public static void tribleGetF1(int s1, int n1, int s2, int n2,int s3,int n3) throws IOException {
		for(int i=0;i<n1;i++){//predict
			doubleGetF1((i+s1)+"\\",s2,n2,s3,n3);
		}
	}
}
