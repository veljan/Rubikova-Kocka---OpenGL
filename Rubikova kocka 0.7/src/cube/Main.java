package cube;
/*
 * Uputsvo
 * 
Nova igra  - N
Resi kocku - R (pozovi solver)
Vrati unazad poteze - H
Razbacaj   - P

Manipulacija kocke
Okretanje X ose: 
  1. deo CW - Q 
  2. deo CW - W
  3. deo CW - E
  1. deo CCW - Shift + Q 
  2. deo CCW - Shift + W
  3. deo CCW - Shift + E 
Okretanje Y ose: 
  1. deo CW - A 
  2. deo CW - S
  3. deo CW - D
  1. deo CCW - Shift + A 
  2. deo CCW - Shift + S
  3. deo CCW - Shift + D
Okretanje Z ose: 
  1. deo CW - Z
  2. deo CW - X
  3. deo CW - C
  1. deo CCW - Shift + Z 
  2. deo CCW - Shift + X
  3. deo CCW - Shift + C

Strelice:
Rotiranje kamere - Up/Down 
Rotiranje kamere - Left/Right 
Rotiranje kamere - Shift + Strelice
Vrati na default - Y

 */

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		RubikovaKocka rc = new RubikovaKocka();
		rc.play();

	}

}
