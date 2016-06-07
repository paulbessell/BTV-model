import org.apache.commons.math3.distribution.BinomialDistribution;

public class bite {
	private int biteDay;
	private double prob;
	private int nBites;

	public bite(int biteDay, double prob){
		this.biteDay = biteDay;
		this.prob = prob;
	}
	
	public bite(int biteDay, int nBites){
		this.biteDay = biteDay;
		this.nBites = nBites;
	}

	
	public double getBiteProb(){
		return this.prob;
	}
	
	public int getBiteDay(){
		return this.biteDay;
	}

	public int getnBites(){
		return this.nBites;
	}
	
	public void calcNBites(int inputBites){
		BinomialDistribution biteBin = new BinomialDistribution(inputBites, this.getBiteProb()); 
		this.nBites = biteBin.sample();
	}

	
	public void printDetails(){
		System.out.println("Day = " +this.biteDay+ " Prob = "+this.prob + " n bites = "+ this.getnBites());
	}
}
