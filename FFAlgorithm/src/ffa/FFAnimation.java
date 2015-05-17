package ffa;

import br.com.etyllica.animation.AnimationHandler;
import br.com.etyllica.animation.scripts.FadeInAnimation;
import br.com.etyllica.animation.scripts.FadeOutAnimation;
import br.com.etyllica.animation.scripts.OpacityAnimation;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.layer.TextLayer;

public class FFAnimation {

	private TextLayer hitLayer = new TextLayer("HIT");
	private TextLayer missLayer = new TextLayer("MISS");
	
	public FFAnimation() {
		super();
	}

	public void draw(Graphic g) {
		hitLayer.draw(g);
		missLayer.draw(g);
	}
	
	public void animateCacheHit(int requestX, int requestY) {
		hitLayer.setCoordinates(requestX,requestY-20);
		
		FadeInAnimation animation = new FadeInAnimation(hitLayer, 0, 2000);
		animation.setNext(new FadeOutAnimation(hitLayer, 0, 2000));
		
		AnimationHandler.getInstance().add(animation);
	}
	
	public void animateCacheMiss(int requestX, int requestY) {
		missLayer.setCoordinates(requestX,requestY-20);

		FadeInAnimation animation = new FadeInAnimation(missLayer, 0, 2000);
		animation.setNext(new FadeOutAnimation(missLayer, 0, 2000));
		
		AnimationHandler.getInstance().add(animation);
	}
	
}
