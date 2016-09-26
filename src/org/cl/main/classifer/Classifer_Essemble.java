package org.cl.main.classifer;
import java.io.IOException;

import org.cl.conf.Config;
import org.cl.servies.GetResult;
import org.cl.utils.SaveInfo;

public class Classifer_Essemble {

	/**
	 * 对于同一批数据集（训练、测试均相同），将使用了不同的特征进行分类得到的预测结果，进行简单投票得到最后结果
	 * @param args
	 * @throws InterruptedException
	 */
	
	static int fold = Config.FOLD;
	static String classifier_type = Config.CLASSIFIER_TYPE;
	static double[] weights = {1,1,1,1,1,1,1,1,1,1,1};//new double[10];
	static String src_dir = "CNNWord_";//"Simple_";//"";//
	static String res_dir;
	//static String[] classfiers = null;//{"VFri","Win8_L100_TagAvgVec","SrcAvgVec_Baike"};
	static String[] classfiers = {"TagVec_3",/*"SrcVec",*/"FriVec"};
	public static void main(String[] args) throws InterruptedException, IOException{
		//classfiers = args;
		
		/*weights[0] = 0.5;
		for(int j=1;j<classfiers.length;j++){
			weights[j] = (1-weights[0])/2;
		}*/
		
		res_dir = "MutiClassifer";
		for(int j=0;j<classfiers.length;j++){
			res_dir+="+"+classfiers[j];
			if(j==0 && weights[j]!=1){
				res_dir+="("+weights[j]+")";
			}
		}
		SaveInfo.mkdir(Config.ResPath_Root+res_dir);
		cross_validation("");
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

		SaveInfo.log_buff_writer(Config.ResPath_Root+res_dir,"\\res.txt",false);
	}

	public static void cross_validation(String dir) throws IOException{
		double aver = 0;
		for(int i=0;i<fold;i++){
			double accuracy =  GetResult.getEssembleResult(i+"", src_dir, res_dir, dir,classfiers);
			aver+=accuracy;
		}
		aver/=fold;
		SaveInfo.option_log("average----"+aver);
		SaveInfo.res_log("average----"+aver,false);
	}
	

	

}
