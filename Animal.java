import org.apache.commons.math3.distribution.*;
public class Animal {
	public int iDay;
	public int remDay;
	public boolean adult;
	public boolean cow;
	private double viraemia; 

	public Animal(int iDay, boolean cow) {
		this.iDay = iDay;
		this.cow = cow;
		if(cow) this.viraemia = new GammaDistribution(20.6,1).sample();
		if(!cow) this.viraemia = new GammaDistribution(16.4,1).sample();
		this.remDay = this.iDay + 2 + (int) Math.ceil(this.viraemia);

	}

	public int getViraemia(){
		return (int) this.viraemia;
	}
		
}
