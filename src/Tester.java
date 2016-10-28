import processing.core.PApplet;

public class Tester {
	public static double[][] sampleData;
	public static String datafile = "data/walkingSampleData-out.csv";
	public static String videofile = "data/walkingSampleData.mp4";

	public static void main(String[] args) {
		// Create data set
		CSVData dataset = CSVData.createDataSet(datafile, 0);

		// Get 2d array of all data
		sampleData = dataset.getAllData();

		// Extract columns for time, and x acceleration, y acceleration, z
		// acceleration
		double[] time = ArrayHelper.extractColumn(sampleData, 0);
		double[][] accelerationData = ArrayHelper.extractColumns(sampleData, new int[] { 4, 5, 6 });

		double[] accelerationMagnitudes = CountSteps.calculateMagnitudesFor(accelerationData);

		double[][] displayData = ArrayHelper.combineAsColumns(time, accelerationMagnitudes);
		
		DataExplorer.runDataExplorer(displayData, new String[] {"time" ,"mags"}, videofile);
	}
}