package world

import newent.Entity

trait Placeable extends Entity {

  /**
    * Can entities enter a tile that has this placeable object as its decoration?
    */
  def canEnter: Boolean

}
