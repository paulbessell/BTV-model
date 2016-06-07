/*
 * This is where the model is run from by calling methods in pReadWrite.
 * 
 * It reads in data held in readWrite, calls the BTV model and then spews output out into data files.
 * 
 * Paul Bessell 18 November 2014
 * 
 */
import java.io.BufferedWriter;
import java.io.File;
import java.util.Date;
import java.util.Vector;
import org.apache.commons.math3.distribution.*;


//import cern.jet.random.*;

public class ewDriver
/* The driver class sets up the model and calls runner method */
{
	static final String USAGE = "Usage: java textIOdrv2 infile outfile \n";

	public static void main(String[] args) throws Exception {
		int t = 5;
		boolean bugger = true;
		if (t > 1 & bugger) {
			System.out.println("Here");
		} else if (t > 1) {
			System.out.println("Here too");
		}

		pEWModel model = new pEWModel();
	//	model.runModel_Census_Big();
		model.runModel_Census_VaccBC();
	//	model.runModel_Census_VaccWash();

	}

}

// model class isolates functional code/data from IO
class pEWModel {



public static void runModel_Census_Big() throws Exception {

	String fi = "/Users/pbessel2/Documents/EPIC/Diseases/BTV/Data/ModelData/EngWalData17112015Kernel.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "/Users/pbessel2/Documents/EPIC/Diseases/BTV/Data/ModelData/EcensusClimateGrid_mean_Full.csv";

	BufferedWriter cphs = t1.initilaiseOutputCPH(new File("EWModel/Baseline/EW_CPH_BTV_BaselineEKernel_s1000.csv"));
	BufferedWriter writer = t1.initilaiseOutputOW(new File("EWModel/Baseline/EWBaselineTest30EKernel_s1000.csv"));
	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("EWModel/Baseline/Sheep/EWBaselineTestEKernel_s1000.csv"));
	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("EWModel/Baseline/Cattle/EWBaselineTestEKernel_s1000.csv"));

	// Loop here
	int startDays = 30;
	String seeds = "42/013/0005";
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
		for(int k = 1; k <= 1; k++){
			for (int l = 1; l <= 1000; l++) {

				Farm[] myFarms = t1.readDataEW();

				myFarms = t1.tempData(new File(temps), myFarms, 0);
				BTVModel myDriver = new BTVModel(myFarms);
				if(k == 1) myDriver.exponential = true;
				myDriver.setStartDay(startDays);

				myDriver.setPreSeeds(1, seeds);

				int[] output = myDriver.runEpidemic(179);
				t1.writeOutput(writer, new int[] { 1, startDays, k, l }, output);
				t1.writeCattleList(cattleWriter, myDriver.cattleInf);
				t1.writeSheepList(sheepWriter, myDriver.sheepInf);
				t1.writeCPH(l, myFarms, cphs);
	
				System.out.println("Iteration = " + l + " Kernel = "+ k + " START DAY = " + startDays + " SEED = " + seeds + " " + new Date().toString());
			}
			System.out.println(new Date().toString());
		}
//	t1.writeMarker();
	}

public static void runModel_Census_VaccBC() throws Exception {

	String fi = "/Users/pbessel2/Documents/EPIC/Diseases/BTV/Data/ModelData/EngWalData17112015Kernel.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "/Users/pbessel2/Documents/EPIC/Diseases/BTV/Data/ModelData/EcensusClimateGrid_mean_Full.csv";

	BufferedWriter cphs = t1.initilaiseOutputCPH(new File("EWModel/Baseline/VaccinateBC/EW_CPH_BTV_Baseline_t500.csv"));
	BufferedWriter writer = t1.initilaiseOutputOW(new File("EWModel/Baseline/VaccinateBC/EWBaselineTest_t500.csv"));
	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("EWModel/Baseline/VaccinateBC/Sheep/EWBaselineTest_t500.csv"));
	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("EWModel/Baseline/VaccinateBC/Cattle/EWBaselineTest_t500.csv"));

	// Loop here
	int startDays = 30;
	String seeds = "42/013/0005";
	double[] etTweaks = { 0 };
	String[] vaccSpecies = {"Both", "Cattle", "Sheep"};
	double[] vaccCoverage = {0.25, 0.5, 0.8};
	System.out.println(new Date().toString());
	
	System.out.println("Vaccinate Bristol Channel");
		for(int k = 0; k <= 1; k++){
			for (int l = 1; l <=500; l++) {
				for(int m = 0; m < vaccSpecies.length; m++){
					for(int n = 0; n < vaccCoverage.length; n++){
						Farm[] myFarms = t1.readDataEW();

						myFarms = t1.tempData(new File(temps), myFarms, 0);
						BTVModel myDriver = new BTVModel(myFarms);
						if(k == 1) myDriver.exponential = true;
						myDriver.setStartDay(startDays);
		
						myDriver.setPreSeeds(1, seeds);
						myDriver.vaccinateCattleBC(vaccCoverage[n], vaccSpecies[m]);
						int[] output = myDriver.runEpidemic(179);
						t1.writeOutput(writer, new int[] { 1, startDays, k, l, m, n}, output);
						t1.writeCattleList(cattleWriter, myDriver.cattleInf);
						t1.writeSheepList(sheepWriter, myDriver.sheepInf);
						t1.writeCPH(l, myFarms, cphs);			
						}		
					}
				System.out.println("Iteration = " + l + " Kernel = "+ k + " START DAY = " + startDays + " SEED = " + seeds + " " + new Date().toString());
				}
			System.out.println(new Date().toString());
		}
	t1.writeMarker();
	}
public static void runModel_Census_VaccWash() throws Exception {

	String fi = "/Users/pbessel2/Documents/EPIC/Diseases/BTV/Data/ModelData/EngWalData17112015Kernel.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "/Users/pbessel2/Documents/EPIC/Diseases/BTV/Data/ModelData/EcensusClimateGrid_mean_Full.csv";

	BufferedWriter cphs = t1.initilaiseOutputCPH(new File("EWModel/Baseline/VaccinateWash/EW_CPH_BTV_Baseline_t500.csv"));
	BufferedWriter writer = t1.initilaiseOutputOW(new File("EWModel/Baseline/VaccinateWash/EWBaselineTest_t500.csv"));
	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("EWModel/Baseline/VaccinateWash/Sheep/EWBaselineTest_t500.csv"));
	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("EWModel/Baseline/VaccinateWash/Cattle/EWBaselineTest_t500.csv"));

	// Loop here
	int startDays = 30;
	String seeds = "42/013/0005";
	double[] etTweaks = { 0 };
	String[] vaccSpecies = {"Both", "Cattle", "Sheep"};
	double[] vaccCoverage = {0.25, 0.5, 0.8};
	System.out.println(new Date().toString());
	
	System.out.println("Vaccinate Wash");
		for(int k = 0; k <= 1; k++){
			for (int l = 1; l <= 500; l++) {
				for(int m = 0; m < vaccSpecies.length; m++){
					for(int n = 0; n < vaccCoverage.length; n++){
						Farm[] myFarms = t1.readDataEW();

						myFarms = t1.tempData(new File(temps), myFarms, 0);
						BTVModel myDriver = new BTVModel(myFarms);
						if(k == 1) myDriver.exponential = true;
						myDriver.setStartDay(startDays);
		
						myDriver.setPreSeeds(1, seeds);
						myDriver.vaccinateCattleWash(vaccCoverage[n], vaccSpecies[m]);
						int[] output = myDriver.runEpidemic(179);
						t1.writeOutput(writer, new int[] { 1, startDays, k, l, m, n}, output);
						t1.writeCattleList(cattleWriter, myDriver.cattleInf);
						t1.writeSheepList(sheepWriter, myDriver.sheepInf);
						t1.writeCPH(l, myFarms, cphs);			
						}		
					}
				System.out.println("Iteration = " + l + " Kernel = "+ k + " START DAY = " + startDays + " SEED = " + seeds + " " + new Date().toString());
				}
			System.out.println(new Date().toString());
		}
	t1.writeMarker();
	}

}
