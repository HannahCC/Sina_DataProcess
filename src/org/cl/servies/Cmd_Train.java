package org.cl.servies;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.cl.conf.Config;

public class Cmd_Train {

	/**
	 * cmd /c dir 是执行完dir命令后关闭命令窗口。 
	 * cmd /k dir 是执行完dir命令后不关闭命令窗口。 
	 * cmd /c start dir 会打开一个新窗口后执行dir指令，原窗口会关闭。 
	 * cmd /k start dir 会打开一个新窗口后执行dir指令，原窗口不会关闭。 
	 * @param group
	 */
	public static void train(String path, String train_data_file) {
		try {//svm-train .\1\training_data.txt
			String cmdStr = "";
			String svm_path = Config.ResDir+"SVM\\";
			String type = Config.SVM_TYPE;
			if(type.equals("lg")){
				cmdStr = "cmd /k "+svm_path+"train -s 0 "+path+train_data_file+".txt";;
			}else if(type.equals("svm")){
				cmdStr = "cmd /k "+svm_path+"svm-train "+path+train_data_file+".txt";
			}else if(type.equals("muti")){
				cmdStr = "cmd /k "+svm_path+"svm_multiclass_learn -c 0.01 "+path+train_data_file+".txt "+path+"mutisvm_struct_model";
			}
			Runtime run = Runtime.getRuntime(); 
			Process process = run.exec(cmdStr);  
			//将调用结果打印到控制台上
			InputStream in = process.getInputStream();     
			 InputStreamReader bi = new InputStreamReader(in);  
             BufferedReader br = new BufferedReader(bi);  
             String message;  
             message =  br.readLine();        
             while(message != null && !"".equals(message)){  
             //将信息输出  
               //System.out.println(message);  
               message =  br.readLine();  
             }
             //System.out.println(Config.ResPath+"DONE!!!!!!!!!!!!!");
            /* if(type.equals("muti")){
            	 SaveInfo.fileCrop(Config.WORKBENCH+"svm_struct_model", Config.ResPath);
             }*/
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
