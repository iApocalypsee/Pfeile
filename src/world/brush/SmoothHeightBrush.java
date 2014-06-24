package world.brush;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import world.IBaseTile;
import world.tile.SeaTile;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Josip Palavra
 * @version 20.06.2014
 */
public class SmoothHeightBrush implements IBrush {

	private int thickness = DEFAULT_THICKNESS;
	private static final String smooth_meta_key = "pfeile.gen.smooth";

	/**
	 * Assigns data to given tiles.
	 *
	 * @param tileArray The tiles to be edited.
	 */
	@Override
	public void assign(List<IBaseTile> tileArray) {
		LinkedList<IBaseTile> edits = new LinkedList<IBaseTile>();

		for(IBaseTile tile : tileArray) {
			LinkedList<IBaseTile> painted = BrushHelper.determineTiles(tile, thickness);

			int avg = 0, sum = 0, count = 1;
			for(IBaseTile paintedTile : painted) {
				if(!(paintedTile instanceof SeaTile)) {
					sum += paintedTile.getTileHeight();
					count++;
				}
			}
			avg = sum / count;

			for(IBaseTile paintedTile : painted) {
				if(paintedTile.getMetadata(smooth_meta_key) == null) {

					if(paintedTile instanceof SeaTile) continue;

					final double defaultDiff = 1.1;
					boolean less = paintedTile.getTileHeight() < avg;
					if(less) {
						int diff = avg - paintedTile.getTileHeight();
						diff /= defaultDiff;
						paintedTile.setMetadata(HeightBrush.meta_key, paintedTile.getTileHeight() + diff);
					} else {
						int diff = paintedTile.getTileHeight() - avg;
						diff /= defaultDiff;
						paintedTile.setMetadata(HeightBrush.meta_key, paintedTile.getTileHeight() - diff);
					}

					edits.add(paintedTile);
					paintedTile.setMetadata(smooth_meta_key, true);
				}
			}
		}


		for(IBaseTile tile : edits) {
			tile.removeMetadata(smooth_meta_key);
		}

	}

	/**
	 * Returns the thickness of the brush.
	 * The thickness is the radius of the brush. Every tile in the radius
	 * gets "painted" by the brush (its metadata or anything else is going to
	 * be edited).
	 *
	 * @return The thickness of the brush.
	 */
	@Override
	public int getThickness() {
		return thickness;
	}

	/**
	 * Sets the thickness of the brush. Should not be
	 * any value less than or equal to 0.
	 *
	 * @param thickness The new thickness.
	 */
	@Override
	public void setThickness(int thickness) {
		if(thickness <= 0) throw new IllegalArgumentException();
		this.thickness = thickness;
	}

	/**
	 * Assigns data to the specified coordinates.
	 *
	 * @param pointList The point list.
	 */
	@Override
	public void assignPoints(List<Point> pointList) {
		throw new NotImplementedException();
	}
}
