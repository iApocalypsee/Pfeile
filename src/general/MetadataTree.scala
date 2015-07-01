package general

import scala.collection.mutable

class MetadataTree private[general] {

  private val root = new RootNode

  def insertValue[A <: AnyRef](path: String, value: A): Unit = {
    val tokens = TokenSplit.tokenize(path).toList
    root.addChild(new NodeValue(tokens.last, value), tokens)
  }

  sealed abstract class TreeNode protected[MetadataTree] (val token: String) {
    def hasChildren: Boolean

    private[MetadataTree] var parent: TreeNode = null
  }

  /**
    * A node that can store children.
    * @param token The name with which this node can be retrieve again.
    * @param initChildren The initial children list.
    */
  class NodeWithChildren protected[MetadataTree] (override val token: String, initChildren: Seq[TreeNode]) extends TreeNode(token) {
    private val m_children = mutable.ArrayBuffer[TreeNode](initChildren: _*)

    def this(token: String) = this(token, Seq())

    override def hasChildren = m_children.nonEmpty

    def isRoot = false

    def children = m_children.toList

    /**
      * Adds a child node to the branch.
      * If this node encounters an already existing child with the same token as the parameter's token,
      * the old token is overridden and replaced with the new one.
      * @param x The node to insert.
      * @return Was the insert operation successful?
      */
    def addChild(x: TreeNode, where: String): Unit = addChild(x, TokenSplit.tokenize(where).toList ++ List(x.token))

    /**
      * Internal implementation of the insert operation.
      * @param x The node to insert ultimately.
      * @param traverseTokenStack The nodes that have to be created.
      */
    private[MetadataTree] def addChild(x: TreeNode, traverseTokenStack: List[String]): Unit = {

      assert(traverseTokenStack.nonEmpty)

      if (doesTokenExist(traverseTokenStack.head)) { // Remove the already existing one, overridden by new
        val existing = m_children.find(node => node.token == x.token)
        existing.get.parent = null
        m_children -= existing.get
      }

      if (traverseTokenStack.size == 1) { // Last element in the stack, should be equal to x.token
        assert(x.token == traverseTokenStack.head)
        x.parent = this
        m_children += x
      }
      else if (traverseTokenStack.size > 1) {
        val bridgeChildNode = new NodeWithChildren(traverseTokenStack.head)
        bridgeChildNode.parent = this
        m_children += bridgeChildNode
        bridgeChildNode.addChild(x, traverseTokenStack.tail)
      }
      else throw new AssertionError("Traverse token stack is empty, should not be empty")

    }

    /**
      * Looks if the specified token already exists in this level.
      * This method does not look for tokens in lower levels, it won't apply recursion.
      * @param token The token to look for.
      * @return Has the token been found?
      */
    def doesTokenExist(token: String) = (for (child <- m_children) yield child.token == token).reduce(_ || _)

    /**
      * Looks if the specified token can be found in the whole branch which is child to this entry.
      * @param token The token to look for in the entire branch.
      * @return Has the token been found?
      */
    //def doesTokenExistRecursive(token: String): Boolean = doesTokenExist(token) || m_children.collect({ case c: NodeWithChildren => c.doesTokenExistRecursive(token) }).reduce(_ || _)
  }

  /**
    * Special class for determining the root of the tree.
    */
  private[MetadataTree] class RootNode extends NodeWithChildren("") {
    override def isRoot = true
  }

  /**
    * Holds a single value.
    * @param token The token under which the value can be retrieved.
    * @param value The saved value.
    * @tparam A The type of the value.
    */
  class NodeValue[A <: AnyRef] protected[MetadataTree] (override val token: String, value: A) extends TreeNode(token) {
    override def hasChildren = false
  }

}

object TokenSplit {
  val Delimiter = "."

  def tokenize(metadataPath: String): Array[String] = metadataPath.split(Delimiter)
}
