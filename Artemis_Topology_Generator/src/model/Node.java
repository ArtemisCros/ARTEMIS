package model;

public class Node {
	private String id;
	private String name;
	
	public Node(String idP, String nameP) {
		id = idP;
		name = nameP;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
