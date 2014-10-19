package newent

import java.awt.Graphics2D

import general.Main
import gui.{Drawable, GameScreen}

import scala.collection.{JavaConversions, mutable}

/** Class for displaying the visuals of entities.
  *
  * @param ec The entity components. Defaults to an empty seq.
  */
class VisualEntity(ec: Seq[EntityComponentWrapper] = Seq.empty[EntityComponentWrapper]) extends Drawable {

  private var _entityComps = mutable.MutableList[EntityComponentWrapper]()

  /** The list of all components that relate to entities. */
  def entityComponents = _entityComps.toList
  /** Ditto. */
  def javaEntityComponents = JavaConversions.seqAsJavaList(entityComponents)

  /** Auxiliary constructor for creating [[VisualEntity]] objects from a java list.
    *
    * @param javaEntityList The java list from where to pull the data from.
    */
  def this(javaEntityList: java.util.List[EntityComponentWrapper]) = this(JavaConversions.asScalaBuffer(javaEntityList))

  // Copy all the references from the constructor list to the private mutable list
  // I need those references in a mutable list, since entities can be REGISTERED and also UNREGISTERED
  entityComponents foreach { _entityComps += _ }

  // Every time an entity has been registered it has to be registered here as well...
  Main.getContext.world.entities.onEntityRegistered += {  _entityComps += new EntityComponentWrapper(_) }
  // And every time an entity has been unlogged it has to be deleted here as well...
  Main.getContext.world.entities.onEntityUnlogged += { ent =>
    _entityComps = _entityComps filter { _.entity ne ent }
  }

  /** Draws every entity. */
  override def draw(g: Graphics2D): Unit = entityComponents foreach { _.component.draw(g) }
}
