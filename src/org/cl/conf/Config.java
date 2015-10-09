package org.cl.conf;

import org.cl.utils.SaveInfo;

public class Config {

	public static String SrcPath_Root = "D:\\Project_DataMinning\\Data\\Sina_res\\Sina_NLPIRext1000_Mute_GenderPre\\";
	public static String ResPath_Root = "D:\\Project_DataMinning\\DataProcessd\\Sina_GenderPre_mute1000\\";
	public static String Public_Info = ResPath_Root + "Public_Info_Rel\\";
	public static String ResPath = ResPath_Root;
	//public static double ratio =  0.2;//测试数据占总数据的比例
	static{
		SaveInfo.mkdir(ResPath_Root);
	}
}
