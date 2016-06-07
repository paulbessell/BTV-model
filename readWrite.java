/*
 * Forms the interface between the driver and the model implementation sections.
 * 
 * Reads and writes datasets and initiates the model runs. 
 * 
 * Overloaded constructor to allow a predefined farm population or a farm population
 * that is subsequently read.
 * 
 * Borrowed from the internet initially, but heavily edited by Paul Bessell
 * 
 * Initiated into BTV package 18 November 2014
 */

// TextIO class reads/writes to files on line by line basis 2008 12 22
// IOException errors are caught locally; no need to throw up the stack
// Methods are closeText(mode), openText(File,mode),
// readText(), writeText(), readFile(fileName), readTag()
// Last modification: desense mode string

import java.io.*;
import java.util.*;

public class readWrite {

	public File census;
	private int fileDim;
	private int marker;
	private FileReader fr;
	private BufferedReader br;

	public readWrite(File census) throws Exception {
		this.census = census;
		this.fileDim = this.getLength();
		// System.out.println("File size = " + this.fileDim + " lines");

	}

	public readWrite() throws Exception {

	}

	public static String newline = System.getProperty("line.separator");
	FileWriter fw;
	BufferedWriter bw;
	String text;

	/*
	 * public void closeText(String mode) { try {
	 * if(mode.equalsIgnoreCase("read")){br.close();} else {bw.close();} } catch
	 * (IOException evt) {System.err.println("Exception: " + evt);} }
	 */

	public BufferedReader openRead() throws Exception {

		fr = new FileReader(this.census);
		return new BufferedReader(fr);
	}

	public BufferedReader openRead(File current) throws Exception {

		fr = new FileReader(current);
		return new BufferedReader(fr);
	}

	public int getLength() throws Exception {

		br = this.openRead();
		String curr = br.readLine();
		int count = 0;

		while (curr != null) {
			String[] currL = curr.split("\\,");
			count++;
			curr = br.readLine();
		}

		return count;
	}

	// Overloaded counterpart to getLength
	public int getLength(File current) throws Exception {

		br = this.openRead(current);
		String curr = br.readLine();
		int count = 0;

		while (curr != null) {

			String[] currL = curr.split("\\,");
			count++;
			curr = br.readLine();
		}

		return count;
	}

	// The bits where the data is read in.
	public Farm[] readData() throws Exception {

		Farm[] farmList = new Farm[this.getLength() - 1];
		br = this.openRead();
		String curr = br.readLine();
		curr = br.readLine();
		int counter = 0;
		while (curr != null) {
			//System.out.println(counter);
			String[] currL = curr.split("\\,");
			Farm currFarm = this.processFarm(currL);
			currFarm.pID = counter;
			farmList[counter] = currFarm;
			curr = br.readLine();
			counter++;
		}
		return farmList;
	}

	private Farm processFarm(String[] currL) throws Exception {

		String cph = currL[0];
		//System.out.println(cph);
		double x = Double.parseDouble(currL[3]);
		double y = Double.parseDouble(currL[4]);
		double sheep = Double.parseDouble(currL[1]);
		double cattle = Double.parseDouble(currL[2]);

		Farm nFarm = new Farm(cph, x, y);
		nFarm.setNumbers(sheep, cattle);
		if (currL.length > 5)
			nFarm.setLandscape(Double.parseDouble(currL[5]));
		if (currL.length > 6)
		//	nFarm.station = Integer.parseInt(currL[6]);
		if (currL.length > 7) {
			//nFarm.gausScale = Double.parseDouble(currL[7]);
			//nFarm.expScale = Double.parseDouble(currL[8]);
			nFarm.gausScaleLS = Double.parseDouble(currL[9]);
			nFarm.expScaleLS = Double.parseDouble(currL[10]);

		}
		if (currL.length == 12)
			nFarm.south = Integer.parseInt(currL[11]) == 1;
		return nFarm;
	}

	public Farm[] readDataEW() throws Exception {

		Farm[] farmList = new Farm[this.getLength() - 1];
		br = this.openRead();
		String curr = br.readLine();
		curr = br.readLine();
		int counter = 0;
		while (curr != null) {
			//System.out.println(counter);
			String[] currL = curr.split("\\,");
			Farm currFarm = this.processFarmEW(currL);
			currFarm.pID = counter;
			farmList[counter] = currFarm;
			curr = br.readLine();
			counter++;
		}
		return farmList;
	}

	private Farm processFarmEW(String[] currL) throws Exception {

		String cph = currL[0];
		//System.out.println(cph);
		double x = Double.parseDouble(currL[3]);
		double y = Double.parseDouble(currL[4]);
		double sheep = Double.parseDouble(currL[1]);
		double cattle = Double.parseDouble(currL[2]);

		Farm nFarm = new Farm(cph, x, y);
		nFarm.setNumbers(sheep, cattle);
		if (currL.length > 5)
			nFarm.setLandscape(1);
		if (currL.length > 6)
		//	nFarm.station = Integer.parseInt(currL[6]);
		if (currL.length > 7) {
			//nFarm.gausScale = Double.parseDouble(currL[7]);
			//nFarm.expScale = Double.parseDouble(currL[8]);
			nFarm.gausScaleLS = Double.parseDouble(currL[8]);
			nFarm.expScaleLS = Double.parseDouble(currL[9]);

		}
			nFarm.south = Integer.parseInt(currL[6]) == 1;
			nFarm.southExt = Integer.parseInt(currL[7]) == 1;
		//	nFarm.seed = Integer.parseInt(currL[10]) == 1;

		return nFarm;
	}

	// Read some temperature data in
	public Farm[] tempData(File inFile, Farm[] myFarms) throws Exception {

		BufferedReader buff = new BufferedReader(new FileReader(inFile));
		String curr = buff.readLine();

		for (int i = 0; i < myFarms.length; i++) {
			curr = buff.readLine();
			String[] currL = curr.split("\\,");
			for (int k = 1; k <= 8; k++) {
				myFarms[i].setMonthTemp(k, Double.parseDouble(currL[k]));
			}
		}
		return myFarms;

	}

	// Read in temperature data with an increment
	public Farm[] tempData(File inFile, Farm[] myFarms, double inc)
			throws Exception {

		BufferedReader buff = new BufferedReader(new FileReader(inFile));
		String curr = buff.readLine();

		for (int i = 0; i < myFarms.length; i++) {
			curr = buff.readLine();
			String[] currL = curr.split("\\,");
			for (int k = 1; k <= 8; k++) {
				myFarms[i].setMonthTemp(k, Double.parseDouble(currL[k]) + inc);
			}
		}
		return myFarms;

	}

	public Farm[] tempData(File inFile, Farm[] myFarms, double[] inc)
			throws Exception {

		BufferedReader buff = new BufferedReader(new FileReader(inFile));
		String curr = buff.readLine();
		for (int i = 0; i < myFarms.length; i++) {
			curr = buff.readLine();
			String[] currL = curr.split("\\,");
			for (int k = 1; k <= 8; k++) {
				myFarms[i].setMonthTemp(k, Double.parseDouble(currL[k])
						+ inc[k - 1]);
			}
		}
		return myFarms;

	}

	public void writeMarker() throws Exception {

		fw = new FileWriter("../../../Dropbox/Paul/EPIC/BTV/ModelMarker/marker.txt");
		bw = new BufferedWriter(fw);
		bw.write(new Date().toString() + newline);
		bw.flush();
	}
	
	public void writeCPH(int iter, Farm[] aFarms, BufferedWriter bwD) throws Exception {

		boolean flag = false;
		
		for (int i = 0; i < aFarms.length; i++) {

			Farm curr = (Farm) aFarms[i];
			if(curr.infected){
				if(!flag){
					bwD.write("iter"+iter + "," + i);
					
				}
				else if(flag) bwD.write("," + i);				
			flag = true;
			}			
		}
		bwD.write(newline);
		bwD.flush();
	}
	public void writeFarmsSingleRun(Farm[] pFarms, File outFile) throws Exception {

		fw = new FileWriter(outFile);
		bw = new BufferedWriter(fw);
		bw.write("cph,x,y,infDay" + newline);

		for (int i = 0; i < pFarms.length; i++) {

			Farm curr = (Farm) pFarms[i];

			if(curr.infected) bw.write(curr.cph + "," + curr.x + "," + curr.y + "," + curr.iDay + newline);
		}
		bw.flush();
	}

	public void writeFarms(Vector pFarms, File outFile) throws Exception {

		fw = new FileWriter(outFile);
		bw = new BufferedWriter(fw);
		bw.write("cph,x,y,infDay,remDay,vaccDay" + newline);

		for (int i = 0; i < pFarms.size(); i++) {

			Farm curr = (Farm) pFarms.elementAt(i);

			bw.write(curr.cph + "," + curr.x + "," + curr.y + "," + curr.iDay
					+ "," + curr.remDay + "," + curr.vDay + newline);
		}
		bw.flush();
	}

	public void writeFarmsN(Vector pFarms, File outFile) throws Exception {

		fw = new FileWriter(outFile);
		bw = new BufferedWriter(fw);
		bw.write("cph,x,y,cattle, sheep,livestock, iDay, infCount,daughters,sheepInf,cattleInf,sheepMalform,cattleMalform"
				+ newline);

		for (int i = 0; i < pFarms.size(); i++) {

			Farm curr = (Farm) pFarms.elementAt(i);

			bw.write(curr.cph + "," + curr.x + "," + curr.y + ","
					+ curr.getCattle() + "," + curr.getSheep() + ","
					+ curr.getLivestock() + "," + curr.iDay + ","
					+ curr.nInfected + "," + curr.daughters + ","
					+ curr.getSheepInfected() + "," + curr.getCattleInfected()
					+ newline);
		}
		bw.flush();
	}

	public void writeOutput(BufferedWriter bw, int[] details, int[] output)
			throws Exception {
		for (int i = 0; i < details.length; i++)
			bw.write(details[i] + ",");
		for (int i = 0; i < output.length - 1; i++)
			bw.write(output[i] + ",");
		bw.write(output[output.length - 1] + newline);
		bw.flush();

	}

	public BufferedWriter initilaiseOutput(File fOut) throws Exception {
		fw = new FileWriter(fOut);
		bw = new BufferedWriter(fw);
		bw.write("StartDay,Seeds,Kernel,Iteration,FarmsInf,AnimalsInf,SheepInf,CattleInf,SheepMal,CattleMal,SheepMalP15"
				+ newline);
		bw.flush();
		return bw;
	}
	
	public BufferedWriter initilaiseOutputCPH(File fOut) throws Exception {
		fw = new FileWriter(fOut);
		bw = new BufferedWriter(fw);
		return bw;
	}

	public BufferedWriter initilaiseOutputOW(File fOut) throws Exception {
		fw = new FileWriter(fOut);
		bw = new BufferedWriter(fw);
		bw.write("Year,seed,StartDay,Seeds,Kernel,temp,inf,eipT,eipR,Iteration,FarmsInf,AnimalsInf,SheepInf,CattleInf,SheepSC,CattleSC,SheepDead,cattleRemoved,sheepRemoved,CattleVacc,SheepVacc" + newline);
		bw.flush();
		return bw;
	}

	public BufferedWriter initilaiseCattleList(File fOut) throws Exception {
		fw = new FileWriter(fOut);
		BufferedWriter bwC = new BufferedWriter(fw);
		// bwf.write("PH" + newline);
		return bwC;
	}

	public BufferedWriter initilaiseSheepList(File fOut) throws Exception {
		fw = new FileWriter(fOut);
		BufferedWriter bwS = new BufferedWriter(fw);
		// bwf.write("PH" + newline);
		return bwS;
	}

	public BufferedWriter initilaiseFarmCattle(File fOut) throws Exception {
		fw = new FileWriter(fOut);
		BufferedWriter bwC = new BufferedWriter(fw);
		return bwC;
	}

	public BufferedWriter initilaiseFarmSheep(File fOut) throws Exception {
		fw = new FileWriter(fOut);
		BufferedWriter bwS = new BufferedWriter(fw);
		return bwS;
	}


	public void writeSheepList(BufferedWriter bwS, int[] aSheep)
			throws Exception {
		for (int i = 0; i < aSheep.length - 1; i++) {
			bwS.write(aSheep[i] + ",");
		}
		bwS.write(aSheep[aSheep.length - 1] + newline);
		bwS.flush();
	}

	public void writeCattleList(BufferedWriter bwC, int[] aCattle)
			throws Exception {
		for (int i = 0; i < aCattle.length - 1; i++) {
			bwC.write(aCattle[i] + ",");
		}
		bwC.write(aCattle[aCattle.length - 1] + newline);
		bwC.flush();
	}

	public Farm[] resetOverwinter(Farm[] myFarms) throws Exception {

		for (int i = 0; i < myFarms.length; i++) {
			myFarms[i].mayBirth = 1;
		}
		return myFarms;

	}
}
