package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

public class ColumnChartData extends AbstractChartData {
	public static final float DEFAULT_FILL_RATIO = 0.75f;
	private List<Column> columns = new ArrayList<Column>();
	private boolean isStacked = false;
	private float fillRatio = DEFAULT_FILL_RATIO;

	public ColumnChartData() {
	}

	public ColumnChartData(List<Column> columns) {
		setColumns(columns);
	}

	public List<Column> getColumns() {
		return columns;
	}

	public ColumnChartData setColumns(List<Column> columns) {
		if (null == columns) {
			this.columns = new ArrayList<Column>();
		} else {
			this.columns = columns;
		}
		return this;
	}

	public boolean isStacked() {
		return isStacked;
	}

	public ColumnChartData setStacked(boolean isStacked) {
		this.isStacked = isStacked;
		return this;
	}

	public float getFillRatio() {
		return fillRatio;
	}

	public ColumnChartData setFillRatio(float fillRatio) {
		if (fillRatio < 0) {
			fillRatio = 0;
		}
		if (fillRatio > 1) {
			fillRatio = 1;
		}
		this.fillRatio = fillRatio;
		return this;
	}
}
