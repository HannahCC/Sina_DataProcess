package org.cl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.cl.model.ResultNode;

public class GetResult {
	/**
	 * 获取结果文件  uid:UID:实际lable##预测label##预测置信度
	 * @param i 
	 * @return 
	 * @throws IOException  
	 */
	public static Map<String, String> getResult(String path, String test_id_file, String test_data_file, String result_file,String type) throws IOException {
		List<String> testing_id = ReadInfo.getList(path,"\\"+test_id_file+".txt","\\s",0);
		Map<String,String> id_res = new TreeMap<String, String>();
		getActualRes(path,"\\"+test_data_file+".txt",testing_id,id_res,"\t",0);
		getPredictRes(path,"\\"+result_file+"_"+type+".txt",testing_id,id_res);
		return id_res;
	}

	/**
	 * 
	 * @param dir   预测结果所在目录
	 * @param filename   预测结果存放的文件
	 * @param testing_id   预测结果对应的ID，ID顺序与结果文件相对应
	 * @param id_predict_res   为空，或者存放了其他分类器对某一ID的预测结果
	 * @throws IOException
	 */
	public static void getPredictRes(String dir, String filename,
			List<String> testing_id, Map<String, ResultNode> id_predict_res,double weight) throws IOException {
		File f = new File(dir+filename);
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		int j = 0;
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			String id = testing_id.get(j++);
			if(id_predict_res.containsKey(id)){
				id_predict_res.get(id).accumulate_results(line,weight,"\\s");//与之前的结果进行累加，每个特征的权值相等
			}else {
				ResultNode res = new ResultNode(line,weight,"\\s");
				id_predict_res.put(id,res);
			}
		}
		r.close();
	}

	/**
	 * 
	 * @param dir   预测结果所在目录
	 * @param filename   预测结果存放的文件
	 * @param testing_id   预测结果对应的ID，ID顺序与结果文件相对应
	 * @param id_actual_res   id对应的实际label
	 * @param regex
	 * @param i
	 * @throws IOException
	 */
	public static void getActualRes(String dir, String filename,
			List<String> testing_id, Map<String,String> id_actual_res,
			String regex, int i) throws IOException {
		File f = new File(dir+filename);
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		int j = 0;
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			String res = line.split(regex)[i];
			id_actual_res.put(testing_id.get(j++),res);
		}
		r.close();
	}

	public static double getAccuracy(Map<String, String> id_actual_res,Map<String, ResultNode> id_predict_res) {
		int num = 0;
		for(Entry<String, String> actual_res_entry:id_actual_res.entrySet()){
			String id = actual_res_entry.getKey();
			int actual_res = Integer.parseInt(actual_res_entry.getValue());
			int predict_res = id_predict_res.get(id).getLabel();
			if(actual_res==predict_res){num++;}
		}
		return num/(double)id_actual_res.size();
	}

	/**
	 * Auther: WangYi
	 * CreatedAt:2015-9-2
	 * @param id_actual_res	id对应的实际label
	 * @param id_predict_res   为空，或者存放了其他分类器对某一ID的预测结果
	 */
	public static double getMicroF1Score(Map<String,String> id_actual_res,Map<String,ResultNode> id_predict_res){
		return 2.0/(double)((1.0/getMicroPrecision(id_actual_res, id_predict_res))+(1.0/getMicroRecall(id_actual_res, id_predict_res)));
	}

	/**
	 * Auther: WangYi
	 * CreatedAt:2015-9-2
	 * @param id_actual_res	id对应的实际label
	 * @param id_predict_res   为空，或者存放了其他分类器对某一ID的预测结果
	 */
	public static double getMacroF1Score(Map<String,String> id_actual_res,Map<String,ResultNode> id_predict_res){
		//macroF1=(F1男+F1女)/2
		return (GetResult.getManF1Score(id_actual_res, id_predict_res)+GetResult.getWomanF1Score(id_actual_res, id_predict_res))/2.0;
	}


	/***********************************************************************************************************************************/
	/**
	 * 
	 * @param dir   预测结果所在目录
	 * @param filename   预测结果存放的文件
	 * @param testing_id   预测结果对应的ID，ID顺序与结果文件相对应
	 * @param id_res	uid:UID:实际lable
	 * @throws IOException
	 */
	private static void getPredictRes(String dir, String filename,
			List<String> testing_id, Map<String, String> id_res) throws IOException {
		File f = new File(dir+filename);
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = "";
		int j = 0;
		while((line = r.readLine())!=null){
			if(line.equals(""))continue;
			String[] item = line.split("\\s");
			double support = Math.abs(Double.parseDouble(item[1])-Double.parseDouble(item[2]));
			String id = testing_id.get(j++);
			id_res.put(id,id_res.get(id)+"##"+item[0]+"##"+support);
		}
		r.close();
	}

	/**
	 * Auther: WangYi
	 * CreatedAt:2015-9-2
	 * @param id_actual_res	id对应的实际label
	 * @param id_predict_res   为空，或者存放了其他分类器对某一ID的预测结果
	 */
	private static double getMicroPrecision(Map<String,String> id_actual_res,Map<String,ResultNode> id_predict_res){
		int num=0;
		for(Entry<String, String> actual_res_entry:id_actual_res.entrySet()){
			String id = actual_res_entry.getKey();
			int actual_res = Integer.parseInt(actual_res_entry.getValue());
			int predict_res = id_predict_res.get(id).getLabel();
			if(actual_res==predict_res){num++;}
		}
		return num/(double)id_actual_res.size();
	}

	/**
	 * Auther: WangYi
	 * CreatedAt:2015-9-2
	 * @param id_actual_res	id对应的实际label
	 * @param id_predict_res   为空，或者存放了其他分类器对某一ID的预测结果
	 */
	private static double getMicroRecall(Map<String,String> id_actual_res,Map<String,ResultNode> id_predict_res){
		int num=0;
		for(Entry<String, String> actual_res_entry:id_actual_res.entrySet()){
			String id = actual_res_entry.getKey();
			int actual_res = Integer.parseInt(actual_res_entry.getValue());
			int predict_res = id_predict_res.get(id).getLabel();
			if(actual_res==predict_res){num++;}
		}
		return num/(double)id_actual_res.size();
	}

	/**
	 * Auther: WangYi
	 * CreatedAt:2015-9-2
	 * @param id_actual_res	id对应的实际label
	 * @param id_predict_res   为空，或者存放了其他分类器对某一ID的预测结果
	 */
	private static double getManPrecision(Map<String,String> id_actual_res,Map<String,ResultNode> id_predict_res){
		/* 
		 *    男(P)        女(N)
                                         判男  6（TP）        2（FP）
                                         判女  4（FN）       8（TN）
             Pre男=6/（6+2）  Recall（男）=6/(6+4)  F1男=2/(1/Pre男+1/Recall男)
		 */
		int guessManCount=0;
		int hitManCount=0;
		for(Map.Entry<String, ResultNode> predict_res_entry:id_predict_res.entrySet()){
			String id=predict_res_entry.getKey();
			int predict_res=predict_res_entry.getValue().getLabel();
			if(predict_res==2){ //1  男2
				++guessManCount;
				int actual_res=Integer.parseInt(id_actual_res.get(id));
				if(2==actual_res) ++hitManCount;
			}
		}
		if(guessManCount>0) return (double)(1.0*hitManCount)/(1.0*guessManCount);
		else return 0.0;
	}
	/**
	 * Auther: WangYi
	 * CreatedAt:2015-9-2
	 * @param id_actual_res	id对应的实际label
	 * @param id_predict_res   为空，或者存放了其他分类器对某一ID的预测结果
	 */
	private static double getManRecall(Map<String,String> id_actual_res,Map<String,ResultNode> id_predict_res){
		/*
		 *    男(P)        女(N)
                                         判男  6（TP）        2（FP）
                                         判女  4（FN）       8（TN）
             Pre男=6/（6+2）  Recall（男）=6/(6+4)  F1男=2/(1/Pre男+1/Recall男)
		 */ 
		int realManCount=0;
		int hitManCount=0;
		for(Map.Entry<String, String> actual_res_entry:id_actual_res.entrySet()){
			String id=actual_res_entry.getKey();
			int actual_res=Integer.parseInt(actual_res_entry.getValue());
			if(2==actual_res){
				++realManCount;
				int predict_res = id_predict_res.get(id).getLabel();
				if(predict_res==actual_res) ++hitManCount;
			}	
		}
		//System.out.println("In getManRecall | "+"\thitman:"+hitManCount);
		return (double)(1.0*hitManCount)/(1.0*realManCount);
	}
	/**
	 * Auther: WangYi
	 * CreatedAt:2015-9-2
	 * @param id_actual_res	id对应的实际label
	 * @param id_predict_res   为空，或者存放了其他分类器对某一ID的预测结果
	 */
	private static double getManF1Score(Map<String,String> id_actual_res,Map<String,ResultNode> id_predict_res){
		/*
		 * F1=2/(1/pre+1/recall)
		 */
		//当 Recall=0，时，其Precision必然为 0
		double recall = GetResult.getManRecall(id_actual_res, id_predict_res);
		double f1 = 0.0;
		if(recall!=0.0){
			double precision = GetResult.getManPrecision(id_actual_res, id_predict_res);
			f1  = 2.0/((1.0/recall)+(1.0/precision));
		}
		return f1;
	}
	
	private static double getWomanPercision(Map<String,String> id_actual_res,Map<String,ResultNode> id_predict_res){
		/* 
		 *    男(P)        女(N)
                                         判男  6（TP）        2（FP）
                                         判女  4（FN）       8（TN）
             Pre男=6/（6+2）  Recall（男）=6/(6+4)  F1男=2/(1/Pre男+1/Recall男)
		 */
		int guessWomanCount=0;
		int hitWomanCount=0;
		for(Map.Entry<String, ResultNode> predict_res_entry:id_predict_res.entrySet()){
			String id=predict_res_entry.getKey();
			int predict_res=predict_res_entry.getValue().getLabel();
			if(predict_res==1){ //1  男2
				++guessWomanCount;
				int actual_res=Integer.parseInt(id_actual_res.get(id));
				if(1==actual_res) ++hitWomanCount;
			}
		}
		if(guessWomanCount>0) return (double)(1.0*hitWomanCount)/(1.0*guessWomanCount);
		else return 0.0;
	}
	
	private static double getWomanRecall(Map<String,String> id_actual_res,Map<String,ResultNode> id_predict_res){
		/*
		 *    男(P)        女(N)
                                         判男  6（TP）        2（FP）
                                         判女  4（FN）       8（TN）
             Pre男=6/（6+2）  Recall（男）=6/(6+4)  F1男=2/(1/Pre男+1/Recall男)
		 */ 
		int realWomanCount=0;
		int hitWomanCount=0;
		for(Map.Entry<String, String> actual_res_entry:id_actual_res.entrySet()){
			String id=actual_res_entry.getKey();
			int actual_res=Integer.parseInt(actual_res_entry.getValue());
			if(1==actual_res){
				++realWomanCount;
				int predict_res = id_predict_res.get(id).getLabel();
				if(predict_res==actual_res) ++hitWomanCount;
			}	
		}
		//System.out.println("In getWomanRecall | "+"\thitWoman:"+hitWomanCount);
		return (double)(1.0*hitWomanCount)/(1.0*realWomanCount);
	}
	
	private static double getWomanF1Score(Map<String,String> id_actual_res,Map<String,ResultNode> id_predict_res){
		/*
		 * F1=2/(1/pre+1/recall)
		 */
		//当 Recall=0，时，其Precision必然为 0
		double recall = GetResult.getWomanRecall(id_actual_res, id_predict_res);
		double f1 = 0.0;
		if(recall!=0.0){
			double precision = GetResult.getWomanPercision(id_actual_res, id_predict_res);
			f1  = 2.0/((1.0/recall)+(1.0/precision));
		}
		return f1;
	}


}
