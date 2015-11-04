package org.cl.model;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ClassNode {
	//int id_set_size = -1;
	int classid = -1;
	List<Set<String>>id_set_list = null;
	Set<String> trainning_id_set = null;
	Set<String> learning_id_set = null;
	Set<String> testing_id_set = null;
	Set<String> testing_id_set_fake = null;//tri_trainning是假设为test id 的unlabeled id集合
	/*String test_data = null;
	String train_data = null;*/
	//Set<String>id_set = null;

	public ClassNode(){}
	public ClassNode(int classid,List<Set<String>>id_set_list){
		this.classid = classid;
		this.id_set_list = id_set_list;
		//this.id_set_size = id_set_size;
	}
	public ClassNode(int classid,Set<String> trainning_id_set,Set<String> testing_id_set) {
		this.classid = classid;
		this.trainning_id_set = trainning_id_set;
		this.testing_id_set = testing_id_set;
	}
	public Set<String> getTesting_id_set_fake() {
		return testing_id_set_fake;
	}
	public void setTesting_id_set_fake(Set<String> testing_id_set_fake) {
		this.testing_id_set_fake = testing_id_set_fake;
	}
	public ClassNode(int classid,Set<String> trainning_id_set,Set<String> testing_id_set,Set<String> testing_id_set_fake) {
		this.classid = classid;
		this.trainning_id_set = trainning_id_set;
		this.testing_id_set = testing_id_set;
		this.testing_id_set_fake = testing_id_set_fake;
	}
	/*public ClassNode(int classid,Set<String> trainning_id_set,
			Set<String> testing_id_set, String test_data, String train_data) {
		this.classid = classid;
		this.trainning_id_set = trainning_id_set;
		this.testing_id_set = testing_id_set;
		this.train_data = train_data;
		this.test_data = test_data;
	}*/


	public List<Set<String>> getId_set_list() {
		return id_set_list;
	}
	public void setId_set_list(List<Set<String>> id_set_list) {
		this.id_set_list = id_set_list;
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


	public Set<String> getLearning_id_set() {
		return learning_id_set;
	}
	public void setLearning_id_set(Set<String> learning_id_set) {
		this.learning_id_set = learning_id_set;
	}
	public Set<String> getTesting_id_set() {
		return testing_id_set;
	}


	public void setTesting_id_set(Set<String> testing_id_set) {
		this.testing_id_set = testing_id_set;
	}


	/*public String getTest_data() {
		return test_data;
	}


	public void setTest_data(String test_data) {
		this.test_data = test_data;
	}


	public String getTrain_data() {
		return train_data;
	}


	public void setTrain_data(String train_data) {
		this.train_data = train_data;
	}*/
	//从id_set_list选第i组作为测试ID
	public void setTesting_id_set(int i) {
		this.testing_id_set = this.id_set_list.get(i);
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
