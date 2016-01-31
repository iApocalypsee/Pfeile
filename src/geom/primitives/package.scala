package geom

/**
	* Created by philip on 31.01.16.
	*/
package object primitives {
	var graphics: java.awt.Graphics2D = null
	var worldMatrix: Matrix = Matrix.newIdentity           //Describes the transformation from object space to world space (is assigned for each object)
	var cameraMatrix: Matrix = Matrix.newIdentity          //Describes the transformation from world space to camera space (e.g. view port, translation)
	var projectionMatrix: Matrix = Matrix.newIdentity      //Describes the transformation from camera space to the screen (e.g. perspective (haha...), scaling, zoom, and so on)
}
