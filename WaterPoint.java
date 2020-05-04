import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
//Object that would've been used for the river creation had time allowed.
public class WaterPoint {
	private List<Coord> tiles;
	private List<Coord> edgeTiles;
	private List<WaterPoint> connectedPoints;
	private int pointSize;
	
	private BufferedImage water = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "water.png");
	
	public WaterPoint() {
		
	}
	public WaterPoint(List<Coord> waterTiles, TileMap map) {
		tiles = waterTiles;
		pointSize = tiles.size();
		connectedPoints = new ArrayList<WaterPoint>();
		
		edgeTiles = new ArrayList<Coord>();
		for(Coord tile : tiles) {
			for(int i = tile.getX()-1; i <= tile.getX()+1; i++) {
				for(int j = tile.getY()-1; j <= tile.getY()+1; j++) {
					if(i == tile.getX() || j == tile.getY()) {
						if(!Utility.compareBufferedImages(map.getTile(i, j), water)){
							edgeTiles.add(tile);
						}
					}
				}
			}
		}
	}
	
	public void AddConnectedPoint(WaterPoint b, boolean iLP) {
		connectedPoints.add(b);
		
		if(!iLP) //infinite loop protection
			b.AddConnectedPoint(this, true);
	}
	
	public boolean IsConnected(WaterPoint b) {
		return connectedPoints.contains(b);
	}
		
}
