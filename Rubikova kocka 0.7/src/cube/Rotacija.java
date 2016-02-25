package cube;
/*
 * Jedna rotacija Rubikove kocke:
 * Izabere se osa (X, Y, Z)
 * pa deo (LEFT, MIDDLE, RIGHT)
 * i na kraju smer rotacije (CW, CCW)
 */
public class Rotacija {
	
	public enum Osa { 
		X, Y, Z; 
	}
	
	public enum Smer { 
		CLOCKWISE {
			@Override public Smer reverse() { return COUNTER_CLOCKWISE; }
		},
		COUNTER_CLOCKWISE {
			@Override public Smer reverse() { return CLOCKWISE; }
		}; 
		
		public abstract Smer reverse();
	}
	
	Osa osa; 	// Osa rotacije
	int deo; 	// indeks dela koji se rotira
	Smer smer; 	// Smer rotacije
	
	public Rotacija(Osa osa, int deo, Smer smer) {
		this.osa = osa;
		this.deo = deo;
		this.smer = smer;
	}

	public int getDeo() {
		return deo;
	}

	public Osa getOsa() {
		return osa;
	}

	public Smer getSmer() {
		return smer;
	}
	
	public boolean isClockwise() {
		return (smer == Smer.CLOCKWISE);
	}
	
	public boolean isNotClockwise() {
		return (smer != Smer.CLOCKWISE);
	}
	
}
