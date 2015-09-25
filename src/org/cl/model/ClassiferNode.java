package org.cl.model;

public class ClassiferNode {

	private String classifer_name = null;
	private int classifer_size = 0;
	
	
	public ClassiferNode() {}
	public ClassiferNode(String classifer_name, int classifer_size) {
		this.classifer_name = classifer_name;
		this.classifer_size = classifer_size;
	}
	
	@Override
	public String toString() {
		return "ClassiferNode [classifer_name=" + classifer_name
				+ ", classifer_size=" + classifer_size + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((classifer_name == null) ? 0 : classifer_name.hashCode());
		result = prime * result + classifer_size;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassiferNode other = (ClassiferNode) obj;
		if (classifer_name == null) {
			if (other.classifer_name != null)
				return false;
		} else if (!classifer_name.equals(other.classifer_name))
			return false;
		if (classifer_size != other.classifer_size)
			return false;
		return true;
	}

	public String getClassifer_name() {
		return classifer_name;
	}
	public void setClassifer_name(String classifer_name) {
		this.classifer_name = classifer_name;
	}
	public int getClassifer_size() {
		return classifer_size;
	}
	public void setClassifer_size(int classifer_size) {
		this.classifer_size = classifer_size;
	}
	
	
}
