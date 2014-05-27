package misc.metadata

/**
 *
 * @author Josip
 */
trait Metadatable {
  /**
   * Returns the metadata associated with the key, or <code>null</code>
   * if none exists.
   * @param key The key.
   * @return The metadata.
   */
  def getMetadata(key: String): AnyRef

  /**
   * Sets a metadata.
   * @param key The key.
   * @param value The value.
   */
  def setMetadata(key: String, value: AnyRef): Unit

  /**
   * Removes a metadata and returns the object that is being deleted,
   * or null, if nothing has been deleted.
   * @param key The key.
   * @return The object that has been deleted, or null, if nothing has
   *         been deleted.
   */
  def removeMetadata(key: String): AnyRef
}

trait OverrideMetadatable extends Metadatable {

  private val metadata = new OverrideMetaList(this)

  override def getMetadata(key: String): AnyRef = {
    metadata get(key)
  }

  override def setMetadata(key: String, value: AnyRef): Unit = {
    metadata addMeta(key, value)
  }

  override def removeMetadata(key: String): AnyRef = {
    metadata get(key)
  }
}

trait AppendMetadatable extends Metadatable {

  private val metadata = ???

  override def getMetadata(key: String): AnyRef = ???
  override def setMetadata(key: String, value: AnyRef) = ???
  override def removeMetadata(key: String): AnyRef = ???

}
