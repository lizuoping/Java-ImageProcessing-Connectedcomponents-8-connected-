import java.io.*;
import java.util.Scanner;

public class connectedCC8{
	static int [][]zeroFramedAry;
	static int []EQAry;
	static String [] Property;
	static int[] neighborAry=new int[8];
	static int numRows,	numCols, minVal, maxVal, newMin, newMax=0, newLabel=0, numLabelUsed=0;
	
	public static void main(String[] args){
		connectedCC(args);
		System.out.println("All work done!");
	}
	
	private static void connectedCC(String[] args) {
		initial(args[0]);
		ConnectCC_Pass1();
		ConnectCC_Pass2();
		manageEQAry();
		ConnectCC_Pass3();
		computeProperty();
		try{
			BufferedWriter outFile = new BufferedWriter(new FileWriter(args[1]));//open output file
			newMin = zeroFramedAry[1][1];
			for (int i = 1; i < numRows+1; i++) 
				for (int j = 1; j < numCols+1; j++){
					newMin=(zeroFramedAry[i][j]<newMin?zeroFramedAry[i][j]:newMin);
					newMax=(zeroFramedAry[i][j]>newMax?zeroFramedAry[i][j]:newMax);
				}
			outFile.write(numRows+" "+numCols+" "+newMin+" "+newMax);
			outFile.newLine();
			for (int i = 1; i < numRows+1; i++) {
				for (int j = 1; j < numCols+1; j++) 
					if (zeroFramedAry[i][j] >0)
						outFile.write(zeroFramedAry[i][j]+" ");
					else
						outFile.write("0 ");
				outFile.newLine();
			}
			outFile.close();
		}
		catch(Exception e){System.out.println(e);}
		try{
			BufferedWriter outFile = new BufferedWriter(new FileWriter(args[2]));
			for(int i=1; i<=numLabelUsed; i++){ 
				outFile.write(Property[i]);
				outFile.newLine();
			}
			outFile.close();
		}
		catch(Exception e){System.out.println(e);}
	}
	
	private static void initial(String fileName) {
		int row = 1, col = 1, order=0;
		Scanner inFile = null;
		try {
			inFile = new Scanner(new File(fileName));
			while(inFile.hasNext()){
				order++;
				if(order==1) numRows=inFile.nextInt();
				else if(order==2){
					numCols=inFile.nextInt();
					zeroFramedAry = new int[numRows+2][numCols+2];
					for(int i=0; i<numRows+2; i++) {
						zeroFramedAry[i][0] = 0;
						zeroFramedAry[i][numCols+1] = 0;
					}
					for(int j=0; j<numCols+2; j++) {
						zeroFramedAry[0][j] = 0;
						zeroFramedAry[numRows+1][j] = 0;
					}
					EQAry = new int[numRows*numCols/4];
					for(int i=0; i<numRows*numCols/4; i++)
						EQAry[i]=i;
				}
				else if(order==3) minVal=inFile.nextInt();
				else if(order==4) maxVal=inFile.nextInt();
				else{
					zeroFramedAry[row][col++] = inFile.nextInt();
					if(col>numCols) {
						row++;
						col=1;
					}
				}
			}
			inFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void ConnectCC_Pass1(){
		for(int i=1; i<numRows+1; i++) 
			for(int j=1; j<numCols+1; j++) 
				if(zeroFramedAry[i][j]>0) {
					loadNeighbors(i,j);
					if(neighborAry[0]==0 && neighborAry[1]==0 && neighborAry[2]==0 && neighborAry[3]==0)
						zeroFramedAry[i][j]=++newLabel;
					else
						zeroFramedAry[i][j]=findMinNotZero(i,j,1);
				}
	}
	
	private static void ConnectCC_Pass2(){
		for(int i=numRows; i>0; i--) 
			for(int j=numCols; j>0; j--) {
				loadNeighbors(i,j);
				if(zeroFramedAry[i][j]>0)
					if(neighborAry[4]!=0 || neighborAry[5]!=0 || neighborAry[6]!=0 || neighborAry[7]!=0)
						if(zeroFramedAry[i][j]!=neighborAry[4] || zeroFramedAry[i][j]!=neighborAry[5] || zeroFramedAry[i][j]!=neighborAry[6] || zeroFramedAry[i][j]!=neighborAry[7]) {
							int minLabel = findMinNotZero(i,j,2);
							updateEQAry(zeroFramedAry[i][j], minLabel);
							zeroFramedAry[i][j] = minLabel;
						}
			}
	}
	
	private static void ConnectCC_Pass3(){
		for(int i=1; i<numRows+1; i++) 
			for(int j=1; j<numCols+1; j++) 
				if(zeroFramedAry[i][j]>0)
					zeroFramedAry[i][j]=EQAry[zeroFramedAry[i][j]];
	}
	
	private static void manageEQAry(){
		for(int i=1; i<=newLabel; i++)
			if(EQAry[i]==i) 
				EQAry[i]=++numLabelUsed;
			else
				EQAry[i]=EQAry[EQAry[i]];
	}
	
	private static void computeProperty() {
		Property = new String[numLabelUsed+1];
		for(int m=1; m<=numLabelUsed; m++) {
			Property[m]=Integer.toString(m)+" ";
			int sumPixel = 0;
			for (int i = 1; i < numRows+1; i++)
				for (int j = 1; j < numCols+1; j++)
					if (zeroFramedAry[i][j]==m)
						sumPixel++;
			Property[m]+=Integer.toString(sumPixel)+" ";
			int minR=numRows, minC=numCols, maxR=0, maxC=0;
			for (int i = 1; i < numRows+1; i++)
				for (int j = 1; j < numCols+1; j++)
					if (zeroFramedAry[i][j]==m) {
						minR=(i<minR? i:minR);
						minC=(j<minC? j:minC);
						maxR=(i>maxR? i:maxR);
						maxC=(j>maxC? j:maxC);
					}
			Property[m]+=Integer.toString(minR)+" "+Integer.toString(minC)+" "+Integer.toString(maxR)+" "+Integer.toString(maxC);
		}
	}
	
	public static void loadNeighbors(int row, int col){
		neighborAry[0]=zeroFramedAry[row-1][col-1];
		neighborAry[1]=zeroFramedAry[row-1][col];
		neighborAry[2]=zeroFramedAry[row-1][col+1];
		neighborAry[3]=zeroFramedAry[row][col-1];
		neighborAry[4]=zeroFramedAry[row][col+1];
		neighborAry[5]=zeroFramedAry[row+1][col-1];
		neighborAry[6]=zeroFramedAry[row+1][col];
		neighborAry[7]=zeroFramedAry[row+1][col+1];
	}
	
	public static void updateEQAry(int index, int val){
		EQAry[index] = val;
	}
	
	public static void printEQAry(BufferedWriter outFile) throws IOException{
		for(int i=1; i<=newLabel; i++)
			outFile.write(i+" ");
		outFile.newLine();
		for(int i=1; i<=newLabel; i++)
			if(i>9 && EQAry[i]<10)
				outFile.write(" "+EQAry[i]+" ");
			else
				outFile.write(EQAry[i]+" ");
		outFile.newLine();
	}
	
	public static int findMinNotZero(int row, int col, int pass){
		int min_num=0, min_value=99;
		int[] not_zero=new int[4];
		if(pass==1) {
			for(int i=0; i<4; i++)
				if(neighborAry[i]!=0)
					not_zero[min_num++]=neighborAry[i];
			for(int j=0; j<min_num; j++)
				if(not_zero[j]<min_value)
					min_value=not_zero[j];
		}
		if(pass==2) {
			for(int i=4; i<8; i++)
				if(neighborAry[i]!=0)
					not_zero[min_num++]=neighborAry[i];
			for(int j=0; j<min_num; j++)
				if(not_zero[j]<min_value)
					min_value=not_zero[j];
		}
		return min_value;
	}
}





