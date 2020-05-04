import java.io.File;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaterDrawer {
	private TileMap mappy;
	
	private BufferedImage ground = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "GroundGrass.png");
	private BufferedImage grass = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "HillGrass1.png");
	private BufferedImage hillGrass = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "HillGrass2.png");
	private BufferedImage water = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "water.png");
	private BufferedImage sand = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "Sand.png");
	private BufferedImage rock = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "Rocks.png");
	
	private Random ran = new Random();
	
	private List<Coord> mouth1 = new ArrayList<Coord> ();
	private List<Coord> mouth2 = new ArrayList<Coord> ();
	
	public WaterDrawer(TileMap m) {
		mappy = m;
	}
	
	public TileMap DrawMouths() {
		int mouthMiddle1 = ran.nextInt(mappy.getWidth()/3) + mappy.getWidth()/3;
		DrawMouthS(mouthMiddle1, false);
		
		int mouth2 = ran.nextInt(3); //Randomly pick which side the second water mouth goes
		if(mouth2==0)
			DrawMouthE();
		else if(mouth2==1)
			DrawMouthW();
		else {
			int mouthMiddle2 = ran.nextInt(mappy.getWidth()/3) + mappy.getWidth()/3;
			
			//Ensures the river mouths don't overlap
			while(Math.abs(mouthMiddle1 - mouthMiddle2) < mappy.getWidth()/4) {
				mouthMiddle2 = ran.nextInt(mappy.getWidth()/3) + mappy.getWidth()/3;
			}
			DrawMouthS(mouthMiddle2, true);
		}
		DrawPond();
		return mappy;
	}
	private void DrawMouthS(int mouthMiddle, boolean secondMouth) {
		// |||X|||| Mouth Tile Structure
		int startY = mappy.getHeight()-1;
		while(!Utility.compareBufferedImages(mappy.getTile(mouthMiddle, startY), sand)) { //Find the lowest sand tile
			startY--;
		}
		int y = startY - 4; //Starting the river mouth four tiles into the island
		int x1 = mouthMiddle;
		int x2 = mouthMiddle+1;
		int depth = 0; //The number of tiles down we've moved since y
		
		/* For the river algorithm we didn't have time to make
		if(!secondMouth) {
			for(int i = x1-1; i <= x2 + 1; i++)
				mouth1.add(new Coord(i,y));
		}
		else {
			for(int i = x1-1; i <= x2 + 1; i++)
				mouth2.add(new Coord(i,y));		
		}
		*/
		
		while(!Utility.compareBufferedImages(mappy.getTile(x1, y), water) || !Utility.compareBufferedImages(mappy.getTile(x2, y), water)) {
			//Makes all the tiles below it water
			while(y < mappy.getHeight()) {
				mappy.setTile(x1, y, water);
				mappy.setTile(x2, y, water);
				y++;
			}
			
			//Makes the mouth progressively wider and move downwards 1 tile
			if(x2-x1 >= 4) {
				x1--;
				x2++;
				depth++;
			}
			//Makes the mouth and river wider
			else {
				for(int i = startY - 4; i > startY - 12; i--) {
					mappy.setTile(x1, i, water);
					mappy.setTile(x2, i, water);
				}
				x1--;
				x2++;
			}
			//Reset y with new depth
			y = startY - 4 + depth;
		}
				
	}
	
	//Draws a river mouth on the West side of the island
	private void DrawMouthW() {
		int mouthMiddle = ran.nextInt(mappy.getHeight()/3) + mappy.getHeight()/3;
		// |||X||||
		int startX = 0;
		
		for(int i = startX; i < mappy.getWidth()/4; i++) { //Ensures water mouth doesn't start on a rock
			if(Utility.compareBufferedImages(mappy.getTile(i, mouthMiddle), rock) || Utility.compareBufferedImages(mappy.getTile(i, mouthMiddle), grass)) {
				i = startX;
				mouthMiddle = ran.nextInt(mappy.getHeight()/3) + mappy.getHeight()/3;
			}
		}
		
		//Finds leftmost sand tile
		while(!Utility.compareBufferedImages(mappy.getTile(startX, mouthMiddle), sand)) {
			startX++;
		}
		int x = startX + 3;
		int y1 = mouthMiddle;
		int y2 = mouthMiddle+1;
		int depth = 0;
		
		/* For the river algorithm we didn't have time for
		for(int i = y1-1; i <= y2 + 1; i++)
			mouth1.add(new Coord(x,i));
		*/
		
		while(!Utility.compareBufferedImages(mappy.getTile(x, y1), water) || !Utility.compareBufferedImages(mappy.getTile(x, y2), water)) {
			//Makes all tiles to the left water
			while(x > 0) {
				mappy.setTile(x, y1, water);
				mappy.setTile(x, y2, water);
				x--;
			}
			//System.out.println("y1 = " + y1);
			//System.out.println("y2 = " + y2);
			//System.out.println("x = " + x);

			if(y2-y1 >= 4) { 
				y1--;
				y2++;
				depth++;
			}
			else {
				for(int i = startX + 3; i < startX + 11; i++) {
					mappy.setTile(i, y1, water);
					mappy.setTile(i, y2, water);
				}
				y1--;
				y2++;
			}
			x = startX + 3 - depth;
		}
	}
	
	private void DrawMouthE() {
		int mouthMiddle = ran.nextInt(mappy.getHeight()/3) + mappy.getHeight()/3;
		// |||X||||
		int startX = mappy.getWidth()-1;
		
		for(int i = startX; i > mappy.getWidth()*0.75; i--) { //Ensures water mouth doesn't start on a rock
			if(Utility.compareBufferedImages(mappy.getTile(i, mouthMiddle), rock) || Utility.compareBufferedImages(mappy.getTile(i, mouthMiddle), grass)) {
				i = startX;
				mouthMiddle = ran.nextInt(mappy.getHeight()/3) + mappy.getHeight()/3;
			}
		}
		
		while(!Utility.compareBufferedImages(mappy.getTile(startX, mouthMiddle), sand)) {
			startX--;
		}
		int x = startX - 3;
		int y1 = mouthMiddle;
		int y2 = mouthMiddle+1;
		int depth = 0;
		
		for(int i = y1-1; i <= y2 + 1; i++)
			mouth1.add(new Coord(x,i));
		
		while(!Utility.compareBufferedImages(mappy.getTile(x, y1), water) || !Utility.compareBufferedImages(mappy.getTile(x, y2), water)) {
			while(x < mappy.getWidth()) {
				mappy.setTile(x, y1, water);
				mappy.setTile(x, y2, water);
				x++;
			}
			//System.out.println("y1 = " + y1);
			//System.out.println("y2 = " + y2);
			//System.out.println("x = " + x);

			if(y2-y1 >= 4) { 
				y1--;
				y2++;
				depth++;
			}
			else {
				for(int i = startX - 3; i > startX - 11; i--) {
					mappy.setTile(i, y1, water);
					mappy.setTile(i, y2, water);
				}
				y1--;
				y2++;
			}
			x = startX - 3 + depth;
		}
	}
	
	//Creates a pond on the highest hill level
	private void DrawPond() {
		List<List<Coord>> hill2Regions = mappy.GetRegions(hillGrass, mappy.getHeight());
		int ranHill = ran.nextInt(hill2Regions.size()); //Randomly pick a hill region
		List<Coord> hill = hill2Regions.get(ranHill);
		
		int count=0;
		while(hill.size() < 55) { //Ensure the hill is big enough for a pond
			hill = hill2Regions.get(count);
			count++;
		}
		
		//Creates a list of coords for edge tiles 7 in
		List<Coord> edgeTiles = new ArrayList<Coord>();
		for(Coord tile : hill) {
			for(int i = tile.getX()-7; i <= tile.getX()+7; i++) {
				for(int j = tile.getY()-7; j <= tile.getY()+7; j++) {
					if(i != tile.getX() && j != tile.getY()) {
						if(!Utility.compareBufferedImages(mappy.getTile(i, j), hillGrass)){
							edgeTiles.add(tile);
						}
					}
				}
			}
		}
		//Make tiles that aren't in the edge tiles category water
		for(Coord tile : hill) {
			if(!edgeTiles.contains(tile)) {
				mappy.setTile(tile.getX(), tile.getY(), water);
			}
				
		}

	}
}
