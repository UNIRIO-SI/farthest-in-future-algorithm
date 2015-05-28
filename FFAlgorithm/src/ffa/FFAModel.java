package ffa;


public class FFAModel {

	public int i = 0;
	public int j = 0;
	public int p = 0;
	
	public final int K;
	
	public int[] request;
	
	public int furthest = 0;
	
	public boolean end = false;
	
	public int cacheUse = 0;
	
	public FFAModel(int K) {
		super();
		
		this.K = K;
	}
	
}
