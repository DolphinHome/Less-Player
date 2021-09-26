package xyz.less.graphic.visualization;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import xyz.less.graphic.Guis;

public class RectangleSpectrum extends HBox {
	private List<Rectangle> rectList;
	private int rectNums;
	private int index = 0;
	private double rectMinHeight = 1;
	
	public RectangleSpectrum(int rectNums) {
		this.rectNums = rectNums;
		rectList = new ArrayList<>(rectNums);
		initGraph();
	}

	private void initGraph() {
		Guis.addStyleClass("rectangle-spectrum", this);
		for(int i = 0; i < rectNums; i++) {
			Rectangle rect = new Rectangle(0, 1);
			Guis.addStyleClass("sp-rectangle", rect);
			rectList.add(rect);
		}
		Guis.addChildren(this, rectList);
	}
	
	public void updateGraph(double timestamp, double duration, 
			float[] magnitudes, float[] phases) {
		index = 0;
		double spacing = getSpacing() + 0.65;
		double width = (getWidth() - getPaddingX()) / rectNums - spacing;
		rectList.forEach(rect -> {
			double percent = getMagnitudePercent(magnitudes[index++]);
			double height = (getHeight() - getPaddingY()) * percent;
			height = height > rectMinHeight ? height : rectMinHeight; 
			rect.setWidth(width);
			rect.setHeight(height);
		});
	}

	private double getPaddingX() {
		return getPadding().getLeft() + getPadding().getRight();
	}
	
	private double getPaddingY() {
		return getPadding().getTop() + getPadding().getBottom();
	}

	private double getMagnitudePercent(float magnitude) {
		return 1 - Math.abs(magnitude) / 60D;
	}
	
	public void setRectangleMinHeight(double value) {
		this.rectMinHeight = value > 1 ? value : 1; 
	}
	
}