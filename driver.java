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

public class driver
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

		pOverwinter model = new pOverwinter();
		
		
	//	Fourth set
	/*	model.runModel_Census_All_Vacc();
		model.runModel_Census_Vacc_Prop_G(0.25);
		model.runModel_Census_All_Vacc_EK();
		model.runModel_Census_Vacc_Prop(0.25);
		*/
		
		
	//	model.runModel_Census_Big();
	//	model.runModel_Census_Vacc_ExtAll();
	//	model.runModel_Census_Vacc_Ext();
	//	model.runModel_Census_Vacc_EK();
	//	model.runModel_Census_Sheep_Vacc_EK();
	//	model.runModel_Census_Vacc_Ext_EK();
		
//		model.runModel_Census_Big();
//		model.runModel_Census_Vacc();
//		model.runModel_Census_Sheep_Vacc();

//		model.runModel_Census_Sheep_Vacc_EK();
//		model.runModel_Census_Sheep_Vacc();
//		model.runModel_Census_Vacc_Prop_G();
//		model.runModel_Census_Vacc_Prop();

//		Model with clinical signs - exponential kernel
/*		model.runModel_Census_EK();
		model.runModel_Census_Vacc_Prop(0.25);
	*/	
	/*	model.runModel_Census_Vacc_EK();
		model.runModel_Census_All_Vacc_EK();
		model.runModel_Census_Vacc_Prop(0.5);
	*/
/*		model.runModel_Census_Sheep_Vacc_EK();
		model.runModel_Census_Vacc_Prop(0.75);
		model.runModel_Census_WorstCaseSecond();
*/
		
//		Model with clinical signs - Gaussian kernel
	/*	model.runModel_Census_Big();
		model.runModel_Census_Vacc_Prop_G(0.25);		
*/
/*		model.runModel_Census_Vacc();
		model.runModel_Census_All_Vacc();
		model.runModel_Census_Vacc_Prop_G(0.5);
*/	
/*		model.runModel_Census_Sheep_Vacc();
		model.runModel_Census_Vacc_Prop_G(0.75);
	 	model.runModel_Census_WorstCaseSecond();
	 	*/
	//	model.runModel_Census_Big();
	//	model.runModel_Census_EK();
// Test

/*		model.runModel_Census_Vacc_Prop_G(0.75);
		model.runModel_Census_Vacc_Prop(0.25);
		model.runModel_Census_Vacc();
	*/
		//model.runModel_Census_All_Vacc();
		//model.runModel_Census_Vacc_Prop(0.25);
		
		model.runModel_Census_Sheep_Vacc();

	//	model.runModel_Census_WorstCaseFP();
	/*	model.runModel_Census_Vacc_EK();
		model.runModel_Census_Vacc_Prop_G(0.25);
		model.runModel_Census_Vacc_Prop(0.75);
		
		/*
		model.runModel_Census_Sheep_Vacc();
		model.runModel_Census_Sheep_Vacc_EK();
		model.runModel_Census_All_Vacc();
		model.runModel_Census_All_Vacc_EK();
		*/
		//System.out.println((int) Math.ceil(total));
		
		// model.runModel_Census_WorstCase();
	}

}

// model class isolates functional code/data from IO
class pOverwinter {

	public static void runModel_IACS() throws Exception {
		String fi = "../../Data/SIACS/Processed/SIACS_Fitted_Kernel_Details.csv";
		readWrite t1 = new readWrite(new File(fi));
		String temps = "../../Data/SIACS/Processed/SIACS_Fitted_Temps.csv";

		BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/TrialRuns/CensusTest.csv"));
		BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/TrialRuns/Sheep/CensusTest.csv"));
		BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/TrialRuns/Cattle/CensusTest.csv"));
		// Loop here
		int[] startDays = { 45 };
		double[] tTweaks = { 0, 1 };
		double[] etTweaks = { 0 };

		System.out.println("BASELINE");

		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 1000; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < etTweaks.length; n++) {

						Farm[] myFarms = t1.readData();

						myFarms = t1.resetOverwinter(myFarms);
						myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
						BTVModel myDriver = new BTVModel(myFarms);
						myDriver.vectorInf = 0.19;

						myDriver.setStartDay(startDays[i]);

						myDriver.eipbase = etTweaks[n];
						myDriver.setSeedsTotallyRandom(5);
						myDriver.nSeeds = 5;

						int[] output = myDriver.runEpidemic(179);
						t1.writeOutput(writer, new int[] { 1, startDays[i], 2, 1, m, n, l }, output);
						t1.writeCattleList(cattleWriter, myDriver.cattleInf);
						t1.writeSheepList(sheepWriter, myDriver.sheepInf);

					}
				}
			}

			System.out.println("START DAY = " + startDays[i]);
		}
	}

	public static void runModel_Census() throws Exception {
		String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011SBVcoordsClimateScaleSimpRev.csv";
		readWrite t1 = new readWrite(new File(fi));
		String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean.csv";

		BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/TrialRuns/CensusTest_revBTV.csv"));
		BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/TrialRuns/Sheep/CensusTest_revBTV.csv"));
		BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/TrialRuns/Cattle/CensusTest_revBTV.csv"));
	
		// Loop here
		int[] startDays = {45};
		double[] tTweaks = {0, 0.5, 1, 1.5};
		double[] etTweaks = { 0 };

		System.out.println("BASELINE");

		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 100; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < etTweaks.length; n++) {

						Farm[] myFarms = t1.readData();

						myFarms = t1.resetOverwinter(myFarms);
						myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
						BTVModel myDriver = new BTVModel(myFarms);
						myDriver.vectorInf = 0.02;

						myDriver.setStartDay(startDays[i]);

						myDriver.eipbase = etTweaks[n];
						myDriver.setSeedsTotallyRandom(5);
						myDriver.nSeeds = 5;
	/*					Farm test = (Farm) myFarms[1];

										
						test.printDetails();
						System.out.println("Temp = "+test.getTemp(30));
						System.out.println("EIP = "+test.getEIP(30));
						System.out.println("Different EIP = "+test.getEIPExact(30));
	//					System.out.println("Mortality = "+test.vectorMortality(60));
	//					System.out.println("Bite rate = "+test.getBiteIntervalRate(60));
						test.cattleInfected = 10;
						test.updateVBites(45, 2500);
						
						Vector bites = test.vBites;
						System.out.println("Vector size = " + bites.size());
						for(int q = 0; q < bites.size(); q++){
							bite cBite = (bite) bites.elementAt(q);
							cBite.printDetails();
						}
						BinomialDistribution testDist = new BinomialDistribution(2500, 0.02);
						for(int w = 1; w < 10; w++)	System.out.println("Test smaple = " + testDist.sample());

*/
						int[] output = myDriver.runEpidemic(179);
						t1.writeOutput(writer, new int[] { 1, startDays[i], 2, 1, m, n, l }, output);
						t1.writeCattleList(cattleWriter, myDriver.cattleInf);
						t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}

			System.out.println("START DAY = " + startDays[i]);
		}
	}


public static void runModel_Census_Big() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllData.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean_1996_2011.csv";

/*	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/Revised/CensusBaseline_1000_2.csv"));
	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/Revised/Sheep/CensusBaseline_1000_2.csv"));
	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/Revised/Cattle/CensusBaseline_1000_2.csv"));
*/
	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/NewFeed/Clinical/CensusBaselineG_1996_2011.csv"));
//	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/NewFeed/Sheep/CensusBaselineG.csv"));
//	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/NewFeed/Cattle/CensusBaselineG.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
//	String[] seeds = {"313/0120", "543/0048", "797/0009", "483/0048"};
	String[] seeds = {"295/0091", "543/0048", "791/0003", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 1000; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
//								t1.writeCattleList(cattleWriter, myDriver.cattleInf);
//								t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
	}

public static void runModel_Census_WorstCase() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllData.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/Revised/CensusBaselineWCWC_1000_9.csv"));
//	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/Revised/Sheep/CensusBaselineWCWC_1000_7.csv"));
//	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/Revised/Cattle/CensusBaselineWCWC_1000_7.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {1};
	double[] vInfTweaks = {0.1};
	double[] eipBaseTweaks = {12.7};
	double[] eipRateTweaks = {0.026};
//	String[] seeds = {"313/0120", "543/0048", "797/0009", "483/0048"};
	String[] seeds = {"295/0091", "543/0048", "791/0003", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for (int l = 1; l <= 1000; l++) {
		for(int h = 0; h < 4; h++){
			for (int i = 0; i < startDays.length; i++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
	//							t1.writeCattleList(cattleWriter, myDriver.cattleInf);
	//							t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());	
			System.out.println("START DAY = " + startDays[i]);
			}
		}
	}
	t1.writeMarker();
	}
public static void runModel_Census_WorstCaseSecond() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllData.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/NewFeed/Clinical/CensusBaseline_WC2.csv"));
//	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/Revised/Sheep/CensusBaselineWCWC_1000_7.csv"));
//	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/Revised/Cattle/CensusBaselineWCWC_1000_7.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0};
	double[] vInfTweaks = {0.1};
	double[] eipBaseTweaks = {13.3};
	double[] eipRateTweaks = {0.019};
//	String[] seeds = {"313/0120", "543/0048", "797/0009", "483/0048"};
	String[] seeds = {"295/0091", "543/0048", "791/0003", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for (int l = 1; l <= 1000; l++) {
		for(int h = 0; h < 4; h++){
			for (int i = 0; i < startDays.length; i++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
	//							t1.writeCattleList(cattleWriter, myDriver.cattleInf);
	//							t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());	
			System.out.println("START DAY = " + startDays[i]);
			}
		}
	}
	t1.writeMarker();
	}

public static void runModel_Census_WorstCaseFP() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllData.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/NewFeed/Clinical/CensusBaseline_RevBites5000.csv"));
//	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/Revised/Sheep/CensusBaselineWCWC_1000_7.csv"));
//	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/Revised/Cattle/CensusBaselineWCWC_1000_7.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0};
	double[] vInfTweaks = {0.01};
	double[] eipBaseTweaks = {13.3};
	double[] eipRateTweaks = {0.019};
//	String[] seeds = {"313/0120", "543/0048", "797/0009", "483/0048"};
	String[] seeds = {"295/0091", "543/0048", "791/0003", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for (int l = 1; l <= 2000; l++) {
		for(int h = 0; h < 4; h++){
			for (int i = 0; i < startDays.length; i++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.changeBites(5000);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
	//							t1.writeCattleList(cattleWriter, myDriver.cattleInf);
	//							t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());	
			System.out.println("START DAY = " + startDays[i]);
			}
		}
	}
	t1.writeMarker();
	}

public static void runModel_SIACS_Big() throws Exception {
	String fi = "../../Data/SIACS/Processed/SIACS_Fitted_BTV_Kernel_Vacc.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/SIACS/Processed/SIACS_Fitted_Temps_2.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/SIACS/SiacsBaseline_All2000_DG.csv"));
	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/SIACS/Sheep/SiacsBaseline_All2000_DG.csv"));
	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/SIACS/Cattle/SiacsBaseline_All2000_DG.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 0.5, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
//	String[] seeds = {"313/0120", "543/0048", "797/0009", "483/0048"};
	String[] seeds = {"NY/35950/73931", "NS/76660/72392", "NT/70366/38996", "NO/77594/95120"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h <1; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 2000; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
								System.out.println("HERE");
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
								t1.writeCattleList(cattleWriter, myDriver.cattleInf);
								t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}}


public static void runModel_Census_EK() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllData.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean_1996_2011.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/NewFeed/Clinical/CensusBaseline_EK_1996_2011_2.csv"));
//	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/NewFeed/Sheep/CensusBaseline_EK.csv"));
//	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/NewFeed/Cattle/CensusBaseline_EK.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
//	String[] seeds = {"313/0120", "543/0048", "797/0009", "483/0048"};
	String[] seeds = {"295/0091", "543/0048", "791/0003", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 500; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.exponential = true;
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
			//					t1.writeCattleList(cattleWriter, myDriver.cattleInf);
			//					t1.writeSheepList(sheepWriter, myDriver.sheepInf);	
								}
							}
						}
					}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
				}
			System.out.println("START DAY = " + startDays[i]);
			}
		}
	t1.writeMarker();
	}


public static void runModel_Census_Vacc() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccRev.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean_1996_2011.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/NewFeed/Clinical/Vaccinate/CattleVacc_2011_2.csv"));
//	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/NewFeed/Vaccinate/Sheep/CattleVacc.csv"));
//	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/NewFeed/Vaccinate/Cattle/CattleVacc.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
	String[] seeds = {"295/0091", "543/0048", "791/0001", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 500; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.vaccinateCattle(0.9);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
			//					t1.writeCattleList(cattleWriter, myDriver.cattleInf);
			//					t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
}

public static void runModel_Census_All_Vacc() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccRev.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean_1996_2011.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/NewFeed/Clinical/Vaccinate/CattleSheepVaccG_2011_2.csv"));
//	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/NewFeed/Vaccinate/Sheep/CattleSheepVaccG.csv"));
//	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/NewFeed/Vaccinate/Cattle/CattleSheepVaccG.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
	String[] seeds = {"295/0091", "543/0048", "791/0001", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 500; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.vaccinateAll(0.9);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
//								t1.writeCattleList(cattleWriter, myDriver.cattleInf);
//								t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
}

public static void runModel_Census_Sheep_Vacc() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccRev.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean_1996_2011.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/NewFeed/Clinical/Vaccinate/SheepVaccG_2011_2.csv"));
//	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/NewFeed/Vaccinate/Sheep/SheepVaccG.csv"));
//	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/NewFeed/Vaccinate/Cattle/SheepVaccG.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
	String[] seeds = {"295/0017", "543/0048", "791/0003", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 500; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.vaccinateSheep(0.9);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
			//					t1.writeCattleList(cattleWriter, myDriver.cattleInf);
			//					t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
}

public static void runModel_Census_Vacc_Ext() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccExt.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/Revised/Vaccinate/CensusBaseline_Cattle_Ext_vacc.csv"));
	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/Revised/Vaccinate/Sheep/CensusBaseline_Cattle_Ext_vacc.csv"));
	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/Revised/Vaccinate/Cattle/CensusBaseline_Cattle_Ext_vacc.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 0.5, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
	String[] seeds = {"295/0091", "543/0048", "791/0001", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 1000; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.vaccinateCattle(0.9);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
								t1.writeCattleList(cattleWriter, myDriver.cattleInf);
								t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
}

public static void runModel_Census_Vacc_ExtAll() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccExt.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/Revised/Vaccinate/CensusBaseline_CattleSheep_Ext_vacc.csv"));
	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/Revised/Vaccinate/Sheep/CensusBaseline_CattleSheep_Ext_vacc.csv"));
	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/Revised/Vaccinate/Cattle/CensusBaseline_CattleSheep_Ext_vacc.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 0.5, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
//	String[] seeds = {"313/0120", "543/0048", "791/0003", "483/0048"};
	String[] seeds = {"295/0091", "543/0048", "791/0003", "483/0048"};

	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 1000; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.vaccinateAll(0.8);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
								t1.writeCattleList(cattleWriter, myDriver.cattleInf);
								t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
}

public static void runModel_Census_Vacc_EK() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccRev.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean_1996_2011.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/NewFeed/Clinical/Vaccinate/CattleVaccEK2011.csv"));
//	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/NewFeed/Vaccinate/Sheep/CattleVaccEK.csv"));
//	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/NewFeed/Vaccinate/Cattle/CattleVaccEK.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
	String[] seeds = {"295/0091", "543/0048", "791/0001", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 500; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.exponential = true;

								myDriver.vaccinateCattle(0.9);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
//								t1.writeCattleList(cattleWriter, myDriver.cattleInf);
//								t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
	}

public static void runModel_Census_All_Vacc_EK() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccRev.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean_1996_2011.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/NewFeed/Clinical/Vaccinate/CattleSheepVaccEK2011.csv"));
//	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/NewFeed/Vaccinate/Sheep/CattleSheepVaccEK.csv"));
//	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/NewFeed/Vaccinate/Cattle/CattleSheepVaccEK.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
	String[] seeds = {"295/0091", "543/0048", "791/0001", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 500; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.exponential = true;

								myDriver.vaccinateAll(0.9);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
			//					t1.writeCattleList(cattleWriter, myDriver.cattleInf);
			//					t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
}

public static void runModel_Census_Sheep_Vacc_EK() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccRev.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean_1996_2011.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/NewFeed/Clinical/Vaccinate/SheepVaccEK2011.csv"));
//	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/NewFeed/Vaccinate/Sheep/SheepVaccEK.csv"));
//	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/NewFeed/Vaccinate/Cattle/SheepVaccEK.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
	String[] seeds = {"295/0017", "543/0048", "791/0003", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 500; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.exponential = true;

								myDriver.vaccinateSheep(0.9);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
			//					t1.writeCattleList(cattleWriter, myDriver.cattleInf);
			//					t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
}

public static void runModel_Census_Vacc_Ext_EK() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccExt.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/Revised/Vaccinate/CensusBaseline_EK_Cattle_Ext_vacc.csv"));
	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/Revised/Vaccinate/Sheep/CensusBaseline_EK_Cattle_Ext_vacc.csv"));
	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/Revised/Vaccinate/Cattle/CensusBaseline_EK_Cattle_Ext_vacc.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 0.5, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
	String[] seeds = {"295/0091", "543/0048", "791/0001", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 1000; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.exponential = true;

								myDriver.vaccinateCattle(0.9);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
								t1.writeCattleList(cattleWriter, myDriver.cattleInf);
								t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
}
public static void runModel_Census_Vacc_ExtAll_EK() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccExt.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/Revised/Vaccinate/CensusBaseline_EK_CS_Ext_vacc.csv"));
	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/Revised/Vaccinate/Sheep/CensusBaseline_EK_CS_Ext_vacc.csv"));
	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/Revised/Vaccinate/Cattle/CensusBaseline_EK_CS_Ext_vacc.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 0.5, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
//	String[] seeds = {"313/0120", "543/0048", "791/0003", "483/0048"};
	String[] seeds = {"295/0091", "543/0048", "791/0003", "483/0048"};

	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 1000; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.exponential = true;

								myDriver.vaccinateAll(0.8);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
								t1.writeCattleList(cattleWriter, myDriver.cattleInf);
								t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
		t1.writeMarker();
}
public static void runModel_Census_Vacc_Prop(double prop) throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccRev.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean_1996_2011.csv";

	String propN = Double.toString(prop * 100);
	String fn = "Output/Census/Revised/NewFeed/Clinical/Vaccinate/VaccP" + propN + "EK_2011.csv";
	BufferedWriter writer = t1.initilaiseOutputOW(new File(fn));
//	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/NewFeed/Vaccinate/Sheep/VaccP25EK.csv"));
//	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/NewFeed/Vaccinate/Cattle/VaccP25EK.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
	String[] seeds = {"295/0091", "543/0048", "791/0001", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 500; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.exponential = true;

								myDriver.vaccinateAllProp(0.9, prop);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
			//					t1.writeCattleList(cattleWriter, myDriver.cattleInf);
			//					t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
	}
public static void runModel_Census_Vacc_Prop_G(double prop) throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccRev.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean_1996_2011.csv";
	
	String propN = Double.toString(prop * 100);
	String fn = "Output/Census/Revised/NewFeed/Clinical/Vaccinate/VaccP" + propN + "G_2011_2.csv";
	BufferedWriter writer = t1.initilaiseOutputOW(new File(fn));
/*	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/NewFeed/Vaccinate/Sheep/VaccP25G.csv"));
	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/NewFeed/Vaccinate/Cattle/VaccP25G.csv"));
*/
	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
	String[] seeds = {"295/0091", "543/0048", "791/0001", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 500; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);

								myDriver.vaccinateAllProp(0.9, prop);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
	/*							t1.writeCattleList(cattleWriter, myDriver.cattleInf);
								t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	*/
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
	}

public static void runModel_Census_Vacc_AC() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccRev.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/Revised/Vaccinate/CensusBaseline_Cattle_S_vacc_AllCatt.csv"));
	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/Revised/Vaccinate/Sheep/CensusBaseline_Cattle_S_vacc_AllCatt.csv"));
	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/Revised/Vaccinate/Cattle/CensusBaseline_Cattle_S_vacc_AllCatt.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 0.5, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
	String[] seeds = {"295/0091", "543/0048", "791/0001", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 1000; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.vaccinateAllCattle(0.8);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
								t1.writeCattleList(cattleWriter, myDriver.cattleInf);
								t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
}
public static void runModel_Census_Vacc_AC_EK() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllDataVaccRev.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/Revised/Vaccinate/CensusBaseline_Cattle_S_vacc_AllCatt_EK.csv"));
	BufferedWriter sheepWriter = t1.initilaiseSheepList(new File("Output/Census/Revised/Revised/Vaccinate/Sheep/CensusBaseline_Cattle_S_vacc_AllCatt_EK.csv"));
	BufferedWriter cattleWriter = t1.initilaiseCattleList(new File("Output/Census/Revised/Revised/Vaccinate/Cattle/CensusBaseline_Cattle_S_vacc_AllCatt_EK.csv"));

	// Loop here
	int[] startDays = {1, 16, 31, 46, 61, 76, 91, 106};
	double[] tTweaks = {0, 0.5, 1};
	double[] vInfTweaks = {0.01, 0.02};
	double[] eipBaseTweaks = {13.3, 12.7};
	double[] eipRateTweaks = {0.019, 0.026};
	String[] seeds = {"295/0091", "543/0048", "791/0001", "483/0048"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 4; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 1000; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.exponential = true;
								myDriver.vaccinateAllCattle(0.8);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
								t1.writeCattleList(cattleWriter, myDriver.cattleInf);
								t1.writeSheepList(sheepWriter, myDriver.sheepInf);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
}
public static void SingleRun() throws Exception {
	String fi = "../../Data/AgCensus/CensusDatasets/Processed/scotAgCensus2011BTVCoords_AllData.csv";
	readWrite t1 = new readWrite(new File(fi));
	String temps = "../../Data/AgCensus/CensusDatasets/Processed/censusClimateGrid_mean.csv";

	BufferedWriter writer = t1.initilaiseOutputOW(new File("Output/Census/Revised/Revised/Single.csv"));

	// Loop here
	int[] startDays = {31};
	double[] tTweaks = {1};
	double[] vInfTweaks = {0.02};
	double[] eipBaseTweaks = {12.7};
	double[] eipRateTweaks = {0.026};
//	String[] seeds = {"313/0120", "543/0048", "797/0009", "483/0048"};
	String[] seeds = {"295/0091"};
	double[] etTweaks = { 0 };
	System.out.println(new Date().toString());
	
	System.out.println("BASELINE");
	for(int h = 0; h < 1; h++){
		for (int i = 0; i < startDays.length; i++) {
			for (int l = 1; l <= 1; l++) {
				for (int m = 0; m < tTweaks.length; m++) {
					for (int n = 0; n < vInfTweaks.length; n++) {
						for (int p = 0; p < eipBaseTweaks.length; p++) {
							for (int q = 0; q < eipRateTweaks.length; q++) {

								Farm[] myFarms = t1.readData();
			
								myFarms = t1.resetOverwinter(myFarms);
								myFarms = t1.tempData(new File(temps), myFarms, tTweaks[m]);
								BTVModel myDriver = new BTVModel(myFarms);
								myDriver.vInfTweak(vInfTweaks[n]);
								myDriver.eipTTweak(eipBaseTweaks[p]);
								myDriver.eipRTweak(eipRateTweaks[q]);

								myDriver.setStartDay(startDays[i]);
			
								myDriver.setPreSeeds(1, seeds[h]);
			//					myDriver.setSeedsTotallyRandom(5);
			
								myDriver.nSeeds = 1;
								int[] output = myDriver.runEpidemic(179);
								t1.writeOutput(writer, new int[] { 1, h, startDays[i], 5, 1, m, n, p, q, l }, output);
								File single = new File("Output/Census/Revised/Revised/SingleFarms.csv");
								t1.writeFarmsSingleRun(myFarms, single);
	
					}
				}
			}
			}
			System.out.println("Iteration = " + l + " START DAY = " + startDays[i] + "SEED = " + seeds[h] + " " + new Date().toString());
	}
			System.out.println("START DAY = " + startDays[i]);
		}
	}
	t1.writeMarker();
	}

}
