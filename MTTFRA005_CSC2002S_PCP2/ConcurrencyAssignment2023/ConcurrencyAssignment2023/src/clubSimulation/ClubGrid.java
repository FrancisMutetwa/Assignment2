//M. M. Kuttel 2023 mkuttel@gmail.com
//Grid for the club

package clubSimulation;

import java.util.concurrent.atomic.AtomicBoolean;

//This class represents the club as a grid of GridBlocks
public class ClubGrid {
	private GridBlock [][] Blocks;
	private final int x;
	private final int y;
	public  final int bar_y;
	//AtomicBoolean flag_capacity= new AtomicBoolean();
	private GridBlock exit;
	private GridBlock entrance; //hard coded entrance
	private final static int minX =5;//minimum x dimension
	private final static int minY =5;//minimum y dimension
	 
	private PeopleCounter counter;
	
	ClubGrid(int x, int y, int [] exitBlocks,PeopleCounter c) throws InterruptedException {
		if (x<minX) x=minX; //minimum x
		if (y<minY) y=minY; //minimum x
		this.x=x;
		this.y=y;
		this.bar_y=y-3;
		Blocks = new GridBlock[x][y];
		this.initGrid(exitBlocks);
		entrance=Blocks[getMaxX()/2][0];
		counter=c;
		}
	
	//AtomicBoolean flag_capacity= new AtomicBoolean(counter.overCapacity());


	//initialise the grsi, creating all the GridBlocks
	private  void initGrid(int []exitBlocks) throws InterruptedException {
		for (int i=0;i<x;i++) {
			for (int j=0;j<y;j++) {
				boolean exit_block=false;
				boolean bar=false;
				boolean dance_block=false;
				if ((i==exitBlocks[0])&&(j==exitBlocks[1])) {exit_block=true;}
				else if (j>=(y-3)) bar=true; 
				else if ((i>x/2) && (j>3) &&(j< (y-5))) dance_block=true;
				//bar is hardcoded two rows before  the end of the club
				Blocks[i][j]=new GridBlock(i,j,exit_block,bar,dance_block);
				if (exit_block) {this.exit = Blocks[i][j];}
			}
		}
	}
	
		public  int getMaxX() {
		return x;
	}
	
		public int getMaxY() {
		return y;
	}

	public GridBlock whereEntrance() { 
		return entrance;
	}

	public  boolean inGrid(int i, int j) {
		if ((i>=x) || (j>=y) ||(i<0) || (j<0)) 
			return false;
		return true;
	}
	
	public  boolean inPatronArea(int i, int j) {
		if ((i>=x) || (j>bar_y) ||(i<0) || (j<0)) 
			return false;
		return true;
	}
	//AtomicBoolean flag_capacity= new AtomicBoolean(counter.overCapacity());
	public synchronized GridBlock  enterClub(PeopleLocation myLocation) throws InterruptedException  {
		//Patrons waiting to see if the club limit is reached or the entrance door is occupied.
		//Maximum number of patrons inside the club should not be exceeded the maximum.
		counter.personArrived(); //add to counter of people waiting 
		while(counter.overCapacity()){
			wait(1);// Wait with 1 second delay for each thread to enter the club
		}
		synchronized (myLocation){
		entrance.get(myLocation.getID());
		counter.personEntered(); //add to counter
		myLocation.setLocation(entrance);
		myLocation.setInRoom(true);
		return entrance;
		}
	}
	
	//Added synchronized keyword to ensure liveness and to ensure Patrons move block by block.
	public synchronized GridBlock move(GridBlock currentBlock,int step_x, int step_y,PeopleLocation myLocation) throws InterruptedException {  //try to move in 
		
		int c_x= currentBlock.getX();
		int c_y= currentBlock.getY();
		
		int new_x = c_x+step_x; //new block x coordinates
		int new_y = c_y+step_y; // new block y  coordinates
		
		//restrict i an j to grid
		if (!inPatronArea(new_x,new_y)) {
			//Invalid move to outside  - ignore
			return currentBlock;
		}

		if ((new_x==currentBlock.getX())&&(new_y==currentBlock.getY())) //not actually moving
			return currentBlock;
		 
		GridBlock newBlock = Blocks[new_x][new_y];
		
		if (!newBlock.get(myLocation.getID())) return currentBlock; //stay where you are
			
		currentBlock.release(); //must release current block
		myLocation.setLocation(newBlock);
		return newBlock;
	} 
	
	//added the synchronized keyword to make sure that only one thread can access the exit one at a time.
	public  synchronized void  leaveClub(GridBlock currentBlock,PeopleLocation myLocation)   {
		synchronized(entrance){	
		synchronized(counter){
		currentBlock.release();
			counter.personLeft(); //add to counter
			myLocation.setInRoom(false);			
			//entrance.notify();
			entrance.notifyAll();
		}
	}
	}

	public synchronized GridBlock getExit() {
		return exit;
	}

	public GridBlock whichBlock(int xPos, int yPos) {
		if (inGrid(xPos,yPos)) {
			return Blocks[xPos][yPos];
		}
		System.out.println("block " + xPos + " " +yPos + "  not found");
		return null;
	}
	
	public void setExit(GridBlock exit) {
		this.exit = exit;
	}

	public int getBar_y() {
		return bar_y;
	}

}


	

	

