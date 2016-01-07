package org.cl.model;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ClassNode {
	//int id_set_size = -1;
	private int classid = -1;
	private List<Set<String>>id_set_list = null;
	private Set<String> trainning_id_set = null;
	private Set<String> testing_id_set = null;
	private Set<String> learning_id_set = null;//对于tri_trainning，它是unlabeled id集合U
	private Set<String> learning_id_subset = null;//对于tri_trainning，它是unlabeled id集合的子集U'

	public ClassNode(){}
	public ClassNode(int classid,List<Set<String>> id_set_list){
		this.classid = classid;
		this.id_set_list = id_set_list;
		//this.id_set_size = id_set_size;
	}
	public ClassNode(int classid,Set<String> trainning_id_set,Set<String> testing_id_set) {
		this.classid = classid;
		this.trainning_id_set = trainning_id_set;
		this.testing_id_set = testing_id_set;
	}
	
	public int getClassid() {
		return classid;
	}
	public void setClassid(int classid) {
		this.classid = classid;
	}
	public Set<String> getTrainning_id_set() {
		return trainning_id_set;
	}
	public void setTrainning_id_set(Set<String> trainning_id_set) {
		this.trainning_id_set = trainning_id_set;
	}
	//从id_set_list选第i组以外的组作为训练ID
	public void setTrainning_id_set(int i) {
		this.trainning_id_set = new TreeSet<String>();
		for(int j=0;j<id_set_list.size();j++){
				if(j!=i){merge(trainning_id_set,id_set_list.get(j));}
		}
	}
	//从id_set_list选第i组以外 选num组作为训练ID
	public void setTrainning_id_set_byfold(int i,int num) {
		this.trainning_id_set = new TreeSet<String>();
		int n = 0;
		for(int j=0;j<id_set_list.size();j++){
			if(j!=i){
				merge(trainning_id_set,id_set_list.get(j));
				n++;
			}
			if(n==num)break;
		}
	}
	public Set<String> getLearning_id_set() {
		return learning_id_set;
	}
	public void setLearning_id_set(Set<String> learning_id_set) {
		this.learning_id_set = learning_id_set;
	}
	public void setLearning_id_set(int ... ii) {
		this.learning_id_set = new TreeSet<String>();
		for(int j=0;j<id_set_list.size();j++){
			boolean flag = true;
			for(int i:ii){
				if(j==i){flag = false;break;}
			}
			if(flag){merge(learning_id_set,id_set_list.get(j));}
		}
		
	}
	public Set<String> getLearning_id_subset() {
		return learning_id_subset;
	}
	public void setLearning_id_subset(Set<String> learning_id_subset) {
		this.learning_id_subset = learning_id_subset;
	}
	public Set<String> getTesting_id_set() {
		return testing_id_set;
	}
	public void setTesting_id_set(Set<String> testing_id_set) {
		this.testing_id_set = testing_id_set;
	}
	//从id_set_list选第i组作为测试ID
	public void setTesting_id_set(int i) {
		this.testing_id_set = this.id_set_list.get(i);
	}
	//从id_set_list选第i组以外的组中各抽取num_i个作为训练ID
	public void setTrainning_id_set_bynum(int i,int num) {
		this.trainning_id_set = new TreeSet<String>();
		int num_i = num/(id_set_list.size()-1);
		for(int j=0;j<id_set_list.size();j++){
			if(j!=i){merge(trainning_id_set,id_set_list.get(j),num_i);}
		}
	}
	
	private void merge(Set<String> trainning_id_set, Set<String> id_set) {
		for(String id : id_set){
			trainning_id_set.add(id);
		}
	}
	private void merge(Set<String> trainning_id_set, Set<String> id_set,int num_i) {
		int num = 0;
		for(String id : id_set){
			trainning_id_set.add(id);
			num++;
			if(num==num_i)break;
		}
	}


}
