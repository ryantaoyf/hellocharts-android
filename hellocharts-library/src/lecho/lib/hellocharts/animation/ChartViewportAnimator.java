package lecho.lib.hellocharts.animation;

import lecho.lib.hellocharts.model.Viewport;

public interface ChartViewportAnimator {

	public static final int FAST_ANIMATION_DURATION = 200;

	public void startAnimation(Viewport startViewport, Viewport targetViewport);

	public void cancelAnimation();

	public boolean isAnimationStarted();

	public void setChartAnimationListener(ChartAnimationListener animationListener);

}
