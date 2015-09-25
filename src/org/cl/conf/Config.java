package org.cl.conf;

import org.cl.utils.SaveInfo;

public class Config {

	public static String SrcPath_Root = "D:\\Project_DataMinning\\Data\\Sina_res\\Sina_GenderPre_Mute_ext1000\\";//Sina_AgePre_JSON\\";//Sina_GenderPre\\";
	public static String ResPath_Root = "D:\\Project_DataMinning\\DataProcessd\\Sina_GenderPre_mute1000\\";//Sina_AgePre_JSON\\";//Sina_AgePre_JSON\\";//Sina_GenderPre\\";
	public static String Public_Info = ResPath_Root + "Public_Info_Rel\\";
	public static String ResPath = ResPath_Root;
	//public static double ratio =  0.2;//测试数据占总数据的比例
	static{
		SaveInfo.mkdir(ResPath_Root);
	}
}
