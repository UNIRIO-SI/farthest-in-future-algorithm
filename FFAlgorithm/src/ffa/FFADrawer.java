package ffa;

import java.util.List;

import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.graphics.SVGColor;

public class FFADrawer {

	private Debugger debugger;
	
	private FFAModel ffa;
	
	public int highlightDist = -1;
	public int highlightCache = -1;
	public int division;
	public int requestX;
	public int requestY;
	
	private static final int CELL_WIDTH = 42;
	private static final int CELL_HEIGHT = 60;
	
	private FFAnimation ffAnimation;
	
	public FFADrawer(FFAModel ffa, Debugger debugger) {
		super();
		this.ffa = ffa;
		this.debugger = debugger;
		ffAnimation = new FFAnimation();
	}
	
	public void drawVariables(Graphic g) {
		
		int vx = requestX+30;
		
		g.drawString("i = "+Integer.toString(ffa.i), vx, 46);

		if(!ffa.end) {
			g.drawString("r[i] = "+Integer.toString(ffa.request[ffa.i]), vx, 66);
		}

		if(debugger.getLine() >= 9) {
			g.drawString("furthest = "+Integer.toString(ffa.furthest), vx, 86);
		}
		if(debugger.getLine() >= 10) {
			g.drawString("j = "+Integer.toString(ffa.j), vx, 106);
		}
		if(debugger.getLine() >= 11) {
			g.drawString("p = "+Integer.toString(ffa.p), vx, 126);
		}

	}
	
	public void drawRequest(Graphic g, int x, int y, int[] request) {
		//Draw request cells
		for(int r = 0; r < request.length; r++) {
			if(r == ffa.i) {
				g.setColor(SVGColor.GAINSBORO);
				g.fillRect(x+CELL_WIDTH*r, y, CELL_WIDTH, CELL_HEIGHT);
				g.setColor(SVGColor.BLACK);
			}
			g.drawRect(x+CELL_WIDTH*r, y, CELL_WIDTH, CELL_HEIGHT);
			g.drawString(x+CELL_WIDTH*r, y, CELL_WIDTH, CELL_HEIGHT, Integer.toString(request[r]));					
		}

		int w = request.length*CELL_WIDTH;
		g.drawString(x, y-10, w, 0, "Request");
	}
	
	public void drawCache(Graphic g, int x, int y, List<Integer> cache, String label) {

		//Draw cache cells
		for(int i=0; i < ffa.K; i++) {
			g.setColor(SVGColor.BEIGE);
			if(highlightCache == i) {
				g.fillRect(x+CELL_WIDTH*i, y, CELL_WIDTH, CELL_HEIGHT);	
			}
			g.setColor(SVGColor.BLACK);
			g.drawRect(x+CELL_WIDTH*i, y, CELL_WIDTH, CELL_HEIGHT);
			if(i < ffa.cacheUse) {
				g.drawString(x+CELL_WIDTH*i, y, CELL_WIDTH, CELL_HEIGHT, Integer.toString(cache.get(i)));
			}
		}

		int w = ffa.K*CELL_WIDTH;
		g.drawString(x, y-10, w, 0, label);

	}
	
	public void drawDist(Graphic g, int x, int y, List<Integer> cache, String label) {

		//Draw cache cells
		for(int i=0; i<cache.size(); i++) {
			if(highlightDist == i) {
				g.setColor(SVGColor.BEIGE);
				g.fillRect(x+CELL_WIDTH*i, y, CELL_WIDTH, CELL_HEIGHT);
				g.setColor(SVGColor.BLACK);
			}
			g.drawRect(x+CELL_WIDTH*i, y, CELL_WIDTH, CELL_HEIGHT);
			g.drawString(x+CELL_WIDTH*i, y, CELL_WIDTH, CELL_HEIGHT, Integer.toString(cache.get(i)));
		}

		int w = cache.size()*CELL_WIDTH;
		g.drawString(x, y-10, w, 0, label);

	}

	public void drawAnimation(Graphic g) {
		ffAnimation.draw(g);
	}
	
	public void animateCacheHit() {
		ffAnimation.animateCacheHit(requestX+CELL_WIDTH*ffa.i, requestY);
	}
	
	public void animateCacheMiss() {
		ffAnimation.animateCacheMiss(requestX+CELL_WIDTH*ffa.i, requestY);
	}
}
