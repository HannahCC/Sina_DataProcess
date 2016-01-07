package org.cl.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FindWrongOrRightID {

	/**
	 * 根据resuly_lg找出判断错误的id，可能为不好的用户
	 */
	static String Path = "D:\\Project_DataMinning\\DataProcessd\\Sina_GenderPre_400\\Simple_LFLDASrcTopic_train_2000_100_nvabg_AVG·1000\\";
	static int fold_i = 0;
	public static void main(String[] args) {
		for(int f=0;f<5;f++){
			fold_i = f;

			List<String> testingid = getneedlist(Path+fold_i+"\\testing_id.txt");
			List<String> testing_data = getneedlist(Path+fold_i+"\\testing_data.txt");
			List<String> resultlist = getneedlist(Path+fold_i+"\\result_lg.txt");
			List<String> RightIdList = new ArrayList<String>();
			List<String> WrongIdList = new ArrayList<String>();
			String result = null;
			String truth = null;
			for(int i = 0; i <resultlist.size(); i++){
				result = resultlist.get(i).split("\\s",2)[0];
				truth = testing_data.get(i).split("\t",2)[0];
				if(result.equals(truth)){
					RightIdList.add(testingid.get(i)+"\t**\t"+resultlist.get(i)+"\t**\t"+testing_data.get(i).split("\t",2)[1]);
				}else{
					WrongIdList.add(testingid.get(i)+"\t**\t"+resultlist.get(i)+"\t**\t"+testing_data.get(i).split("\t",2)[1]);
				}
			}

			try {
				save(RightIdList,"result_lg_right.txt");
				save(WrongIdList,"result_lg_wrong.txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	private static List<String> getneedlist(String filename){
		List<String> needList = new ArrayList<String>();
		File file1 = new File(filename);
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file1),"utf-8");
			BufferedReader inOne = new BufferedReader(read);

			String s = null;
			while((s = inOne.readLine())!=null){
				needList.add(s);
			}
			System.out.println("the size of file "+filename+" is "+needList.size());
			inOne.close();
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return needList;

	}

	private static void save(List<String> list,String saveName) throws IOException {
		// TODO Auto-generated method stub
		if(list!=null&&list.size()>0){
			File file = new File(Path+fold_i+"\\"+saveName);
			//	System.out.println("E:\\\\"+list+".txt");
			OutputStreamWriter out1=new OutputStreamWriter(new FileOutputStream(file),"utf-8");
			BufferedWriter outOne = new BufferedWriter(out1);
			for(String str :list){
				outOne.write(str+"\r\n");
			}

			outOne.flush();
			outOne.close();
		}
	}

}
