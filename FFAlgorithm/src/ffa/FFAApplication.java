package ffa;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.etyllica.context.Application;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.graphics.SVGColor;
import br.com.etyllica.linear.Point2D;

public class FFAApplication extends Application {

	private List<Integer> cache;
	private List<Integer> dist;
	private int cacheUse = 0;
	private int furthest = 0;

	private int[] request;
	private int requestX;
	private int requestY = 160;
	
	private int cacheX;
	private int cacheY = 260;

	private static final int INFINITY = 10000;

	private static final int CELL_WIDTH = 48;
	private static final int CELL_HEIGHT = 64;

	private static final int K = 3; //Cache Size
	private static final int N = 16; //Request Size

	private int i = 0;
	private int j = 0;
	private int p = 0;

	private Debugger debugger;
	private FFAnimation ffAnimation;
	
	private Point2D originArrow;
	private Point2D endArrow;
	private boolean drawArrow = false;
	
	private int highlightDist = -1;
	private int highlightCache = -1;

	private boolean end = false;

	public FFAApplication(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {

		String[] pseudoCode = buildPseudoCode();

		debugger = new Debugger(w, h, pseudoCode);

		ffAnimation = new FFAnimation();

		cache = new ArrayList<Integer>(K);
				
		resetDist();

		request = new int[N];
				
		// 1,2,4,1,4,3,2,4,1,2,1,4,3,1,3,2
		request[0] = 1;
		request[1] = 2;
		request[2] = 4;
		request[3] = 1;
		request[4] = 4;
		request[5] = 3;
		request[6] = 2;
		request[7] = 4;
		request[8] = 1;
		request[9] = 2;
		request[10] = 1;
		request[11] = 4;
		request[12] = 3;
		request[13] = 1;
		request[14] = 3;
		request[15] = 2;

		requestX = w/2+30;
		
		cacheX = w/2+110;

		loading = 100;
	}

	protected void resetDist() {
		dist = new ArrayList<Integer>(Collections.nCopies(K, -1));
	}

	private int putInCache(int value) {
		cache.add(value);
		cacheUse++;
		return cacheUse-1;
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

		drawCache(g, cacheX, cacheY, cache, "Cache(K="+Integer.toString(K)+")");
		
		drawDist(g, w/2+110, 380, dist, "Dist");

		ffAnimation.draw(g);
		
		if(drawArrow) {
			g.drawArrow(originArrow, endArrow);
		}
	}

	private void drawVariables(Graphic g) {
		g.drawString("i = "+Integer.toString(i), w/2+30, 46);

		if(!end) {
			g.drawString("r[i] = "+Integer.toString(request[i]), w/2+30, 66);
		}

		if(debugger.getLine() >= 9) {
			g.drawString("furthest = "+Integer.toString(furthest), w/2+30, 86);
		}
		if(debugger.getLine() >= 10) {
			g.drawString("j = "+Integer.toString(j), w/2+30, 106);
		}
		if(debugger.getLine() >= 11) {
			g.drawString("p = "+Integer.toString(p), w/2+30, 126);
		}

	}

	private void drawRequest(Graphic g, int x, int y, int[] request) {
		//Draw request cells
		for(int r = 0; r < request.length; r++) {
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

	private void drawCache(Graphic g, int x, int y, List<Integer> cache, String label) {

		//Draw cache cells
		for(int i=0; i < K; i++) {
			g.setColor(SVGColor.BEIGE);
			if(highlightCache == i) {
				g.fillRect(x+CELL_WIDTH*i, y, CELL_WIDTH, CELL_HEIGHT);	
			}
			g.setColor(SVGColor.BLACK);
			g.drawRect(x+CELL_WIDTH*i, y, CELL_WIDTH, CELL_HEIGHT);
			if(i < cacheUse) {
				g.drawString(x+CELL_WIDTH*i, y, CELL_WIDTH, CELL_HEIGHT, Integer.toString(cache.get(i)));
			}
		}

		int w = K*CELL_WIDTH;
		g.drawString(x, y-10, w, 0, label);

	}
	
	private void drawDist(Graphic g, int x, int y, List<Integer> cache, String label) {

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

		//Reset highlight
		highlightDist = -1;
		highlightCache = -1;
		
		switch(line) {

		//Verify if r is in cache
		case 2:
			//If number is not in cache
			int index = isInCache(request[i]); 
			if(index < 0) {
				debugger.offsetLine(2);
			} else {
				//cache hit
				drawHitArrow(index);
				highlightCache = index;
			}
			//drawArrow()
			break;
			//Cache hit
		case 3:
			ffAnimation.animateCacheHit(requestX+CELL_WIDTH*i, requestY);
			drawArrow = false;
			nextLoop();
			break;

			//Verify if cache is full
		case 5:
			if(cacheIsFull()) {
				//Cache is full
				debugger.offsetLine(2);
			} //else line 6
			break;

			//Execute Put in cache
		case 6:
			highlightCache = putInCache(request[i]);
			nextLoop();
			break;
		case 8:
			ffAnimation.animateCacheMiss(requestX+CELL_WIDTH*i, requestY);
			break;
		case 9:
			furthest = 0;
			resetDist();
			j = 0;
			break;
		case 10:
			//If j>K exit loop
			if(j >= K) {
				debugger.offsetLine(12);
				highlightCache = furthest;
			}
			break;
		case 11:
			p = i+1;
			break;
		case 12:
			if(p >= N)
				debugger.offsetLine(4);
			break;
		case 14:
			if(request[p] != cache.get(j)) {
				p++;
				debugger.offsetLine(-3);
			} else {
				debugger.offsetLine(1);
			}
			break;
		case 15:
			debugger.offsetLine(2);
			break;
		case 17:
			if(p!=N) {
				debugger.offsetLine(2);
			}
			break;
		case 18:
			//dist[j] = Infinity
			dist.set(j, INFINITY);
			highlightDist = j;
			debugger.offsetLine(2);
			break;
		case 20:
			//dist[j] = p-1
			dist.set(j, p-1);
			break;
		case 21:
			if(dist.get(j)>dist.get(furthest)) {
				highlightDist = j;
				furthest = j;
			} else {
				j++;
				debugger.offsetLine(-12);	
			}
			break;
		case 22:
			//End of for loop (back to line 10)
			j++;
			debugger.offsetLine(-13);
			break;
		case 23:
			cache.remove(furthest);
			cacheUse--;
			break;
		case 24:
			putInCache(request[i]);
			nextLoop();
			break;
			
		}
	}

	protected void drawHitArrow(int index) {
		int ax = requestX+CELL_WIDTH/2+CELL_WIDTH*i;
		int ex = cacheX+CELL_WIDTH/2+CELL_WIDTH*index;
		
		originArrow = new Point2D(ax, requestY+CELL_HEIGHT);
		endArrow = new Point2D(ex, cacheY);
		drawArrow = true;
	}

	private void nextLoop() {
		i++;
		
		if(i < request.length) {
			debugger.setLine(1);
		} else {
			//end loop
			System.out.println("End Loop");
			end = true;
			debugger.offsetLine(25);//Last Line
		}
	}

	private boolean cacheIsFull() {
		return cacheUse == K;
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
				"            furthest = 0",
				"            for j = 0 to k do",
				"                p = i + 1",
				"                while p < n do",
				"                    if (r[p] != cache[j])",
				"                        p = p + 1",
				"                    else",
				"                        break;",
				"                if p == n",
				"                    dist[j] = 10000; //Infinity",
				"                else",
				"                    dist[j] = p - 1",
				"                if(distance[j] > distance[furthest])",
				"                    furthest = j",
				"            cache.pop(furthest)",
				"            cache.append(r[i])",
				"end"
		};

		return sentences;
	}

	private int isInCache(int value) {

		int result = -1;

		for(int c = 0; c < cacheUse; c++) {
			if(value == cache.get(c)) {
				result = c;
			}
		}

		return result;
	}

}
