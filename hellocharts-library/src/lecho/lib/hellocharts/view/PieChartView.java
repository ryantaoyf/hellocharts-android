package lecho.lib.hellocharts.view;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.PieChartDataProvider;
import lecho.lib.hellocharts.animation.PieChartRotationAnimator;
import lecho.lib.hellocharts.animation.PieChartRotationAnimatorV14;
import lecho.lib.hellocharts.animation.PieChartRotationAnimatorV8;
import lecho.lib.hellocharts.gesture.PieChartTouchHandler;
import lecho.lib.hellocharts.model.ArcValue;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.renderer.PieChartRenderer;
import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * PieChart is a little different than others charts. It doesn't have axes. It doesn't support viewport so changing
 * viewport wont work. Instead it support "Circle Oval". Pinch-to-Zoom and double tap zoom wont work either. Instead of
 * scroll there is chart rotation if isChartRotationEnabled is set to true. PieChart looks the best when it has the same
 * width and height, drawing chart on rectangle with proportions other than 1:1 will left some empty spaces.
 * 
 * @author Leszek Wach
 * 
 */
public class PieChartView extends AbstractChartView implements PieChartDataProvider {
	private static final String TAG = "PieChartView";
	protected PieChartData data;
	protected PieChartOnValueTouchListener onValueTouchListener = new DummyOnValueTouchListener();
	protected PieChartRenderer pieChartRenderer;
	protected PieChartRotationAnimator rotationAnimator;

	public PieChartView(Context context) {
		this(context, null, 0);
	}

	public PieChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PieChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		pieChartRenderer = new PieChartRenderer(context, this, this);
		chartRenderer = pieChartRenderer;
		touchHandler = new PieChartTouchHandler(context, this);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			this.rotationAnimator = new PieChartRotationAnimatorV8(this);
		} else {
			this.rotationAnimator = new PieChartRotationAnimatorV14(this);
		}
		setPieChartData(generateDummyData());
	}

	@Override
	public void setPieChartData(PieChartData data) {
		if (null == data) {
			this.data = generateDummyData();
		} else {
			this.data = data;
		}
		// TODO calculateContentArea is not needed here.
		chartCalculator.calculateContentArea(getWidth(), getHeight(), getPaddingLeft(), getPaddingTop(),
				getPaddingRight(), getPaddingBottom());
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		chartRenderer.initDataAttributes();
		axesRenderer.initAxesAttributes();

		ViewCompat.postInvalidateOnAnimation(PieChartView.this);
	}

	@Override
	public PieChartData getPieChartData() {
		return data;
	}

	@Override
	public ChartData getChartData() {
		return data;
	}

	@Override
	public void callChartTouchListener(SelectedValue selectedValue) {
		ArcValue arcValue = data.getValues().get(selectedValue.firstIndex);
		onValueTouchListener.onValueTouched(selectedValue.firstIndex, arcValue);
	}

	public PieChartOnValueTouchListener getOnValueTouchListener() {
		return onValueTouchListener;
	}

	public void setOnValueTouchListener(PieChartOnValueTouchListener touchListener) {
		if (null == touchListener) {
			this.onValueTouchListener = new DummyOnValueTouchListener();
		} else {
			this.onValueTouchListener = touchListener;
		}
	}

	@Override
	public void animationDataUpdate(float scale) {
		for (ArcValue arcValue : data.getValues()) {
			arcValue.update(scale);
		}
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public void animationDataFinished(boolean isFinishedSuccess) {
		for (ArcValue arcValue : data.getValues()) {
			arcValue.finish(true);
		}
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	/**
	 * Returns rectangle that will constraint pie chart area.
	 * 
	 * @return
	 */
	public RectF getCircleOval() {
		return pieChartRenderer.getCircleOval();
	}

	/**
	 * Use this to change pie chart area. Because by default CircleOval is calculated onSizeChanged() you must call this
	 * method after size of PieChartView is calculated to make it works.
	 * 
	 * @param orginCircleOval
	 */
	public void setCircleOval(RectF orginCircleOval) {
		pieChartRenderer.setCircleOval(orginCircleOval);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	/**
	 * Returns pie chart rotation, 0 rotation means that 0 degrees is at 3 o'clock. Don't confuse with
	 * {@link View#getRotation()}.
	 * 
	 * @return
	 */
	public float getChartRotation() {
		return pieChartRenderer.getChartRotation();
	}

	/**
	 * Set pie chart rotation. Don't confuse with {@link View#getRotation()}.
	 * 
	 * @param rotation
	 * 
	 * @see #getChartRotation()
	 */
	public void setChartRotation(float rotation, boolean isAnimated) {
		if (isAnimated) {
			rotationAnimator.cancelAnimation();
			rotationAnimator.startAnimation(pieChartRenderer.getChartRotation(), rotation);
		} else {
			pieChartRenderer.setChartRotation(rotation);
		}
		ViewCompat.postInvalidateOnAnimation(this);
	}

	public boolean isChartRotationEnabled() {
		if (touchHandler instanceof PieChartTouchHandler) {
			return ((PieChartTouchHandler) touchHandler).isRotationEnabled();
		} else {
			return false;
		}
	}

	/**
	 * Set false if you don't wont the chart to be rotated by touch gesture. Rotating programmatically will still work.
	 * 
	 * @param isRotationEnabled
	 */
	public void setChartRotationEnabled(boolean isRotationEnabled) {
		if (touchHandler instanceof PieChartTouchHandler) {
			((PieChartTouchHandler) touchHandler).setRotationEnabled(isRotationEnabled);
		}
	}

	protected PieChartData generateDummyData() {
		final int numValues = 4;
		PieChartData data = new PieChartData();
		List<ArcValue> values = new ArrayList<ArcValue>(numValues);
		values.add(new ArcValue(40f));
		values.add(new ArcValue(20f));
		values.add(new ArcValue(30f));
		values.add(new ArcValue(50f));
		data.setValues(values);
		return data;
	}

	public interface PieChartOnValueTouchListener {
		public void onValueTouched(int selectedArc, ArcValue value);
	}

	private static class DummyOnValueTouchListener implements PieChartOnValueTouchListener {

		@Override
		public void onValueTouched(int selectedArc, ArcValue value) {
			// do nothing
		}
	}
}
