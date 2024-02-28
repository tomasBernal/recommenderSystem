package recommendationSystem;

import java.util.LinkedList;

// This class represent the list of ratings of a user
public class UserRating {

	// The name of the user
	private int userId;
	
	// The list of ratings of the user
	private LinkedList<Rating> list;
	
	
	// Constructor of the class
	UserRating(int userId, LinkedList<Rating> list){
		
		this.userId = userId;
		this.list = list;
	}
	
	
	// Getters of the class
	int getUserId() {
		return userId;
	}
	
	LinkedList<Rating> getRatingList() {
		return list;
	}
}