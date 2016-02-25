package cube;
import java.util.ArrayList;
import java.util.List;

public abstract class Solver {
	
	protected Kocka kocka;
	protected List<Rotacija> rotacije;

	public abstract List<Rotacija> getResenje();
	
	public Solver(Kocka kocka) {
		this.kocka = kocka;
		this.rotacije = new ArrayList<Rotacija>();
	}
	
	protected void rotirajPaDodaj(Rotacija rotacija) {
		rotacije.add(rotacija);
		kocka.rotiraj(rotacija);
	}
	
}
