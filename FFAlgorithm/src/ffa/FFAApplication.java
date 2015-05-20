package ffa;

import java.awt.Color;

import br.com.etyllica.context.Application;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.graphics.SVGColor;

public class FFAApplication extends Application {

	private int[] cache;
	private int cacheUse = 0;
	private int maxDist = 0;
	private int furthest = 0;

	private int[] request;
	private int requestX;
	private int requestY = 160;

	private static final int K = 3;

	private static final int CELL_WIDTH = 32;
	private static final int CELL_HEIGHT = 48;

	private static final int N = 5;

	private int i = 0;
	private int j = 1;
	private int l = 0;

	private Debugger debugger;
	private FFAnimation ffAnimation;

	private boolean end = false;

	public FFAApplication(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {

		String[] pseudoCode = buildPseudoCode();

		debugger = new Debugger(w, h, pseudoCode);

		ffAnimation = new FFAnimation();

		cache = new int[K];

		request = new int[N];
		request[0] = 40;
		request[1] = 3;
		request[2] = 3;
		request[3] = 8;
		request[4] = 10;

		requestX = w/2+100;

		loading = 100;
	}

	private void putInCache(int value) {
		if(cacheUse < cache.length) {
			cache[cacheUse] = value;
			cacheUse++;
		}
	}

	@Override
	public void draw(Graphic g) {

		g.setColor(Color.BLACK);

		drawVariables(g);

		debugger.draw(g, 32, 50-14);

		//Offset in (50, 50)
		debugger.drawCode(g, 50, 50);

		//Offset in (0, 30)
		debugger.drawDivision(g, 30);

		drawRequest(g, requestX, requestY, request);

		drawCache(g, w/2+150, 260, cache);

		ffAnimation.draw(g);
	}

	private void drawVariables(Graphic g) {
		g.drawString("i = "+Integer.toString(i), w/2+30, 46);

		if(!end) {
			g.drawString("r[i] = "+Integer.toString(request[i]), w/2+30, 66);
		}

		if(debugger.getLine() >= 9) {
			g.drawString("max_dist = "+Integer.toString(maxDist), w/2+30, 86);
		}
		if(debugger.getLine() >= 10) {
			g.drawString("furthest = "+Integer.toString(furthest), w/2+30, 106);
		}
		if(debugger.getLine() >= 11) {
			g.drawString("j = "+Integer.toString(j), w/2+30, 126);
		}

		if(debugger.getLine() >= 12) {
			g.drawString("l = "+Integer.toString(l), w/2+30, 146);
		}
	}

	private void drawRequest(Graphic g, int x, int y, int[] request) {
		//Draw request cells
		for(int r = 0; r<request.length; r++) {

			if(r == i) {
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

	private void drawCache(Graphic g, int x, int y, int[] cache) {

		//Draw cache cells
		for(int i=0; i<cache.length; i++) {
			g.drawRect(x+CELL_WIDTH*i, y, CELL_WIDTH, CELL_HEIGHT);
			if(i < cacheUse) {
				g.drawString(x+CELL_WIDTH*i, y, CELL_WIDTH, CELL_HEIGHT, Integer.toString(cache[i]));
			}
		}

		int w = cache.length*CELL_WIDTH;
		g.drawString(x, y-10, w, 0, "Cache");

	}

	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {
		if(event.isKeyUp(KeyEvent.TSK_DOWN_ARROW)) {
			if(!end) {
				debugger.nextLine();	
			}

		}
		if(event.isKeyUp(KeyEvent.TSK_UP_ARROW)) {
			if(!end) {
				debugger.previousLine();	
			}
		}

		if(event.isKeyUp(KeyEvent.TSK_N)) {
			if(!end) {
				debugger.nextLine();			
				executeLine(debugger.getLine());
			}
		}

		return null;
	}

	private void executeLine(int line) {

		switch(line) {

		//Verify if r is in cache
		case 2:
			if(!isInCache(request[i])) {
				debugger.offsetLine(2);
			} //else line 3
			break;
			//Cache hit
		case 3:
			ffAnimation.animateCacheHit(requestX+CELL_WIDTH*i, requestY);			
			nextLoop();
			break;

			//Verify if cache is full
		case 5:
			if(cacheIsFull()) {
				System.out.println("Full");
				//Cache is full
				debugger.offsetLine(2);
			} //else line 6
			break;

			//Execute Put in cache
		case 6:
			putInCache(request[i]);
			nextLoop();

			if(i >= request.length) {
				//end loop
				System.out.println("End Loop");
				end = true;
				debugger.offsetLine(19);
			}

			break;

		case 8:
			ffAnimation.animateCacheMiss(requestX+CELL_WIDTH*i, requestY);
			break;

		case 11:
			j = 1;
			break;
		case 12:
			l = i+1;
			break;
		case 14:
			if(request[i] != cache[j]) {
				l++;
				debugger.offsetLine(-2);
			} else {
				debugger.offsetLine(1);
			}
			break;
		}


	}

	private void nextLoop() {
		i++;
		debugger.setLine(1);
	}

	private boolean cacheIsFull() {
		return cacheUse == cache.length;
	}

	private String[] buildPseudoCode() {
		String[] sentences = {
				"for i = 0 to n do",
				"    if r[i] is in the cache then",
				"        \"cache hit\"",
				"    else",
				"        if cache is not full then",
				"            put r[i] in cache",
				"        else",
				"            \"cache miss\"",
				"            max_dist = 0",
				"            furthest = 0",
				"            for j = 1 to cache.length() do",
				"                l = i + 1",
				"                    while r[i] != cache[j] do",
				"                        l = l+1",
				"                    dist[j] = l-i",
				"                    if dist[j] > max_dist then",
				"                        furthest = cache[j]",
				"                        max_dist = dist[j]",
				"            evict furthest and put r[i] in cache",
				"end"
		};

		return sentences;
	}



	private boolean isInCache(int value) {

		boolean result = false;

		for(int c = 0; c < cacheUse; c++) {
			if(value == cache[c]) {
				result = true;
			}
		}

		return result;
	}

}
