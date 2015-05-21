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

public class FFAApplication extends Application {

	private List<Integer> cache;
	private List<Integer> dist;
	private int cacheUse = 0;
	private int furthest = 0;

	private int[] request;
	private int requestX;
	private int requestY = 160;

	private static final int INFINITY = 10000;

	private static final int CELL_WIDTH = 48;
	private static final int CELL_HEIGHT = 64;

	private static final int K = 3; //Cache Size
	private static final int N = 6; //Request Size

	private int i = 0;
	private int j = 1;
	private int p = 0;

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

		cache = new ArrayList<Integer>(K);
		
		dist = new ArrayList<Integer>(Collections.nCopies(K, -1));

		request = new int[N];
		request[0] = 40;
		request[1] = 3;
		request[2] = 3;
		request[3] = 8;
		request[4] = 10;
		request[5] = 19;

		requestX = w/2+100;

		loading = 100;
	}

	private void putInCache(int value) {
		cache.add(value);
		cacheUse++;
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

		drawCache(g, w/2+150, 260, cache, "Cache");
		
		drawDist(g, w/2+150, 380, dist, "Dist");

		ffAnimation.draw(g);
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

	private void drawCache(Graphic g, int x, int y, List<Integer> cache, String label) {

		//Draw cache cells
		for(int i=0; i < K; i++) {
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

		switch(line) {

		//Verify if r is in cache
		case 2:
			if(!isInCache(request[i])) {
				debugger.offsetLine(2);
			} //else line 3
			
			//drawArrow()
			break;
			//Cache hit
		case 3:
			ffAnimation.animateCacheHit(requestX+CELL_WIDTH*i, requestY);			
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
			putInCache(request[i]);
			nextLoop();
			break;

		case 8:
			ffAnimation.animateCacheMiss(requestX+CELL_WIDTH*i, requestY);
			break;
		case 9:
			furthest = 0;
			break;			
		case 10:
			j = 1;
			break;
		case 11:
			p = i+1;
			break;
		case 12:			
			if(p >= K)
				debugger.offsetLine(4);
			break;
		case 14:
			if(request[p] != cache.get(j)) {
				System.out.println("r[p] = "+request[p]);
				p++;
				debugger.offsetLine(-2);
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
			debugger.offsetLine(3);
			break;
		case 20:
			//dist[j] = p-1
			dist.set(j, p - 1);
			break;
		case 21:
			if(dist.get(j)<=dist.get(furthest)) {
				debugger.offsetLine(2);
			}
			break;
		case 22:
			furthest = j;
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
				"            for j = 1 to k do",
				"                p = i + 1",
				"                    while p < k do",
				"                        if (r[p] != cache[j])",
				"                            p = p + 1",
				"                        else",
				"                            break;",
				"                    if p == n",
				"                        dist[j] = 10000; //Infinity",
				"                    else",
				"                        dist[j] = p - 1",
				"                    if(distance[j] > distance[furthest])",
				"                        furthest = j",
				"                    cache.pop(furthest)",
				"                    cache.append(r[i])",
				"end"
		};

		return sentences;
	}

	private boolean isInCache(int value) {

		boolean result = false;

		for(int c = 0; c < cacheUse; c++) {
			if(value == cache.get(c)) {
				result = true;
			}
		}

		return result;
	}

}
