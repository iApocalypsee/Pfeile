package world

private[world] class ZoomBehavior(val terrain: TerrainLike) {

  val tileList = terrain.tiles.map { x => TileEntry(x) }

  def zoomFrom(rootX: Int, rootY: Int, factor: Float): Unit = {

    // First transformation, since I am pulling at this one...
    retrieveTile(rootX, rootY).get.transform(rootX, rootY, factor)

    // Now scale and move the rest of the map
    for(y <- 0 until terrain.height;
        x <- 0 until terrain.width) {
      retrieveTile(x, y).map { x =>
        x.transform(rootX, rootY, factor)
      }
    }

    clearVisitStatus()
  }

  def clearVisitStatus() = {
    for(entry <- tileList) entry.visited = false
  }

  def retrieveTile(x: Int, y: Int): Option[TileEntry] = {
    if(terrain.isTileValid(x, y)) Some(tileList(terrain.tiles.indexOf(terrain.tileAt(x, y))))
    else None
  }

  case class TileEntry(tile: TileLike, var visited: Boolean = false) {

    def transform(rootX: Int, rootY: Int, factor: Float): Unit = if(!visited) {

      def diffX = tile.getGridX - rootX
      def diffY = tile.getGridY - rootY

      visited = true

      val c = tile.component

      if(diffX != 0 || diffY != 0) {

        val zeroTile = retrieveTile(rootX, rootY).get
        val zeroTileComponent = zeroTile.tile.component
        val newWidth = zeroTileComponent.getWidth
        val newHeight = zeroTileComponent.getHeight
        val zeroTilePosition = zeroTileComponent.getLocation.toPoint

        val newX = zeroTilePosition.x + diffX * newWidth / 2 + diffY * newWidth / 2
        val newY = zeroTilePosition.y + diffX * newHeight / 2 - diffY * newHeight / 2

        c.getTransformation.scale(factor, factor)
        c.setLocation(newX, newY)


      } else {
        c.getTransformation.scale(factor, factor)
      }

    }

  }

}
