package org.cl.main.classifer;

import java.io.IOException;

import org.cl.conf.Config;
import org.cl.servies.Cmd_Predict;
import org.cl.servies.Cmd_Train;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;


public class Predict {
	public static boolean isAverage = true;

	public static void main(String args[]) throws InterruptedException, IOException{
		String[] classifers = {"Simple_FriAvgVec_skn10wc200l100i15_Train-Test","Simple_FriAvgVec_skn10wc200l100i15_Train",
				"Simple_FriAvgVec_skn10wc200l100i15_Train+Test","Simple_FriAvgVec_skn15wc200l100i15_Train+Test","Simple_FriAvgVec_skn5wc200l100i15_Train+Test",};
		//String[] classifers = args;
		for(String classifer : classifers){
			Config.ResPath = Config.ResPath_Root + classifer +"\\"; 
			singleTrain("",0,5,"training_data");
			singlePredict("",0,5,"testing_data","result");
			/*singleTrain("",0,1,"rbm_training_data");
			singlePredict("",0,1,"rbm_testing_data","rbm_result");*/
			//singlePredict("",0,5,"learning_data","learning_result");
			//singleGetF1("",0,5,"testing_id","result");
			//singleGetUserNoFeature("",0,5,"training_data","testing_data");
			/*doubleTrain("",0,7,0,5,"training_data");
			doublePredict("",0,7,0,5,"testing_data","result");
			doubleGetF1("",0,7,0,5,"testing_id","result");*/
			//doubleGetUserNoFeature("",0,19,0,5,"training_data","testing_data");
			/*tribleTrain(0,10,0,6,0,5,"training_data");
			triblePredict(0,10,0,6,0,5,"testing_data","result");
			tribleGetF1(0,10,0,6,0,5,"testing_id","result");
			tribleGetUserNoFeature(0,10,0,6,0,5,"training_data","testing_data");*/
			SaveInfo.log_buff_writer(Config.ResPath,"res.txt");
		}
	}
	public static void singleTrain(String dir, int s, int n,String train_data_file) {
		for(int i=0;i<n;i++){//train
			String path = Config.ResPath+dir+(i+s)+"\\";
			Cmd_Train.train(path,train_data_file);
		}
	}	
	public static void singlePredict(String dir, int s, int n,String test_data_file,String result_file){
		double average = 0;
		for(int i=0;i<n;i++){//train
			String path = Config.ResPath+dir+(i+s)+"\\";
			double accuracy = Cmd_Predict.predict(path,test_data_file,result_file);
			average+=accuracy;
		}
		if(isAverage){
			average /= n;
			SaveInfo.option_log(Config.CLASSIFIER_TYPE+" accuracy average="+average);
			SaveInfo.res_log(Config.CLASSIFIER_TYPE+" accuracy average="+average,false);
		}
	}
	/*public static void singleGetF1(String dir, int s, int n,String test_id_file,String result_file) throws IOException {
		double average1 = 0, average2 = 0;
		for(int i=0;i<n;i++){//train
			String path = Config.ResPath+dir+(i+s)+"\\";
			Map<String,String> id_actual_res = new TreeMap<String, String>();
			Map<String,ResultNode> id_predict_res = new TreeMap<String, ResultNode>();
			// testing_id.txt testing_data.txt result_lg.txt 同一行为同一个用户
			List<String> testing_id = ReadInfo.getList(path,"\\"+test_id_file+".txt","\\s",0);
			GetResult.getActualRes(path,"\\"+test_id_file+".txt",testing_id,id_actual_res,"\\s",1);
			GetResult.getPredictRes(path,"\\"+result_file+"_"+Config.SVM_TYPE+".txt",testing_id,id_predict_res,1);
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
			SaveInfo.saveResult(Config.ResPath+dir+"---"+Config.SVM_TYPE+" MicroF1Score average="+average1);
			SaveInfo.saveResult(Config.ResPath+dir+"---"+Config.SVM_TYPE+" MacroF1Score average="+average2);
		}
	}*/
	public static void singleGetUserNoFeature(String dir,int s, int n,String train_data_file,String test_data_file) throws IOException {
		int sum = 0;
		for(int i=0;i<n;i++){
			String path = Config.ResPath_Root+dir+(i+s)+"\\";
			int num = ReadInfo.getUserNum(path, train_data_file, "\t", 1);
			num += ReadInfo.getUserNum(path, test_data_file, "\t", 1);
			sum+= num;
		}
		double avg = sum/(double)5;
		SaveInfo.option_log("User has no feature average="+avg);
	}

	public static void doubleTrain(String dir, int s1, int n1, int s2, int n2,String train_data_file){
		for(int i=0;i<n1;i++){//train
			singleTrain(dir+(i+s1)+"\\",s2,n2,train_data_file);
		}
	}
	public static void doublePredict(String dir, int s1, int n1, int s2, int n2,String test_data_file,String result_file) {
		for(int i=0;i<n1;i++){//predict
			singlePredict(dir+(i+s1)+"\\",s2,n2,test_data_file,result_file);
		}
	}
	public static void doubleGetUserNoFeature(String dir, int s1, int n1, int s2, int n2,String test_data_file,String result_file) throws IOException {
		for(int i=0;i<n1;i++){
			singleGetUserNoFeature(dir+(i+s1)+"\\",s2,n2,test_data_file,result_file);
		}
	}
	/*public static void doubleGetF1(String dir, int s1, int n1, int s2, int n2,String test_data_file,String result_file) throws IOException {
		for(int i=0;i<n1;i++){
			singleGetF1(dir+(i+s1)+"\\",s2,n2,test_data_file,result_file);
		}
	}*/
	public static void tribleTrain(int s1, int n1, int s2, int n2,int s3,int n3,String train_data_file){
		for(int i=0;i<n1;i++){//train
			doubleTrain((i+s1)+"\\",s2,n2,s3,n3,train_data_file);
		}
	}
	public static void triblePredict(int s1, int n1, int s2, int n2,int s3,int n3,String test_data_file,String result_file) {
		for(int i=0;i<n1;i++){//predict
			doublePredict((i+s1)+"\\",s2,n2,s3,n3,test_data_file,result_file);
		}
	}
	public static void tribleGetUserNoFeature(int s1, int n1, int s2, int n2,int s3,int n3,String test_data_file,String result_file) throws IOException {
		for(int i=0;i<n1;i++){//predict
			doubleGetUserNoFeature((i+s1)+"\\",s2,n2,s3,n3,test_data_file,result_file);
		}
	}
	/*public static void tribleGetF1(int s1, int n1, int s2, int n2,int s3,int n3,String test_data_file,String result_file) throws IOException {
		for(int i=0;i<n1;i++){//predict
			doubleGetF1((i+s1)+"\\",s2,n2,s3,n3,test_data_file,result_file);
		}
	}*/
}
