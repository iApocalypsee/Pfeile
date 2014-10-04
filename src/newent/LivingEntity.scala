package newent

import player.Life

/**
 *
 * @author Josip Palavra
 */
trait LivingEntity extends Entity {

  val life: Life

  def getLife = life

}
