package cube;

import java.util.ArrayList;
import java.util.List;

import cube.Kockica.Color;
import cube.Rotacija.Osa;
import cube.Rotacija.Smer;

/* 
 * Losa implementacija slovera za 3x3x3 Rubikovu kocku, u smislu da nema nikakvu optimizaciju i umanjenje broja koraka
 * i ne implementira ni jedan algoritam pretage. Ukratko, resava kocku sloj po sloj, prema uputsvu sa sajta
 * http://www.youcandothecube.com/secret-unlocked/solution-stage-one.aspx 
 */

public class SolverNeoptimizovani extends Solver {
	
	public static final KockicaPozicija CENTER_FRONT              = new KockicaPozicija(1, 1, 0);
	public static final KockicaPozicija CENTER_REAR               = new KockicaPozicija(1, 1, 2);
	public static final KockicaPozicija CENTER_TOP                = new KockicaPozicija(1, 2, 1);
	public static final KockicaPozicija CENTER_BOTTOM             = new KockicaPozicija(1, 0, 1);
	public static final KockicaPozicija CENTER_LEFT               = new KockicaPozicija(0, 1, 1);
	public static final KockicaPozicija CENTER_RIGHT              = new KockicaPozicija(2, 1, 1);
	public static final KockicaPozicija EDGE_FRONT_LEFT           = new KockicaPozicija(0, 1, 0);
	public static final KockicaPozicija EDGE_FRONT_RIGHT          = new KockicaPozicija(2, 1, 0);
	public static final KockicaPozicija EDGE_FRONT_TOP            = new KockicaPozicija(1, 2, 0);
	public static final KockicaPozicija EDGE_FRONT_BOTTOM         = new KockicaPozicija(1, 0, 0);
	public static final KockicaPozicija EDGE_MIDDLE_TOP_LEFT      = new KockicaPozicija(0, 2, 1);
	public static final KockicaPozicija EDGE_MIDDLE_BOTTOM_LEFT   = new KockicaPozicija(0, 0, 1);
	public static final KockicaPozicija EDGE_MIDDLE_TOP_RIGHT     = new KockicaPozicija(2, 2, 1);
	public static final KockicaPozicija EDGE_MIDDLE_BOTTOM_RIGHT  = new KockicaPozicija(2, 0, 1);
	public static final KockicaPozicija EDGE_REAR_LEFT            = new KockicaPozicija(0, 1, 2);
	public static final KockicaPozicija EDGE_REAR_RIGHT           = new KockicaPozicija(2, 1, 2);
	public static final KockicaPozicija EDGE_REAR_TOP             = new KockicaPozicija(1, 2, 2);
	public static final KockicaPozicija EDGE_REAR_BOTTOM          = new KockicaPozicija(1, 0, 2);
	public static final KockicaPozicija CORNER_FRONT_TOP_LEFT     = new KockicaPozicija(0, 2, 0);
	public static final KockicaPozicija CORNER_FRONT_BOTTOM_LEFT  = new KockicaPozicija(0, 0, 0);
	public static final KockicaPozicija CORNER_FRONT_TOP_RIGHT    = new KockicaPozicija(2, 2, 0);
	public static final KockicaPozicija CORNER_FRONT_BOTTOM_RIGHT = new KockicaPozicija(2, 0, 0);
	public static final KockicaPozicija CORNER_REAR_TOP_LEFT      = new KockicaPozicija(0, 2, 2);
	public static final KockicaPozicija CORNER_REAR_BOTTOM_LEFT   = new KockicaPozicija(0, 0, 2);
	public static final KockicaPozicija CORNER_REAR_TOP_RIGHT     = new KockicaPozicija(2, 2, 2);
	public static final KockicaPozicija CORNER_REAR_BOTTOM_RIGHT  = new KockicaPozicija(2, 0, 2);
	
	public SolverNeoptimizovani(Kocka kocka) {
		super(kocka);
		
		if (kocka.getSize() != 3)
			throw new RuntimeException(this.getClass().getName() + " solver podrzava samo 3x3x3 kocke");
	}
	
	@Override
	public List<Rotacija> getResenje() {
		poziconirajCentar(); // pozicioniranje kocke u orijentaciju gde je bela napred, a zelena odozgo
		resiKorak1(); // prednja (bela) stana u "krst"
		resiKorak2(); // prednja (bela) stana uglovi
		resiKorak3(); // srdenja strana ivice
		resiKorak4(); // zadnja (zuta) stana u "krst"
		resiKorak5(); // zadnja (zuta) stana uglovi
		resiKorak6(); // zadnja strana - pozicije uglova
		resiKorak7(); // zadnja strana - orijentacija uglova
		return rotacije;
	}	
	
	private void poziconirajCentar() {
		// prebaci belu centralnu kockicu na prednju stanu
		if (kocka.getKockica(CENTER_REAR).rearColor == Color.WHITE) {
			rotirajPaDodaj(new Rotacija(Osa.X, Kocka.COLUMN_MIDDLE, Smer.COUNTER_CLOCKWISE));
			rotirajPaDodaj(new Rotacija(Osa.X, Kocka.COLUMN_MIDDLE, Smer.COUNTER_CLOCKWISE));
		}
		else if (kocka.getKockica(CENTER_TOP).topColor == Color.WHITE) {
			rotirajPaDodaj(new Rotacija(Osa.X, Kocka.COLUMN_MIDDLE, Smer.COUNTER_CLOCKWISE));
		}
		else if (kocka.getKockica(CENTER_BOTTOM).bottomColor == Color.WHITE) {
			rotirajPaDodaj(new Rotacija(Osa.X, Kocka.COLUMN_MIDDLE, Smer.CLOCKWISE));
		}
		else if (kocka.getKockica(CENTER_LEFT).leftColor == Color.WHITE) {
			rotirajPaDodaj(new Rotacija(Osa.Y, Kocka.ROW_MIDDLE, Smer.COUNTER_CLOCKWISE));
		}
		else if (kocka.getKockica(CENTER_RIGHT).rightColor == Color.WHITE) {
			rotirajPaDodaj(new Rotacija(Osa.Y, Kocka.ROW_MIDDLE, Smer.CLOCKWISE));
		}
		
		//prebaci zelenu centralnu kockicu na gornju stanu 
		if (kocka.getKockica(CENTER_BOTTOM).bottomColor == Color.GREEN) {
			rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_MIDDLE, Smer.COUNTER_CLOCKWISE));
			rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_MIDDLE, Smer.COUNTER_CLOCKWISE));
		}
		else if (kocka.getKockica(CENTER_LEFT).leftColor == Color.GREEN) {
			rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_MIDDLE, Smer.CLOCKWISE));
		}
		else if (kocka.getKockica(CENTER_RIGHT).rightColor == Color.GREEN) {
			rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_MIDDLE, Smer.COUNTER_CLOCKWISE));
		}
	}
	
	/*********************************************************************************************************************************************************/
	
	private void resiKorak1() {		
		List<KockicaPozicija> pozicije = getPozicijePrednjihIvica();
		for (KockicaPozicija pozicija : pozicije) {
			korak1ResiIvicu(pozicija);
		}
	}
	
	private void korak1ResiIvicu(KockicaPozicija odrediste) {
		List<Color> colors = kocka.getVidljiveBojeResenjeKocke(odrediste);
		
		while (!kocka.pozicijaResena(odrediste)) {
			KockicaPozicija pocetna = nadjiPozicijuKockiceSaBojama(colors.toArray(new Color[0]));
		
			if (pocetna.equals(odrediste)) {				
				Osa osa1 = pocetna.redSredina() ? Osa.X : Osa.Y;
				int deo1 = pocetna.redSredina() ? pocetna.x : pocetna.y;
				Smer smer1 = (pocetna.kolonaLevo() || pocetna.redDole()) ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
				
				Osa osa2 = pocetna.redSredina() ? Osa.Y : Osa.X;
				int deo = 0;
				if (pocetna.kolonaLevo())      
					deo = Kocka.ROW_TOP;
				else if (pocetna.kolonaDesno()) 
					deo = Kocka.ROW_BOTTOM;
				else if (pocetna.redDole())   
					deo = Kocka.COLUMN_LEFT;
				else if (pocetna.redGore())      
					deo = Kocka.COLUMN_RIGHT;
				Smer smer2 = (pocetna.kolonaLevo() || pocetna.redGore()) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE;
				
				rotirajPaDodaj(new Rotacija(osa1, deo1, smer1));
				rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_FRONT, Smer.CLOCKWISE));
				rotirajPaDodaj(new Rotacija(osa2, deo, smer2));
				rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_FRONT, Smer.COUNTER_CLOCKWISE));
			}
			else if (pocetna.ravanNapred()) {				
				if (pocetna.kolonaSredina()) {
					rotirajPaDodaj(new Rotacija(Osa.Y, pocetna.y, Smer.CLOCKWISE));
					rotirajPaDodaj(new Rotacija(Osa.Y, pocetna.y, Smer.CLOCKWISE));
				}
				else if (pocetna.redSredina()) {
					rotirajPaDodaj(new Rotacija(Osa.X, pocetna.x, Smer.CLOCKWISE));
					rotirajPaDodaj(new Rotacija(Osa.X, pocetna.x, Smer.CLOCKWISE));
				}
			}
			else if (pocetna.ravanSredina()) {				
				rotirajPaDodaj(new Rotacija(Osa.X, pocetna.x, (pocetna.y == 0) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE));
				rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
				rotirajPaDodaj(new Rotacija(Osa.X, pocetna.x, (pocetna.y == 0) ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE));
			}
			else if (pocetna.ravanPozadi()) {
				if (pocetna.x != odrediste.x && pocetna.y != odrediste.y) {
					Smer smer = ((odrediste.kolonaLevo() && pocetna.redGore()) || (odrediste.kolonaDesno() && pocetna.redDole()))
						? Smer.COUNTER_CLOCKWISE
						: Smer.CLOCKWISE;
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, smer));
				}
				else if ((pocetna.x == odrediste.x && pocetna.y != odrediste.y) || (pocetna.x != odrediste.x && pocetna.y == odrediste.y)) {
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
				}
				
				Osa osa = odrediste.redSredina() ? Osa.X : Osa.Y;
				int deo = odrediste.redSredina() ? odrediste.x : odrediste.y;
				rotirajPaDodaj(new Rotacija(osa, deo, Smer.CLOCKWISE));
				rotirajPaDodaj(new Rotacija(osa, deo, Smer.CLOCKWISE));
			}
		}
	}
	
	/*********************************************************************************************************************************************************/
	
	private void resiKorak2() {		
		List<KockicaPozicija> pozicije = getPozicijePrednjihUglova();
		for (KockicaPozicija pozicija : pozicije) {
			korak2ResiUgao(pozicija);
		}
	}
	
	private void korak2ResiUgao(KockicaPozicija odrediste) {
		List<Color> colors = kocka.getVidljiveBojeResenjeKocke(odrediste);
		
		while (!kocka.pozicijaResena(odrediste)) {
			KockicaPozicija pocetna = nadjiPozicijuKockiceSaBojama(colors.toArray(new Color[0]));
			
			if (pocetna.ravanNapred()) {
				rotirajPaDodaj(new Rotacija(Osa.X, pocetna.x, pocetna.redGore() ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE));
				rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
				rotirajPaDodaj(new Rotacija(Osa.X, pocetna.x, pocetna.redGore() ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE));
			}
			else if (pocetna.ravanPozadi() && (pocetna.x == odrediste.x && pocetna.y == odrediste.y)) {				
				Kockica pocetnaKockica = kocka.getKockica(pocetna);
				if ((pocetna.kolonaLevo() && pocetna.redDole() && pocetnaKockica.bottomColor == Color.WHITE)
						|| (pocetna.kolonaDesno() && pocetna.redDole() && pocetnaKockica.rightColor == Color.WHITE)
						|| (pocetna.kolonaDesno() && pocetna.redGore() && pocetnaKockica.topColor == Color.WHITE)
						|| (pocetna.kolonaLevo() && pocetna.redGore() && pocetnaKockica.leftColor == Color.WHITE))
				{
					Osa osa = (pocetna.x == pocetna.y) ? Osa.X : Osa.Y;
					int deo = (pocetna.x == pocetna.y) ? pocetna.x : pocetna.y;
					Smer smer = (pocetna.redGore()) ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
					
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.COUNTER_CLOCKWISE));
					rotirajPaDodaj(new Rotacija(osa, deo, smer));
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
					rotirajPaDodaj(new Rotacija(osa, deo, smer.reverse()));
				}
				else if ((pocetna.kolonaLevo() && pocetna.redDole() && pocetnaKockica.leftColor == Color.WHITE)
						|| (pocetna.kolonaDesno() && pocetna.redDole() && pocetnaKockica.bottomColor == Color.WHITE)
						|| (pocetna.kolonaDesno() && pocetna.redGore() && pocetnaKockica.rightColor == Color.WHITE)
						|| (pocetna.kolonaLevo() && pocetna.redGore() && pocetnaKockica.topColor == Color.WHITE))
				{
					Osa osa = (pocetna.x == pocetna.y) ? Osa.Y : Osa.X;
					int deo = (pocetna.x == pocetna.y) ? pocetna.y : pocetna.x;
					Smer smer = (pocetna.kolonaLevo()) ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
					
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
					rotirajPaDodaj(new Rotacija(osa, deo, smer));
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.COUNTER_CLOCKWISE));
					rotirajPaDodaj(new Rotacija(osa, deo, smer.reverse()));
				}
				else if (pocetnaKockica.rearColor == Color.WHITE) {
					Osa osa = (pocetna.x == pocetna.y) ? Osa.X : Osa.Y;
					int deo = (pocetna.x == pocetna.y) ? pocetna.x : pocetna.y;
					Smer smer = (pocetna.redDole()) ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
					
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
					rotirajPaDodaj(new Rotacija(osa, deo, smer));
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.COUNTER_CLOCKWISE));
					rotirajPaDodaj(new Rotacija(osa, deo, smer.reverse()));
					rotirajPaDodaj(new Rotacija(osa, deo, smer.reverse()));
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
					rotirajPaDodaj(new Rotacija(osa, deo, smer));
					rotirajPaDodaj(new Rotacija(osa, deo, smer));
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
					rotirajPaDodaj(new Rotacija(osa, deo, smer.reverse()));
				}
			}
			else if (pocetna.ravanPozadi() && (pocetna.x != odrediste.x || pocetna.y != odrediste.y)) {				
				if (pocetna.x != odrediste.x && pocetna.y != odrediste.y) {					
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
				}
				else if (pocetna.x != odrediste.x || pocetna.y != odrediste.y) {					
					Smer smer = ((odrediste.kolonaLevo() && pocetna.redGore()) || (odrediste.kolonaDesno() && pocetna.redDole()))
						? Smer.COUNTER_CLOCKWISE
						: Smer.CLOCKWISE;
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, smer));
				}
			}
		}
	}
	
	/*********************************************************************************************************************************************************/

	private void resiKorak3() {
		// prodji kroz svaku srednju ivicu i resi
		List<KockicaPozicija> pozicije = getPozicijeSrednjihIvica();
		for (KockicaPozicija pozicija : pozicije) {
			korak3ResiIvicu(pozicija);
		}
	}
	
	private void korak3ResiIvicu(KockicaPozicija odrediste) {
		List<Color> colors = kocka.getVidljiveBojeResenjeKocke(odrediste);
		
		while (!kocka.pozicijaResena(odrediste)) {
			KockicaPozicija pocetna = nadjiPozicijuKockiceSaBojama(colors.toArray(new Color[0]));
			
			if (jeLiSrednjaIvicaResiva(pocetna)) {				
				korak3ResiIvicuSaZadnjeStrane(pocetna);
			}
			else if (pocetna.ravanPozadi()) {				
				Color color = getBojaNeZadnjeIvice(pocetna);
				
				KockicaPozicija tmpO = null;
				if (color == Color.GREEN)       
					tmpO = EDGE_REAR_TOP;
				else if (color == Color.RED)    
					tmpO = EDGE_REAR_LEFT;
				else if (color == Color.ORANGE) 
					tmpO = EDGE_REAR_RIGHT;
				else if (color == Color.BLUE)   
					tmpO = EDGE_REAR_BOTTOM;
				
				if ((pocetna.x == tmpO.x) || (pocetna.y == tmpO.y)) {
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
				}
				else {
					Smer smer = ((pocetna.redGore() && tmpO.kolonaDesno())
							|| (pocetna.kolonaDesno() && tmpO.redDole())
							|| (pocetna.redDole() && tmpO.kolonaLevo())
							|| (pocetna.kolonaLevo() && tmpO.redGore()))
						? Smer.CLOCKWISE
						: Smer.COUNTER_CLOCKWISE;
					rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, smer));
				}
			}
			else if (pocetna.ravanSredina()) {				
				Smer zSmer = ((pocetna.redDole() && pocetna.kolonaDesno()) || (pocetna.redGore() && pocetna.kolonaLevo()))
					? Smer.CLOCKWISE
					: Smer.COUNTER_CLOCKWISE;
				Osa osa1 = Osa.X;
				Osa osa2 = Osa.Y;
				int deo1 = pocetna.x;
				int deo2 = pocetna.y;
				Smer smer1 = pocetna.redGore() ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
				Smer smer2 = pocetna.kolonaLevo() ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
				moveRearEdgeToMiddle(zSmer, osa1, deo1, smer1, osa2, deo2, smer2);
			}
		}
	}	
	
	private void korak3ResiIvicuSaZadnjeStrane(KockicaPozicija pozicija) {
		Kockica kockica = kocka.getKockica(pozicija);
		Color color = getBojaNeZadnjeIvice(pozicija);
		
		Osa osa1 = null, osa2 = null;
		int deo1 = 0, deo2 = 0;
		Smer zSmer = null, smer1 = null, smer2 = null;
				
		if (color == Color.GREEN) {
			zSmer = (kockica.rearColor == Color.RED) ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
			osa1 = Osa.X;
			osa2 = Osa.Y;
			deo1 = (kockica.rearColor == Color.RED) ? Kocka.COLUMN_LEFT : Kocka.COLUMN_RIGHT;
			deo2 = Kocka.ROW_TOP;
			smer1 = Smer.CLOCKWISE;
			smer2 = (kockica.rearColor == Color.RED) ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
		}
		else if (color == Color.BLUE) {
			zSmer = (kockica.rearColor == Color.ORANGE) ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
			osa1 = Osa.X;
			osa2 = Osa.Y;
			deo1 = (kockica.rearColor == Color.ORANGE) ? Kocka.COLUMN_RIGHT : Kocka.COLUMN_LEFT;
			deo2 = Kocka.ROW_BOTTOM;
			smer1 = Smer.COUNTER_CLOCKWISE;
			smer2 = (kockica.rearColor == Color.ORANGE) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE;
		}
		else if (color == Color.RED) {
			zSmer = (kockica.rearColor == Color.BLUE) ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
			osa1 = Osa.Y;
			osa2 = Osa.X;
			deo1 = (kockica.rearColor == Color.BLUE) ? Kocka.ROW_BOTTOM : Kocka.ROW_TOP;
			deo2 = Kocka.COLUMN_LEFT;
			smer1 = Smer.CLOCKWISE;
			smer2 = (kockica.rearColor == Color.BLUE) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE;
		}
		else if (color == Color.ORANGE) {
			zSmer = (kockica.rearColor == Color.GREEN) ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
			osa1 = Osa.Y;
			osa2 = Osa.X;
			deo1 = (kockica.rearColor == Color.GREEN) ? Kocka.ROW_TOP : Kocka.ROW_BOTTOM;
			deo2 = Kocka.COLUMN_RIGHT;
			smer1 = Smer.COUNTER_CLOCKWISE;
			smer2 = (kockica.rearColor == Color.GREEN) ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
		}
		
		moveRearEdgeToMiddle(zSmer, osa1, deo1, smer1, osa2, deo2, smer2);
	}
	
	private void moveRearEdgeToMiddle(Smer zSmer, Osa osa1, int deo1, Smer smer1, Osa osa2, int deo2, Smer smer2) {
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, zSmer));
		rotirajPaDodaj(new Rotacija(osa1, deo1, smer1));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, zSmer.reverse()));
		rotirajPaDodaj(new Rotacija(osa1, deo1, smer1.reverse()));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, zSmer.reverse()));
		rotirajPaDodaj(new Rotacija(osa2, deo2, smer2));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, zSmer));
		rotirajPaDodaj(new Rotacija(osa2, deo2, smer2.reverse()));
	}
	
	// vraca true ako se srednja ivica moze resiti iz zadate pozicije
	private boolean jeLiSrednjaIvicaResiva(KockicaPozicija pozicija) {
		if (!pozicija.ravanPozadi()) 
			return false;
		
		Color color = getBojaNeZadnjeIvice(pozicija);
		
		Color centerColor = null;
		if (pozicija.redGore())           
			centerColor = Kockica.BSK_TOP;
		else if (pozicija.redDole())   
			centerColor = Kockica.BSK_BOTTOM;
		else if (pozicija.kolonaLevo())  
			centerColor = Kockica.BSK_LEFT;
		else if (pozicija.kolonaDesno()) 
			centerColor = Kockica.BSK_RIGHT;
		
		return color == centerColor;
	}
	
	/*********************************************************************************************************************************************************/
	
	private void resiKorak4() {
		List<KockicaPozicija> pozicije = getPozicijeZadnjihIvica();
		
		int i = 0;
		while (!korak4Resen()) {
			korak4RtirajZadnjuIvicu(pozicije.get(i%4));
			i++;
		}
	}
	
	private void korak4RtirajZadnjuIvicu(KockicaPozicija pozicija) {
		Color color = getBojaNeZadnjeIvice(pozicija);
		
		if (color != Kockica.BSK_REAR) 
			return;
		
		Osa osa1 = null, osa2 = null;
		int deo1 = 0, deo2 = 0;
		Smer smer1 = null, smer2 = null;
		
		if (pozicija.redGore() || pozicija.redDole()) {
			osa1 = Osa.Y;
			osa2 = Osa.X;
			deo1 = pozicija.y;
			deo2 = pozicija.redGore() ? Kocka.COLUMN_RIGHT : Kocka.COLUMN_LEFT;
			smer1 = pozicija.redGore() ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
			smer2 = pozicija.redGore() ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
		}
		else if (pozicija.kolonaLevo() || pozicija.kolonaDesno()) {
			osa1 = Osa.X;
			osa2 = Osa.Y;
			deo1 = pozicija.x;
			deo2 = pozicija.kolonaLevo() ? Kocka.ROW_TOP : Kocka.ROW_BOTTOM;
			smer1 = pozicija.kolonaLevo() ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE;
			smer2 = pozicija.kolonaLevo() ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
		}
			
		rotirajPaDodaj(new Rotacija(osa1, deo1, smer1));
		rotirajPaDodaj(new Rotacija(osa2, deo2, smer2));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.COUNTER_CLOCKWISE));
		rotirajPaDodaj(new Rotacija(osa2, deo2, smer2.reverse()));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
		rotirajPaDodaj(new Rotacija(osa1, deo1, smer1.reverse()));
	}
	
	private boolean korak4Resen() {
		List<KockicaPozicija> pozicije = getPozicijeZadnjihIvica();
		
		for (KockicaPozicija pozicija : pozicije) {
			Kockica kockica = kocka.getKockica(pozicija);
			if (kockica.rearColor != Kockica.BSK_REAR) 
				return false;
		}
		
		return true;
	}
	
	/*********************************************************************************************************************************************************/
	
	private void resiKorak5() {
		korak5ReskiGornjuZadnjuIvicu();
		
		while (!korak5Resen()) {
			if (kocka.pozicijaResena(EDGE_REAR_LEFT)) {
				rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
				rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
			}
			else if (kocka.pozicijaResena(EDGE_REAR_RIGHT)) {
				rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
			}
			
			rotirajPaDodaj(new Rotacija(Osa.X, Kocka.COLUMN_RIGHT, Smer.CLOCKWISE));
			rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.COUNTER_CLOCKWISE));
			rotirajPaDodaj(new Rotacija(Osa.X, Kocka.COLUMN_RIGHT, Smer.COUNTER_CLOCKWISE));
			rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.COUNTER_CLOCKWISE));
			rotirajPaDodaj(new Rotacija(Osa.X, Kocka.COLUMN_RIGHT, Smer.CLOCKWISE));
			rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.COUNTER_CLOCKWISE));
			rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.COUNTER_CLOCKWISE));
			rotirajPaDodaj(new Rotacija(Osa.X, Kocka.COLUMN_RIGHT, Smer.COUNTER_CLOCKWISE));
			
			korak5ReskiGornjuZadnjuIvicu();
		}
	}
	
	private void korak5ReskiGornjuZadnjuIvicu() {
		while (kocka.getKockica(EDGE_REAR_TOP).topColor != Kockica.BSK_TOP) {
			rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
		}
	}
	
	private boolean korak5Resen() {
		List<KockicaPozicija> pozicije = getPozicijeZadnjihIvica();
		
		for (KockicaPozicija pozicija : pozicije) {
			if (!kocka.pozicijaResena(pozicija)) return false;
		}
		
		return true;
	}
	
	/*********************************************************************************************************************************************************/
	
	private void resiKorak6() {
		KockicaPozicija pozicija = nadjiZadnjiUgaoNaTacnojPoziciji();
		while (pozicija == null) {
			korak6RotirajZadnjeUglove(Osa.X, Kocka.COLUMN_RIGHT, Kocka.COLUMN_LEFT, Smer.COUNTER_CLOCKWISE);
			pozicija = nadjiZadnjiUgaoNaTacnojPoziciji();
		}
		
		while(!korak6Resen()) {
			Osa osa = null;
			int deo1 = 0, deo2 = 0;
			Smer smer = pozicija.redGore() ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
			
			if (pozicija.equals(CORNER_REAR_BOTTOM_LEFT)) {
				osa = Osa.X;
				deo1 = Kocka.COLUMN_RIGHT;
				deo2 = Kocka.COLUMN_LEFT;
			}
			else if (pozicija.equals(CORNER_REAR_BOTTOM_RIGHT)) {
				osa = Osa.Y;
				deo1 = Kocka.ROW_TOP;
				deo2 = Kocka.ROW_BOTTOM;
			}
			else if (pozicija.equals(CORNER_REAR_TOP_LEFT)) {
				osa = Osa.Y;
				deo1 = Kocka.ROW_BOTTOM;
				deo2 = Kocka.ROW_TOP;
			}
			else if (pozicija.equals(CORNER_REAR_TOP_RIGHT)) {
				osa = Osa.X;
				deo1 = Kocka.COLUMN_LEFT;
				deo2 = Kocka.COLUMN_RIGHT;
			}
			
			korak6RotirajZadnjeUglove(osa, deo1, deo2, smer);
		}
	}
	
	private void korak6RotirajZadnjeUglove(Osa osa, int deo1, int deo2, Smer smer) {
		rotirajPaDodaj(new Rotacija(osa, deo1, smer));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.COUNTER_CLOCKWISE));
		rotirajPaDodaj(new Rotacija(osa, deo2, smer));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
		rotirajPaDodaj(new Rotacija(osa, deo1, smer.reverse()));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.COUNTER_CLOCKWISE));
		rotirajPaDodaj(new Rotacija(osa, deo2, smer.reverse()));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
	}
	
	private KockicaPozicija nadjiZadnjiUgaoNaTacnojPoziciji() {
		List<KockicaPozicija> pozicije = getPozicijeZadnjihUglova();
		for (KockicaPozicija pozicija : pozicije) {
			if (kocka.pozicijaIspravna(pozicija)) 
				return pozicija;
		}
		return null;
	}
	
	private boolean korak6Resen() {
		List<KockicaPozicija> pozicije = getPozicijeZadnjihUglova();
		
		for (KockicaPozicija pozicija : pozicije) {
			if (!kocka.pozicijaIspravna(pozicija)) 
				return false;
		}
		
		return true;
	}
	
	/*********************************************************************************************************************************************************/
	
	private void resiKorak7() {		
		List<KockicaPozicija> pozicije = new ArrayList<KockicaPozicija>();
		pozicije.add(CORNER_REAR_TOP_RIGHT);
		pozicije.add(CORNER_REAR_BOTTOM_RIGHT);
		pozicije.add(CORNER_REAR_BOTTOM_LEFT);
		pozicije.add(CORNER_REAR_TOP_LEFT);
		
		for (KockicaPozicija pozicija : pozicije) {
			while (!kocka.pozicijaResena(pozicija)) {
				korak7RotirajZadnjiUgao(pozicija);
			}
		}
	}
	
	private void korak7RotirajZadnjiUgao(KockicaPozicija pozicija) {
		Osa osa = null;
		int deo1 = 0, deo2 = 0;
		Smer smer = pozicija.redDole() ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE;
			
		if (pozicija.equals(CORNER_REAR_TOP_RIGHT)) {
			osa = Osa.X;
			deo1 = Kocka.COLUMN_LEFT;
			deo2 = Kocka.COLUMN_RIGHT;
		}
		else if (pozicija.equals(CORNER_REAR_BOTTOM_RIGHT)) {
			osa = Osa.Y;
			deo1 = Kocka.ROW_TOP;
			deo2 = Kocka.ROW_BOTTOM;
		}
		else if (pozicija.equals(CORNER_REAR_BOTTOM_LEFT)) {
			osa = Osa.X;
			deo1 = Kocka.COLUMN_RIGHT;
			deo2 = Kocka.COLUMN_LEFT;
		}
		else if (pozicija.equals(CORNER_REAR_TOP_LEFT)) {
			osa = Osa.Y;
			deo1 = Kocka.ROW_BOTTOM;
			deo2 = Kocka.ROW_TOP;
		}
		
		rotirajPaDodaj(new Rotacija(osa, deo1, smer));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.COUNTER_CLOCKWISE));
		rotirajPaDodaj(new Rotacija(osa, deo1, smer.reverse()));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.COUNTER_CLOCKWISE));
		
		rotirajPaDodaj(new Rotacija(osa, deo1, smer));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
		rotirajPaDodaj(new Rotacija(osa, deo1, smer.reverse()));
		
		rotirajPaDodaj(new Rotacija(osa, deo2, smer));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
		rotirajPaDodaj(new Rotacija(osa, deo2, smer.reverse()));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
		
		rotirajPaDodaj(new Rotacija(osa, deo2, smer));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
		rotirajPaDodaj(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, Smer.CLOCKWISE));
		rotirajPaDodaj(new Rotacija(osa, deo2, smer.reverse()));
	}
	
	/*********************************************************************************************************************************************************/
	
	private KockicaPozicija nadjiPozicijuKockiceSaBojama(Color ... colors) {
		KockicaPozicija pozicija = null;
		outerLoop:
		for (int x=0; x<kocka.getSize(); x++) {
			for (int y=0; y<kocka.getSize(); y++) {
				innerLoop:
				for (int z=0; z<kocka.getSize(); z++) {
					pozicija = new KockicaPozicija(x, y, z);
					List<Color> bojePozicije = kocka.getVidljiveBoje(pozicija);
					
					if (colors.length != bojePozicije.size()) 
						continue;
					
					for (Color color : colors) {
						if (!bojePozicije.contains(color)) 
							continue innerLoop;
					}
					
					break outerLoop;
				}
			}
		}
		return pozicija;
	}
	
	// vraca boju koja nije na zadnjoj strani. Pozicija mora da bude na zadnjoj strani
	private Color getBojaNeZadnjeIvice(KockicaPozicija pozicija) {
		Kockica kockica = kocka.getKockica(pozicija);
		if (pozicija.redGore()) 
			return kockica.topColor;
		else if (pozicija.redDole()) 
			return kockica.bottomColor;
		else if (pozicija.kolonaLevo()) 
			return kockica.leftColor;
		else 
			return kockica.rightColor;
	}
	
	
	private List<KockicaPozicija> getPozicijePrednjihIvica() {
		List<KockicaPozicija> ivice = new ArrayList<KockicaPozicija>();
		ivice.add(EDGE_FRONT_TOP);
		ivice.add(EDGE_FRONT_BOTTOM);
		ivice.add(EDGE_FRONT_LEFT);
		ivice.add(EDGE_FRONT_RIGHT);
		return ivice;
	}
	
	private List<KockicaPozicija> getPozicijePrednjihUglova() {
		List<KockicaPozicija> uglovi = new ArrayList<KockicaPozicija>();
		uglovi.add(CORNER_FRONT_BOTTOM_LEFT);
		uglovi.add(CORNER_FRONT_BOTTOM_RIGHT);
		uglovi.add(CORNER_FRONT_TOP_LEFT);
		uglovi.add(CORNER_FRONT_TOP_RIGHT);
		return uglovi;
	}
	
	private List<KockicaPozicija> getPozicijeSrednjihIvica() {
		List<KockicaPozicija> ivice = new ArrayList<KockicaPozicija>();
		ivice.add(EDGE_MIDDLE_TOP_RIGHT);
		ivice.add(EDGE_MIDDLE_BOTTOM_RIGHT);
		ivice.add(EDGE_MIDDLE_TOP_LEFT);
		ivice.add(EDGE_MIDDLE_BOTTOM_LEFT);
		return ivice;
	}
	
	private List<KockicaPozicija> getPozicijeZadnjihIvica() {
		List<KockicaPozicija> ivice = new ArrayList<KockicaPozicija>();
		ivice.add(EDGE_REAR_TOP);
		ivice.add(EDGE_REAR_BOTTOM);
		ivice.add(EDGE_REAR_LEFT);
		ivice.add(EDGE_REAR_RIGHT);
		return ivice;
	}
	
	private List<KockicaPozicija> getPozicijeZadnjihUglova() {
		List<KockicaPozicija> uglovi = new ArrayList<KockicaPozicija>();
		uglovi.add(CORNER_REAR_BOTTOM_LEFT);
		uglovi.add(CORNER_REAR_BOTTOM_RIGHT);
		uglovi.add(CORNER_REAR_TOP_LEFT);
		uglovi.add(CORNER_REAR_TOP_RIGHT);
		return uglovi;
	}
	
}