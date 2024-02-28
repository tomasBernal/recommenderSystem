package recommendationSystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class Knowledge {

	// This function ask the user the genres that he don't like
	public static HashSet<String> getGenres (HashSet<String> allGenres, Scanner keyboard){
		
		// This set contains the genres that the user don't like
		HashSet<String> genres = new HashSet<String> ();
		
		// The user likes all the genres
		allGenres.add("none");
				
		System.out.println();
		// Printing the catalog of movies
		System.out.println("Set of genres : ");
				
		for(String genre : allGenres){
			System.out.println(genre);
		}
		System.out.println();
		
		
		
			// This variable is used to indicate if the user wants to write more genres
			Boolean finish = false;
			
			do {	
				
				// Asking for the genre
				
				System.out.println("Write the genre that you don't like : ");
				String genre = keyboard.nextLine();
								
				// We check if the genre belongs to the set of genres
				while(!allGenres.contains(genre)){
											
					// If not we ask again
					System.out.println("The genre must be in the set of genres");
												
					// Asking for the movie
					System.out.println("Write the genre that you don't like : ");
					genre = keyboard.nextLine();
								
				}
											
				// If yes we add the genre to the set
				genres.add(genre);
				
										
									
								
									
				// Asking if the user wants to write more genres
				System.out.println("Press y to continue and n to finish");
									
								
				// Checking if the user wants to rate more movies
				if (keyboard.nextLine().equals("n")) {
					finish = true;
				}
						
			// Repeat until the user doesn't want to write more genres
			} while (!finish);
		
		return genres;
	}
	
	
	// This function returns a new movie recommendation list with the movies that doesn't has the genres that the user don't like
	public static LinkedHashMap<String, Double> filterGenres (HashMap<String, Double> recommendations, HashMap<String, LinkedList<String>> movieGenres, HashSet<String> knowledgeGenres){
		
		// Creating the new list
		LinkedHashMap<String, Double> new_recommendations = new LinkedHashMap<> ();
		
		// For each movie
		for(String m : recommendations.keySet()) {
			
			// We get the genres of the movie
			LinkedList<String> mg = movieGenres.get(m);
			
			// If the movie don't have any genre that is in the set of genres that the user don't like
			// We conserve that movie in the recommendation map
			if(!mg.containsAll(knowledgeGenres)) {
				new_recommendations.put(m, recommendations.get(m));
			}
			
			
			
		}
		
		return new_recommendations;
		
	}
	
}
