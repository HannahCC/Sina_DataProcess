package org.cl.main.classifer;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cl.conf.Config;
import org.cl.model.ResultNode;
import org.cl.utils.GetResult;
import org.cl.utils.ReadInfo;
import org.cl.utils.SaveInfo;

public class Classifer_Essemble {

	/**
	 * 对于同一批数据集（训练、测试均相同），将使用了不同的特征进行分类得到的预测结果，进行简单投票得到最后结果
	 * @param args
	 * @throws InterruptedException
	 */

	static int fold = 5;
	static int[] labels = {1,2};

	static String[] classfiers = {"Tag","Fri_Fol_Tag","Description","Fri_Fol_Description",};
	//static String[] classfiers = {"Fri_Tag","Fol_Tag","Fri_Description","Fol_Description"};
	
	/*static String[] classfiers = {"vecAll_line","Tag_AvgVecIn18w","Tag","Fri_Tag","Fol_Tag",
		"Description_AvgVecIn18w","Description","Fri_Description","Fol_Description","ScreenName","Fri_Fol_Tag","Fri_Fol_Description"};*/
	static double[] weights = {1,1,1,1,1,1,1,1,1,1,1};//new double[10];
	static String res_dir;
	public static String type = "lg";
	public static void main(String[] args) throws InterruptedException, IOException{
		/*weights[0] = 0.1;
		for(int j=1;j<classfiers.length;j++){
			weights[j] = (1-weights[0])/8;
		}*/
		
		res_dir = "MutiClassifer";
		for(int j=0;j<classfiers.length;j++){
			res_dir+="+"+classfiers[j];
			if(j==0 && weights[j]!=1){
				res_dir+="("+weights[j]+")";
			}
		}
		SaveInfo.mkdir(Config.ResPath_Root+res_dir);
		cross_validation();
		/*---------------------------------1vsall---------------------------------*/
		/*int k = 0;
		for(int i=0;i<4;i++){
			cross_validation(k);
			k++;
		}*/
		/*---------------------------------1vs2---------------------------------*/
		/*int k = 0;
		for(int i=1;i<4;i++){
			for(int j=i+1;j<5;j++){
				cross_validation(k);
				k++;
			}
		}*/
		/*---------------------------------diff_train_id_size---------------------------------*/
		/*int[] train_id_size = {480,320,160,80,40,20,640};
		for(int size=0;size<train_id_size.length;size++){
			cross_validation(size);
		}*/

		SaveInfo.saveResult(Config.ResPath_Root+res_dir,"\\res.txt");
	}
	public static void cross_validation() throws IOException{
		double accuracy_avg = 0, microF1_avg = 0, macroF1_avg = 0;
		for(int i=0;i<fold;i++){
			Map<String,String> id_actual_res = new TreeMap<String, String>();
			Map<String,ResultNode> id_predict_res = new TreeMap<String, ResultNode>();
			for(int j=0;j<classfiers.length;j++){
				// testing_id.txt testing_data.txt result_lg.txt 同一行为同一个用户
				List<String> testing_id = ReadInfo.getList(Config.ResPath_Root+"Simple_"+classfiers[j]+"\\"+i,"\\testing_id.txt","\\s",0);
				GetResult.getActualRes(Config.ResPath_Root+"Simple_"+classfiers[j]+"\\"+i,"\\testing_id.txt",testing_id,id_actual_res,"\\s",1);
				GetResult.getPredictRes(Config.ResPath_Root+"Simple_"+classfiers[j]+"\\"+i,"\\result_"+type+".txt",testing_id,id_predict_res,weights[j]);
			}
			SaveInfo.result_writer(Config.ResPath_Root+res_dir,"\\final_result_"+i+"_"+type+".txt","final_testing_id_"+i+".txt",id_actual_res,id_predict_res);
			double accuracy =  GetResult.getAccuracy(id_actual_res,id_predict_res);
			double microF1 = GetResult.getMicroF1Score(id_actual_res, id_predict_res);
			double macroF1 = GetResult.getMacroF1Score(id_actual_res, id_predict_res);
			SaveInfo.saveResult(Config.ResPath_Root+res_dir+"\\final_result_"+i+"_"+type+"----accuracy="+accuracy);
			SaveInfo.saveResult(Config.ResPath_Root+res_dir+"\\final_result_"+i+"_"+type+"----MicroF1Score="+microF1);
			SaveInfo.saveResult(Config.ResPath_Root+res_dir+"\\final_result_"+i+"_"+type+"----MacroF1Score="+macroF1);
			accuracy_avg+=accuracy;
			microF1_avg+=microF1;
			macroF1_avg+=macroF1;
		}
		accuracy_avg/=fold;
		microF1_avg/=fold;
		macroF1_avg/=fold;
		SaveInfo.saveResult(Config.ResPath_Root+res_dir+"accuracy average="+accuracy_avg);
		SaveInfo.saveResult(Config.ResPath_Root+res_dir+"MicroF1Score average="+microF1_avg);
		SaveInfo.saveResult(Config.ResPath_Root+res_dir+"MacroF1Score average="+macroF1_avg);
	}

	public static void cross_validation(int k) throws IOException{
		double aver = 0;
		for(int i=0;i<fold;i++){
			Map<String,String> id_actual_res = new TreeMap<String, String>();
			Map<String,ResultNode> id_predict_res = new TreeMap<String, ResultNode>();
			for(String classfier : classfiers){
				// testing_id.txt testing_data.txt result_lg.txt 同一行为同一个用户
				List<String> testing_id = ReadInfo.getList(Config.ResPath_Root+classfier+"\\"+k+"\\"+i,"\\testing_id.txt","\\s",0);
				GetResult.getActualRes(Config.ResPath_Root+classfier+"\\"+k+"\\"+i,"\\testing_data.txt",testing_id,id_actual_res,"\t",0);
				GetResult.getPredictRes(Config.ResPath_Root+classfier+"\\"+k+"\\"+i,"\\result_"+type+".txt",testing_id,id_predict_res,1);
			}
			SaveInfo.result_writer(Config.ResPath_Root+res_dir+k,"\\final_result_"+i+"_"+type+".txt","final_testing_id_"+i+".txt",id_actual_res,id_predict_res);
			double accuracy =  GetResult.getAccuracy(id_actual_res,id_predict_res);
			SaveInfo.saveResult(Config.ResPath_Root+res_dir+k+"\\final_result_"+i+"_"+type+"----"+accuracy);
			aver+=accuracy;
		}
		aver/=fold;
		SaveInfo.saveResult("average----"+aver);
	}
	/*-------------------------------------Private-----------------------------------------------------------------*/
	

	

}
