package cube;

/* 
 * Wrapper class za cuvanje koordinata Kockice 
 */

public class KockicaPozicija {

	int x, y, z;
	
	public KockicaPozicija(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean kolonaLevo()   { 
		return this.x == Kocka.COLUMN_LEFT;   
	}
	
	public boolean kolonaSredina() { 
		return this.x == Kocka.COLUMN_MIDDLE; 
	}
	
	public boolean kolonaDesno()  { 
		return this.x == Kocka.COLUMN_RIGHT;  
	}
	
	public boolean redDole()    { 
		return this.y == Kocka.ROW_BOTTOM;    
	}
	
	public boolean redSredina()    { 
		return this.y == Kocka.ROW_MIDDLE;    
	}
	public boolean redGore()       { 
		return this.y == Kocka.ROW_TOP;       
	}
	
	public boolean ravanNapred()    { 
		return this.z == Kocka.PLAIN_FRONT;   
	}
	
	public boolean ravanSredina()   { 
		return this.z == Kocka.PLAIN_MIDDLE;  
	}
	
	public boolean ravanPozadi()     { 
		return this.z == Kocka.PLAIN_REAR;   
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		KockicaPozicija other = (KockicaPozicija) obj;
		if (x != other.x) return false;
		if (y != other.y) return false;
		if (z != other.z) return false;
		return true;
	}
	
}