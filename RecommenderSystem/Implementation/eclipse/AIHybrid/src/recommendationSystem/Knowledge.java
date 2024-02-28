package recommendationSystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;


// This class implements the necessary functions to apply the knowledge filtering
public class Knowledge {

	// This function ask the user the genres that he don't like (Its interactive)
	// This function receives the list of genres of all the movies (allGenres) and the scanner needed to get user data (keyboard)
	// This function returns a list with the genres that the user don't like
	public static ArrayList<String> getGenresUser (LinkedList<String> allGenres, Scanner keyboard){
		
		// This set contains the genres that the user don't like
		ArrayList<String> genres = new ArrayList<String> ();
		
		// In case that the user likes all the genres
		allGenres.add("none");
			
		
		// Printing the catalog of movies
		System.out.println();
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
											
				// Asking for the genre again
				System.out.println("Write the genre that you don't like : ");
				
				genre = keyboard.nextLine();
							
			}
			
			// We check if the genre is already in the list
			if(!genres.contains(genre)) {
				
				// If not we add the genre to the list
				genres.add(genre);
				
			}
					
			// Asking if the user wants to write more genres
			System.out.println("Press y to continue and n to finish");
								
							
			// Checking if the user wants to write more genres
			if (keyboard.nextLine().equals("n")) {
				
				finish = true;
				
			}
					
		// Repeat until the user doesn't want to write more genres
		} while (!finish);
		
		return genres;
	}
	
	
	// This function receives the genres that the user don't like from a file (Its not interactive)
	// This function receives the list of genres of all the movies (allGenres) and the path of the used file (path)
	// This function returns a list with the genres that the user don't like
	public static ArrayList<String> getGenresFile(LinkedList<String> allGenres, String path){
		
		// This set contains the genres that the user don't like
		ArrayList<String> genres = new ArrayList<String> ();
		
		// In case that the user likes all the genres
		allGenres.add("none");

		// This variables are used when the file is read
		FileReader fr = null;
		BufferedReader br = null;

		try {
			
			// Creating the reader
			fr = new FileReader (path);
			br = new BufferedReader(fr);

			// Reading the file
			String line;
			while((line=br.readLine())!= null){
				
				// We check if we can add the actual genre
				// If it is in the list of all genres
				// If it is not in the list of hated genres
				if(allGenres.contains(line) && !genres.contains(line)) {
					genres.add(line);
				}
				
			}
				
		} catch(Exception e){
			
			e.printStackTrace();
			
		}finally{
			
			// Closing the reader
			try{                    
				if( null != fr ){  
				
					fr.close();  
					
				}    
				
			}catch (Exception e2){ 
				e2.printStackTrace();
			}
		}
		
		return genres;
	}
		
		
		
	// This function filter a list of recommendations applying the knowledge filtering
	// This function implements the knowledge-based filtering
	// This function receives a list of ratings (recommendations), the list of genres the user don't like (knowledgeGenres) and the list of films (films)
	// This function returns the original list of recommendations minus the movies that have a genre that the user don't like
	public static LinkedList<LinkedList<String>> filterGenres(LinkedList<LinkedList<String>> recommendations, ArrayList<String> knowledgeGenres){
		
		// This list will contain the recommendations with the knowledge filtering applied
		LinkedList<LinkedList<String>> new_recommendations = new LinkedList<> ();
		
		// This list will contain the movies that has any genre that the user don't like
		// Movies that we have to remove from the list
		LinkedList<LinkedList<String>> deleteMovies = new LinkedList<> ();
		
		
		// For each recommended movie
		for(LinkedList<String> m : recommendations) {
			
			// Used for the iteration in the genres list
			int index = 0;
			
			// Used for deleting movies with hated genres
			boolean deleted = false;
			
			// For each genre that the user don't like
			while(index < knowledgeGenres.size() && deleted == false) {
				
				// We check if the movie has this genre
				if(m.contains(knowledgeGenres.get(index))) {
					
					//If yes
					
					// We add the movie to the deletedMovies list
					deleteMovies.add(m);
					
					// We break the loop, we dont need to search more genres in this movie
					deleted = true;
			
				}
				
				index++;
			}

			
		}
		
		// Initialization of the list that we have to return
		new_recommendations = recommendations;
		
		// Deleting the movies with any genre that the user don't like
		new_recommendations.removeAll(deleteMovies);
		
		return new_recommendations;
	}
	
}
