import java.util.ArrayList;

import javax.swing.JFrame;
import org.math.plot.Plot2DPanel;

public class CountSteps {
	
	private static final int DEADZONE_THRESHOLD = 2;

	public static double calculateMagnitude(double x, double y, double z) {
		return Math.pow(x*x + y*y + z*z, 0.5);
	}
	
	public static double[] calculateMagnitudesFor(double[][]sensorData){
		double[] output = new double [sensorData.length];
		for(int i = 0; i < output.length; i++){
			output[i] = calculateMagnitude(sensorData[i][0], sensorData[i][1], sensorData[i][2]);
		}
		return output;
	}
	
	public static double calculateStandardDeviation (double[] arr, double mean) {
		double sum = 0;
		
		for (Double dataValue : arr) 
			sum += Math.pow(dataValue - mean, 2);
		sum /= (arr.length-1);
		
		return Math.pow(sum, 0.5);
	}
	
	public static double calculateMean(double[]arr){
		double sum = 0;
		for(int i = 0; i < arr.length; i++){
			sum += arr[i];
		}
		return sum / arr.length;
	}
	
	/***
	 * Locates the peaks within the data
	 * 
	 * @param times the times of the data 
	 * @param magnitudes the magnitudes of the data
	 * @return a double array with values of 1 where there are peaks, and 0 otherwise
	 */
	public static int[] findPeaks(double[] magnitudes) {
		int[] peaks = new int[magnitudes.length];
		
		for (int i = 1; i < magnitudes.length-1; i++) 
			if (magnitudes[i] > magnitudes[i-1] && magnitudes[i] > magnitudes[i+1]) {
				peaks[i] = 1;
			}
		clearExtraPeaks(peaks, magnitudes, DEADZONE_THRESHOLD);
		
		return peaks; 
	}
	
	/***
	 * Clears extra peaks
	 * @param peaks the peak locations
	 * @param magnitudes the magnitudes of the peaks
	 * @param deadzone the absolute value range of values to check
	 */
	public static void clearExtraPeaks(int[] peaks, double[] magnitudes, int deadzone) {
		for (int i = 0; i < peaks.length; i++) {
			if (peaks[i] == 1) checkDeadzoneForTallestPeak(peaks, i, deadzone, magnitudes); 
		}
	}

	/***
	 * Removes extra peaks that are in very close vicinity
	 * @param peaks an array containing the peak locations
	 * @param index the index to search values around
	 * @param deadzone the number of values in front of the index to search (also searches deadzone 
	 * number of values behind it)
	 * @param magnitudes the magnitudes for the peaks
	 */
	public static void checkDeadzoneForTallestPeak(int[] peaks, int index, int deadzone, double[] magnitudes) {
		int startIndex = index - deadzone, endIndex = index + deadzone;
		double currentMag = magnitudes[index];
		
		if (startIndex < 0) startIndex = 0;
		if (endIndex >= peaks.length) endIndex = peaks.length-1;
		
		for (int i = startIndex; i < endIndex; i++) {
			if (i != index && peaks[i] == 1) {
				if (magnitudes[i] > currentMag) peaks[index] = 0;
				else peaks[i] = 0;
				break;
			}
		}
	}

	/***
	 * Counts the number of steps based on sensor data.
	 * 
	 * @param times a 1d-array with the elapsed times in milliseconds for each row in the sensorData array.
	 * @param sensorData a 2d-array where rows represent successive sensor data samples, and the columns 
	 * represent different sensors. We assume there are 6 columns.Columns 0-2 are data from the x, y, and
	 *  z axes of an accelerometer, and 3-5 are data from the x,y,z axes
	 * of a gyro.
	 * @return an int representing the number of steps
	 */
	public static int countSteps(double[] times, double[][] sensorData) {
		int stepCount = 0; 
		double[] magnitudes = calculateMagnitudesFor(sensorData);
		double mean = calculateMean(magnitudes);
		int[] peaks = findPeaks(magnitudes);
		double threshold = calculateThreshold(magnitudes, mean);
		
		for (int i = 0; i < magnitudes.length; i++) {
			if (magnitudes[i] > threshold && peaks[i] == 1) stepCount++;
		}
		
		return stepCount;
	}
	
	/***
	 * Displays a table of the peak times and peak magnitude values
	 * @param peaks the array of peak locations
	 * @param times the array of the times
	 * @param mags the array of the peak magnitudes
	 */
	public static void displayPeaks(int[] peaks, double[] times, double[] mags) {
		System.out.println("Peak time\t\tMagnitude");
		for (int i = 1; i < peaks.length; i++) {
			if (peaks[i-1] == 1) System.out.println("    " + times[i-1] + "\t\t    " + mags[i-1]);
		}
	}
	
	/***
	 * Displays a table of the peak times and peak magnitude values
	 * @param peaks an array of the peak locations
	 * @param mags an array of the peak magnitudes
	 */
	public static void displayPeaks(int[] peaks, double mags[]) {
		System.out.println("Peak time\t\tMagnitude");
		for (int i = 1; i < peaks.length; i++) {
			if (peaks[i-1] == 1) System.out.println("    " + i + "\t\t    " + mags[i-1]);
		}
	}
	
	/***
	 * Displays a table of peak times and peak magnitudes above a threshold
	 * @param peaks an array of the peak locations
	 * @param mags an array of the peak magnitudes
	 * @param threshold the threshold value
	 */
	public static void displayPeaksAboveTheshold(int[] peaks, double mags[], double threshold) {
		System.out.println("Peak time\t\tMagnitude");
		for (int i = 1; i < peaks.length; i++) {
			if (peaks[i-1] == 1 && mags[i-1] > threshold) System.out.println("    " + i + "\t\t    " + mags[i-1]);
		}
	}
	
	/***
	 * Displays a table of peak times and peak magnitudes above a threshold
	 * @param peaks an array of the peak locations
	 * @param mags an array of the peak magnitudes
	 * @param threshold the threshold value
	 */
	public static void displayAllPeaksWithThreshold(int[] peaks, double mags[], double threshold) {
		System.out.println("Peak time\t\tMagnitude");
		for (int i = 1; i < peaks.length; i++) {
			if (peaks[i-1] == 1) { System.out.print("    " + i + "\t\t    " + mags[i-1]);
				if (mags[i-1] > threshold) System.out.println("*");
				else System.out.println();
			}
		}
	}
	
	public static double calculateThreshold(double[] magnitudes, double mean) {
		return (calculateStandardDeviation(magnitudes, mean) + mean);
	}

	public static void displayJFrame(Plot2DPanel plot) {
		JFrame frame = new JFrame("Results");
		frame.setSize(800, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);
	}

}