package com.lowdragmc.lowdraglib.gui.compass.component;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lowdragmc.lowdraglib.gui.compass.CompassManager;
import com.lowdragmc.lowdraglib.gui.compass.ILayoutComponent;
import com.lowdragmc.lowdraglib.gui.compass.LayoutPageWidget;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.lowdragmc.lowdraglib.utils.XmlUtils;

import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

/**
 * @author BlackDragon2447
 * @date 2023/12/23
 * @implNote TableComponent
 */
@NoArgsConstructor
public class TableComponent extends AbstractComponent {
	protected List<List<TableCell>> cells = new ArrayList<>();

	@Override
	public ILayoutComponent fromXml(Element element) {
		super.fromXml(element);
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Element nodeElement) {
				if (nodeElement.getNodeName() == "tr") {
					this.processRow(nodeElement, i);
				}

			}

		}
		return this;
	}

	private void processRow(Element element, int idx) {
		cells.add(new ArrayList<>());
		NodeList rowList = element.getChildNodes();
		for (int j = 0; j < rowList.getLength(); j++) {
			Node cellNode = rowList.item(j);
			if (cellNode instanceof Element cellElement) {
				if (cellElement.getNodeName() == "td") {
					cells.get(idx).add(new TableCell(element));
				}
			}
		}

	}

	@Override
	@Environment(EnvType.CLIENT)
	protected LayoutPageWidget addWidgets(LayoutPageWidget currentPage) {

		var table = new WidgetGroup(new Position(0, 0));

		var cells = buildWigets();
		int collums = 1;
		for (var row : cells) {
			collums = Math.max(collums, row.size());
		}
		int cellMaxWidth = width(currentPage) / collums;

		int height = 0;

		for (int i = 0; i < cells.size(); i++) {
			var row = new WidgetGroup(new Position(0, height));
			int rowWidth = 0;
			for (int j = 0; j < cells.get(i).size(); j++) {
				var cell = cells.get(i).get(j);
				cell.setMaxWidthLimit(cellMaxWidth);
				cell.setSelfPosition(new Position(height, rowWidth));
				rowWidth += Math.min(cell.toRectangleBox().getWidth(), cellMaxWidth);
				row.addWidget(cell);
			}
			height += row.toRectangleBox().getHeight();
		}

		return currentPage.addStreamWidget(wrapper(table));

	}

	private List<List<ComponentPanelWidget>> buildWigets() {
		List<List<ComponentPanelWidget>> builtCells = new ArrayList<>();
		for (var row : this.cells) {
			List<ComponentPanelWidget> builtRow = new ArrayList<>();
			for (var cell : row) {
				ComponentPanelWidget panel = new ComponentPanelWidget(0, 0, cell.components) {
					@Override
					public void updateComponentTextSize() {
						var fontRenderer = Minecraft.getInstance().font;
						int totalHeight = cacheLines.size() * (fontRenderer.lineHeight + space);
						if (totalHeight > 0) {
							totalHeight -= space;
							totalHeight += 2;
						}
						setSize(new Size(maxWidthLimit, totalHeight));
					}
				}.clickHandler(CompassManager::onComponentClick);
				builtRow.add(panel);
			}
			builtCells.add(builtRow);
		}
		return builtCells;
	}

	public static class TableCell {
		protected List<Component> components = new ArrayList<>();

		public TableCell(Element element) {
			components.addAll(XmlUtils.getComponents(element, Style.EMPTY));
		}

	}

}
