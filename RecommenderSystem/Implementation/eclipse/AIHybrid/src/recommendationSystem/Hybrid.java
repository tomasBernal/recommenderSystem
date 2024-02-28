package recommendationSystem;


import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


//This class implements the necessary functions to apply the hybrid system
public class Hybrid {

	
	// Files routes
	// In this string you have to write the route where you have the AI folder
	public final static String pathSource = "/Users/tomas/Desktop/";
	
	public final static String pathUser = pathSource+"AI/User/";
	public final static String pathResults = pathSource+"AI/Results/";
	public final static String pathDatasets = pathSource+"AI/Datasets/";
	
	
	// This function makes the intersection of the two recommendation lists
		// First, we include the recommendations that are in the two lists
		// Secondly, we include the remaining recommendations of the collaborative filtering
		// Third, we include the remaining recommendations of the content based filtering
	// This function receives the list of recommendations from the collaborative filtering (coll) and the list of recommendations from the content based filtering (cont)
	// This function returns a list of ratings that represents the final recommendations for the user
	public static LinkedList<LinkedList<String>> intersection(LinkedList<LinkedList<String>> coll, LinkedList<LinkedList<String>> cont){
		
		// This list will contain the intersection
		LinkedList<LinkedList<String>> recommendations = new LinkedList<LinkedList<String>> ();
		
		// This list will contain the recommendations added to the intersection list
		LinkedList<LinkedList<String>> addedOnes = new LinkedList<LinkedList<String>> ();
		
		// For each recommendation of the collaborative filtering
		for(LinkedList<String> rcoll : coll) {
			
			// We check if the recommendation is in the list of the content based too
			if(cont.contains(rcoll)) {
				
				// We add the recommendation to the intersection list
				recommendations.add(rcoll);
				
				// We add the recommendation to the list of added recommendations
				addedOnes.add(rcoll);
			}
		}
		
		
		// We remove the added movies on the collaborative filtering recommendation list
		coll.removeAll(addedOnes);
		
		// We remove the added movies on the content based filtering recommendation list
		cont.removeAll(addedOnes);

		// We add the remaining recommendations of the collaborative filtering
		recommendations.addAll(coll);
		
		// We add the remaining recommendations of the content filtering
		recommendations.addAll(cont);
		
		return recommendations;
	}
	
	
	// This function allows a recommendation based on a hybrid system, to do so, it calls the other functions of other classes
	// This function implements the hybrid system
	// This system uses content based filtering, collaborative filtering and knowledge filtering
	public static void main(String[] args) {
			
		/*
		// This is only necessary when a user interacts with the system
		// Creating the scanner that we will use in the knowledge filtering and for getting the user ratings
		Scanner keyboard = new Scanner(System.in);
		*/		
				
		// We store a list with the id, the name and the genres of the movies
		// The first node stores the id of the movie, the second the name and the rest the genres of the movie
		// The index of each node is the index of the movie in the matrix
		List<LinkedList<String>> films;
		
		// Here we obtain the list of films
		// From the movies file of the Toy Dataset
		//films = ReaderOfFiles.getMovies(pathDatasets+"ToySet/toy_set_movies.csv", ",");
		// From the movies file of the MovieLens Dataset 
		films = ReaderOfFiles.getMovies(pathDatasets+"MovieLens/movies.csv", ",");
			
		/*
		This is a pre-procesing of the csv file
		Its only necessary the first time we use a new ratings.csv
		*/ 
		
		// From the ratings file of the Toy Dataset
		//ReaderOfFiles.extractContent(pathDatasets+"ToySet/toy_set_ratings.csv", ",", films, pathDatasets+"ToySet/toy_preprocesed.txt");
		// From the ratings file of the MovieLens Dataset
		//ReaderOfFiles.extractContent(pathDatasets+"MovieLens/ratings.csv", ",", films, pathDatasets+"MovieLens/preprocesed.txt");
		
		/*
		// This print checks the function getMovies()
		System.out.println("FILMS : ");

		for (LinkedList<String> m : films) {

			for (String s : m) {
				System.out.print(s + " ");
			}
			System.out.println();
		}
		*/
		
		
		// We obtain the genres of all the movies (without repetitions) 
		// We need a list without repetitions, because we need to know the order of the genres for the content based filtering
		LinkedList<String> genres = new LinkedList<String> ();
		
		// For each movie
		for(LinkedList<String> movie : films) {
			
			// For each genre of the movie
			// The list that represent a movie contains the genres in the range of [2-last] positions
			for(int i = 2; i < movie.size(); i++) {
				
				// We check if the genre is in the list
				if(!genres.contains(movie.get(i))) {
					
					// If not
					
					// We add the genre to the list
					genres.add(movie.get(i));
				}
				
			}
		}
		
		/*
		// This print checks the function getGenres()
		System.out.println("");
		System.out.println("Genres of the films:");
		
		for(String g : genres) {
			System.out.println(g);
		}
		*/
		
		/*
		This function need a user who interacts with the system
		We obtain the genres that the user don't like for the knowledge filtering
		*/
		//ArrayList<String> knowledgeGenres = Knowledge.getGenresUser(genres, keyboard);
		
		
		// This function generates the genres file
		// You need to write the number of genres as a parameter
		//ReaderOfFiles.generateGenres(genres, 2, pathUser+"genres.txt");
		
		// This function receives the genres from a .txt file
		ArrayList<String> knowledgeGenres = Knowledge.getGenresFile(genres, pathUser+"genres.txt");
		
		// Here we store the genres that the user hates
		FileWriter file = null;
		PrintWriter pw = null;
        
        try
        {
        	file = new FileWriter(pathResults+"hatedGenres.txt");
            pw = new PrintWriter(file);

            for(String g : knowledgeGenres) {
            	pw.println(g);
            }

        } catch (Exception e) {
        	
            e.printStackTrace();
            
        } finally {
        	
           try {
        	   
           // Closing the file
           if (null != file)
        	   file.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }
        
		/*
		// Printing the catalog of movies
		System.out.println();
		System.out.println("Catalog of movies : ");
				
		for(LinkedList<String> row : films){
			System.out.println(row.get(1));
		}
		*/
        
        
		/*
		Asking the user the movie and the rating
		This function need a user who interacts with the system
		//This map stores the ratings of the user
		*/
		//Map<String, Double> userRatings = getRatingsUser(keyboard, films);

		
		// This function generates the ratings file
        // You need to write the number of ratings as a parameter
		//ReaderOfFiles.generateRatings(films, 120, pathUser+"ratings.txt");
		
		// This function receives the genres from a .txt file
		// This map stores the ratings of the user
		Map<String, Double> userRatings = ReaderOfFiles.getRatingsFile(films, pathUser+"ratings.txt");
			
		
		/*
		// This print checks the ratings of the user
		System.out.println();
		System.out.println("Movies rated by the user : ");
		
		for( String name : userRatings.keySet()) {
			
			System.out.println(name + " " + userRatings.get(name));
		}
		System.out.println();
		*/
			
		// Here we store the ratings of the user
		
		file = null;
		pw = null;
        
        try
        {
        	file = new FileWriter(pathResults+"userRatings.txt");
            pw = new PrintWriter(file);

            for(String m : userRatings.keySet()) {
            	pw.println(m + " with a " + userRatings.get(m));
            }

        } catch (Exception e) {
        	
            e.printStackTrace();
            
        } finally {
        	
           try {
        	   
           // Closing the file
           if (null != file)
        	   file.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }
        
        // This variable is used to calculate the execution time
        long startTime = System.currentTimeMillis();
        

		// We obtain the collaborative recommendations
        //Using Toy Dataset
      	//LinkedList<LinkedList<String>> collRecommendations = CollaborativeFiltering.collaborativeFiltering(userRatings, films, pathDatasets+"ToySet/toy_preprocesed.txt");
		//Using MovieLens Dataset
        
		LinkedList<LinkedList<String>> collRecommendations = CollaborativeFiltering.collaborativeFiltering(userRatings, films, pathDatasets+"MovieLens/preprocesed.txt");
		
		/*
		// This print check the collaborative filtering
		System.out.println("Collaborative filtering recommendations : ");
		for(LinkedList<String> r : collRecommendations) {
			
			System.out.println(r.get(1));
		}
		System.out.println();
		*/
		
		// Here we store the recommendations of the collaborative filtering
		file= null;
		pw = null;
		try {
			file = new FileWriter(pathResults+"collaborative.txt");
		    pw = new PrintWriter(file);
		    int counter = 1;
		    
		    for(LinkedList<String> r : collRecommendations) {
		    	pw.print(counter + " " +r.get(1) + " : " + r.get(2));
		    	
		    	for(int i = 3; i < r.size(); i++) {
		    		pw.print(" | " + r.get(i));
		    		
		    	}
		    	pw.println();
		    	counter++;
		    }

		 } catch (Exception e) {
		        	
			 e.printStackTrace();
		            
		 } finally {
		        	
			 try {
		        	   
				 // Closing the file
				 if (null != file)
					 file.close();
		     } catch (Exception e2) {
		             e2.printStackTrace();
		     }
		 }
		        
		
		// We obtain the content recommendations
		LinkedList<LinkedList<String>> contRecommendations = ContentBased.contentFiltering(films, genres, userRatings);
		
		/*
		// This print check the content based filtering
		System.out.println("Content based filtering recommendations : ");
		for(LinkedList<String> r : contRecommendations) {
			
			System.out.println(r.get(1));
		}
		System.out.println();
		*/
		
		// Here we store the recommendations of the content-based filtering
		file = null;
		pw = null;
		
		try {
			file = new FileWriter(pathResults+"content.txt");
			pw = new PrintWriter(file);

			int counter = 1;
			
			for(LinkedList<String> r : contRecommendations) {
		    	pw.print(counter + " " +r.get(1) + " : " + r.get(2));
		    	
		    	for(int i = 3; i < r.size(); i++) {
		    		pw.print(" | " + r.get(i));
		    		
		    	}
		    	pw.println();
		    	counter++;
		    }

		} catch (Exception e) {
				        	
			e.printStackTrace();
				            
		} finally {
				        	
			try {
				        	   
				// Closing the file
				if (null != file)
					file.close();
				} catch (Exception e2) {
				    e2.printStackTrace();
				}
		}
		
		
		// Here we obtain the recommendations of the intersection (without knowledge)
		LinkedList<LinkedList<String>> finalRecommendation = intersection(collRecommendations, contRecommendations);
		
		/*
		// This print check the hybrid system
		System.out.println("Hybrid system recommendations : ");
		for(LinkedList<String> r : finalRecommendation) {
			
			System.out.println(r.get(1));
		}
		System.out.println();
		*/
		
		// Here we store the recommendations of the intersection (without knowledge)
		file = null;
		pw = null;
		
		try {
			file = new FileWriter(pathResults+"hybrid.txt");
			pw = new PrintWriter(file);

			int counter = 1;
			
			for(LinkedList<String> r : finalRecommendation) {
		    	pw.print(counter + " " +r.get(1) + " : " + r.get(2));
		    	
		    	for(int i = 3; i < r.size(); i++) {
		    		pw.print(" | " + r.get(i));
		    		
		    	}
		    	pw.println();
		    	counter++;
		    }

		} catch (Exception e) {
						        	
			e.printStackTrace();
						            
		} finally {
						        	
			try {
						        	   
				// Closing the file
				if (null != file)
					file.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		
		// Here we obtain the final recommendation, the recommendation of the whole hybrid system
		finalRecommendation = Knowledge.filterGenres(finalRecommendation, knowledgeGenres);
		
		/*
		// This print check the knowledge filtering 
		System.out.println("Recommendations before applying the knowledge filtering : ");
		for(LinkedList<String> r : finalRecommendation) {
			
			System.out.print(r.get(1) + ", " + r.get(2));
			
			for(int i = 3; i < r.size(); i++) {
				System.out.print(" | " + r.get(i) );
			}
			
			System.out.println();
		}
		*/
		
		// Here we store the final recommendation, the recommendation of the whole hybrid system
		file = null;
		pw = null;
				
		try {
			file = new FileWriter(pathResults+"final.txt");
			pw = new PrintWriter(file);
			
			int counter = 1;
			
			for(LinkedList<String> r : finalRecommendation) {
		    	pw.print(counter + " " +r.get(1) + " : " + r.get(2));
		    	
		    	for(int i = 3; i < r.size(); i++) {
		    		pw.print(" | " + r.get(i));
		    		
		    	}
		    	pw.println();
		    	
		    	counter++;
		    }
	
		} catch (Exception e) {
						        	
			e.printStackTrace();
						            
		} finally {
						        	
			try {
						        	   
				// Closing the file
				if (null != file)
					file.close();
				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
		}
		
		// This variable is used to calculate the execution time
		long finalTime = System.currentTimeMillis();
	   
		
		
		
		// Here we store the execution time
		file = null;
		pw = null;
			
		try {
			file = new FileWriter(pathResults+"time.txt");
			pw = new PrintWriter(file);
			
			pw.println(finalTime-startTime);
	
		} catch (Exception e) {
						        	
			e.printStackTrace();
						            
		} finally {
						        	
			try {
						        	   
				// Closing the file
				if (null != file)
					file.close();
				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
