package org.geogebra.common.kernel.geos;

import org.geogebra.common.euclidian.draw.ArrowVectorShape;
import org.geogebra.common.euclidian.draw.DefaultVectorShape;
import org.geogebra.common.euclidian.draw.DrawVectorModel;
import org.geogebra.common.euclidian.draw.VectorShape;

public enum VectorHeadStyle {
	DEFAULT {
		@Override
		public VectorShape createShape(DrawVectorModel properties) {
			return new DefaultVectorShape(properties);
		}
	}, ARROW {
		@Override
		public VectorShape createShape(DrawVectorModel properties) {
			return new ArrowVectorShape(properties);
		}
	};

	public abstract VectorShape createShape(DrawVectorModel properties);
}
