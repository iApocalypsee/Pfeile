package general

import scala.beans.BeanProperty

/**
  * An object that can store metadata for its object lifetime.
  *
  * Metadata can be quite useful in some situations; for example when an instance variable is needed
  * and it is not declared in that particular class.
  * Just insert an entry in the metadata. Done.
  */
trait Metadatable {

  /** The object that holds all metadata for this object. */
  @BeanProperty lazy val metadata = new MetadataTree

}

/**
  * A class that can be used in Java. Since traits cannot be mixed into Java classes,
  * I need to have a class for compatibility reasons.
  */
class MetadatableClass extends Metadatable
