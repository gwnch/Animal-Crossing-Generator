import java.awt.image.*;
import java.util.Random;

// creates the sand and grass layers using the given parameters

public class SandGrass {
	
	static PerlinNoise PerlinNoise = new PerlinNoise(0);
	private double perlin, random;
	private int size = 8;
	Random rand = new Random();
	
	// generates grass or sand on the south side of  map
	public void south(int x1, int y1, int y2, TileMap mappy, BufferedImage tile) {
		for (int x = x1; x < (mappy.getWidth() - x1); x++) {
			random = rand.nextDouble();
			perlin = PerlinNoise.noise(x, random);
			//System.out.println(perlin);
			
			if (perlin < -0.3) {
				size = size + 1;
			}
			else if (perlin > 0.3) {
				size = size - 1;
			}
			
			if (size > 13) {
				size = size - 1;
			}
												
			for (int y = y1; y < size + y2; y++) {
				mappy.setTile(x, y, tile);
			}
		}
		size = 8;		
	}
	
	// generates grass or sand on the east side of  map
	public void east(int y1, int y2, int x1, TileMap mappy, BufferedImage tile) {
		for (int y = y1; y < (mappy.getHeight() - y2); y++) {
			random = rand.nextDouble();
			perlin = PerlinNoise.noise(y, random);

			if (perlin < -0.3) {
				size = size + 1;
			}
			else if (perlin > 0.3) {
				size = size - 1;
			}
			
			if (size > 13) {
				size = size - 1;
			}
			
			for (int x = x1; x < size + x1; x++) {
				mappy.setTile(x, y, tile);
			}
		}	
		size = 8;		
	}
	
	// generates grass or sand on the west side of  map
	public void west(int y1, int y2, int x1, int x2, TileMap mappy, BufferedImage tile) {
		for (int y = y1; y < (mappy.getHeight() - y2); y++) {
			random = rand.nextDouble();
			perlin = PerlinNoise.noise(y, random);
			
			if (perlin < -0.3) {
				size = size + 1;
			}
			else if (perlin > 0.3) {
				size = size - 1;
			}
			
			if (size > 13) {
				size = size - 1;
			}
			
			for (int x = size + x1; x < x2; x++) {
				mappy.setTile(x, y, tile);
			}
		}	
		size = 8;		
	}
	
	// generates grass or sand in the middle of map
	public void fill(int x1, int x2, int y1, int y2, TileMap mappy, BufferedImage tile) {
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < (mappy.getHeight() - y2); y++) {
				mappy.setTile(x, y, tile);
			}
		}
	}
}