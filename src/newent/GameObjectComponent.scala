package newent

import java.awt.geom._
import java.awt.{Point => AwtPoint}
import java.util.concurrent.TimeoutException
import java.util.{Collection => ICollection, Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet}

import akka.actor.ActorDSL._
import akka.actor._
import com.sun.istack.internal.Nullable
import com.sun.javafx.geom.{Line2D => FXLINE2D}
import comp.Component
import general.JavaInterop.Implicits._
import general.JavaInterop._
import geom.{Vector => Vector2}
import newent.pathfinding.Path
import newent.pathfinding.Path.Step
import world.Tile

import scala.collection.JavaConverters._
import scala.collection.mutable.{ArrayBuffer, Queue}
import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.{NotNull => DeprecatedNotNullAnnot}

/**
  * Base class for components displaying a game object on the map.
  *
  * This class is currently only capable of making the component move along a certain path defined
  * by a [[java.awt.geom.Path2D]] object among other input types. For better performance (really?), the implementation
  * of the calculation of the points used in the animation is async and producer-consumer oriented.
  */
class GameObjectComponent(private val tie: GameObject) extends Component {

  // Code for objects following a Gui path as found in
  // http://stackoverflow.com/questions/32392095/how-to-rotate-a-rectangle-after-reaching-specified-position
  // with minor changes due to difference in programming language.

  // <editor-fold desc="Actor async implementations for moving objects on paths">

  /**
    * Actor which calculates position vectors solely by "stupidly" traversing each
    * break in the line/curve and returning the position of the break, without any interpolations.
    *
    * Makes up for some choppy movements if this actor is applied to straight lines. Use this actor if
    * the `Curve2D` object represented by given `PathIterator` really contains a curve and no straight lines.
    * If this condition is satisfied, smooth movement will result.
    *
    * @todo Add FPS hook for adjusting safety net to delta time.
    * @note This actor iterates through every vertex, it does not interpolate in between them though.
    *       Improper data (like described above) may result in choppy positional data when used in animation.
    */
  private val walkCalculator = actor(new Actor {

    private var iterator: PathIterator = null

    /**
      * The last position that has been calculated.
      * This var is usually set when the queue gets empty.
      */
    private var lastAvailable: Vector2 = null

    /**
      * All positions that have been precomputed.
      */
    private val previous = Queue.empty[Vector2]

    /**
      * How many positions should be computed in advance.
      */
    private val safetyNet = 10

    override def receive = {
      case AsyncMessages.IsFollowing            => sender ! (iterator != null)
      case AsyncMessages.Fetch                  => sender ! fetch()
      case AsyncMessages.Precompute             => precompute()
      case AsyncMessages.Terminate              => context stop self
      case AsyncMessages.PrepareComputation(pi) => prepareComputation(pi)
      case other                                => unhandled(other)
    }

    private def prepareComputation(pi: PathIterator): Unit = {
      iterator = pi
      previous.clear()
      lastAvailable = null
      for (_ <- 1 to safetyNet) self ! AsyncMessages.Precompute
    }

    /**
      * Retrieves the oldest position in the queue, sends it back and immediately schedules another
      * position precomputation.
      */
    private def fetch(): Option[Vector2] = {
      self ! AsyncMessages.Precompute

      val first = previous.dequeueFirst(_ => true)

      // Remind self of the last vector it has sent
      first.foreach(vec => lastAvailable = vec)
      first.orElse(Option(lastAvailable))
    }

    /**
      * Precompute one position based on the current state of the iterator.
      * If no iterator is present or existing iterator is done iterating, no action is taken.
      */
    private def precompute(): Unit = {
      if (iterator != null && !iterator.isDone) {
        val coords = Array.ofDim[Double](6)
        iterator.currentSegment(coords) match {
          case PathIterator.SEG_MOVETO => previous.enqueue(new Vector2(coords(0), coords(1)))
          case PathIterator.SEG_LINETO => previous.enqueue(new Vector2(coords(0), coords(1)))
          case _                       =>
        }
        iterator.next()

        // If the path iterator is now done iterating, stash the currently calculated element
        // for later calls.
        if (iterator.isDone) {
          lastAvailable = new Vector2(coords(0), coords(1))
          iterator = null
        }
      }
    }

  })

  /**
    * Actor which interpolates smoothly in between vertices supplied by the `PathIterator` object.
    *
    * This actor may be slower than the [[newent.GameObjectComponent#walkCalculator()]] when the `Curve2D`
    * object represented by the `PathIterator` object really contains a curve; although I have not tested the
    * performance of this actor when feeding it with real curves, I suspect it is fairly optimized to be used
    * for zig-zag paths with breaks in between.
    *
    * @todo Add FPS hook for adjusting safety net to delta time.
    */
  private val interpolateCalculator = actor(new Actor {

    private var iterator: PathIterator = null

    /**
      * Index of the vertex in the `vertices` buffer that has been used lastly as the begin vector
      * in the interpolation calculations.
      */
    private var beginUsedIndex = 0

    /**
      * All relevant positions that have been precomputed.
      */
    private val subdivs = Queue.empty[Vector2]

    /**
      * Vertices that have been captured by the path iterator.
      */
    private val vertices = ArrayBuffer.empty[Vector2]

    private var lerpFactor = 0.0

    private var followFlag = false

    /**
      * How many positions should be computed in advance.
      */
    private val safetyNet = 10

    override def receive = {
      case AsyncMessages.ClearComputation       => clearCalculation()
      case AsyncMessages.IsFollowing            => sender ! isFollowing
      case AsyncMessages.Fetch                  => sender ! fetch()
      case AsyncMessages.Precompute             => precompute()
      case AsyncMessages.PrepareComputation(pi) => prepareComputation(pi)
      case AsyncMessages.Terminate              => context stop self
      case other                                => unhandled(other)
    }

    private def clearCalculation(): Unit = {
      iterator = null
      subdivs.clear()
      vertices.clear()
      followFlag = false
      beginUsedIndex = 0
    }

    //private def isFollowing = !subdivs.lastOption.exists(vec => vec == vertices.last)
    private def isFollowing = followFlag

    /**
      * Clears previous computations and prepares the actor for calculating the
      * next vertices and interpolated vectors based on given path iterator.
      *
      * @param pi The new path iterator to base the GUI path calculations on now.
      */
    private def prepareComputation(pi: PathIterator): Unit = {
      clearCalculation()
      followFlag = true
      iterator = pi
      for (_ <- 1 to safetyNet) self ! AsyncMessages.Precompute
    }

    /**
      * Get the next position vector enqueued for retrieval, if any should exist.
      */
    private def fetch(): Option[Vector2] = {
      self ! AsyncMessages.Precompute
      // Dequeue first element, I want an Option
      val retOpt = subdivs.dequeueFirst(_ => true)
      if(retOpt.contains(vertices.last) && iterator == null) {
        followFlag = false
      }
      retOpt
    }

    /**
      * Precompute one position based on the current state of the iterator.
      * If no iterator is present or existing iterator is done iterating, no action is taken.
      */
    private def precompute(): Unit = {
      precomputeVertices()
      precomputeInterpolation()
    }

    private def precomputeVertices(): Unit = {
      if (iterator != null && !iterator.isDone) {
        val coords = Array.ofDim[Double](6)
        iterator.currentSegment(coords) match {
          case PathIterator.SEG_MOVETO => vertices += new Vector2(coords(0), coords(1))
          case PathIterator.SEG_LINETO => vertices += new Vector2(coords(0), coords(1))
          case _                       =>
        }
        iterator.next()

        // If the path iterator is now done iterating, stash the currently calculated element
        // for later calls.
        if (iterator.isDone) {
          iterator = null
        }
      }
    }

    private def precomputeInterpolation(): Unit = {

      //if(vertices.size <= beginUsedIndex + 1) return
      if(beginUsedIndex + 2 > vertices.size) return

      val beginInterpolate = vertices(beginUsedIndex)
      val endInterpolate = vertices(beginUsedIndex + 1)

      def interpolateOverLength = (endInterpolate - beginInterpolate).length
      def interpolationSpeedPerPass = interpolateOverLength / 300

      val factorAccum = if(interpolateOverLength < 2) 1.5 else 0.05 * interpolationSpeedPerPass + 0.001

      subdivs enqueue Vector2.lerp(beginInterpolate, endInterpolate, lerpFactor)

      if(lerpFactor + factorAccum >= 1.0) {
        if(beginUsedIndex + 2 < vertices.size) {
          lerpFactor = 0.0
          beginUsedIndex += 1
        } else {
          lerpFactor = 1.0
        }
      } else {
        lerpFactor += factorAccum
      }

    }

  })

  // </editor-fold>

  /**
    * Reference to the actor whose implementation this component will be using.
    */
  private val usedMoveImpl = interpolateCalculator

  /**
    * Inbox for interacting with the calculating actor from the outside.
    */
  private val inbox = Inbox.create(actorSystem)

  def followNewPath(@Nullable it: PathIterator): Unit = {
    Option(it).foreach(i => usedMoveImpl ! AsyncMessages.PrepareComputation(i))
  }

  /**
    * Makes the component's center position follow a defined path.
    *
    * The time which the component needs to traverse the whole specified path depends on the calculating
    * actor used underneath. Generally speaking, it takes longer for the calculator based on interpolation to empty
    * its queue of positional data than for the calculator solely based on the `PathIterator` instance.
    *
    * @param x The path to follow up.
    */
  def followNewPath(@Nullable x: java.awt.Shape): Unit = {
    followNewPath(Option(x).map(y => y.getPathIterator(null, 0.01)).orNull)
  }

  /**
    * Makes this component follow given world path.
    *
    * Care should be taken with this method, as it usually calculates the GUI path for the __whole__ world path
    * the game object is traversing. Most of the time you only want the game object to traverse a part of it,
    * in which case you are better off using [[newent.GameObjectComponent#followNewPath(java.util.List)]].
    *
    * @param path The path to make this component follow.
    *             Specified path is transformed exactly to the GUI center points of the tiles.
    *             Method has no effect if parameter is null.
    */
  def followNewPath(@Nullable path: Path): Unit = {
    followNewPath(Option(path).map(p => p.steps.asJava.toImmutableList).orNull)
  }

  /**
    * Makes this component follow given steps representing the world path an entity has walked.
    *
    * The list supplied to this method must contain the initial step, that is, the step which is not really a step.
    * This special step is just a step object in which the entity's world position is stored before
    * it has actually begun moving on the world.
    *
    * @param path The list of steps which the represented game object has walked.
    *             Must be at least of size 2 to be able to construct a GUI path.
    *             If null or size condition is not satisfied, no action is taken.
    */
  def followNewPath(@Nullable path: IList[Path.Step]): Unit = {
    if (path == null || path.size < 2) return

    // Whole bunch of mappings (get from the path to the center points of the components of the tiles)
    // Get the tail because of prerequisite listed in documentation.
    val steps: List[Step] = path.asScala.toList
    val tileSteps: List[Tile] = steps.map(step => tie.world.terrain.tileAt(step.x, step.y))
    val tileCenterPoints: List[AwtPoint] = tileSteps.map(tile => tile.component.center)

    // Real code
    val guiPath = new Path2D.Double
    guiPath.moveTo(center.x, center.y)

    tileCenterPoints.foreach(centerPoint => guiPath.lineTo(centerPoint.x, centerPoint.y))

    // Used for supplying real curves to the calculating actor.
    // These lines should be used in conjunction with the 'walkCalculator', though be careful
    // with the results you might still get out of it in return; the input values for real curves
    // that will be fed to the calculator are in no way optimized and correct.

//    var oldCenter: AwtPoint = null
//    tileCenterPoints.foreach { centerPoint =>
//      if(oldCenter == null) guiPath.curveTo(centerPoint.x, centerPoint.y, centerPoint.x, centerPoint.y, centerPoint.x, centerPoint.y)
//      else guiPath.curveTo(oldCenter.x, oldCenter.y, centerPoint.x, centerPoint.y, centerPoint.x, centerPoint.y)
//      oldCenter = centerPoint
//    }

    followNewPath(guiPath)
  }

  /**
    * Fetches position data and assigns it immediately to given component as its center position.
    * This method assumes that a path has been defined via [[newent.GameObjectComponent#followNewPath(java.awt.geom.Path2D)]]
    * at least once in the past.
    *
    * The position of the component depends on the state of the path iterator: if the path iterator is not done iterating
    * over the defined path, the latest position produced by this iterator is used. Otherwise the position is set to
    * the last position of the path that has been calculated.
    *
    * If no path has been defined at all (via `followNewPath`), the current center position of the component is used.
    */
  def keepFollowingPath(): Unit = {
    inbox.send(usedMoveImpl, AsyncMessages.Fetch)

    // Wait for the answer from the calculator, then store it.
    val optRetrieval = blocking {
      try { inbox.receive(1 second).asInstanceOf[Option[Vector2]] }
      catch { case e: TimeoutException => None }
    }

    optRetrieval.foreach(retrieval => setCenteredLocation(retrieval.getX.asInstanceOf[Int], retrieval.getY.asInstanceOf[Int]))
  }

  private val followReceiver = Inbox.create(actorSystem)

  /**
    * Returns true if the component is currently being moved on a defined path (supplied with `followNewPath(...)`).
    */
  def isFollowingPath(): Boolean = {
    followReceiver.send(usedMoveImpl, AsyncMessages.IsFollowing)
    blocking { followReceiver.receive(1 day).asInstanceOf[Boolean] }
  }

  def clearPathComputation(): Unit = {
    inbox.send(usedMoveImpl, AsyncMessages.ClearComputation)
  }

  override protected def finalize() = {
    super.finalize()
    interpolateCalculator ! AsyncMessages.Terminate
    walkCalculator ! AsyncMessages.Terminate
    inbox.getRef() ! PoisonPill
  }

  /**
    * Collection of the messages used by the calculator actors to interface to the client code.
    *
    * @todo Add [[scala.Serializable]] to [[geom.Vector]].
    */
  private object AsyncMessages {

    // All messages better be serializable, so they are.
    case class PrepareComputation(iterator: PathIterator)
    // This one is not serializable thanks to the non-serializability of Vector.
    // Should I add serializability to Vector?
    case class InterpolationVectors(vectors: (Vector2, Vector2))

    case object ClearComputation
    case object Fetch
    case object Precompute
    case object IsFollowing

    private[GameObjectComponent] case object Terminate

  }

  /**
    * @todo Supply values for easy fine-tuning.
    */
  private object Values

}

object GameObjectComponent {

  def javaFxPathIteratorAsAwtPathIterator(x: com.sun.javafx.geom.PathIterator): PathIterator = new PathIterator {
    override def next() = x.next()
    override def currentSegment(coords: Array[Float]) = x.currentSegment(coords)
    override def currentSegment(coords: Array[Double]) = {
      val fcoords = Array.ofDim[Float](coords.length)
      val ret = x.currentSegment(fcoords)
      for(i <- fcoords.indices) coords(i) = fcoords(i)
      ret
    }
    override def isDone = x.isDone
    override def getWindingRule = x.getWindingRule
  }

}
