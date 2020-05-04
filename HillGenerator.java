import java.util.Random;
import java.awt.image.*;
import java.io.File;
import java.util.*;
public class HillGenerator {
	private TileMap mappy;
	private int hillW;
	private int hillH;
	private int[][] hillNumbers;
	
	private BufferedImage ground;
	private BufferedImage grass;
	private BufferedImage hillGrass;
	
	private int layer;
	private Random ran = new Random();
	private String seed;
	private Boolean useRandomSeed;
	private int fillPercent;
	private int iterations = 4;
	
	//Constructor with a seed
	public HillGenerator(TileMap m, int lay, int fp, String s){
		mappy = m;
		hillW = mappy.getWidth();
		hillH = mappy.getHeight()/2;
		hillNumbers = new int[hillW][hillH];
		seed = s;
		useRandomSeed = false;
		fillPercent = fp;
		layer = lay;
		if(layer==1) {
			grass = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "GroundGrass.png");
			hillGrass = Utility.LoadImage(".\\Resources\\HillGrass1.png");
		}
		else {
			grass = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "HillGrass1.png");
			hillGrass = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "HillGrass2.png");
		}
	}
	
	//Constructor without a seed
	public HillGenerator(TileMap m, int lay, int fp){
		mappy = m;
		hillW = mappy.getWidth();
		hillH = mappy.getHeight()/2;
		hillNumbers = new int[hillW][hillH];
		useRandomSeed = true;
		fillPercent = fp;
		layer = lay;
		if(layer==1) {
			ground = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "GroundGrass.png");
			grass = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "GroundGrass.png");
			hillGrass = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "HillGrass1.png");
		}
		else {
			ground = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "GroundGrass.png");
			grass = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "HillGrass1.png");
			hillGrass = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "HillGrass2.png");
		}
	}
	
	//The main function
	public TileMap CreateHills() {
		RandomFill();
		DrawToMap();
		//mappy.saveMap("RandomFill");
		for(int i = 0; i < iterations; i++) {
			SmoothMap();
		}
		DrawToMap();
		//mappy.saveMap("Smoothed");
		
		Cleanup();
		DrawToMap();
		//mappy.saveMap("Cleaned");
		
		return mappy;
	}
	
	//Removes pieces of land smaller than the threshold size specified
	private void Cleanup() {
		if(layer==1) {
			List<List<Coord>> grassRegions = GetRegions(1);
			//List<List<Coord>> grassRegions = mappy.GetRegions(grass, hillH);
			int grassThreshholdSize = 50;
		
			for (List<Coord> grassRegion : grassRegions) {
				if(grassRegion.size() < grassThreshholdSize) {
					for(Coord tile : grassRegion) {
						hillNumbers[tile.getX()][tile.getY()] = 0;
					}
				}
			}
		}
		
		List<List<Coord>> hillRegions = GetRegions(0);
		//List<List<Coord>> hillRegions = mappy.GetRegions(hillGrass, hillH);
		int hillThreshholdSize = 30;
		
		for (List<Coord> hillRegion : hillRegions) {
			if(hillRegion.size() < hillThreshholdSize) {
				for(Coord tile : hillRegion) {
					hillNumbers[tile.getX()][tile.getY()] = 1;
				}
			}
		}
	}
	
	//Grabs all regions of a specific tile type from hillNumbers and returns a list of a list of Coordinates
	//Modified copy from TileMap.java so that it excludes edge tiles when cleaning
	public List<List<Coord>> GetRegions(int tileType){
		List<List<Coord>> regions = new ArrayList<List<Coord>>();
		int[][] flags = new int[hillW][hillH]; //Flag to show if a tiles been visited (1)
		
		for(int i = 0; i < hillW; i++) {
			for(int j = 0; j < hillH; j++) {
				if(flags[i][j] == 0 && hillNumbers[i][j] == tileType) {
					List<Coord> region = GetRegionTiles(i,j);
					regions.add(region);
					
					for(Coord tile : region) {
						flags[tile.getX()][tile.getY()] = 1;
					}
				}
			}
		}
		return regions;
	}
	//Grabs all tiles of a specific region from hillNumbers and returns a list of Coordinates
	//Modified copy from TileMap.java so that it excludes edge tiles when cleaning
	private List<Coord> GetRegionTiles(int xStart, int yStart){
		List<Coord> tiles = new ArrayList<Coord> ();
		int[][] flags = new int[hillW][hillH];
		//Determine initial type
		int tileType = hillNumbers[xStart][yStart];
		
		Queue<Coord> q = new LinkedList<Coord>(); //Queue to add a tile's neighbors to to check
		q.add(new Coord(xStart,yStart));
		flags [xStart][yStart] = 1;
		
		while(q.peek() != null) {
			Coord tile = q.remove();
			tiles.add(tile);
			
			for(int i = tile.getX() - 1; i <= tile.getX() + 1; i++) {
				for(int j = tile.getY() - 1; j <= tile.getY() + 1; j++) {
					if(i >= 0 && i < hillW && j >= 0 && j < hillH) {
						if(i == tile.getX() || j == tile.getY()) { //Exclude diagonals
							//If tile hasn't been visited and its the correct type flag it and add it to the queue
							if(flags[i][j] == 0 && hillNumbers[i][j] == tileType) {
								flags[i][j] = 1;
								q.add(new Coord(i,j));
							}
						}
					}
				}
			}
		}
		
		return tiles;
	}
	
	//Randomly populates the upper half of the map with grass and hill
	private void RandomFill() {
		if(!useRandomSeed) {
			ran.setSeed(Long.parseLong(seed));
		}
		/*
		 * 0 = hill
		 * 1 = grass
		 * 2 = nothing
		 */
		for(int i = 0; i < hillW; i++) {
			for(int j = 0; j < hillH; j++) {
				if(Utility.compareBufferedImages(mappy.getTile(i, j), grass)) { //Make sure the tiles grass
					//Checking it's neighbors and 4 above the tile to prevent any cliffs
					if(!Utility.compareBufferedImages(mappy.getTile(i-1, j), grass) && !Utility.compareBufferedImages(mappy.getTile(i-1, j), hillGrass)) {
						hillNumbers[i][j] = 1;
					}
					else if(!Utility.compareBufferedImages(mappy.getTile(i+1, j), grass) && !Utility.compareBufferedImages(mappy.getTile(i+1, j), hillGrass)) {
						hillNumbers[i][j] = 1;
					}
					else if(!Utility.compareBufferedImages(mappy.getTile(i, j-1), grass) && !Utility.compareBufferedImages(mappy.getTile(i, j-1), hillGrass)) {
						hillNumbers[i][j] = 1;
					}
					else if(!Utility.compareBufferedImages(mappy.getTile(i, j-2), grass) && !Utility.compareBufferedImages(mappy.getTile(i, j-2), hillGrass) && !Utility.compareBufferedImages(mappy.getTile(i, j-2), ground)) {
						hillNumbers[i][j] = 0;
					}
					else if(!Utility.compareBufferedImages(mappy.getTile(i, j-3), grass) && !Utility.compareBufferedImages(mappy.getTile(i, j-3), hillGrass) && !Utility.compareBufferedImages(mappy.getTile(i, j-3), ground)) {
						hillNumbers[i][j] = 0;
					}
					else if(layer != 1 && !Utility.compareBufferedImages(mappy.getTile(i, j-4), grass) && !Utility.compareBufferedImages(mappy.getTile(i, j-4), hillGrass) && !Utility.compareBufferedImages(mappy.getTile(i, j-4), ground)) {
						hillNumbers[i][j] = 0;
					}
					//Discourage hills lower down to keep curves
					else if(j >= hillH-(ran.nextInt(5)+3)) {
						hillNumbers[i][j] = 1;
					}
					else {
						if(ran.nextInt(100) < fillPercent) {
							hillNumbers[i][j] = 1;
						}
						else {
							hillNumbers[i][j] = 0;
						}
					}
				}
				else {
					hillNumbers[i][j] = 2; //Ignore these
				}
			}
		}
	}
	
	private void SmoothMap() {
		for(int i = 0; i < hillW; i++) {
			for(int j = 0; j < hillH; j++) {
				if(Utility.compareBufferedImages(mappy.getTile(i, j), grass) || Utility.compareBufferedImages(mappy.getTile(i, j), hillGrass)) { //Make sure the tiles lower
					//If the tile to the left isn't green make the current tile grass
					if(!Utility.compareBufferedImages(mappy.getTile(i-1, j), grass) && !Utility.compareBufferedImages(mappy.getTile(i-1, j), hillGrass)) {
						hillNumbers[i][j] = 1;
					}
					//If the tile to the right isn't green make the current tile grass
					else if(!Utility.compareBufferedImages(mappy.getTile(i+1, j), grass) && !Utility.compareBufferedImages(mappy.getTile(i+1, j), hillGrass)) {
						hillNumbers[i][j] = 1;
					}
					//If the tile above isn't green make the current tile grass
					else if(!Utility.compareBufferedImages(mappy.getTile(i, j-1), grass) && !Utility.compareBufferedImages(mappy.getTile(i, j-1), hillGrass)) {
						hillNumbers[i][j] = 1;
					}
					//For hill on hill prevent cliffs
					else if(!Utility.compareBufferedImages(mappy.getTile(i, j+1), grass) && !Utility.compareBufferedImages(mappy.getTile(i, j+1), hillGrass)) {
						hillNumbers[i][j] = 1;
					}
					else if(!Utility.compareBufferedImages(mappy.getTile(i, j+2), grass) && !Utility.compareBufferedImages(mappy.getTile(i, j+2), hillGrass)) {
						hillNumbers[i][j] = 1;
					}
					//If the tile 2 above isn't green perform smoothing leaning towards a hill
					else if(!Utility.compareBufferedImages(mappy.getTile(i, j-2), grass) && !Utility.compareBufferedImages(mappy.getTile(i, j-2), hillGrass) && !Utility.compareBufferedImages(mappy.getTile(i, j-2), ground)) {
						int wallTiles = GetSurroundingCount(i,j);
						
						if(wallTiles > 3)
							hillNumbers[i][j] = 0;
						else if(wallTiles < 3)
							hillNumbers[i][j] = 1;
					}
					//Otherwise perform smoothing
					else {
						int wallTiles = GetSurroundingCount(i,j);
				
						if(wallTiles >= 4)
							hillNumbers[i][j] = 0;
						else if(wallTiles < 4)
							hillNumbers[i][j] = 1;
					}
				}
			}
		}
	}
	//Returns an int for the number of neighbors a tile has
	private int GetSurroundingCount(int gridX, int gridY) {
		int count = 0;
		for(int nx = gridX - 1; nx <= gridX + 1; nx++) {
			for(int ny = gridY - 1; ny <= gridY + 1; ny++) {
				if(nx >= 0 && nx < hillW && ny >= 0 && ny < hillH) { //avoid out of bounds
					if(nx != gridX || ny != gridY) { //Don't count the tile passed
						if(hillNumbers[nx][ny] == 0)
							count++;
					}
				}
				else { //Encourage more grass on edges
					count++;
				}
			}
		}
		return count;
	}
	
	//Apply hillNumbers coordinates to the TileMap
	private void DrawToMap() {
		for(int i = 0; i < hillW; i++) {
			for(int j = 0; j < hillH; j++) {
				if(hillNumbers[i][j] == 0) {
					mappy.setTile(i,j,hillGrass);
				}
				else if(hillNumbers[i][j] == 1) {
					mappy.setTile(i, j, grass);
				}
			}
		}
	}
}
