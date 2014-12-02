package general

/** An object that can store metadata for its object lifetime.
  *
  * Metadata can be quite useful in some situations; for example when an instance variable is needed
  * and it is not declared in that particular class.
  * Just insert an entry in the metadata. Done.
  */
trait Metadatable {

  /** The object that holds all metadata for this object. */
  val metadata = Property(new Metadata)
  // If the metadata for this object is reset, it may not be null.
  metadata.onSet += { c => require(c.newVal.isDefined) }

}
