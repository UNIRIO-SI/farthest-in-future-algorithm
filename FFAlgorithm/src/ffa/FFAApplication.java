package ffa;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.etyllica.context.Application;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.linear.Point2D;

public class FFAApplication extends Application {

	private List<Integer> cache;
	private List<Integer> dist;
	private int requestX;
	private int requestY = 160;
	
	private int cacheX;
	private int cacheY = 260;

	private static final int INFINITY = 10000;

	private static final int CELL_WIDTH = 42;
	private static final int CELL_HEIGHT = 60;

	private static final int N = 16; //Request Size

	private Debugger debugger;
	private FFAModel ffa = new FFAModel(3);//K = 3; //Cache Size
		
	private Point2D originArrow;
	private Point2D endArrow;
	private boolean drawArrow = false;
	
	private FFADrawer drawer;

	public FFAApplication(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {

		String[] pseudoCode = buildPseudoCode();

		debugger = new Debugger(w, h, pseudoCode);

		cache = new ArrayList<Integer>(ffa.K);
				
		resetDist();

		drawer = new FFADrawer(ffa, debugger);
		drawer.division = w/2-150;
		
		ffa.request = new int[N];
		
		// 1,2,4,1,4,3,2,4,1,2,1,4,3,1,3,2
		ffa.request[0] = 1;
		ffa.request[1] = 2;
		ffa.request[2] = 4;
		ffa.request[3] = 1;
		ffa.request[4] = 4;
		ffa.request[5] = 3;
		ffa.request[6] = 2;
		ffa.request[7] = 4;
		ffa.request[8] = 1;
		ffa.request[9] = 2;
		ffa.request[10] = 1;
		ffa.request[11] = 4;
		ffa.request[12] = 3;
		ffa.request[13] = 1;
		ffa.request[14] = 3;
		ffa.request[15] = 2;

		requestX = drawer.division+30;
		drawer.requestX = requestX;
		drawer.requestY = requestY;
		
		cacheX = w/2+110;

		loading = 100;
	}

	protected void resetDist() {
		dist = new ArrayList<Integer>(Collections.nCopies(ffa.K, -1));
	}

	private int putInCache(int value) {
		cache.add(value);
		ffa.cacheUse++;
		return ffa.cacheUse-1;
	}

	@Override
	public void draw(Graphic g) {

		g.setColor(Color.BLACK);

		drawer.drawVariables(g);

		debugger.draw(g, 32, 50-14);

		//Offset in (50, 50)
		debugger.drawCode(g, 50, 50);

		//Offset in (0, 30)
		debugger.drawDivision(g, 30);

		drawer.drawRequest(g, requestX, requestY, ffa.request);

		drawer.drawCache(g, cacheX, cacheY, cache, "Cache (K="+Integer.toString(ffa.K)+")");
		
		drawer.drawDist(g, w/2+110, 380, dist, "Dist");

		drawer.drawAnimation(g);
		
		if(drawArrow) {
			g.drawArrow(originArrow, endArrow);
		}
	}

	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {
		if(event.isKeyUp(KeyEvent.TSK_DOWN_ARROW)) {
			if(!ffa.end) {
				debugger.nextLine();	
			}
		}
		if(event.isKeyUp(KeyEvent.TSK_UP_ARROW)) {
			if(!ffa.end) {
				debugger.previousLine();	
			}
		}

		if(event.isKeyUp(KeyEvent.TSK_N)) {
			if(!ffa.end) {
				debugger.nextLine();			
				executeLine(debugger.getLine());
			}
		}
		return null;
	}

	private void executeLine(int line) {

		//Reset highlight
		drawer.highlightDist = -1;
		drawer.highlightCache = -1;
		
		switch(line) {

		//Verify if r is in cache
		case 2:
			//If number is not in cache
			int index = isInCache(ffa.request[ffa.i]); 
			if(index < 0) {
				debugger.offsetLine(2);
			} else {
				//cache hit
				drawHitArrow(index);
				drawer.highlightCache = index;
			}
			//drawArrow()
			break;
			//Cache hit
		case 3:
			drawer.animateCacheHit();
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
			drawer.highlightCache = putInCache(ffa.request[ffa.i]);
			nextLoop();
			break;
		case 8:
			drawer.animateCacheMiss();
			break;
		case 9:
			ffa.furthest = 0;
			resetDist();
			ffa.j = 0;
			break;
		case 10:
			//If j>K exit loop
			if(ffa.j >= ffa.K) {
				debugger.offsetLine(12);
				drawer.highlightCache = ffa.furthest;
			}
			break;
		case 11:
			ffa.p = ffa.i+1;
			break;
		case 12:
			if(ffa.p >= N)
				debugger.offsetLine(4);
			break;
		case 14:
			if(ffa.request[ffa.p] != cache.get(ffa.j)) {
				ffa.p++;
				debugger.offsetLine(-3);
			} else {
				debugger.offsetLine(1);
			}
			break;
		case 15:
			debugger.offsetLine(2);
			break;
		case 17:
			if(ffa.p!=N) {
				debugger.offsetLine(2);
			}
			break;
		case 18:
			//dist[j] = Infinity
			dist.set(ffa.j, INFINITY);
			drawer.highlightDist = ffa.j;
			debugger.offsetLine(2);
			break;
		case 20:
			//dist[j] = p-1
			dist.set(ffa.j, ffa.p-1);
			break;
		case 21:
			if(dist.get(ffa.j)>dist.get(ffa.furthest)) {
				drawer.highlightDist = ffa.j;
				ffa.furthest = ffa.j;
			} else {
				ffa.j++;
				debugger.offsetLine(-12);	
			}
			break;
		case 22:
			//End of for loop (back to line 10)
			ffa.j++;
			debugger.offsetLine(-13);
			break;
		case 23:
			cache.remove(ffa.furthest);
			ffa.cacheUse--;
			break;
		case 24:
			putInCache(ffa.request[ffa.i]);
			nextLoop();
			break;
			
		}
	}

	protected void drawHitArrow(int index) {
		int ax = requestX+CELL_WIDTH/2+CELL_WIDTH*ffa.i;
		int ex = cacheX+CELL_WIDTH/2+CELL_WIDTH*index;
		
		originArrow = new Point2D(ax, requestY+CELL_HEIGHT);
		endArrow = new Point2D(ex, cacheY);
		drawArrow = true;
	}

	private void nextLoop() {
		ffa.i++;
		
		if(ffa.i < ffa.request.length) {
			debugger.setLine(1);
		} else {
			//end loop
			System.out.println("End Loop");
			ffa.end = true;
			debugger.offsetLine(25);//Last Line
		}
	}

	private boolean cacheIsFull() {
		return ffa.cacheUse == ffa.K;
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

		for(int c = 0; c < ffa.cacheUse; c++) {
			if(value == cache.get(c)) {
				result = c;
			}
		}

		return result;
	}

}
