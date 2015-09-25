package org.cl.test;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Test{
	static Set<String> sWoman = new HashSet<String>();
	static Set<String> sMan = new HashSet<String>();
	static List<String> res_Woman = new ArrayList<String>();
	static List<String> res_Man= new ArrayList<String>();


	public static void main(String args[]){
		//		String id;
		File file1 = new File("E:\\Working\\test.txt");
		File file2 = new File("E:\\Working\\man.txt");
		File file3 = new File("E:\\Working\\woman.txt");
		try{
			InputStreamReader read=new InputStreamReader(new FileInputStream(file1),"utf-8");
			BufferedReader inTwo = new BufferedReader(read);//(new FileReader(file1));
			InputStreamReader read1=new InputStreamReader(new FileInputStream(file2),"utf-8");
			BufferedReader inThree = new BufferedReader(read1);//(new FileReader(file1));
			InputStreamReader read2=new InputStreamReader(new FileInputStream(file3),"utf-8");
			BufferedReader inFour= new BufferedReader(read2);//(new FileReader(file1));

			String s1 = null;
			String s2 = null;
			while((s2 = inThree.readLine())!=null){
					sMan.add(s2);
			}
			while((s2 = inFour.readLine())!=null){
					sWoman.add(s2);
			}
			while((s1 = inTwo.readLine())!=null){
				String id = s1.split("\\s")[0];
				if(sMan.contains(id)){res_Man.add(s1);}
				else if(sWoman.contains(id)){res_Woman.add(s1);}
				else{
					System.out.println(id+"为什么没有我！！！！");
				}
			}
			inTwo.close();
			inThree.close();
			inFour.close();
			save(res_Woman,"Woman_res");
			save(res_Man,"Man_res");
		}catch(IOException e){
			e.printStackTrace();
		}
	}


	private static void save(List<String> list,String saveName) throws IOException {
		// TODO Auto-generated method stub
		if(list!=null&&list.size()>0){
			File file = new File("E:\\Working\\"+saveName+".txt");
			//	System.out.println("E:\\\\"+list+".txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			for(String str :list){
				out.write(str+"\r\n");
			}
			out.flush();
			out.close();
		}
	}


}