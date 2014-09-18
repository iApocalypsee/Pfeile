package world


abstract class AbstractTerrain(override val width: Int, override val height: Int) extends TerrainLike {

  require(width > 0, "Width cannot be negative or 0.")
  require(height > 0, "Height cannot be negative or 0.")

}
