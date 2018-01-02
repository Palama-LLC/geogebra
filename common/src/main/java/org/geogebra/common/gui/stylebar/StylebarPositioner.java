package org.geogebra.common.gui.stylebar;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawLine;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.util.Rectangle;

/**
 * dynamic stylebar positioner logic, also used for preview point popup
 *
 */
public class StylebarPositioner {

    private static final int MARGIN = 4;
    private final App app;
	/**
	 * euclidian view
	 */
    protected final EuclidianView euclidianView;
    private final SelectionManager selectionManager;
	private boolean center;

    /**
     * @param app
     *              The instance of the App class.
     */
	public StylebarPositioner(App app) {
        this.app = app;
        euclidianView = app.getActiveEuclidianView();
        selectionManager = app.getSelectionManager();
    }

	/**
	 * @param center
	 *            true if should be center positioned
	 */
	public void setCenter(boolean center) {
		this.center = center;
	}

    private boolean hasVisibleGeos(List<GeoElement> geoList) {
        for (GeoElement geo : geoList) {
            if (geo.isVisibleInView(euclidianView.getViewID())
                    && geo.isEuclidianVisible()
                    && !geo.isAxis()) {
                return true;
            }
        }
        return false;
    }

    private List<GeoElement> createActiveGeoList() {
        List<GeoElement> selectedGeos = selectionManager.getSelectedGeos();
        List<GeoElement> justCreatedGeos = euclidianView.getEuclidianController().getJustCreatedGeos();
        if (hasVisibleGeos(selectedGeos) || hasVisibleGeos(justCreatedGeos)) {
            selectedGeos.addAll(justCreatedGeos);
            return selectedGeos;
        }
        return Collections.emptyList();
    }

    @SuppressWarnings({"MethodWithTooManyParameters", "OverlyComplexMethod", "OverlyLongMethod", "ReturnOfNull"})
    private GPoint getStylebarPositionForDrawable(
            GRectangle2D gRectangle2D,
            boolean hasBoundingBox,
            boolean isPoint,
            boolean isFunction,
            int popupHeight, int popupWidth,
            Rectangle canvasRect) {
        if (gRectangle2D == null) {
            if (!isFunction || isPoint) {
                return null;
            }
        }

        int minXPosition = canvasRect.getMinX();
        int maxXPosition = canvasRect.getMaxX();
        int minYPosition = canvasRect.getMinY();
        int maxYPosition = canvasRect.getMaxY();

		// final int BOTTOM_MARGIN = 7 * MARGIN;
        double top;

        if (isFunction) {
            GPoint mouseLoc = euclidianView.getEuclidianController()
                    .getMouseLoc();
            if (mouseLoc == null) {
                return null;
            }
            top = mouseLoc.y + MARGIN;
        } else if (isPoint) {
			top = gRectangle2D.getMaxY() /* + MARGIN */;
        } else {
            if (hasBoundingBox) {
				top = gRectangle2D.getMinY()
						- popupHeight /*- BOTTOM_MARGIN*/;
            } else {
                top = gRectangle2D.getMinY();
            }
        }

        if (top < minYPosition) {
			top = (gRectangle2D != null ? gRectangle2D.getMaxY()
					: 0) /* + MARGIN */;
        }

        if (top > maxYPosition) {
			if (isPoint) {
				top = gRectangle2D.getMinY() - popupHeight /*- BOTTOM_MARGIN*/;
            } else {
                top = maxYPosition;
            }
        }

        double left;
        if (isFunction) {
            left = euclidianView.getEuclidianController().getMouseLoc().x + MARGIN;
        } else {
            if (isPoint) {
                left = center
                        ? (gRectangle2D.getMaxX() + gRectangle2D.getMinX()) / 2 - ((float) popupWidth / 2)
                        : gRectangle2D.getMaxX();
            } else {
                left = gRectangle2D.getMaxX();
            }
        }

        left = left < minXPosition ? minXPosition : left;
        left = left > maxXPosition ? maxXPosition : left;
        return new GPoint((int) left, (int) top);
    }

    /**
     * Calculates the position of the dynamic stylebar on the EuclidianView
     * @param stylebarHeight
     *                          The height of the stylebar.
     * @param minYPosition
     *                          The minimum y position on the canvas for the top of the stylebar.
     * @param maxYPosition
     *                          The maximum y position on the canvas for the top of the stylebar.
     *                          The top of the stylebar is allowed to be at this position,
     *                          so if the entire stylebar should be on the canvas
     *                          then the height of the stylebar should be already subtracted from this value.
     * @return
     *          Returns a GPoint which contains the x and y coordinates for the top of the stylebar.
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public GPoint getPositionOnCanvas(int stylebarHeight, int minYPosition, int maxYPosition) {
        return getPositionOnCanvas(
                stylebarHeight, 0,
                new Rectangle(0, minYPosition, Integer.MAX_VALUE, maxYPosition));
    }

    /**
     * Calculates the position of the dynamic stylebar on the EuclidianView
     * @param stylebarHeight
     *                          The height of the stylebar.
     * @param minYPosition
     *                          The minimum y position on the canvas for the top of the stylebar.
     * @param maxYPosition
     *                          The maximum y position on the canvas for the top of the stylebar.
     *                          The top of the stylebar is allowed to be at this position,
     *                          so if the entire stylebar should be on the canvas
     *                          then the height of the stylebar should be already subtracted from this value.
     * @param minXPosition
     *                          The minimum x position on the canvas for the left end of the stylebar.
     * @param maxXPosition
     *                          The maximum x position on the canvas for the left end of the stylebar.
     *                          The left end of the stylebar is allowed to be on this position,
     *                          so if the entire stylebar should be on the canvas
     *                          then the width of the stylebar should be already subtracted from this value.
     * @return
     *          Returns a GPoint which contains the x and y coordinates for the top of the stylebar.
     */
    @SuppressWarnings({"WeakerAccess", "SameParameterValue", "unused"})
    public GPoint getPositionOnCanvas(
            int stylebarHeight,
            int minYPosition, int maxYPosition,
            int minXPosition, int maxXPosition) {
        return getPositionOnCanvas(
                stylebarHeight, 0,
                new Rectangle(minXPosition, minYPosition, maxXPosition, maxYPosition));
    }

    /**
     * Calculates the position of the dynamic stylebar on the EuclidianView
     * @param stylebarHeight
     *                          The height of the stylebar.
     * @param stylebarWidth
     *                          The width of the stylebar.
     * @param canvasRect
     *                          The rectangle on the euclidian view where the top-left corner of the stylebar is allowed to be.
     *                          If the whole stylebar should be on the euclidian view
     *                          then the width and the height of the stylebar should be already subtracted from the rectangle's dimensions.
     * @return
     *          Returns a GPoint which contains the x and y coordinates for the top of the stylebar.
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public GPoint getPositionOnCanvas(
            int stylebarHeight, int stylebarWidth,
            Rectangle canvasRect) {
        List<GeoElement> activeGeoList = createActiveGeoList();
        if (activeGeoList.isEmpty()) {
            return null;
        }

        GeoElement selectedPreviewPoint = getSelectedPreviewPoint();
        if (selectedPreviewPoint != null) {
            return getPositionFor(selectedPreviewPoint, stylebarHeight, stylebarWidth, canvasRect);
        }

        if (app.has(Feature.DYNAMIC_STYLEBAR_SELECTION_TOOL)
                && app.getMode() == EuclidianConstants.MODE_SELECT) {
            if (!app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR)) {
                return getPositionForSelection(stylebarHeight, stylebarWidth, canvasRect);
            }
        }

        GeoElement geo = activeGeoList.get(0);
        if (geo.isEuclidianVisible()) {
            if (app.has(Feature.FUNCTIONS_DYNAMIC_STYLEBAR_POSITION)
                    && geo instanceof GeoFunction) {
                return getPositionForFunction(
                        geo,
                        stylebarHeight, stylebarWidth,
                        canvasRect);
            }
			return getPositionFor(geo, stylebarHeight, stylebarWidth,
					canvasRect);
		}
        return null;
    }

	/**
	 * @param activeGeoList
	 *            selected geos
	 * @param stylebarHeight
	 *            height of stylebar
	 * @param minYPosition
	 *            min y pos of popup
	 * @param maxYPosition
	 *            max y pos of popup
	 * @param minXPosition
	 *            min x pos of popup
	 * @param maxXPosition
	 *            max x pos of popup
	 * @return position of popup
	 */
    @Deprecated
    @SuppressWarnings({"unused", "MethodWithTooManyParameters", "deprecation", "ReturnOfNull"})
    public GPoint getPositionFor(List<GeoElement> activeGeoList,
                                 int stylebarHeight,
                                 int minYPosition, int maxYPosition,
                                 int minXPosition, int maxXPosition) {
        if (activeGeoList != null && !activeGeoList.isEmpty()) {
            return getPositionFor(
                    activeGeoList.get(0),
                    stylebarHeight, 0,
                    new Rectangle(minXPosition, minYPosition, maxXPosition, maxYPosition));
        }
        return null;
    }

    private GeoElement getSelectedPreviewPoint() {
        List<GeoElement> visiblePreviewPoints = app.getSpecialPointsManager().getSelectedPreviewPoints();
        if (visiblePreviewPoints != null && !visiblePreviewPoints.isEmpty()) {
            for (GeoElement previewPoint : visiblePreviewPoints) {
                if (euclidianView.getHits().contains(previewPoint)) {
                    return previewPoint;
                }
            }
        }
        return null;
    }

	/**
	 * @param geo
	 *            geoElement
	 * @param stylebarHeight
	 *            height of stylebar
	 * @param stylebarWidth
	 *            width of stylebar
	 * @param canvasRect
	 *            canvas
	 * @return position
	 */
    @SuppressWarnings("WeakerAccess")
    public GPoint getPositionFor(GeoElement geo, int stylebarHeight, int stylebarWidth, Rectangle canvasRect) {
        Drawable dr = (Drawable) euclidianView.getDrawableND(geo);
        if (dr != null) {
            return getStylebarPositionForDrawable(
                    dr.getBoundsForStylebarPosition(),
                    !(dr instanceof DrawLine),
                    dr instanceof DrawPoint,
                    false,
                    stylebarHeight, stylebarWidth,
                    canvasRect);
        }
        //noinspection ReturnOfNull
        return null;
    }

    private GPoint getPositionForFunction (
            GeoElement geo,
            int stylebarHeight, int stylebarWidth,
            Rectangle canvasRect) {
        if (euclidianView.getHits().contains(geo)) {
            GPoint position = getStylebarPositionForDrawable(
                    null,
                    true,
                    false,
                    true,
                    stylebarHeight, stylebarWidth,
                    canvasRect);
            if (position != null) {
                return position;
            }
        } else {
            // with select tool, it happens that first selected geo is a function, and then
            // the user select another geo (e.g. a point). Then we still want to show style bar.
            if (app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR) && app.getMode() == EuclidianConstants.MODE_SELECT) {
                Drawable dr = (Drawable) euclidianView.getDrawableND(geo);
                if (dr != null) {
                    GPoint position = getStylebarPositionForDrawable(
                            dr.getBoundsForStylebarPosition(),
                            !(dr instanceof DrawLine),
                            false,
                            true,
                            stylebarHeight, stylebarWidth,
                            canvasRect);
                    if (position != null) {
                        return position;
                    }
                }
            }
        }
        return null;
    }

    private GPoint getPositionForSelection(
            int stylebarHeight, int stylebarWidth,
            Rectangle canvasRect) {
        GRectangle selectionRectangle = euclidianView.getSelectionRectangle();
        if (selectionRectangle != null) {
            return getStylebarPositionForDrawable(
                    selectionRectangle,
                    true,
                    false,
                    false,
                    stylebarHeight, stylebarWidth,
                    canvasRect);
        }
        return null;
    }
}
