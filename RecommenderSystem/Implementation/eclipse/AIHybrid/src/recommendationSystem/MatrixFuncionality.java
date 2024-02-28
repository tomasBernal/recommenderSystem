package recommendationSystem;

import java.util.ArrayList;
import java.util.List;

// This class implements the functions necessary to work with matrix
public class MatrixFuncionality {

	// This function show the matrix given as a parameter in the console
	// This function receives the matrix to be printed (matrix)
	public static void printMatrix(List<ArrayList<Double>> matrix) {

		for (int i = 0; i < matrix.size(); i++) {

			System.out.print(matrix.get(i).get(0));

			for (int j = 1; j < matrix.get(i).size(); j++) {

				System.out.print(", " + matrix.get(i).get(j));

			}

			System.out.println();
		}

	}
	
	
	// This function standardized a list (row of the matrix)
		// Standardized value = (value - rowMeanValue) / (rowMax - rowMin)
	// This function receives the row to be standardized
	// This function returns the list (row of the matrix) given as a parameter but standardized
	public static ArrayList<Double> standardizeRow(List<Double> row) {
			
		// New row
		ArrayList<Double> standarizedRow = new ArrayList<>();
				
		// Needed variables
		double mean, maximum, minimum, total;
			
		// Initialization of the variables
		minimum = row.get(0);
		maximum = row.get(0);
		total = row.get(0);

		// For each element
		for (int j = 0; j < row.size(); j++) {

			// We need this for the calculation of the mean
			total += row.get(j);

			// Checking the maximum
			if (row.get(j) > maximum) {
				maximum = row.get(j);
			}

			// Checking the minimum
			if (row.get(j) < minimum) {
				minimum = row.get(j);
			}

		}

		// Calculating the mean
		mean = total / row.size();

		// Calculating the new standardized row
		for (int j = 0; j < row.size(); j++) {

			// Standardized value = (value - rowMeanValue) / (rowMax - rowMin)
			standarizedRow.add((row.get(j) - mean) / (maximum - minimum));
		}
			
		return standarizedRow;

	}

		
	// This function standardized a matrix
		// Standardized value = (value - rowMeanValue) / (rowMax - rowMin)
	// This function receives the matrix to be standardized (matrix)
	// This function returns the matrix given as a parameter but standardized	
	public static List<ArrayList<Double>> standardizeMatrix(List<ArrayList<Double>> matrix) {

		// New matrix
		ArrayList<ArrayList<Double>> standarizedMatrix = new ArrayList<>();

		// For each row
		for (int i = 0; i < matrix.size(); i++) {

			// The row is standardized using standardizeRow()
			standarizedMatrix.add((ArrayList<Double>) standardizeRow(matrix.get(i)));

		}
			
		return standarizedMatrix;
	}

	
	// This function transpose a matrix
	// This function receives the matrix to be transpose (matrix)
	// This function returns the matrix given as a parameter (matrix) but transpose	
	public static List<ArrayList<Double>> transpose(List<ArrayList<Double>> matrix) {
			
		// New matrix (transposed)
		ArrayList<ArrayList<Double>> transposed = new ArrayList<>();
		
		// All the columns have the same size, so we use matrix.get(0).size()
		int size = matrix.get(0).size();
			
		// For each column
		for (int x = 0; x < size; x++) {
				
			// New row (transposed)
			ArrayList<Double> transposedCol = new ArrayList<>();
				
			// For each row
			for (ArrayList<Double> row : matrix) {
					
				// We add the corresponding value in the new row
				transposedCol.add(row.get(x));
					
			}
				
			// Adding the new row to the matrix
			transposed.add(transposedCol);
		}
			
		return transposed;
	}
		
}
