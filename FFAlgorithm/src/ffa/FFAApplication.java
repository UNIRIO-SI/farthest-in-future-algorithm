package ffa;

import java.awt.Color;

import br.com.etyllica.context.Application;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.graphics.SVGColor;

public class FFAApplication extends Application {

	private String[] pseudoCode;
	
	private int debuggerLine = 0;
	
	private int[] cache;
	private int cacheUse = 0;
	
	private int[] request;
	
	private static final int K = 5;
	
	private static final int LINE_SIZE = 18;
	
	public FFAApplication(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {
		cache = new int[K];
		
		cache[0] = 100;
		cacheUse = 1;
		
		request = new int[2];
		request[0] = 40;
		request[1] = 3;
		
		pseudoCode = buildPseudoCode();
		loading = 100;
	}

	@Override
	public void draw(Graphic g) {
		
		drawDebugger(g, 32, 50-14);
		
		//Offset in (50, 50)
		drawPseudoCode(g, 50, 50);
		
		//Offset in (0,50)
		drawDivision(g, 30);
		
		//drawRequest(g, w/2+150, 260, cache);
		
		drawCache(g, w/2+150, 260, cache);
	}

	private void drawDebugger(Graphic g, int x, int y) {
		
		int offsetY = y+LINE_SIZE*debuggerLine;
		
		//Fill debugger line
		g.setColor(SVGColor.KHAKI);
		g.fillRect(0,  offsetY, w/2, 18);
		
		g.setColor(Color.RED);
		g.fillOval(x,  offsetY+4, 10, 10);
		g.setColor(Color.BLACK);
		g.drawOval(x,  offsetY+4, 10, 10);
	}
	
	private void drawCache(Graphic g, int x, int y, int[] cache) {
		
		int cacheCellWidth = 32;
		int cacheCellHeight = 48;
		
		//Draw cache cells
		for(int i=0; i<cache.length; i++) {
			g.drawRect(x+cacheCellWidth*i, y, cacheCellWidth, cacheCellHeight);
			if(i < cacheUse) {
				g.drawString(x+cacheCellWidth*i, y, cacheCellWidth, cacheCellHeight, Integer.toString(cache[i]));
			}
		}
		
		int w = cache.length*cacheCellWidth;
		g.drawString(x, y-10, w, 0, "Cache");
		
	}

	private void drawDivision(Graphic g, int y) {
		g.drawLine(w/2, y, w/2, h-y);
	}

	private void drawPseudoCode(Graphic g, int x, int y) {
		g.setColor(Color.BLACK);
		g.setFontSize(14);
		
		int lineSize = 18;
		int i=0;
		
		for(String s:pseudoCode) {
			g.drawString(s, x, y+lineSize*i);
			i++;
		}
	}
	
	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {
		if(event.isKeyUp(KeyEvent.TSK_DOWN_ARROW)) {
			debuggerLine++;
		}
		if(event.isKeyUp(KeyEvent.TSK_UP_ARROW)) {
			debuggerLine--;
		}
		return null;
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
			"            maxdist = 0",
			"            furthest = cache[j]",
			"            for j = 1 to k do",
			"                l = i + 1",
			"                    while r[i] != cache[j] do",
			"                        l = l+1",
			"                    dist[j] = l-i",
			"                    if dist[j] > max_dist then",
			"                        furthest = cache[j]",
			"                        max_dist = dist[j]",
			"            evict furthest and put r[i] in cache"
		};
		
		return sentences;
	}

}
