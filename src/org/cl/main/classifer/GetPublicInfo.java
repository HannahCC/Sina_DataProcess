package org.cl.main.classifer;
import java.io.IOException;

import org.cl.conf.Config;
import org.cl.servies.GetTrainTestID;
import org.cl.utils.SaveInfo;

public class GetPublicInfo {
	
	public static void main(String args[]) throws IOException{
		SaveInfo.mkdir(Config.TrainTestID);
		//获取UserId
		//GetUserID.setUserID();//获取每类用户ID各ID_NUMBER个放在USERID_ROOT目录下
		//GetUserID.setUserIDWeibonumOverN();//获取每类CLASSFIERS特征都有的ID（通过getUserID()得到的ID）,且微博数超过WEIBO_NUMBER的用户,主要用户获取WeiboLevel特征时控制特征个数
		//在特殊情况下获取训练和测试用户的ID,得到[labelid]_testingid.txt， [size]_[labelid]_trainingid.txt存放在PUBLIC_INFO_ROOT/FOLD_i下
		//GetTrainTestID.tmp_setTrain_TestID(0.8f);
		//GetTrainTestID.tmp_setTrain_TestID("82");
		//GetTrainTestID.tmp_setTestID();
		//GetTrainTestID.setTrain_TestID();//从筛选过的ID中获取测试和训练用户ID组,分5组，每次取1份为测试，4份为训练
		//GetTrainTestID.setTrain_TestID2();//从筛选过的ID中获取测试和训练用户ID组,分5组，每次取1份为测试，4份为训练  格式与前面不同(for usertextNN)
		GetTrainTestID.setTrain_TestID_diffSize_byFOLD();//从筛选过的ID中获取测试和训练用户ID组,分5组，每次取1份为测试，4份中1份或2份、3份、4份为训练，以验证不同训练数据数量下模型效果
		//GetTrainTestID.setTrain_TestID_diffSize_bySIZE();//从筛选过的ID中获取测试和训练用户ID组,分5组，每次取1份为测试，4份中共抽取TRAIN_ID_SIZE_ARR个数据为训练数据
		//GetTrainTestID.setTrain_TestID_1vsall();//获取除labelid以外的类用户的ID组成的800个ID，分成5组
		//GetTrainTestID.setTrain_Test_LearnID();//从筛选过的ID中获取测试和训练用户ID组,分5组，每次取1份为测试，3份学习，1份为训练
		//GetTrainTestID.setTrain_Test_LearnID2();//从筛选过的ID中获取测试和训练用户ID组,分5组，每次取1份为测试，1份学习，3份为训练  格式与前面不同(for usertextNN)
		
		/*----获取LABELS中所有类用户区分时的idf和chi------*/
		/*GetUserFeature.getUserFeatureMap();
		for(int i=0;i<Config.FOLD;i++){
			for(int size : Config.TRAIN_ID_SIZE_ARR){//不同train_size
				//GetCHI.calculateCHI(i,size);//根据chi求解公式求出每一类用户，每个特征的chi值，并将各特征按chi降序排列，存储在[train_id_size]_[labelid]_[CLASSFIERS]chi.txt 或者 [train_id_size]_[非labelid]_[labelid]_[CLASSFIERS]chi.txt 
				//GetDF.calculateDF(i,size);//根据chi求解公式求出每一类用户，每个特征的chi值，并将各特征按chi降序排列，存储在[train_id_size]_[labelid]_[CLASSFIERS]chi.txt 或者 [train_id_size]_[非labelid]_[labelid]_[CLASSFIERS]chi.txt 
				GetIDF.calculateIDF(i,size,0);//根据各特征出现在测试用户文档集中的文档数，得到该特征的idf值，存储在[train_id_size]_[combination]_[classifername]_[typename]idf.txt中
				GetIDF.calculateIDF(i,size,1);//根据各特征出现在训练用户文档集中的文档数，得到该特征的idf值
			}
		}
		*/
		/*----获取1vs1的idf和chi------*/
		/*for(int i=0;i<Config.FOLD;i++){
			int[] LABELS = new int[2];
			for(int k=1;k<=4;k++){
				LABELS[0] = k;
				for(int j=k+1;j<=4;j++){
					LABELS[1] = j;
					GetCHI.calculateCHI(i,640);
					GetIDF.calculateIDF(i,640,0);
					GetIDF.calculateIDF(i,640,1);
				}
			}
		}*/
		
		/*----获取1vsall的idf和chi------*/
		/*for(int i=0;i<Config.FOLD;i++){
			int[] LABELS = new int[2];
			for(int k=1;k<=4;k++){
				LABELS[0] = k;
				LABELS[1] = k*10+k;
				GetCHI.calculateCHI(i,640);
				GetIDF.calculateIDF(i,640,0);
				GetIDF.calculateIDF(i,640,1);
			}
		}*/
	}
}
