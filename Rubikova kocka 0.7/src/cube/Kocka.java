package cube;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cube.Kockica.Color;
import cube.Rotacija.Osa;

/*
 * Rubikova kocka predstavljena kao trodimenzionalni niz Kockica
 */
public class Kocka {
	
	// indeksi po kolonama, redovima i ravnima
	public static final int COLUMN_LEFT   = 0;
	public static final int COLUMN_MIDDLE = 1;
	public static final int COLUMN_RIGHT  = 2;
	public static final int ROW_BOTTOM    = 0;
	public static final int ROW_MIDDLE    = 1;
	public static final int ROW_TOP       = 2;
	public static final int PLAIN_FRONT    = 0;
	public static final int PLAIN_MIDDLE   = 1;
	public static final int PLAIN_REAR     = 2;	
	

	private final int size;
	private Kockica[][][] state;	

	public Kocka(int size) {
		this.size = size;
		this.state = new Kockica[size][size][size];
		resetState();
	}
	
	public Kocka(Kockica[][][] state) {
		this.size = state.length;
		this.state = state;
	}
	
	public int getSize() {
		return size;
	}
	
	public Kockica[][][] getState() {
		return state;
	}
	
	public Kockica getKockica(KockicaPozicija position) {
		return getKockica(position.x, position.y, position.z);
	}
	
	public Kockica getKockica(int x, int y, int z) {
		return state[x][y][z];
	}	

	// vreca true ako je kockica na pravoj poziciji i u pravoj orijentaciji
	public boolean pozicijaResena(KockicaPozicija pozicija) {
		Kockica kockica = getKockica(pozicija);
		
		int lastIdx = size-1;
		if (pozicija.x == 0 && kockica.leftColor != Kockica.BSK_LEFT)         
			return false;
		if (pozicija.x == lastIdx && kockica.rightColor != Kockica.BSK_RIGHT) 
			return false;
		if (pozicija.y == 0 && kockica.bottomColor != Kockica.BSK_BOTTOM)     
			return false;
		if (pozicija.y == lastIdx && kockica.topColor != Kockica.BSK_TOP)     
			return false;
		if (pozicija.z == 0 && kockica.frontColor != Kockica.BSK_FRONT)       
			return false;
		if (pozicija.z == lastIdx && kockica.rearColor != Kockica.BSK_REAR)   
			return false;
		return true;
	}
	
	// vraca true ako je na pravoj poziciji
	public boolean pozicijaIspravna(KockicaPozicija pozicija) {
		List<Color> colors = getVidljiveBoje(pozicija);
		List<Color> solvedColors = getVidljiveBojeResenjeKocke(pozicija);
		
		if (colors.size() != solvedColors.size()) 
			return false;
		
		for (Color color : solvedColors) {
			if (!colors.contains(color)) return false;
		}
		
		return true;
	}
	
	// vraca integer koji oznacava koje od strana kockice na zadataoj poziciju su vidljive
	public int getVidljiveStrane(int x, int y, int z) {
		int lastIdx = size-1;
		int vidljiveStrane = (x == 0) ? Kockica.KOCKICA_LEFT   : ((x == lastIdx) ? Kockica.KOCKICA_RIGHT : 0);
		vidljiveStrane    |= (y == 0) ? Kockica.KOCKICA_BOTTOM : ((y == lastIdx) ? Kockica.KOCKICA_TOP   : 0);
		vidljiveStrane    |= (z == 0) ? Kockica.KOCKICA_FRONT  : ((z == lastIdx) ? Kockica.KOCKICA_REAR  : 0);
		return vidljiveStrane;
	}
	
	public List<Color> getVidljiveBoje(KockicaPozicija pozicija) {
		return getVidljiveBoje(pozicija.x, pozicija.y, pozicija.z);
	}
	
	//vraca listu svih vidljivih boja za kockicu na zadatoj poziciji
	public List<Color> getVidljiveBoje(int x, int y, int z) {
		List<Color> colors = new ArrayList<Color>(3);
		int VidljiveStrane = getVidljiveStrane(x, y, z);
		
		if ((VidljiveStrane & Kockica.KOCKICA_LEFT) > 0)   
			colors.add(getKockica(x, y, z).leftColor);
		if ((VidljiveStrane & Kockica.KOCKICA_RIGHT) > 0)  
			colors.add(getKockica(x, y, z).rightColor);
		if ((VidljiveStrane & Kockica.KOCKICA_BOTTOM) > 0) 
			colors.add(getKockica(x, y, z).bottomColor);
		if ((VidljiveStrane & Kockica.KOCKICA_TOP) > 0)    
			colors.add(getKockica(x, y, z).topColor);
		if ((VidljiveStrane & Kockica.KOCKICA_FRONT) > 0)  
			colors.add(getKockica(x, y, z).frontColor);
		if ((VidljiveStrane & Kockica.KOCKICA_REAR) > 0)   
			colors.add(getKockica(x, y, z).rearColor);
		
		return colors;
	}
	
	public List<Color> getVidljiveBojeResenjeKocke(KockicaPozicija pozicija) {
		return getVidljiveBojeReseneKocke(pozicija.x, pozicija.y, pozicija.z);
	}
	
	// vraca listu svih vidljivih boja za kockicu na zadatoj poziciji kada je kockica u pocetnoj/resenoj poziciji, tj na svom mestu
	public List<Color> getVidljiveBojeReseneKocke(int x, int y, int z) {
		List<Color> colors = new ArrayList<Color>(3);
		int vidljiveStrane = getVidljiveStrane(x, y, z);
		
		if ((vidljiveStrane & Kockica.KOCKICA_LEFT) > 0)   
			colors.add(Kockica.BSK_LEFT);
		if ((vidljiveStrane & Kockica.KOCKICA_RIGHT) > 0)  
			colors.add(Kockica.BSK_RIGHT);
		if ((vidljiveStrane & Kockica.KOCKICA_BOTTOM) > 0) 
			colors.add(Kockica.BSK_BOTTOM);
		if ((vidljiveStrane & Kockica.KOCKICA_TOP) > 0)    
			colors.add(Kockica.BSK_TOP);
		if ((vidljiveStrane & Kockica.KOCKICA_FRONT) > 0)  
			colors.add(Kockica.BSK_FRONT);
		if ((vidljiveStrane & Kockica.KOCKICA_REAR) > 0)   
			colors.add(Kockica.BSK_REAR);
		
		return colors;
	}
		
	public void rotiraj(Rotacija rotacija) {
		if (rotacija.getDeo() >= size)
			throw new RuntimeException("Izvan okvira " + rotacija.getDeo());
			
		if (rotacija.getOsa() == Osa.X)
			rotirajX(rotacija);
		else if (rotacija.getOsa() == Osa.Y)
			rotirajY(rotacija);
		else if (rotacija.getOsa() == Osa.Z)
			rotirajZ(rotacija);		
	}
	
	public void resetState() {
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				for (int z=0; z<size; z++) {
					state[x][y][z] = new Kockica();
				}
			}
		}
	}
	
	public Kocka getCopy() {
		return new Kocka(copyState());
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Kocka other = (Kocka) obj;
		if (size != other.size)
			return false;
		if (!Arrays.deepEquals(state, other.state))
			return false;
		return true;
	}

	private void rotirajX(Rotacija rotacija) {
		int x = rotacija.getDeo();
		int j = size-1;
		
		Kockica[][][] copy = copyState();
		for (int i=0, ir=j; i<size; i++, ir--) {
			copy[x][j][i].topColor    = rotacija.isClockwise() ? state[x][i][0].frontColor   : state[x][ir][j].rearColor;
			copy[x][0][i].bottomColor = rotacija.isClockwise() ? state[x][i][j].rearColor    : state[x][ir][0].frontColor;
			copy[x][i][0].frontColor  = rotacija.isClockwise() ? state[x][0][ir].bottomColor : state[x][j][i].topColor;
			copy[x][i][j].rearColor   = rotacija.isClockwise() ? state[x][j][ir].topColor    : state[x][0][i].bottomColor;
		}
		for (int y=0, yr=j; y<size; y++, yr--) {
			for (int z=0, zr=j; z<size; z++, zr--) {
				copy[x][y][z].leftColor  = rotacija.isClockwise() ? state[x][z][yr].leftColor  : state[x][zr][y].leftColor;
				copy[x][y][z].rightColor = rotacija.isClockwise() ? state[x][z][yr].rightColor : state[x][zr][y].rightColor;
			}
		}
		state = copy;
	}
	
	private void rotirajY(Rotacija rotacija) {	
		int y = rotacija.getDeo();
		int j = size-1;
		
		Kockica[][][] copy = copyState();
		for (int i=0, ir=j; i<size; i++, ir--) {
			copy[0][y][i].leftColor  = rotacija.isClockwise() ? state[ir][y][0].frontColor : state[i][y][j].rearColor;
			copy[j][y][i].rightColor = rotacija.isClockwise() ? state[ir][y][j].rearColor  : state[i][y][0].frontColor;
			copy[i][y][0].frontColor = rotacija.isClockwise() ? state[j][y][i].rightColor  : state[0][y][ir].leftColor;
			copy[i][y][j].rearColor  = rotacija.isClockwise() ? state[0][y][i].leftColor   : state[j][y][ir].rightColor;
		}
		for (int x=0, xr=j; x<size; x++, xr--) {
			for (int z=0, zr=j; z<size; z++, zr--) {
				copy[x][y][z].topColor    = rotacija.isClockwise() ? state[zr][y][x].topColor    : state[z][y][xr].topColor;
				copy[x][y][z].bottomColor = rotacija.isClockwise() ? state[zr][y][x].bottomColor : state[z][y][xr].bottomColor;
			}
		}
		state = copy;
	}
	
	private void rotirajZ(Rotacija rotacija) {
		int z = rotacija.getDeo();
		int j = size-1;
		
		Kockica[][][] copy = copyState();
		for (int i=0, ir=j; i<size; i++, ir--) {
			copy[i][j][z].topColor    = rotacija.isClockwise() ? state[0][i][z].leftColor    : state[j][ir][z].rightColor;
			copy[i][0][z].bottomColor = rotacija.isClockwise() ? state[j][i][z].rightColor   : state[0][ir][z].leftColor;
			copy[0][i][z].leftColor   = rotacija.isClockwise() ? state[ir][0][z].bottomColor : state[i][j][z].topColor;
			copy[j][i][z].rightColor  = rotacija.isClockwise() ? state[ir][j][z].topColor    : state[i][0][z].bottomColor;
		}
		for (int x=0, xr=j; x<size; x++, xr--) {
			for (int y=0, yr=j; y<size; y++, yr--) {
				copy[x][y][z].frontColor = rotacija.isClockwise() ? state[yr][x][z].frontColor : state[y][xr][z].frontColor;
				copy[x][y][z].rearColor  = rotacija.isClockwise() ? state[yr][x][z].rearColor  : state[y][xr][z].rearColor;
			}
		}
		state = copy;
	}
	
	private Kockica[][][] copyState() {
		Kockica[][][] tmp = new Kockica[size][size][size];
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				for (int z=0; z<size; z++) {
					tmp[x][y][z] = state[x][y][z].getCopy();
				}
			}
		}
		return tmp;
	}

}
