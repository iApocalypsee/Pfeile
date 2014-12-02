package general

import scala.collection.JavaConverters._
import scala.collection.mutable

/** Holds metadata values for the [[general.Metadatable]] object.
  *
  */
class Metadata {

  private val entryBuffer = mutable.ListBuffer[Entry]()

  /** Adds new metadata to the metadata listing.
    *
    * This method adds a new key-value pair to the existing list of key-value entries of the
    * metadatable object. If the key already holds a value, the value is overridden.
    *
    * @param key The key to store the new metadata value under.
    * @param value The value to associate the key with.
    * @return An entry object tieing the key-value pair together.
    */
  def setMetadata(key: String, value: Any): Entry = {
    // The new entry is saved, no matter what.
    val newEntry = Entry(key, value)
    // Remove the old metadata associated with the key.
    removeMetadata(key)
    entryBuffer += newEntry
    newEntry
  }

  /** Retrieves the value of the given key as long as the given key can
    * be mapped to a value inside of this particular metadatable object.
    *
    * @param key The key to look for its value.
    * @return Maybe an entry.
    */
  def getMetadata(key: String): Option[Entry] = entryBuffer.find { _.key == key }

  /** Removes a given entry explicitly.
    *
    * If x does not represent an entry in this metadatable object, nothing special happens.
    *
    * @param x The entry to remove.
    */
  def removeMetadata(x: Entry): Unit = entryBuffer -= x

  /** Removes a given entry by finding its key.
    *
    * @param key The key to remove its entry.
    */
  def removeMetadata(key: String): Unit = {
    // Find the first occurrence of the given key.
    val occurrence = getMetadata(key)
    // And remove the occurrence if it is given.
    occurrence.map { removeMetadata }
  }

  /** The entries in a scala list. */
  def entries = entryBuffer.toList

  /** Returns a java representation of the metadata entries. */
  def getEntries = entries.asJava

  /** Java interop method. */
  def exists(key: String): Boolean = getMetadata(key).isDefined

  case class Entry private[Metadata](key: String, value: Any)

}
