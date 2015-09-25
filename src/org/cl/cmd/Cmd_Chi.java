package org.cl.cmd;

import org.cl.conf.Config;

public class Cmd_Chi {
	public static boolean chi(String classifer) {
		try {
			String path = Config.ResPath_Root+"TF_IDF-CHI\\";
			String cmdStr = "cmd /c start "+path+"CHI "+path+classifer+"_CHI-In.txt "+path+classifer+"_CHI-Out.txt";
			Runtime.getRuntime().exec(cmdStr);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
