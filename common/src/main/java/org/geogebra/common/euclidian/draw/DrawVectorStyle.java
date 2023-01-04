package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;

public interface DrawVectorStyle {
	void update(double[] coordsA, double[] coordsB, double[] coordsV, double lineThickness,
			GBasicStroke stroke);
	void draw(GGraphics2D g2);

	GShape getShape();

}
