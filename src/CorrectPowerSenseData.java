
public class CorrectPowerSenseData {

	public static void main(String[] args) {
		CSVData data =  CSVData.newCSVCorrectedPowerSenseData("PowerSenseRawData/10_step_trial.csv");
		
		System.out.println(data.displayNRows(0, 40));
		
		data.saveCurrentState("myData/10stepForwardWalk.csv");
	}
	
}
