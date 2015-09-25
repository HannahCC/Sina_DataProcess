package org.cl.cmd;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.cl.conf.Config;
import org.cl.utils.SaveInfo;

public class Cmd_Predict {

	public static double predict(String path,String test_id_file,String result_file,String type) {
		String cmdStr = getCmdStr(path,test_id_file,result_file,type);
		double accuracy = execute(path,cmdStr);
		return accuracy;
	}
	

	public static double predict(String path,String type) {
		String cmdStr = getCmdStr(path,"testing_data","result",type);
		double accuracy = execute(path,cmdStr);
		return accuracy;
	}

	private static double execute(String path,String cmdStr) {
		try {

			Runtime run = Runtime.getRuntime(); 
			Process process = run.exec(cmdStr);  
			//将调用结果打印到控制台上
			InputStream in = process.getInputStream();     
			InputStreamReader bi = new InputStreamReader(in);  
			BufferedReader br = new BufferedReader(bi);  
			String message;  
			message =  br.readLine();
			double accuracy = 0;
			while(message != null && !"".equals(message)){  
				//将信息输出  
				if(message.contains("Accuracy")){
					accuracy = Double.parseDouble(message.split("(Accuracy = )|(%)")[1]);
					SaveInfo.saveResult(path+"----"+message);
				}else if(message.contains("Zero/one-error")){
					accuracy = 100 - Double.parseDouble(message.split("(: )|(%)")[1]);
					SaveInfo.saveResult(path+"----"+message);
				}
				message =  br.readLine();  
			}
			return accuracy;
		}catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}


	private static String getCmdStr(String path, String test_data_file,String result_file,
			String type) {
		String save_path = Config.ResPath_Root+"SVM\\";
		String cmdStr = "";
		if(type.equals("lg")){
			cmdStr = "cmd /k "+save_path+"predict -b 1 "
					+path+"\\"+test_data_file+".txt "
					+path+"\\training_data.txt.model "
					+path+"\\"+result_file+"_lg.txt";
		}else if(type.equals("svm")){
			cmdStr = "cmd /k "+save_path+"svm-predict "
					+path+"\\"+test_data_file+".txt "
					+path+"\\training_data.txt.model "
					+path+"\\"+result_file+"_svm.txt";
		}else if(type.equals("muti")){
			cmdStr = "cmd /k "+save_path+"svm_multiclass_classify "
					+path+"\\"+test_data_file+".txt "
					+path+"\\mutisvm_struct_model "
					+path+"\\"+result_file+"_muti.txt";
		}
		return cmdStr;
	}
}
