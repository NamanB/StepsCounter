import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CSVData {
	private static boolean DEBUG = false;
	private double[][] rawData;

	private CSVData(double[][] data) {
		rawData = data;
	}
	
	public static CSVData createDataSet(String filepath, int linesToSkip) {
		debug("Reading file: " + filepath);
		
		String data = readFileAsString(filepath);
		String[] lines = data.split("\n");
		
		debug("Reading " + lines.length + " total lines from file");
		debug("Using index " + (linesToSkip) + " as header row");
		
		String headerLine = lines[linesToSkip];
		debug("Headers: " + headerLine);
		
		String[] headers = headerLine.split(",");
		debug("Parsed header line into: " + headers.length + " total columns");
		
		int startColumn = 0;
		return createDataSet(filepath, linesToSkip + 1, headers, startColumn);
	}
	
	public static CSVData createDataSet(String filepath, int linesToSkip, String[] columnHeaders, int startColumn) {
		debug("Reading file: " + filepath);
		
		String data = readFileAsString(filepath);
		String[] lines = data.split("\n");
		
		debug("Reading " + lines.length + " total lines from file");
		
		int numColumns = columnHeaders.length;
		debug("Reading " + numColumns + " total columns");
		
		int startRow = linesToSkip;
		
		// create storage for data
		double[][] numdata = new double[lines.length - linesToSkip][numColumns];

		for (int r = startRow; r < lines.length; r++) {
			String line = lines[r];
			String[] coords = line.split(",");

			for (int j = startColumn; j < numColumns; j++) {
				if (coords[j].endsWith("#")) coords[j] = coords[j].substring(0, coords[j].length()-1);
				double val = Double.parseDouble(coords[j]);
				numdata[r - 1][j - startColumn] = val;
			}
		}

		return new CSVData(numdata);
	}
	
	public double[][] getAllData() {
		return rawData;
	}
	
	
		
	public static void writeDataToFile(String filePath, String data) {
		File outFile = new File(filePath);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
			writer.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String readFileAsString(String filepath) {
		StringBuilder output = new StringBuilder();

		try (Scanner scanner = new Scanner(new File(filepath))) {

			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				output.append(line + System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output.toString();
	}
	
	
	private static void debug(String string) {
		if (DEBUG ) {
			System.err.println(string);
		}
	}
	
	private int numRows;
	private String filePathToCSV;
	private double[][] data;
	private String[] columnNames;
	
	/***
	 * Returns a new CVSData object for a file ignoring lines at the top. 
	 * All other data is stored as doubles.
	 * 
	 * @param filename the file to read
	 * @param numLinesToIgnore number of lines at the top to ignore
	 * @param colunmNames the names of the columns
	 * @return a CVSData object for that file
	 */
	public CSVData(String filepath, int numLinesToIgnore, String[] columnNames) {
		this.filePathToCSV = filepath;

		String dataString = readFileAsString(filepath);
		String[] lines = dataString.split("\n");

		// number of data points
		int n = lines.length - numLinesToIgnore;
		this.numRows = n;
		int numColumns = columnNames.length;

		// create storage for column names
		this.columnNames = columnNames;

		// create storage for data
		this.data = new double[n][numColumns];
		for (int i = 0; i < lines.length - numLinesToIgnore; i++) {
			String line = lines[numLinesToIgnore + i];
			String[] coords = line.split(",");
			for (int j = 0; j < numColumns; j++) {
				if (coords[j].endsWith("#")) coords[j] = coords[j].substring(0, coords[j].length()-1);
				double val = Double.parseDouble(coords[j]);
				data[i][j] = val;
			}
		}
	}
	
	/***
	 * Returns a new CVSData object for a file ignoring lines at the top. 
	 * All other data is stored as doubles. The first line in the CSV file 
	 * must contain the names of the columns
	 * 
	 * @param filename the file to read
	 * @param numLinesToIgnore the line where the column names is, where the next line has the data
	 * @return a CVSData object for that file
	 */
	public CSVData(String filepath, int numLinesToIgnore) {
		this.filePathToCSV = filepath;

		String dataString = readFileAsString(filepath);
		String[] lines = dataString.split("\n");

		// create storage for column names
		String[] colNames = getColumnNames(lines[numLinesToIgnore]);
		this.columnNames = colNames;
		
		numLinesToIgnore++;
		
		// number of data points
		int n = lines.length - numLinesToIgnore;
		this.numRows = n;
		int numColumns = colNames.length;

		
		// create storage for data
		this.data = new double[n][numColumns];
		for (int i = 0; i < lines.length - numLinesToIgnore; i++) {
			String line = lines[numLinesToIgnore + i];
			String[] coords = line.split(",");
			for (int j = 0; j < numColumns; j++) {
				if (coords[j].endsWith("#")) coords[j] = coords[j].substring(0, coords[j].length()-1);
				double val = Double.parseDouble(coords[j]);
				data[i][j] = val;
			}
		}
	}
	
	/***
	 * Returns a new CVSData object for a file ignoring lines at the top. 
	 * It uses the first row as the column names. All other data is stored 
	 * as doubles.
	 * 
	 * @param filename the file to read
	 * @param numLinesToIgnore number of lines at the top to ignore
	 * @return a CVSData object for that file
	 */
	public static CSVData readCSVData(String filepath, int numLinesToIgnore) {
		return new CSVData(filepath, numLinesToIgnore);
	}
	
	/***
	 * Returns a new CVSData object for a file ignoring lines at the top. 
	 * It uses the first row as the column names. All other data is stored 
	 * as doubles.
	 * 
	 * @param filename the file to read
	 * @param numLinesToIgnore number of lines at the top to ignore
	 * @return a CVSData object for that file
	 */
	public static CSVData readCSVData(String filepath, int numLinesToIgnore, String[] columnNames) {
		return new CSVData(filepath, numLinesToIgnore, columnNames);
	}
	
	/***
	 * Creates a CSVData object specifically for data from powerSense
	 * 
	 * @param filepath
	 */
	public CSVData(String filepath) {
		this.filePathToCSV = filepath;

		String dataString = readFileAsString(filepath);
		String[] lines = dataString.split("\n");

		// create storage for column names
		this.columnNames = new String[] {"time(ms)", "gyro x", "gyro y", "gyro z", 
				"accel x", "accel y", "accel z"};
		
		// number of data points
		int n = lines.length - 1;
		this.numRows = n;
		int numColumns = 13;

		// create storage for data
		this.data = new double[n][this.columnNames.length];	
		for (int i = 0; i < lines.length - 1; i++) {
			String line = lines[1 + i];
			String[] coords = line.split(",");
			int currentCol = 0; 
			
			for (int j = 0; j < numColumns; j++) {
				if (coords[j].endsWith("#")) coords[j] = coords[j].substring(0, coords[j].length()-1);
				if (j != 1 && j != 2 && j != 3 && j != 7 && j != 8 && j != 9 && j != 13 && j != 14 && 
						j != 15 && j != 16 && j != 17 && j != 18 && j != 19 && j != 20) {
					double val = Double.parseDouble(coords[j]);
					data[i][currentCol++] = val;
				}
			}
		}
		
		this.correctTime();
		
		//re-orders the columns in the order of acceleration then gryo
		for (int i = 1; i < 4; i++) 
			swapColumns(i, i+3);
	}
	
	/***
	 * Corrects the PowerSense Data into the specific data and format we want
	 * 
	 * @param filepath the file path
	 * @return the corrected CSVData object
	 */
	public static CSVData newCSVCorrectedPowerSenseData(String filepath) {
		return new CSVData(filepath);
	}
	
	/***
	 * Swaps two columns with the indexes specified. It makes sure to keep the titles in corresponding 
	 * order with the data.
	 * 
	 * @param index1 the index for the 1st column to be swapped
	 * @param index2 the index for the 2nd column to be swapped
	 */
	public void swapColumns(int index1, int index2) {
		String temp = this.columnNames[index1];
		this.columnNames[index1] = this.columnNames[index2];
		this.columnNames[index2] = temp;
		
		double[] col1 = this.getColumn(index1);
		double[] col2 = this.getColumn(index2);
		
		this.setColumn(index1, col2);
		this.setColumn(index2, col1);
	}
	
	/***
	 * Returns an array containing the column names
	 * 
	 * @param titleLine the string containing the title names separated by columns
	 * @return the array containing the column names
	 */
	public String[] getColumnNames(String titleLine) {
		return titleLine.split(",");
	}

	/***
	 * Returns all the values in a row
	 * 
	 * @param rowIndex the index of the row
	 * @return all the values in a row
	 */
	public double[] getRow(int rowIndex) {
		return data[rowIndex];
	}
	
	/***
	 * Returns all the values in a column
	 * 
	 * @param columnIndex the index of the column
	 * @return all the values in a column
	 */
	public double[] getColumn(int columnIndex) {
		double[] columnValues = new double[data.length];
		
		for (int i = 0; i < data.length; i++) 
			columnValues[i] = data[i][columnIndex];
		
		return columnValues;
	}
	
	/***
	 * Returns all the values in a column
	 * 
	 * @param name the name of the column
	 * @return all the values in a column
	 */
	public double[] getColumn(String name) {
		int index = getColumnIndex(name);
		
		return getColumn(index);
	}
	
	/***
	 * Returns all the values from multiple rows. 
	 * It keeps the rows sorted.
	 * 
	 * @param rowIndexes the indexes of the rows
	 * @return all the values from multiple rows
	 */
	public double[][] getRows(int[] rowIndexes) {
		double[][] output = new double[data.length][rowIndexes.length];
		
		for (int i = 0; i < rowIndexes.length; i++) 
			output[i] = data[rowIndexes[i]];
		
		return output;
	}
	
	/***
	 * Returns all the values from rows between a start row and an end row. 
	 * It keeps the rows sorted.
	 * 
	 * @param startIndex the row index to start from
	 * @param endIndex the column index to start from
	 * @return all the values in the rows
	 */
	public double[][] getRows(int startIndex, int endIndex) {
		int difference = endIndex - startIndex, currentIndex = 0;
		double[][] output = new double[data.length][difference];
		
		for (int i = startIndex; i <= endIndex; i++) 
			output[currentIndex++] = data[i];
		
		return output;
	}
	
	/***
	 * Returns all the values in columns. 
	 * It keeps the columns sorted.
	 * 
	 * @param columnIndexes the indexes of the columns to return
	 * @return the values in the columns specified
	 */
	public double[][] getColumns(int[] columnIndexes) {
		double[][] output = new double[data[0].length][columnIndexes.length];
		
		for (int i = 0; i < columnIndexes.length; i++) 
			output[i] = getColumn(columnIndexes[i]);
		
		return output;
	}
	
	/***
	 * Returns all the values in columns. 
	 * It keeps the columns sorted.
	 * 
	 * @param startIndex the index to start from
	 * @param endIndex the index to end at
	 * @return the values in the columns specified
	 */
	public double[][] getColumns(int startIndex, int endIndex) {
		int difference = endIndex-startIndex;
		double[][] output = new double[data[0].length][difference];
		
		for (int i = 0; i < difference; i++) 
			output[i] = getColumn(startIndex+i);
		
		return output;
	}
	
	/***
	 * Returns all the values in columns. 
	 * It keeps the columns sorted.
	 * 
	 * @param colNames the names of the columns to return values from
	 * @return the values in the columns specified
	 */
	public double[][] getColumns(String[] colNames) {
		int[] columnIndexes = new int[colNames.length];
		
		for (int i = 0; i < colNames.length; i++) 
			columnIndexes[i] = getColumnIndex(colNames[i]);
		
		return getColumns(columnIndexes);
	}
	
	/***
	 * Returns one value
	 * 
	 * @param rowIndex the row index
	 * @param colIndex the column index
	 * @return the value at that point
	 */
	public double getValue(int rowIndex, int colIndex) {
		return this.data[rowIndex][colIndex];
	}
	
	/***
	 * Sets a value at a specific spot
	 * 
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 * @param value the value to save
	 */
	public void setValue(int rowIndex, int columnIndex, double value) {
		data[rowIndex][columnIndex] = value;
	}
	
	/***
	 * Returns then sets a value at a specific spot 
	 * 
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 * @param value the value to store
	 * @return the old value in that spot
	 */
	public double returnSetValue(int rowIndex, int columnIndex, double value) {
		double output = data[rowIndex][columnIndex];
		
		data[rowIndex][columnIndex] = value;
		
		return output;
	}
	
	/***
	 * Sets a full row
	 * 
	 * @param rowIndex the index for the row to replace
	 * @param rowValues the values to save into the row
	 */
	public void setRow(int rowIndex, double[] rowValues) {
		data[rowIndex] = rowValues;
	}
	
	/***
	 * Sets a full column
	 * 
	 * @param columnIndex the index of the column
	 * @param columnValues the values to store in the column
	 */
	public void setColumn(int columnIndex, double[] columnValues) {
		for (int i = 0; i < data[0].length; i++)
			data[i][columnIndex] = columnValues[i];
	}
	
	/***
	 * Sets a full column
	 * 
	 * @param colName the name of the column to set
	 * @param columnValues the values to store in the column
	 */
	public void setColumn(String colName, double[] columnValues) {
		int columnIndex = getColumnIndex(colName);
		
		setColumn(columnIndex, columnValues);
	}
	
	/***
	 * Sets all the data to the input data 
	 * 
	 * @param data the double array data
	 */
	public void setData(double[][] data) {
		this.data = data;
	}
	
	/***
	 * Returns the column titles
	 * 
	 * @return the column titles
	 */
	public String[] getColumnTitles() {
		return columnNames;
	}
	
	/***
	 * Returns the number of rows
	 * 
	 * @return the number of rows
	 */
	public int getNumRows() {
		return this.numRows;
	}
	
	/***
	 * Saves the current state of the file
	 * 
	 * @param filepath the file path to save the file (something like /Users/naman/Desktop/state1.txt)
	 */
	public void saveCurrentState(String filepath) {
		File outFile = new File(filepath);
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
			writer.write(this.dataToString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/***
	 * Finds the index for the column specified by name. 
	 * Returns -1 if the name is invalid
	 * 
	 * @param colName the name of the column
	 * @return the column index
	 */
	public int getColumnIndex(String colName) {
		for (int i = 0; i < columnNames.length; i++) 
			if (colName.equals(columnNames[i])) return i;
		
		System.out.println("The column name " + colName + " does not exist as a column title");
		return -1;
	}
	
	public String getFilePath() {
		return filePathToCSV;
	}
	
	/***
	 * Returns a string that contains the titles, and data in the rows specified
	 * 
	 * @param startIndex the starting index
	 * @param numRows the number of rows to get the data from
	 * @return a string version of those rows after the titles
	 */
	public String displayNRows(int startIndex, int numRows) {
		StringBuilder output = new StringBuilder();
		
		for (String columnName : this.columnNames)
			output.append(columnName + ", ");
		
		for (int i = startIndex; i < numRows; i++) {
			int length = output.length();
			output.delete(length-2, length);
			output.append("\n");
			for (int j = 0; j < data[0].length; j++) {
				output.append(data[i][j] + ", ");
			}
		}
		output.delete(output.length()-2, output.length());
		
		return output.toString();
	}
	
	/***
	 * Returns a string version of the CSVData object that could be turned into a .txt file
	 * @return a string version of the CSVData object that could be turned into a .txt file
	 */
	public String dataToString() {
		StringBuilder output = new StringBuilder(); 
		
		for (String columnName : this.columnNames)
			output.append(columnName + ", ");
		
		for (int i = 0; i < data.length; i++) {
			int length = output.length();
			output.delete(length-2, length);
			output.append("\n");
			for (int j = 0; j < data[0].length; j++) {
				output.append(data[i][j] + ", ");
			}
		}
		output.delete(output.length()-2, output.length());
		
		return output.toString();
	}
	
	/***
	 * Corrects the time to elapsed time
	 * @param a a CSV Data object
	 */
	public static void correctTime(CSVData a) {
		double startTime = a.data[0][0];
		
		System.out.println(a.data[0][0] - a.data[0][0] + "\n\n\n\n");
		
		for (int i = 0; i < a.data.length; i++)
			a.data[i][0] -= startTime;
		
		a.columnNames[0] = "Elapsed Time";
	}
	
	/***
	 * Corrects the time to elapsed time
	 * @param a a CSV Data object
	 */
	public void correctTime() {
		double startTime = this.data[0][0];
		
		System.out.println(this.data[0][0] - this.data[0][0] + "\n\n\n\n");
		
		for (int i = 0; i < this.data.length; i++){
			this.data[i][0] -= startTime;
			this.data[i][0] *= 1000;
		}
			
		this.columnNames[0] = "Elapsed Time";
	}
	
	/***
	 * Corrects the time to elapsed time
	 * @param a a CSV Data object
	 */
	public static void correctTime(double[] time) {
		double startTime = time[0];
		
		for (int i = 0; i < time.length; i++)
			time[i] -= startTime;
	}
}