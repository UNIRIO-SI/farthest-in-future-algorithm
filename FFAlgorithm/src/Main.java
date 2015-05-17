import ffa.FFAApplication;
import br.com.etyllica.EtyllicaFrame;
import br.com.etyllica.context.Application;


public class Main extends EtyllicaFrame {

	private static final long serialVersionUID = 7739713774644387495L;

	public Main() {
		super(800,600);
	}

	// Main program
	public static void main(String[] args) {
		Main app = new Main();
		app.init();
	}

	@Override
	public Application startApplication() {
		return new FFAApplication(w, h);
	}

}
