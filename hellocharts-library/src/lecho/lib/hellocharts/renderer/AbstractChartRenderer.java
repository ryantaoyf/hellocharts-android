package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.Utils;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Typeface;

public abstract class AbstractChartRenderer implements ChartRenderer {
	public int DEFAULT_LABEL_MARGIN_DP = 4;
	protected Chart chart;
	protected Paint labelPaint = new Paint();
	protected FontMetricsInt fontMetrics = new FontMetricsInt();
	protected Viewport tempMaxViewport = new Viewport();

	protected float density;
	protected float scaledDensity;

	protected SelectedValue selectedValue = new SelectedValue();
	protected SelectedValue oldSelectedValue = new SelectedValue();

	protected char[] labelBuffer = new char[32];
	protected int labelOffset;
	protected int labelMargin;

	public AbstractChartRenderer(Context context, Chart chart) {
		this.density = context.getResources().getDisplayMetrics().density;
		this.scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		this.chart = chart;

		labelMargin = Utils.dp2px(density, DEFAULT_LABEL_MARGIN_DP);
		labelOffset = labelMargin;

		labelPaint.setAntiAlias(true);
		labelPaint.setStyle(Paint.Style.FILL);
		labelPaint.setTextAlign(Align.LEFT);
		labelPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		labelPaint.setColor(Color.WHITE);
	}

	public void initCurrentViewport() {
		ChartCalculator chartCalculator = chart.getChartCalculator();
		chartCalculator.setCurrentViewport(chartCalculator.getMaximumViewport());
	}

	@Override
	public boolean isTouched() {
		return selectedValue.isSet();
	}

	@Override
	public void clearTouch() {
		selectedValue.clear();
		oldSelectedValue.clear();

	}

	@Override
	public void callChartTouchListener() {
		chart.callChartTouchListener(selectedValue);
	}

	@Override
	public void setMaxViewport(Viewport maxViewport) {
		if (null == maxViewport) {
			initMaxViewport();
		} else {
			this.tempMaxViewport.set(maxViewport);
			chart.getChartCalculator().setMaxViewport(maxViewport);
		}
	}

	@Override
	public Viewport getMaxViewport() {
		return tempMaxViewport;
	}

	@Override
	public void setViewport(Viewport viewport) {
		if (null == viewport) {
			initCurrentViewport();
		} else {
			chart.getChartCalculator().setCurrentViewport(viewport);
		}
	}

	@Override
	public Viewport getViewport() {
		return chart.getChartCalculator().getCurrentViewport();
	}

	@Override
	public void selectValue(SelectedValue selectedValue) {
		this.selectedValue.set(selectedValue);
	}

	@Override
	public SelectedValue getSelectedValue() {
		return selectedValue;
	}
}
