package cube;
/*
 * Predstavlja jednu kockicku u Rubikovoj kocki 3x3x3
 */
public class Kockica {
	
	public static enum Color { 
		WHITE, YELLOW, GREEN, ORANGE, BLUE, RED; 
	};
	
	// 
	public static final int KOCKICA_FRONT  = (1 << 0);
	public static final int KOCKICA_REAR   = (1 << 1);
	public static final int KOCKICA_LEFT   = (1 << 2);
	public static final int KOCKICA_RIGHT  = (1 << 3);
	public static final int KOCKICA_TOP    = (1 << 4);
	public static final int KOCKICA_BOTTOM = (1 << 5);
	
	// BSK - Boja Slozene Kocke 
	public static final Color BSK_FRONT  = Color.WHITE; 	// BSK - prednje strane
	public static final Color BSK_REAR   = Color.YELLOW;	// BSK - zadnje strane
	public static final Color BSK_TOP    = Color.GREEN;		// BSK - gornje strane
	public static final Color BSK_BOTTOM = Color.BLUE;		// BSK - donje strane
	public static final Color BSK_LEFT   = Color.RED;		// BSK - leve strane
	public static final Color BSK_RIGHT  = Color.ORANGE;	// BSK - desne strane
	
	
	Color frontColor  = BSK_FRONT;
	Color rearColor   = BSK_REAR;
	Color topColor    = BSK_TOP;
	Color bottomColor = BSK_BOTTOM;
	Color leftColor   = BSK_LEFT;
	Color rightColor  = BSK_RIGHT;
	
	public Kockica() { }
	
	public Kockica(Color front, Color rear, Color top, Color bottom, Color left, Color right) {
		this.frontColor = front;
		this.rearColor = rear;
		this.topColor = top;
		this.bottomColor = bottom;
		this.leftColor = left;
		this.rightColor = right;
	}
	
	public Kockica getCopy() {
		return new Kockica(frontColor, rearColor, topColor, bottomColor, leftColor, rightColor);
	}	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Kockica other = (Kockica) obj;
		if (bottomColor != other.bottomColor)
			return false;
		if (frontColor != other.frontColor)
			return false;
		if (leftColor != other.leftColor)
			return false;
		if (rearColor != other.rearColor)
			return false;
		if (rightColor != other.rightColor)
			return false;
		if (topColor != other.topColor)
			return false;
		return true;
	}

}
