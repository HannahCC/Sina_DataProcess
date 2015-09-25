package org.cl.model;

import java.util.ArrayList;
import java.util.List;

import org.cl.utils.Utils;

public class ResultNode {
	private int label;
	private List<Double> results;
	

	public ResultNode(String res_str, double weight,String regex) {
		String[] res = res_str.split(regex);
		label = Integer.parseInt(res[0]);
		results = new ArrayList<Double>();
		for(int i=1;i<res.length;i++){
			results.add(weight*Double.parseDouble(res[i]));
		}
	}
	public ResultNode(String[] res, int ... index) {
		results = new ArrayList<Double>();
		for(int i : index){
			results.add(Double.parseDouble(res[i]));
		}
		int max = Utils.getMax(results);
		label = max+1;
	}
	
	public void accumulate_results(String res_str, double weight, String regex){
		String[] res = res_str.split(regex);
		for(int i=1;i<res.length;i++){
			double sum = results.get(i-1)+Double.parseDouble(res[i])*weight;
			results.set(i-1, sum);
		}
		int max = Utils.getMax(results);
		label = max+1;
	}
	
	public void accumulate_results(String[] res, double weight, int ... index){
		for(int i=0;i<index.length;i++){
			double sum = results.get(i)+Double.parseDouble(res[index[i]])*weight;
			results.set(i, sum);
		}
		int max = Utils.getMax(results);
		label = max+1;
	}

	
	public String toString(String regex){
		StringBuffer str = new StringBuffer();
		str.append(label+regex);
		//DecimalFormat df = new DecimalFormat("#0.000000");   
		for(Double res : results){
			//String res_str = df.format(res);
			str.append(res+regex);
		}
		return str.toString();
	}

	public int getLabel() {
		return label;
	}


	public void setLabel(int label) {
		this.label = label;
	}


	public List<Double> getResults() {
		return results;
	}


	public void setResults(List<Double> results) {
		this.results = results;
	}
	
	
}
