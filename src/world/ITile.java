package world;

import entity.Entity;
import misc.metadata.IMetadatable;

import java.awt.*;
import java.util.List;

/**
 * @author Josip
 */
public interface ITile extends IBaseTile {

	List<? extends Entity> getEntities();

}
