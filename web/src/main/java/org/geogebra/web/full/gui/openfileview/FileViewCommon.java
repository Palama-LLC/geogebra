package org.geogebra.web.full.gui.openfileview;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.HeaderView;
import org.geogebra.web.full.gui.MessagePanel;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileViewCommon extends MyHeaderPanel {

	/**
	 * application
	 */
	protected AppW app;
	private final String title;
	// header
	private HeaderView headerView;

	// content panel
	private FlowPanel contentPanel;
	// material panel
	private FlowPanel materialPanel;
	private FlowPanel infoPanel;
	private MessagePanel messagePanel;
	private final LocalizationW loc;

	public FileViewCommon(AppW app, String title) {
		this.app = app;
		loc = app.getLocalization();
		this.title = title;
		initGUI();
	}

	private void initGUI() {
		this.setStyleName("openFileView");
		initHeader();
		initContentPanel();
		initMaterialPanel();
		setLabels();
	}

	private void initMaterialPanel() {
		materialPanel = new FlowPanel();
		materialPanel.addStyleName("temporaryMaterialPanel");
	}

	private void initHeader() {
		headerView = new HeaderView();
		headerView.setCaption(title);
		StandardButton backButton = headerView.getBackButton();
		backButton.addFastClickHandler(source -> close());

		this.setHeaderWidget(headerView);
	}

	private void initContentPanel() {
		contentPanel = new FlowPanel();
		contentPanel.setStyleName("fileViewContentPanel");
		this.setContentWidget(contentPanel);
	}

	/**
	 * adds content if available
	 */
	public void addContent() {
		contentPanel.clear();
		//contentPanel.add(sortDropDown);
		contentPanel.add(materialPanel);
	}

	/**
	 * Clear contents and infopanel
	 */
	public void clearPanels() {
		if (contentPanel != null) {
			contentPanel.clear();
		}
		if (infoPanel != null) {
			infoPanel.clear();
		}
	}

	@Override
	public void setLabels() {
		headerView.setCaption(localize(title));
//		if (sortDropDown != null) {
//			sortDropDown.setItemText(0, localize("SortBy"));
//			for (int i = 0; i < map.length; i++) {
//				sortDropDown.setItemText(i + 1, localize(labelFor(map[i])));
//			}
//		}
		for (int i = 0; i < materialPanel.getWidgetCount(); i++) {
			Widget widget = materialPanel.getWidget(i);
			if (widget instanceof MaterialCard) {
				((MaterialCard) widget).setLabels();
			}
		}
		if (messagePanel != null) {
			setMessagePanelLabels(messagePanel);
		}
	}

	@Override
	public AppW getApp() {
		return null;
	}

	@Override
	public void resizeTo(int width, int height) {

	}

	/**
	 * adds MaterialPanel
	 */
	void addMaterialPanel() {
		contentPanel.add(materialPanel);
	}

	public void clearMaterials() {
		materialPanel.clear();
	}

	void showEmptyListNotification() {
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("emptyMaterialListInfo");

		messagePanel = createMessagePanel();
		infoPanel.add(messagePanel);

		contentPanel.clear();
		contentPanel.add(infoPanel);
	}

	private MessagePanel createMessagePanel() {
		MessagePanel panel = new MessagePanel();
		panel.setImageUri(MaterialDesignResources.INSTANCE.mow_lightbulb());
		setMessagePanelLabels(panel);
		return panel;
	}

	private void setMessagePanelLabels(MessagePanel messagePanel) {
		messagePanel.setPanelTitle(localize("emptyMaterialList.caption.mow"));
		messagePanel.setPanelMessage(localize("emptyMaterialList.info.mow"));
	}

	String localize(String key) {
		return loc.getMenu(key);
	}

	/**
	 *
	 * @param card to add.
	 */
	public void addMaterialCard(Widget card) {
		materialPanel.add(card);
	}
}
