package recommendationSystem;

//We use this class as a struct

public class Rating {
	
	private String movie;
	private double rating;
	
	Rating(String movie, double rating){
		this.movie = movie;
		this.rating = rating;
	}
	
	// Getters
	String getMovieName() {
		return movie;
	}
	
	double getMovieRate() {
		return rating;
	}
}


	