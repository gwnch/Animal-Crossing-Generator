import java.awt.image.*;
import java.io.File;

public class Main {

	static SandGrass SandGrass = new SandGrass();
	static Rocks Rocks = new Rocks();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TileMap mappy = new TileMap(112,112);
		BufferedImage water = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "water.png");
		for(int i = 0; i < mappy.getWidth(); i++) {
			for(int j = 0; j < mappy.getHeight(); j++) {
				mappy.setTile(i, j, water);
			}
		}
		
		// sand layer
		BufferedImage sand = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "Sand.png");
		SandGrass.south(14, 90, 90, mappy, sand);
		SandGrass.east(12, 19, 90, mappy, sand);
		SandGrass.west(12, 19, 3, 20, mappy, sand); 
		SandGrass.fill(20, 90, 12, 22, mappy, sand);
		
		// ground grass layer
		BufferedImage groundGrass = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "GroundGrass.png");
		SandGrass.south(24, 88, 85, mappy, groundGrass);
		SandGrass.east(12, 20, 83, mappy, groundGrass); 
		SandGrass.west(12, 20, 10, 25, mappy, groundGrass);
		SandGrass.fill(25, 83, 12, 23, mappy, groundGrass);
		
		// Hills Layer 1
		// 0 Random fill = everything hills
		// 100 Random fill = everything grass
		HillGenerator hilly = new HillGenerator(mappy, 1, 62);
		mappy = hilly.CreateHills();
		
		// Hills Layer 2
		HillGenerator hilly2 = new HillGenerator(mappy, 2, 65);
		mappy = hilly2.CreateHills();
		
		//Rock Layer
		BufferedImage rock = Utility.LoadImage("."+ File.separator + File.separator + "Resources" + File.separator + File.separator + "Rocks.png");
		Rocks.rock(mappy, rock);
		
		//Create water mouths
		WaterDrawer wD = new WaterDrawer(mappy);
		mappy = wD.DrawMouths();
		
		//Save the map as Island.png
		mappy.saveMap();
	}
}