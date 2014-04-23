package player;

import general.Field;
import general.World;

import java.util.*;

/**
 * @author Josip
 * @version 23.03.14
 */
public class VisionableArea {

	/**
	 * The data of all of the positionables.
	 */
	VisionData visionData = new VisionData(this);

	/**
	 * The entity to which the vision object is attached to.
	 */
	private Entity entity;

	@SuppressWarnings(value = "unused")
	static Class<VisionData> visionDataClass = VisionData.class;

	/**
	 * Creates a new vision area with the
	 * @param entity The entity to attach to. Value must not be <code>null</code>.
	 * @throws java.lang.NullPointerException if <code>entity == null</code>.
	 */
	public VisionableArea(Entity entity) {
		if(entity == null) {
			throw new NullPointerException();
		}
		this.entity = entity;
	}

	/**
	 * Adds a vision point to the vision area.
	 * @param positionable The coordinates of the vision point.
	 * @param strength The strength, measured in meters.
	 */
	public void putVision(BoardPositionable positionable, int strength) {
		VisionPoint p = new VisionPoint(positionable, this);
		p.setStrength(strength);
		visionData.addVisionPoint(p);
	}

	/**
	 * Returns the visibility of any Field object.
	 * @param positionable The positionable board object.
	 * @return The visibility of that object.
	 */
	public VisionState getVisibility(Field positionable) {
		return visionData.getVisibility(positionable);
	}

	/**
	 * Updates the vision for the entity.
	 */
	public void updateVision() {
		visionData.updateAll();
	}


	/**
	 * @author Josip
	 * @version 23.03.14
	 */
	static class VisionPoint {

		private int strength;

		private BoardPositionable hook;

		private VisionableArea vision;

		public VisionPoint(BoardPositionable hook, VisionableArea sight) {

			if(hook == null || sight == null) {
				throw new IllegalArgumentException();
			}

			strength = 0;
			this.hook = hook;
			this.vision = sight;


		}

		public int getStrength() {
			return strength;
		}

		public void setStrength(int strength) {
			this.strength = strength;
		}
	}

	/**
	 * Adds a field to the vision system.
	 * @param field The field to add.
	 */
	static synchronized void addEntry(Field field) {
		VisionData.injectPositionable(field);
	}

	/**
	 * Represents the vision status of a field.
	 */
	public enum VisionState {
		OBSERVABLE, NONVISIBLE, UNREVEALED
	}

	static class VisionData {

		/**
		 * Every instance of VisionData will be collected here.
		 */
		private static final LinkedList<VisionData> allData = new LinkedList<VisionData>();
		private static final LinkedList<Field> allFields = new LinkedList<Field>();

		private VisionableArea area = null;

		/**
		 * The vision list.
		 */
		VisionList visionList = new VisionList();

		/**
		 * The vision points.
		 */
		ArrayList<VisionPoint> visionPoints = new ArrayList<VisionPoint>();

		/**
		 * Creates a new vision data object.
		 */
		public VisionData(VisionableArea area) {
			this.area = area;
			allData.add(this);
			for(Field field : allFields) {
				addEntry(field);
			}
		}

		/**
		 * Adds a new vision point to the vision system.
		 * @param positionable The positionable board object.
		 */
		void addEntry(Field positionable) {
			if(check(positionable)) {
				throw new ExistingEntryException();
			}

			visionList.add(new VisionEntry(positionable));
		}

		/**
		 * Adds a vision point to the vision system.
		 * @param point The point.
		 */
		void addVisionPoint(VisionPoint point) {
			visionPoints.add(point);
		}

		/**
		 * Returns the visibility of any Field object.
		 * @param positionable The positionable board object.
		 * @return The visibility of that object.
		 */
		VisionState getVisibility(Field positionable) {
			if(!check(positionable)) {
				throw new NoSuchElementException();
			}
			if(visionList.indexOf(positionable) != Integer.MIN_VALUE) {
				return visionList.get(visionList.indexOf(positionable)).value;
			} else {
				return null;
			}
		}

		/**
		 * Sets the visibility of any positionable object.
		 * @param positionable The positionable board object.
		 * @param visibility The new visibility of the board object.
		 */
		void setVisibility(Field positionable, VisionState visibility) {
			if(!check(positionable)) {
				throw new NoSuchElementException(positionable + " is not listed.");
			}
			if(visionList.indexOf(positionable) != Integer.MIN_VALUE) {
				visionList.get(visionList.indexOf(positionable)).value = visibility;
			} else {
				return;
			}
		}

		/**
		 * Clears the whole data and sets all visibility values to <code>false</code>.
		 */
		void clearData() {
			for(VisionEntry entry : visionList) {
				if(entry.isCartographed()) {
					entry.value = VisionState.NONVISIBLE;
				} else {
					entry.value = VisionState.UNREVEALED;
				}
			}
		}

		void updateAll() {
			// clear the whole array, or else I'll get value collisions
			clearData();

			for(VisionPoint point : visionPoints) {

				Field[] fields = determineFields(point);
				for(Field field : fields) {
					visionList.get(visionList.indexOf(field)).value = VisionState.OBSERVABLE;
					visionList.get(visionList.indexOf(field)).cartographed = true;
				}

			}
		}

		/**
		 * Determines the fields used for the vision system.
		 * The method is written sooo badly complicated. Why? Because
		 * every time an entity moves, the whole vision system has to be
		 * refreshed, so there have to be a lot of "if"s to only execute code that is needed. <br></br>
		 * TODO Vision barriers (like mountains) not coded yet. And method could need refinement for sure.
		 * @param point The point from which to analyze.
		 * @return The fields affected by the vision point.
		 */
		private Field[] determineFields(VisionPoint point) {
			final Entity ent = area.entity;
			final Field root = ent.getStandingOn().getWorld().getFieldAt(point.hook.getBoardX(), point.hook.getBoardY());
			final World w = ent.getWorld();

			final LinkedList<Field> fields = new LinkedList<Field>();

			for(int i = 0; i < point.strength; i++) {

				if(i == 0) {
					fields.add(root);
				} else {

					for (int side = 0; side < 8; side++) {

						/*
						 describes the angle of the pivot; true == on x-axis, false == on y-axis
						 Meaning:

						 pivot_angle == true if:
						    pivot point is above or below root field
						 pivot_angle == false if:
						    pivot point is left or right of root field
						  */
						boolean pivot_angle = false;
						/*
						Describes the direction.

						if the pivot is on x-axis:
							if direction == true:
								direction is right
							else:
								direction is left
						else:
							if direction == true:
								direction is down
							else:
								direction is up
						 */
						boolean direction = true;

						// set up the pivot variable for use
						if (side == 0 || side == 3 || side == 4 || side == 7) {
							pivot_angle = true;

							if(side == 4 || side == 7) {
								direction = false;
							}
						} else {

							if (side == 1 || side == 6) {
								direction = false;
							}
						}

						// add field at pivot point to list
						// so that i dont cycle multiple times over the pivot field
						if(pivot_angle) {
							fields.add(w.getFieldAt(root.getBoardX(), root.getBoardY() + i));
							fields.add(w.getFieldAt(root.getBoardX(), root.getBoardY() - i));
						} else {
							fields.add(w.getFieldAt(root.getBoardX() + i, root.getBoardY()));
							fields.add(w.getFieldAt(root.getBoardX() - i, root.getBoardY()));
						}

						for (int it = 1; it < i; it++) {

							if(pivot_angle) {

								if (direction) {

									Field c = w.getFieldAt(root.getBoardX() + it, root.getBoardY() + i);
									if (!fields.contains(c)) {
										fields.add(c);
									}

									c = w.getFieldAt(root.getBoardX() + it, root.getBoardY() - i);
									if (!fields.contains(c)) {
										fields.add(c);
									}

								} else {

									Field c = w.getFieldAt(root.getBoardX() - it, root.getBoardY() + i);
									if (!fields.contains(c)) {
										fields.add(c);
									}

									c = w.getFieldAt(root.getBoardX() - it, root.getBoardY() - i);
									if (!fields.contains(c)) {
										fields.add(c);
									}

								}

							} else {

								if (direction) {

									Field c = w.getFieldAt(root.getBoardX() + i, root.getBoardY() + it);
									if (!fields.contains(c)) {
										fields.add(c);
									}

									c = w.getFieldAt(root.getBoardX() - i, root.getBoardY() + it);
									if (!fields.contains(c)) {
										fields.add(c);
									}

								} else {

									Field c = w.getFieldAt(root.getBoardX() + i, root.getBoardY() - it);
									if (!fields.contains(c)) {
										fields.add(c);
									}

									c = w.getFieldAt(root.getBoardX() - i, root.getBoardY() - it);
									if (!fields.contains(c)) {
										fields.add(c);
									}

								}

							}

						}

					}

				}


			} // END OF THE FREAKING FOR-LOOP
			return fields.toArray(new Field[0]);
		}

		/**
		 * Cycles through the vision entries and returns <code>true</code> if the
		 * board positionable object is already listed in the entries.
		 * @param positionable The The positionable board object.
		 * @return A boolean value indicating whether the positionable object is listed.
		 */
		boolean check(Field positionable) {
			for(VisionEntry entry : visionList) {
				if(entry.key == positionable) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Thrown when no double entries should be allowed.
		 */
		private class ExistingEntryException extends RuntimeException {
			public ExistingEntryException() {
				super("Entry already exists.");
			}
		}

		/**
		 * Injects the positionable to the vision system.
		 * @param positionable The positionable to inject.
		 */
		static void injectPositionable(Field positionable) {
			allFields.add(positionable);
			for(VisionData data : allData) {
				data.addEntry(positionable);
			}
		}



		/**
		 * Represents an entry for the vision system.
		 * The methods hashCode() and equals() are overriden.
		 */
		class VisionEntry implements Map.Entry<Field, VisionState> {

			private Field key;
			private VisionState value;
			private boolean cartographed = false;

			VisionEntry(Field key) {
				this.key = key;
				this.value = VisionState.UNREVEALED;
			}

			/**
			 * Returns the key corresponding to this entry.
			 *
			 * @return the key corresponding to this entry
			 * @throws IllegalStateException implementations may, but are not
			 *                               required to, throw this exception if the entry has been
			 *                               removed from the backing map.
			 */
			@Override
			public Field getKey() {
				return key;
			}

			/**
			 * Returns the value corresponding to this entry.  If the mapping
			 * has been removed from the backing map (by the iterator's
			 * <tt>remove</tt> operation), the results of this call are undefined.
			 *
			 * @return the value corresponding to this entry
			 * @throws IllegalStateException implementations may, but are not
			 *                               required to, throw this exception if the entry has been
			 *                               removed from the backing map.
			 */
			@Override
			public VisionState getValue() {
				return value;
			}

			/**
			 * Replaces the value corresponding to this entry with the specified
			 * value (optional operation).  (Writes through to the map.)  The
			 * behavior of this call is undefined if the mapping has already been
			 * removed from the map (by the iterator's <tt>remove</tt> operation).
			 *
			 * @param value new value to be stored in this entry
			 * @return old value corresponding to the entry
			 * @throws UnsupportedOperationException if the <tt>put</tt> operation
			 *                                       is not supported by the backing map
			 * @throws ClassCastException            if the class of the specified value
			 *                                       prevents it from being stored in the backing map
			 * @throws NullPointerException          if the backing map does not permit
			 *                                       null values, and the specified value is null
			 * @throws IllegalArgumentException      if some property of this value
			 *                                       prevents it from being stored in the backing map
			 * @throws IllegalStateException         implementations may, but are not
			 *                                       required to, throw this exception if the entry has been
			 *                                       removed from the backing map.
			 */
			@Override
			public VisionState setValue(VisionState value) {
				VisionState old = this.value;
				this.value = value;
				return old;
			}

			@Override
			public int hashCode() {
				return key.hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				return obj.getClass() == Field.class;
			}

			public boolean isCartographed() {
				return cartographed;
			}
		}

		static class VisionList extends ArrayList<VisionEntry> {

			/**
			 * The standard capacity of the list.
			 * TODO If more positionables are introduced later, I'll have to set a valueable number.
			 */
			private static final int STD_CAPACITY = 10;

			/**
			 * Constructs an empty list with the specified initial capacity.
			 *
			 * @throws IllegalArgumentException if the specified initial capacity
			 *                                  is negative
			 */
			public VisionList() {
				super(STD_CAPACITY);
			}

			/**
			 * Retrieves the index for the positionable object.
			 * @param positionable The positionable board object.
			 * @return The index of the positionable object.
			 * @throws java.util.NoSuchElementException if no entry can be determined with the parameter.
			 */
			public int indexOf(Field positionable) {
				for(int i = 0; i < size(); i++) {
					if(get(i).equals(positionable)) {
						return i;
					}
				}
				return Integer.MIN_VALUE;
			}

		}
	}
}

