package ffa;

import java.awt.Color;

import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.graphics.SVGColor;

public class Debugger {

	private int w, h;
	
	private int debuggerLine = 0;
	
	public static final int LINE_SIZE = 18;
	
	private String[] code;
	
	public Debugger(int w, int h, String[] code) {
		super();
		this.w = w;
		this.h = h;
		this.code = code;
	}

	public void draw(Graphic g, int x, int y) {

		int offsetY = y+LINE_SIZE*debuggerLine;

		//Fill debugger line
		g.setColor(SVGColor.KHAKI);
		g.fillRect(0,  offsetY, w/2, 18);

		//Draw debugger ball
		g.setColor(Color.RED);
		g.fillOval(x-3,  offsetY+4, 10, 10);
		g.setColor(Color.BLACK);
		g.drawOval(x-3,  offsetY+4, 10, 10);
	}
	
	public int getLine() {
		return debuggerLine;
	}
		
	public void nextLine() {
		debuggerLine++;
		debuggerLine %= code.length;
	}
	
	public void previousLine() {
		debuggerLine += code.length-1;
		debuggerLine %= code.length;
	}

	public void offsetLine(int offset) {
		this.debuggerLine += offset;
	}

	public void drawCode(Graphic g, int x, int y) {
		g.setColor(Color.BLACK);
		g.setFontSize(14);
	
		int lineSize = 18;
		int count = 0;
	
		for(String s:code) {
			g.drawString(s, x, y+lineSize*count);
			count++;
			g.drawString(Integer.toString(count), x-40, y+lineSize*(count-1));
		}
	}
	
	public void drawDivision(Graphic g, int y) {
		g.drawLine(w/2, y, w/2, h-y);
	}

	public void setLine(int line) {
		this.debuggerLine = line;
	}
}
