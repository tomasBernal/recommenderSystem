package recommendationSystem;

import java.util.Objects;


// This class represents a rating from a user for a movie
public class Rating {
	
	// The name  of the movie
	private String movie;
	
	// The rating for the movie
	private double rating;
	
	
	// Constructor of the class
	Rating(String movie, double rating){
		this.movie = movie;
		this.rating = rating;
	}
	
	
	// Getters of the class
	String getMovieName() {
		return movie;
	}
	
	double getMovieRate() {
		return rating;
	}

	
	// Necessary for comparing Rating objects
	@Override
	public int hashCode() {
		return Objects.hash(movie);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Rating other = (Rating) obj;
		
		// Two objects of the class are equals if the name of the movie are the same
		return Objects.equals(movie, other.movie);
	}
	
	
}


	