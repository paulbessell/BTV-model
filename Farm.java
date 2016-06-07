/*
 * Models the behaviour of a farm. 
 * 
 * Constructor requires a unique identifier (cph number), and x and y coordinates.
 * 
 * Additional attributes set using methods are the numbers of cattle and sheep.
 * 
 * By default the boolean infected parameter is false and infection day -1. If the
 *  farm is infected these are altered and the farm becomes an IP.
 */

import java.util.Vector;

import org.apache.commons.math3.distribution.BinomialDistribution;

public class Farm {
	public String cph;
	private double nSheep;
	private double nCattle;
	public double x;
	public double y;
	public boolean infected;
	public int iDay;
	public int remDay;
	public int vDay;
	public boolean removed;
	public boolean susceptible;
	public boolean exposed;
	public double landscape;
	public int latent;
//	public double gausScale;
//	public double expScale;
	public double gausScaleLS;
	public double expScaleLS;
	private String parentCPH;
	public int infectiousC;
	public int recovered;
	public int nInfected;
	public Vector infectedAnimals;
	public Vector infectedCattle;
	public Vector infectedSheep;
	public int daughters;
	private int livestock;
	private int sheepInfected;
	private int cattleInfected;
	private int sheepRecovered;
	private int cattleRecovered;
	public int cattleVacc;
	public int sheepVacc;
	public int pID;
	public double[] tempData;
	public boolean shortCattle;
	public boolean seed;
	public double mayBirth;
	public Vector carryOvers;
	public double tempOffset;
	public int newCowInf;
	public int newSheepInf;
	public double tempAdj;
	public boolean preference;
	public double prefFeed;
	public int vaccCattle;
	public int vaccSheep;
	public boolean south;
	public boolean southExt;
	// 2*2 matirx cols sheep/cattle; rows juveils/adult
	public double vectorInfProb;
	public Vector vBites;
	public double sheepMortality;
	public double eipRate;
	public double baseEIP;
	public int nSheepDeaths;
	public int station;
	public boolean vaccinated;
	private int[] aBites;
	private double sheepClinical;
	private double cattleClinical;
	public int nCattleRemoved;
	public int nSheepRemoved;
	

	public Farm(String cph, double x, double y) {
		this.cph = cph;
		this.infected = false;
		this.removed = false;
		this.x = x;
		this.y = y;
		this.nSheep = 0;
		this.nCattle = 0;
		this.iDay = -1;
		this.remDay = -1;
		this.vDay = -1;
		this.susceptible = true;
		this.exposed = false;
		this.landscape = 1;
		this.latent = -1;
//		this.gausScale = 1;
//		this.expScale = 1;
		this.gausScaleLS = 1;
		this.expScaleLS = 1;
		this.parentCPH = "999/9999";
		this.infectiousC = 0;
		this.recovered = 0;
		this.nInfected = 0;
		this.infectedAnimals = new Vector();
		this.infectedSheep = new Vector();
		this.infectedCattle = new Vector();
		this.daughters = 0;
		this.sheepInfected = 0;
		this.cattleInfected = 0;
		this.sheepRecovered = 0;
		this.cattleRecovered = 0;
		this.pID = 0;
		this.tempData = new double[8];
		this.shortCattle = true;
		this.seed = false;
		this.mayBirth = 0;
		this.carryOvers = new Vector();
		this.tempOffset = 0;
		this.newCowInf = 0;
		this.newSheepInf = 0;
		this.tempAdj = 0;
		this.preference = false;
		this.prefFeed = 0.8704;
		this.vaccCattle = 0;
		this.vaccSheep = 0;
		this.south = false;
		this.southExt = false;
		this.vectorInfProb = 0.19;
		this.vBites = new Vector();
//		this.sheepMortality = 0.02;
		this.sheepMortality = 0.1;
		this.eipRate = 0.019;
		this.baseEIP = 13.3;
		this.nSheepDeaths = 0;
		this.station = -1;
		this.cattleVacc = 0;
		this.sheepVacc = 0;
		this.sheepClinical = 0.047;
		this.cattleClinical = 0.0218;
		this.nSheepRemoved = 0;
		this.nCattleRemoved = 0;
	}
	
	public void setAArray(){
		this.aBites = new int[180];
	}

	public double getInfectedSheep() {

		return (double) this.infectedSheep.size();
	}

	public void changePref(double adj) {
		this.preference = true;
		// this.prefFeed = 0.128 * (1/adj);
		// this.prefFeed = 0.5 * (1/adj);

		this.prefFeed = this.prefFeed * adj;
	}

	public void setLandscape(double ls) {

		this.landscape = ls;
	}

	public double getLandscape() {
		return this.landscape;
	}

	public void updateLatent(int latentPeriod) {

		this.latent = latentPeriod;
	}

	public String getID() {

		return this.cph;
	}

	public void setNumbers(double sheep, double cattle) {

		this.nSheep = sheep;
		this.nCattle = cattle;
		this.livestock = (int) (this.getCattle() + this.getSheep());
	}


	public void setMonthTemp(int month, double val) {
		this.tempData[month - 1] = val;
	}

	public double getCattle() {
		return this.nCattle;
	}

	public double getSheep() {
		return this.nSheep;
	}

	public int getCattleInfected() {
		return this.cattleInfected;
	}

	public int getSheepInfected() {
		return this.sheepInfected;
	}

	public int getCattleRecovered() {
		return this.cattleRecovered;
	}

	public int getSheepRecovered() {
		return this.sheepRecovered;
	}

	public int getSusceptibleSheep() {
		return (int) this.nSheep - this.getSheepInfected();
	}

	public int getSusceptibleCattle() {
		return (int) this.nCattle - this.getCattleInfected();
	}

	public int getLivestock() {
		return this.livestock;
	}

	public void printDetails() {

		System.out.println("x = " + this.x + ";y = " + this.y + ";sheep = " + this.nSheep + ";cattle = " + this.nCattle);
	}

	public void setInfected(int day) {
		this.infected = true;
		this.iDay = day;
	}

	public void updateParent(String parent) {
		if (!parent.matches(this.cph))
			this.parentCPH = parent;

	}

	public void updateInfectious(int day, boolean cow) {
		this.nInfected++;

		Animal newAnimal = new Animal(day, cow);
		// System.out.println(this.cph + " Day = "+ day + " EIP = " + eip);
		this.infectedAnimals.addElement(newAnimal);
		if(newAnimal.cow) this.cattleInfected ++;
		if(!newAnimal.cow) this.sheepInfected ++;

	}

	public void updateStatus(int day, int latent) {

		for (int i = 0; i < this.infectedAnimals.size(); i++) {
			Animal currA = (Animal) this.infectedAnimals.elementAt(i);
		
			if ((currA.iDay + latent) == day) {
				this.infectiousC++;
//				if(currA.cow) this.cattleInfected ++;
//				if(!currA.cow) this.sheepInfected ++;
			}
			if(!currA.cow){
				if((currA.iDay + latent) <= day && (currA.iDay + latent + currA.getViraemia()) > day){
					double currAVir = (double) currA.getViraemia();
					if((this.sheepMortality / currAVir) > Math.random()){
						this.nSheepDeaths ++;
						this.infectiousC--;
//						if(this.sheepInfected > 0) this.sheepInfected --;			
						this.sheepInfected --;			
						this.infectedAnimals.removeElementAt(i);
						i--;
					}
					else if(this.sheepClinical > Math.random()){
						this.nSheepRemoved ++;
						this.infectiousC--;
						this.sheepInfected --;			
						this.infectedAnimals.removeElementAt(i);
						i--;					
					}
				}
			}
			if(currA.cow){
				if((currA.iDay + latent) <= day && (currA.iDay + latent + currA.getViraemia()) > day){
					if(this.cattleClinical > Math.random()){
						this.nCattleRemoved ++;
						this.infectiousC--;
						this.cattleInfected --;			
						this.infectedAnimals.removeElementAt(i);
						i--;					
					}
				}
			}
			
			if ((currA.iDay + latent + currA.getViraemia()) == day) {
				this.recovered++;
				this.infectiousC--;
				if(currA.cow) {
//					if(this.cattleInfected > 0) this.cattleInfected --;
					this.cattleInfected --;
					this.cattleRecovered++;
				}
				if(!currA.cow) {
//					if(this.sheepInfected > 0) this.sheepInfected --;
					this.sheepInfected --;
					this.sheepRecovered ++;
				}
				this.infectedAnimals.removeElementAt(i);
				i--;
			}
		}
	}

	public int newAnimalInfected(int day) {
		boolean newInf = false;
		int intInf = 0;
		boolean sheep = ((this.getSheep() * (1 - this.getCattleBiteProp())) / (double) this.livestock) >= Math.random();
		// System.out.println(this.getSheep()+", "+this.getCattle()+", "+this.livestock+", "+sheep);
		if (sheep) {
			newInf = ((double) (this.sheepInfected + this.sheepRecovered + this.sheepVacc) / this.getSheep()) < Math.random();
			if (newInf) {
				// System.out.println("HERE");
				//this.sheepInfected++;
				intInf = 1;
			}
		}
		if (!sheep) {
			newInf = ((double) (this.cattleInfected + this.cattleRecovered + this.cattleVacc) / this.getCattle()) < Math.random();
			if (newInf) {
				//this.cattleInfected++;
				intInf = 2;
			}
		}
		return intInf;
	}

	// Get the number of bites due that day
	public int getNInfBites(int day){
		
		int nBites = 0;
		if(this.vBites.size() > 0){
			for(int i = 0; i < this.vBites.size(); i++){
				bite currBite = (bite) this.vBites.elementAt(i);
				if(currBite.getBiteDay() == day && this.getTemp(day) > 10.5){
					nBites = nBites + currBite.getnBites();
					this.vBites.removeElementAt(i);
					i--;
				}
				else if(currBite.getBiteDay() == day && this.getTemp(day) <= 10.5){
					this.vBites.removeElementAt(i);
					i--;
				}				
				if(currBite.getBiteDay() < day) System.out.println("BITE VECTOR ERROR");
			}
		}		
		return nBites;
	}

	public int getNInfBitesA(int day){
		
		int nBites = 0;
		if(this.aBites[day] > 0){
				if(this.getTemp(day) > 10.5){
					nBites = aBites[day];
					}							
		}		
		return nBites;
	}

	public double getTempW(int day) {
		day = day + 60;
		int tDay = day / 30;
		int rDay = day % 30;
		double temp = this.tempData[tDay];
		if (rDay > 15 && tDay != 5) temp = (this.tempData[tDay] + this.tempData[tDay + 1]) / 2;
		return temp + this.tempOffset;
	}

	public double getTemp(int day) {
		int tDay = day / 30;
		double rDay = day % 30;
		double temp = this.tempData[tDay] * ((30 - rDay) / 30) + (this.tempData[tDay + 1] * (rDay / 30));
		return temp;
	}
	
	public int getEIP(int day){
		int iDay = 0;
		double tot = 0;
		int cDay = day;
		while(tot < 1 & cDay <= 180){
			tot = tot + 0.019 * (this.getTemp(cDay) - 13.3);
			cDay ++;
			iDay ++;
		}
		if(tot < 1) iDay = 280;
		return iDay;
	}

	public double getEIPExact(int day){
		double iDay = 0;
		int cDay = day;
		double tot = 0;
		double memTot = 0;
		while(tot < 1 & cDay <= 180){
			memTot = tot;
			tot = tot + (this.eipRate * (this.getTemp(cDay) - this.baseEIP));
//			tot = tot + (0.03 * (this.getTemp(cDay) - 12.35));

			cDay ++;
			iDay ++;
		}
		if(tot < 1) iDay = 280;
		if(tot >= 1) iDay = iDay - (tot - 1) / (tot - memTot);
		return iDay;
	}

	public double vectorMortality(double temp) {
		return 0.009 * Math.exp(0.16 * temp);
	}
	

	public double getBiteIntervalRate(double temp) {
		double out = (0.0002 * temp * (temp - 3.7) * Math.pow((41.9 - temp), (1 / 2.7)));
		return out;
	}

// This is the big one where all the business takes place
	public void updateVBites(int day, int nBites){
		int nInfBites = this.runBiting(nBites);
		double cEIP = this.getEIPExact(day);
		if(cEIP < 180){
			double cumProb = 1;
			double biteSum = 0;
			for(int i = day; i < 180; i++){
				double biteSumMem = biteSum;
				double cTemp = this.getTemp(i);
				cumProb = cumProb * (1 - this.vectorMortality(cTemp));
				biteSum = biteSum + this.getBiteIntervalRate(cTemp);
				if(i > Math.floor(day + cEIP)){
					if(Math.floor(biteSum) > biteSumMem){
						bite aBite = new bite(i, cumProb);
						aBite.calcNBites(nInfBites);
						if(aBite.getnBites() > 0) this.vBites.addElement(aBite);
					}
				}				
			}
		}		
	}

	public void updateABites(int day, int nBites){
		int nInfBites = this.runBiting(nBites);
		double cEIP = this.getEIPExact(day);
		if(cEIP < 180){
			double cumProb = 1;
			double biteSum = 0;
			for(int i = day; i < 180; i++){
				double biteSumMem = biteSum;
				double cTemp = this.getTemp(i);
				cumProb = cumProb * (1 - this.vectorMortality(cTemp));
				biteSum = biteSum + this.getBiteIntervalRate(cTemp);
				if(i > Math.floor(day + cEIP)){
					if(Math.floor(biteSum) > biteSumMem){
						bite aBite = new bite(i, cumProb);
						aBite.calcNBites(nInfBites);
						this.aBites[i] = this.aBites[i] + aBite.getnBites();
					}
				}				
			}
		}		
	}
	
	private double getCattleBites(double nBites){
		double cattleBites = 0;
		
		if(this.getCattle() == 0) cattleBites = 0;
		else if(this.getCattle() < this.getSheep()) cattleBites = (nBites * this.getCattle()) - (nBites * this.getCattle() * (1 - this.prefFeed)) + (nBites * this.prefFeed * this.getCattle());
		else if(this.getCattle() >= this.getSheep()) cattleBites = (nBites * this.getCattle()) - (nBites * this.getSheep() * (1 - this.prefFeed)) + (nBites * this.prefFeed * this.getSheep());
				
		return cattleBites;
	}

	private double getCattleBiteProp(){
		
		double cattleBites= 0;
		if(this.getCattle() == 0) cattleBites = 0;
		else if(this.getCattle() < this.getSheep()) cattleBites = (this.getCattle()) - (this.getCattle() * (1-this.prefFeed)) + (this.prefFeed * this.getCattle());
		
//Dropped - wrong bit of code
		//else if(this.getCattle() >= this.getSheep()) cattleBites = (this.getCattle() * this.prefFeed) - (this.getSheep() * (1-this.prefFeed)) + (this.prefFeed * this.getSheep());
		else if(this.getCattle() >= this.getSheep()) cattleBites = this.getCattle() - (this.getSheep() * (1-this.prefFeed)) + (this.prefFeed * this.getSheep());
		//	System.out.println("cattle bite prob = " + cattleBites / this.livestock);
		
		return cattleBites / this.livestock;
	}

	private int getBitesInfected(int bites){
		BinomialDistribution bDist = new BinomialDistribution(bites, this.vectorInfProb);
		return bDist.sample();
	}
	
	private int runBiting(int nBites){
		int nInfBites = 0;
		double totBites = ((this.getCattle() + this.getSheep()) * nBites);
/*		System.out.println("tot bites = "+ totBites + " Cattle bites = " + this.getCattleBites(nBites));
		System.out.println("n sheep = "+ this.getSheep() + " n Cattle = " + this.getCattle());
*/
		double sheepBites = 0;
		if(this.getSheep() > 0) sheepBites = (totBites - this.getCattleBites(nBites)) / (this.getSheep());
		double cattleBites = 0;
		if(this.getCattle() > 0) cattleBites = this.getCattleBites(nBites) / (this.getCattle());
/*		
		System.out.println("n bites = "+nBites+" Sheep bites = "+ sheepBites + " Cattle bites = " + cattleBites);
		System.out.println("Sheep infected = "+ this.getSheepInfected() + " Cattle infected = " + this.getCattleInfected());
*/
		nInfBites = this.getBitesInfected((int) ((sheepBites * this.getSheepInfected()) + (cattleBites * this.getCattleInfected())));
		
		return nInfBites;
		
	}

	// Check this bit!!!
	public double getTempC(int day) {
		double temp = this.getTemp(day);
		if (temp < 13.46) {
			double temp2 = this.getTemp(day + 30);
			if (temp2 < 12.35)
				temp = 12.3;
			else if (temp2 < temp)
				temp = (temp + temp2) / 2;
		}
		return temp;
	}

	// Check this bit!!!
	public double getTempCE(int day) {
		double temp = this.getTemp(day);
		if (temp < 13.46) {
			double temp2 = this.getTemp(day + 30);
			if (temp2 < 12.35)
				temp = 12.3;
			else if (temp2 < temp)
				temp = (temp + temp2) / 2;
		}
		return temp;
	}

	public double getTempD(int day) {
		int tDay = day / 30;
		double temp = this.tempData[tDay];
		double rDay = day % 30;
		if (rDay < 15)
			temp = (this.tempData[tDay] * ((15 - rDay) / 15))
					+ (this.tempData[tDay - 1] * (rDay / 15));
		if (rDay > 15)
			temp = (this.tempData[tDay + 1] * ((rDay - 15) / 15))
					+ (this.tempData[tDay] * ((15 - (rDay - 15)) / 15));
		return temp;
	}

	public double getTempF(int day) {
		// day = day + 60;
		int tDay = day / 30;
		double rDay = day % 30;
		// System.out.println("RDay = " + rDay);
		// double temp = this.tempData[tDay];
		// if(tDay != 5)
		double temp = this.tempData[tDay] * ((30 - rDay) / 30)
				+ (this.tempData[tDay + 1] * (rDay / 30));
		return temp - tempAdj;
	}

}
