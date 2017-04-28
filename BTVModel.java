import java.util.*;
import org.apache.commons.math3.distribution.*;

public class BTVModel {

	public Farm[] farmList;
	public int susceptibles;
	public int exposed;
	public int infectious;
	public int recovered;
	public Vector vSusceptibles;
	public Vector vExposed;
	public Vector vInfectious;
	public Vector vRecovered;
	public Vector vVaccinated;

	// Disease parameters
	private int latent;
	public int viraemiaS;
	public int viraemiaC;
	public double temper;
	public double acq;

	public int gausSD;
	public boolean speciesNum;
	public boolean doubleDip;
	private int startDay;
	public boolean logInf;
	public boolean vaccination;
	public int vaccinated;
	public boolean exponential;
	public double nBites;
	public double vectorInf;
	public boolean spiked;
	public boolean withinHerd;
	public int[] sheepInf;
	public int[] cattleInf;
	private boolean eipRev;
	public double eipbase;
	private double tempSD;
	private double tempVal;
	public boolean oneYearTweak;
	public boolean adjustedYear;
	public boolean feedPreference;
	public double prefN;
	private boolean annualVaccination;
	private double vaccProp;
	public int nSeeds;
	private int nac;
	private int nas;

	public BTVModel(Farm[] farmList) {
		this.farmList = farmList;
		this.susceptibles = this.farmList.length;
		this.exposed = 0;
		this.infectious = 0;
		this.recovered = 0;
		this.vSusceptibles = new Vector();
		for (int i = 0; i < this.susceptibles; i++) this.vSusceptibles.addElement(this.farmList[i]);
		this.vExposed = new Vector();
		this.vInfectious = new Vector();
		this.vRecovered = new Vector();
		this.latent = 3;
		this.viraemiaS = 16;
		this.viraemiaC = 20;
		this.gausSD = 20;
		this.acq = 0.9;
		this.speciesNum = false;
		this.doubleDip = true;
		this.startDay = 0;
		this.logInf = false;
		this.vaccination = false;
		this.vaccinated = 0;
		this.exponential = false;
		this.nBites = 2500;
		this.vectorInf = 0.19;
		// this.vectorInf = 0.05;
		this.spiked = false;
		this.withinHerd = false;
		this.sheepInf = new int[180];
		this.cattleInf = new int[180];
		this.eipRev = true;
		this.eipbase = 0;
		this.tempSD = 1.14;
		this.tempVal = 0;
		this.oneYearTweak = false;
		this.adjustedYear = false;
		this.feedPreference = false;
		this.prefN = 1;
		this.annualVaccination = false;
		this.vaccProp = 0;
		this.nSeeds = 0;
		this.nac = 0;
		this.nas = 0;
	}

/*  
 * The following methods are a bunch of methods for adjusting the parameters. Most of them are called from the driver class 	
 */
	
	public void changeBites(double newBite){
		this.nBites = newBite;
	}

	public void changePreference(double pref) {
		this.feedPreference = true;
		this.prefN = pref;
		this.setPreferences();
	}

	public void sheepInc(int day) {
		this.sheepInf[day]++;
	}

	public void cattleInc(int day) {
		this.cattleInf[day]++;
	}

	public void setStartDay(int day) {
		this.startDay = day;
	}

	public void eipRTweak(double inc) {

		for (int i = 0; i < this.vSusceptibles.size(); i++) {
			Farm curr = (Farm) this.vSusceptibles.elementAt(i);
			curr.eipRate = inc;
		}
	}
	public void eipTTweak(double inc) {

		for (int i = 0; i < this.vSusceptibles.size(); i++) {
			Farm curr = (Farm) this.vSusceptibles.elementAt(i);
			curr.baseEIP = inc;
		}
	}
	public void vInfTweak(double inc) {

		for (int i = 0; i < this.vSusceptibles.size(); i++) {
			Farm curr = (Farm) this.vSusceptibles.elementAt(i);
			curr.vectorInfProb = inc;
		}
	}

	public void tTweak(int month, double inc) {

		for (int i = 0; i < this.vSusceptibles.size(); i++) {
			Farm curr = (Farm) this.vSusceptibles.elementAt(i);
			curr.tempData[month - 5] = curr.tempData[month - 5] + inc;
		}
	}

	private void setSeeds(Farm seed) {

		if (!seed.infected) this.infectious++;
		seed.infected = true;
		seed.seed = true;
		seed.setAArray();
		seed.iDay = this.startDay;
		int stat = 0;
		while(stat == 0) stat = seed.newAnimalInfected(this.startDay);
		if (stat != 0) {
			boolean cowM = false;
			if (stat == 2) cowM = true;
			seed.updateInfectious(this.startDay, cowM);
			if (stat == 1) this.sheepInc(this.startDay);
			if (stat == 2) this.cattleInc(this.startDay);
			this.vInfectious.addElement(seed);
		}
	}

	public void setPreSeeds(int number, String cph) {
		
		Farm curr = this.getFarm(cph);
		for (int i = 1; i <= number; i++) {
			this.setSeeds(curr);
		}
	}

	public void setSeedsTotallyRandom(int number) {

		for (int i = 1; i <= number; i++) {
			int pos = (int) Math.floor(Math.random() * this.vSusceptibles.size());
			Farm curr = (Farm) this.vSusceptibles.elementAt(pos);
			if (curr.getCattle() == 0 & this.prefN == 0)
				i--;
			else
				this.setSeeds((Farm) this.vSusceptibles.elementAt(pos));

		}

	}

	public Farm getFarm(String cph) {

		Farm curr = (Farm) this.vSusceptibles.elementAt(0);
		int i = 1;
		boolean match = false;
		while (!match) {
			curr = (Farm) this.vSusceptibles.elementAt(i);
			if (cph.matches(curr.cph))
				match = true;
			i++;

		}
		return (curr);
	}


	public double normValue(double x) {

		return ((0.034 / Math.sqrt(Math.PI)) * (Math.exp(-(0.034 * 0.034) * (x * x))));

		// return (1 / (this.gausSD * Math.sqrt(2 * Math.PI))) *
		// Math.exp(-((Math.pow(x,2) / (2 * (Math.pow(this.gausSD, 2))))));
	}

	private double expValue(double x) {

		return (0.2 * Math.exp(-0.2 * x));
	}

	public void getTempValue() {

		Random generator = new Random();
		this.tempVal = generator.nextGaussian() * this.tempSD;
		for (int i = 0; i < this.vSusceptibles.size(); i++) {
			Farm next = (Farm) this.vSusceptibles.elementAt(i);
			next.tempOffset = this.tempVal;
			next.tempAdj = 0;
			if (this.adjustedYear)
				next.tempAdj = 1.0;
		}
	}

	private double aBites(int day) {
		double temp = this.nBites;
		if (day < 30)
			temp = temp / 4;
		if (day >= 65 & day < 100)
			temp = temp / 4;
		if (day >= 140)
			temp = temp / 4;

		return temp;

	}

/*
 *  	This is a somewhat long winded way of generating an array of random numbers that is used for the spatial kernel  
 */
	private double[] getRanArray(int size) {
		double[] myarray = new double[size];

		for (int i = 0; i < myarray.length; i++) {
			myarray[i] = Math.random();
		}
		Arrays.sort(myarray);
		return myarray;

	}
	

	private void runHostToVectorTrans(int day) {

		int infMem = this.infectious;

		// This loops through the infectious farms and updates the disease compartments 
		for (int i = 0; i < infMem; i++) {
			Farm currInf = (Farm) this.vInfectious.elementAt(i);
			currInf.updateStatus(day, this.latent);

			if (currInf.infectiousC > 0) {
				int bites = (int) (this.aBites(day) * currInf.landscape);
				currInf.updateABites(day, bites);
			}
		}
	}

	
	private void runVectorToHostTrans(int day){

		for (int i = 0; i < this.vInfectious.size(); i++) {
			Farm currInf = (Farm) this.vInfectious.elementAt(i);
			
			// This calls up the number of infectious bites on that day
			int todaysBites = currInf.getNInfBitesA(day);
					
			// This method creates a sorted array of random doubles
			double[] aThresh = this.getRanArray(todaysBites);
			double total = 0;
			double totMem = 0;

			int counter = 0;

		for (int j = 0; j < todaysBites; j++) {
			// If transmission is successful then continue
			if (this.acq > Math.random()) {
				double threshold = aThresh[j];
				boolean flag = false;
				
				// Loops through the vector of susceptible farms and tests transmission 
				while (!flag && counter < this.vSusceptibles.size()) {

					Farm currFarm = (Farm) this.vSusceptibles.elementAt(counter);

					double dist = Math.sqrt(Math.pow( (currInf.x - currFarm.x), 2) + Math.pow((currInf.y - currFarm.y), 2)) / 1000;

					// Samples which farm will be bitten given the kernel
					double kerVal = this.normValue(dist) * currInf.gausScaleLS;
					if (this.exponential) kerVal = this.expValue(dist) * currInf.expScaleLS;

					double cAcq = currFarm.getCattle() + currFarm.getSheep();
					total = total + (cAcq * kerVal);
					
					if (total >= threshold) {
						if (currFarm.getTempD(day) > 7.5) {
							flag = true;
							total = totMem;
							counter--;
							int stat = currFarm.newAnimalInfected(day);
							if (stat != 0) {
								// THIS BIT
								if (stat == 1) {
									this.sheepInc(day);
									currFarm.updateInfectious(day, false);
									this.nas++;

								}
								if (stat == 2) {
									this.cattleInc(day);
									currFarm.updateInfectious(day, true);
									this.nac++;
								}

								if (!currFarm.infected) {
									currFarm.infected = true;
									this.vInfectious.addElement(currFarm);
									this.infectious++;
									currInf.daughters++;
									currFarm.iDay = day;
									currFarm.setAArray();
								}
							}
						} else flag = false;
					}
					counter++;
					totMem = total;
				}
			}
		}
	}
}



	public int[] runEpidemic(int duration) {
		for (int i = this.startDay; i <= duration; i++) {
			this.runHostToVectorTrans(i);
			this.runVectorToHostTrans(i);
		}
		return this.getOutput();
	}

	public int[] getOutput() {
		int[] output = { 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0};
		for (int i = 0; i < this.vSusceptibles.size(); i++) {
			Farm curr = (Farm) vSusceptibles.elementAt(i);
			if (curr.infected) output[0]++;
			output[1] = output[1] + curr.nInfected;
			output[2] = output[2] + (int) curr.getSheepInfected() + curr.getSheepRecovered() + curr.nSheepDeaths + curr.nSheepRemoved;
			output[3] = output[3] + (int) curr.getCattleInfected() + curr.getCattleRecovered() + curr.nCattleRemoved;
			output[4] = output[4] + curr.getSusceptibleSheep();
			output[5] = output[5] + curr.getSusceptibleCattle();
			output[6] = output[6] + curr.nSheepDeaths;
			output[7] = output[7] + curr.nCattleRemoved;
			output[8] = output[8] + curr.nSheepRemoved;
			if(curr.vaccinated){
				output[9] = output[9] + (int) curr.getCattle();
				output[10] = output[10] + (int) curr.getSheep();			
			}
		}
		return output;
	}

	public void setPreferences() {
		for (int i = 0; i < this.vSusceptibles.size(); i++) {
			Farm curr = (Farm) this.vSusceptibles.elementAt(i);
			if (this.feedPreference)
				curr.changePref(this.prefN);
		}
	}
	public void vaccinateCattle(double efficacy){
		for (int i = 0; i < this.vSusceptibles.size(); i++) {
			Farm curr = (Farm) this.vSusceptibles.elementAt(i);
			if(curr.south && curr.getCattle() > 0){
				curr.vaccinated = true;
				curr.cattleVacc = curr.cattleVacc + new BinomialDistribution((int) curr.getCattle(), efficacy).sample();
			}
		}
		
	}

	public void vaccinateAllCattle(double efficacy){
		for (int i = 0; i < this.vSusceptibles.size(); i++) {
			Farm curr = (Farm) this.vSusceptibles.elementAt(i);
			if(curr.getCattle() > 0){
				curr.vaccinated = true;
				curr.cattleVacc = curr.cattleVacc + new BinomialDistribution((int) curr.getCattle(), efficacy).sample();
			}
		}
		
	}

	public void vaccinateSheep(double efficacy){
		for (int i = 0; i < this.vSusceptibles.size(); i++) {
			Farm curr = (Farm) this.vSusceptibles.elementAt(i);
			if(curr.south && curr.getSheep() > 0){
				curr.vaccinated = true;
				curr.sheepVacc = curr.sheepVacc + new BinomialDistribution((int) curr.getSheep(), efficacy).sample();
			}
		}
	}
	
		public void vaccinateAll(double efficacy){
			for (int i = 0; i < this.vSusceptibles.size(); i++) {
				Farm curr = (Farm) this.vSusceptibles.elementAt(i);
				if(curr.south && curr.getSheep() > 0) curr.sheepVacc = curr.sheepVacc + new BinomialDistribution((int) curr.getSheep(), efficacy).sample();
				if(curr.south && curr.getCattle() > 0) curr.cattleVacc = curr.cattleVacc + new BinomialDistribution((int) curr.getCattle(), efficacy).sample();
				curr.vaccinated = curr.south;
			}
	}
		public void vaccinateAllProp(double efficacy, double prop){
			for (int i = 0; i < this.vSusceptibles.size(); i++) {
				if(Math.random() < prop){
					Farm curr = (Farm) this.vSusceptibles.elementAt(i);
					curr.vaccinated = true;
					if(curr.getSheep() > 0) curr.sheepVacc = curr.sheepVacc + new BinomialDistribution((int) curr.getSheep(), efficacy).sample();
					if(curr.getCattle() > 0) curr.cattleVacc = curr.cattleVacc + new BinomialDistribution((int) curr.getCattle(), efficacy).sample();
				}
			}
	}

		public void vaccinateCattleBC(double coverage, String species){
			for (int i = 0; i < this.vSusceptibles.size(); i++) {
				Farm curr = (Farm) this.vSusceptibles.elementAt(i);
				if(Math.random() < coverage){
				if(curr.south && curr.getCattle() > 0 && (species == "Cattle" | species == "Both")){
					curr.vaccinated = true;
					curr.cattleVacc = curr.cattleVacc + (int) curr.getCattle();
					}
				if(curr.south && curr.getSheep() > 0 && (species == "Sheep" | species == "Both")){
					curr.vaccinated = true;
					curr.sheepVacc = curr.sheepVacc + (int) curr.getSheep();
					}
				}
			}			
		}
		public void vaccinateCattleWash(double coverage, String species){
			for (int i = 0; i < this.vSusceptibles.size(); i++) {
				Farm curr = (Farm) this.vSusceptibles.elementAt(i);
				if(Math.random() < coverage & !curr.seed){
					if(curr.southExt && curr.getCattle() > 0 && (species == "Cattle" | species == "Both")){
						curr.vaccinated = true;
						curr.cattleVacc = curr.cattleVacc + (int) curr.getCattle();
						}
					if(curr.southExt && curr.getSheep() > 0 && (species == "Sheep" | species == "Both")){
						curr.vaccinated = true;
						curr.sheepVacc = curr.sheepVacc + (int) curr.getSheep();
						}
				}
			}			
		}

}
