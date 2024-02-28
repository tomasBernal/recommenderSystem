package recommendationSystem;

import java.util.LinkedList;

public class UserRating {

	private int userId;
	private LinkedList<Rating> list;
	
	UserRating(int userId, LinkedList<Rating> list){
		
		this.userId = userId;
		this.list = list;
	}
	
	// Getters
	
	int getUserId() {
		return userId;
	}
	
	LinkedList<Rating> getRatingList() {
		return list;
	}
}