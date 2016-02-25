package cube;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_NICEST;
import static com.jogamp.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static com.jogamp.opengl.GL2GL3.GL_QUADS;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;

import cube.Kockica.Color;
import cube.Rotacija.Osa;
import cube.Rotacija.Smer;


public class RubikovaKocka extends Frame implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
	
	
	private static final long serialVersionUID = 1L;

	private static final String TITLE = "Rubikova kocka JOGL";
	
	private static final int CANVAS_WIDTH  = 720;
	private static final int CANVAS_HEIGHT = 480;	
	
	private static final float ZERO_F = 0.0f;
	private static final float ONE_F  = 1.0f;	
	
	private static final float RAZMAK_TRANSLATE = 2.0f + 0.1f;	//  razmak izmedju kockica + faktor translacije kockica
	
	private static final float X_UGAO_KAMERE_DEFAULT = 45.0f;
	private static final float Y_UGAO_KAMERE_DEFALUT  = 45.0f;
		
	private static final int UGAO_ROTIRANJA_DELA = 90;
	private static final int UGAO_RORIRANJA_KAMERE  = 5;
	
	private GLU glu;
	
	private GLCapabilities capabilities;
    private Animator animator;
    private GLProfile profile;
	private GLCanvas canvas;
	
	private float ugaoKamereX = X_UGAO_KAMERE_DEFAULT;
	private float ugaoKamereY = Y_UGAO_KAMERE_DEFALUT;
	private float ugaoKamereZ = 0.0f;	
	
	private float[] ugaoKoleneX;
	private float[] ugaoRedaY;
	private float[] ugaoRavniZ;
	
	private int rotacijaDelaX = -1;
	private int rotacijaDelaY = -1;
	private int rotacijaDelaZ = -1;
	private float ugaonaBrzina = 1.0f; // brzina i smer rotirajucih delova
	
	private int mouseX = CANVAS_WIDTH/2;
	private int mouseY = CANVAS_HEIGHT/2;
	
	private int size = 3;
	
	private Kocka rubikovaKocka, slozenaKocka;
	public static RubikovaKocka kocka;
	
	private RotationAnimatorThread razbacajAnimatorThread;
	private RotationAnimatorThread srediAnimatorThread;
	private RotationAnimatorThread resiAnimatorThread;
	
	private int brojRotacija  = 50;
	private boolean igra = false;
	
	public ArrayList<Rotacija> unazad = new ArrayList<Rotacija>();

	public RubikovaKocka() {
		rubikovaKocka = new Kocka(size);
		slozenaKocka = new Kocka(size); 
		
		this.ugaoKoleneX = new float[size];
		this.ugaoRedaY = new float[size];
		this.ugaoRavniZ = new float[size];
		
		profile = GLProfile.getDefault();
	    capabilities = new GLCapabilities(profile);
    	canvas = new GLCanvas(capabilities);
		animator = new Animator(canvas);		
		
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);		
		canvas.addMouseMotionListener(this);	
		
		add(canvas, BorderLayout.CENTER);
		setResizable(false);		

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				animator.stop();
				dispose();
				System.exit(0);
			}
		});
	}
	
	public void play() {
		// TODO Auto-generated method stub
		setTitle(TITLE);
		setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setVisible(true);
		animator.start();
		canvas.requestFocus();
	}	
		
	public void novaIgra(){	
		rubikovaKocka = new Kocka(3);	
		unazad.clear();				
		ugaoKoleneX = new float[rubikovaKocka.getSize()];
		ugaoRedaY = new float[rubikovaKocka.getSize()];
		ugaoRavniZ = new float[rubikovaKocka.getSize()];
		rubikovaKocka.resetState();	
		razbacaj();		
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		glu = new GLU();
		gl.glClearColor(ZERO_F, ZERO_F, ZERO_F, ZERO_F);
		gl.glClearDepth(ONE_F); 
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		gl.glShadeModel(GL_SMOOTH);
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
	      
		if (height == 0) 
			height = 1;
		float aspect = (float) width/height;
		
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0, aspect, 0.1, 100.0);
			 
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void display(GLAutoDrawable drawable) {		
		azurirajUgloveRotacije();
		drawRubikovuKocku(drawable.getGL().getGL2());
		pobedaIspis();		
	}
	
	private void drawRubikovuKocku(GL2 gl) {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		// camera transformations
		gl.glTranslatef(ZERO_F, ZERO_F, -20.0f);
		gl.glRotatef(ugaoKamereX, ONE_F, ZERO_F, ZERO_F);
		gl.glRotatef(ugaoKamereY, ZERO_F, ONE_F, ZERO_F);
		gl.glRotatef(ugaoKamereZ, ZERO_F, ZERO_F, ONE_F);
		
		gl.glPushMatrix();					
			gl.glTranslatef(0.0f, 0.0f, 0.0f);
		
			gl.glBegin(GL2.GL_LINES);
				// X osa
				gl.glColor3f(0.2f, 0.2f, 0.2f);
				gl.glVertex3f(-10.0f, 0.0f, 0.0f);
				gl.glColor3f(0.2f, 0.8f, 0.2f);
				gl.glVertex3f(10.0f, 0.0f, 0.0f);
				// Y osa
				gl.glColor3f(0.2f, 0.2f, 0.2f);
				gl.glVertex3f(0.0f, -10.0f, 0.0f);
				gl.glColor3f(0.8f, 0.2f, 0.2f);
				gl.glVertex3f(0.0f, 10.0f, 0.0f);
				// Z osa
				gl.glColor3f(0.2f, 0.2f, 0.2f);
				gl.glVertex3f(0.0f, 0.0f, -10.0f);
				gl.glColor3f(0.2f, 0.2f, 0.8f);
				gl.glVertex3f(0.0f, 0.0f, 10.0f);
				gl.glEnd();				
			gl.glPopMatrix();
		
		int lastIdx = rubikovaKocka.getSize()-1;
		for (int x = 0; x < rubikovaKocka.getSize(); x++) {
			for (int y = 0; y < rubikovaKocka.getSize(); y++) {
				for (int z = 0; z < rubikovaKocka.getSize(); z++) {
					gl.glPushMatrix();
					
					gl.glRotatef(ugaoKoleneX[x], ONE_F, ZERO_F, ZERO_F);
					gl.glRotatef(ugaoRedaY[y], ZERO_F, ONE_F, ZERO_F);
					gl.glRotatef(ugaoRavniZ[z], ZERO_F, ZERO_F, ONE_F);
					
					float t = (float) lastIdx/2;
					gl.glTranslatef((x-t)*RAZMAK_TRANSLATE, (y-t)*RAZMAK_TRANSLATE, -(z-t)*RAZMAK_TRANSLATE);
					
					drawKockicu(gl, rubikovaKocka.getVidljiveStrane(x, y, z), rubikovaKocka.getKockica(x, y, z));
						
					gl.glPopMatrix();
				}
			}
		}
	}
	
	private void drawKockicu(GL2 gl, int vidljiveStrane, Kockica kockica) {
		gl.glBegin(GL_QUADS);
		
		// gornja strana
		gl.glColor3f(0.3f, 0.3f, 0.3f);
		if ((vidljiveStrane & Kockica.KOCKICA_TOP) > 0) 
			glDadajBoju(gl, kockica.topColor);
    	gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
	 
		// donja strana
		gl.glColor3f(0.3f, 0.3f, 0.3f);
		if ((vidljiveStrane & Kockica.KOCKICA_BOTTOM) > 0) 
			glDadajBoju(gl, kockica.bottomColor);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
			 
		// prednja strana
		gl.glColor3f(0.3f, 0.3f, 0.3f);
		if ((vidljiveStrane & Kockica.KOCKICA_FRONT) > 0) 
			glDadajBoju(gl, kockica.frontColor);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
			 
		// zadnja strana
		gl.glColor3f(0.3f, 0.3f, 0.3f);
		if ((vidljiveStrane & Kockica.KOCKICA_REAR) > 0) 
			glDadajBoju(gl, kockica.rearColor);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
			 
		// leva strana (u odnosu na prednju)
		gl.glColor3f(0.3f, 0.3f, 0.3f);
		if ((vidljiveStrane & Kockica.KOCKICA_LEFT) > 0) 
			glDadajBoju(gl, kockica.leftColor);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
	 
		// desna strana (u odnosu na prednju)
		gl.glColor3f(0.3f, 0.3f, 0.3f);
		if ((vidljiveStrane & Kockica.KOCKICA_RIGHT) > 0) 
			glDadajBoju(gl, kockica.rightColor);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
			
		gl.glEnd();
	}
	
	private void glDadajBoju(GL2 gl, Color color) {
		switch (color) {
			case WHITE:
				gl.glColor3f(1.0f, 1.0f, 1.0f); 
				break;
			case YELLOW:
				gl.glColor3f(1.0f, 1.0f, 0.0f); 
				break;
			case GREEN:
				gl.glColor3f(0.0f, 1.0f, 0.0f); 
				break;
			case ORANGE:
				gl.glColor3f(1.0f, 1.0f/2, 0.0f); 
				break;
			case BLUE:
				gl.glColor3f(0.0f, 0.0f, 1.0f); 
				break;
			case RED:
				gl.glColor3f(1.0f, 0.0f, 0.0f); 
				break;
		}
	}
	
	private boolean daLiRotira() {
		return rotacijaDelaX + rotacijaDelaY + rotacijaDelaZ > -3;
	}
	
	private void azurirajUgloveRotacije() {
		Smer smer = (ugaonaBrzina > 0) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE;
		
		if (rotacijaDelaX >= 0) {
			ugaoKoleneX[rotacijaDelaX] += ugaonaBrzina;
			if (ugaoKoleneX[rotacijaDelaX] % UGAO_ROTIRANJA_DELA == 0) {
				ugaoKoleneX[rotacijaDelaX] = 0;
				rubikovaKocka.rotiraj(new Rotacija(Osa.X, rotacijaDelaX, smer));
				rotacijaDelaX = -1;
			}
		}
		else if (rotacijaDelaY >= 0) {
			ugaoRedaY[rotacijaDelaY] += ugaonaBrzina;
			if (ugaoRedaY[rotacijaDelaY] % UGAO_ROTIRANJA_DELA == 0) {
				ugaoRedaY[rotacijaDelaY] = 0;
				rubikovaKocka.rotiraj(new Rotacija(Osa.Y, rotacijaDelaY, smer));
				rotacijaDelaY = -1;
			}
		}
		else if (rotacijaDelaZ >= 0) {
			ugaoRavniZ[rotacijaDelaZ] += ugaonaBrzina;
			if (ugaoRavniZ[rotacijaDelaZ] % UGAO_ROTIRANJA_DELA == 0) {
				ugaoRavniZ[rotacijaDelaZ] = 0;
				rubikovaKocka.rotiraj(new Rotacija(Osa.Z, rotacijaDelaZ, smer));
				rotacijaDelaZ = -1;
			}
		}
	}
	
	// deo je indeks kolone/reda/ravni koji treba da se rotira.
	// ako je reverse == true onda je rotacija clockwise
	private void rotirajDeo(int deo, Osa osa, boolean reverse) {
		// da li se trenutno rotira?
		if (!daLiRotira()) {
			if (osa == Osa.X)
				rotacijaDelaX = deo;
			if (osa == Osa.Y) 
				rotacijaDelaY = deo;
			if (osa == Osa.Z) 
				rotacijaDelaZ = deo;
			ugaonaBrzina = reverse ? -Math.abs(ugaonaBrzina) : Math.abs(ugaonaBrzina);
		}		
	}
	
	private void pobedaIspis(){	
		if(pobeda() && igra){	
			igra=false;		
			JOptionPane.showMessageDialog(canvas, "POBEDA!");			
		}
	}
	
	private boolean pobeda(){		
		int size = slozenaKocka.getSize();		
		for (int j=0; j<Osa.values().length; j++){
			for (int i=0; i<=size; i++) {				
				if(!slozenaKocka.equals(rubikovaKocka) ){
					rotirajCeluKocku(Osa.values()[j]);					
				}
				else {
					//animator.pause();
					//igra = true;
					return true;					
				}
			}
		}
		return false;		
	}
	
	private void rotirajCeluKocku(Osa osa) {
		// TODO Auto-generated method stub
		slozenaKocka.rotiraj(new Rotacija(osa, 0, Smer.CLOCKWISE));
		slozenaKocka.rotiraj(new Rotacija(osa, 1, Smer.CLOCKWISE));
		slozenaKocka.rotiraj(new Rotacija(osa, 2, Smer.CLOCKWISE));		
		
	}	

	private abstract class RotationAnimatorThread extends Thread {
		private boolean isTerminated = false;
		
		public void terminate() { 
			isTerminated = true;			
		}
		
		protected abstract int getDeo(int i);
		protected abstract Osa getOsa(int i);
		protected abstract boolean isReverse(int i);
		protected abstract boolean isComplete(int i);
		
		@Override
		public void run() {
			int i = 0;
			while (!isTerminated && !isComplete(i)) {
				while (daLiRotira()) {
					try { 
						Thread.sleep(0); 
					}
					catch (InterruptedException e) { }
				}
				rotirajDeo(getDeo(i), getOsa(i), isReverse(i));
				i++;
			}
		}
	}
	
	
	private void razbacaj() {		
		if (razbacajAnimatorThread == null || !razbacajAnimatorThread.isAlive()) {
			ArrayList<Rotacija> rotacije = new ArrayList<Rotacija>();
			int i = 0;
			while(i < brojRotacija){
				Osa OSA = Osa.values()[new Random().nextInt(Osa.values().length)];
				int DEO = new Random().nextInt(rubikovaKocka.getSize());
				Smer SMER = new Random().nextBoolean() ? Smer.CLOCKWISE : Smer.COUNTER_CLOCKWISE; 
				
				rotacije.add(new Rotacija(OSA, DEO, SMER));
				unazad.add(new Rotacija(OSA, DEO, SMER.reverse()));
				i++;
			}
			
			System.out.println("Napravljeno je " + rotacije.size() + " pokreta");
			
			razbacajAnimatorThread = new RotationAnimatorThread() {
				@Override 
				protected int getDeo(int i) { 					
					return rotacije.get(i).getDeo(); 
				}
				@Override 
				protected Osa getOsa(int i) {					
					return rotacije.get(i).getOsa(); 
				}
				@Override 
				protected boolean isReverse(int i) { 
					return rotacije.get(i).isClockwise();
				}
				@Override 
				protected boolean isComplete(int i) {
					if(i == brojRotacija){
						igra = true	;
					}
					return igra;
				}
			};
			razbacajAnimatorThread.start();			
		}
		else {
			razbacajAnimatorThread.terminate();			
		}
	}
	
	private void sredi() {
		if (srediAnimatorThread == null || !srediAnimatorThread.isAlive()) {			
			List<Rotacija> rotacije = unazad;				
			Collections.reverse(rotacije);
			System.out.println("Napravljeno je " + rotacije.size() + " pokreta");
		
			srediAnimatorThread = new RotationAnimatorThread() {
				@Override protected int getDeo(int i) { 
					return rotacije.get(i).getDeo(); 
				}
				@Override protected Osa getOsa(int i) { 
					return rotacije.get(i).getOsa(); 
				}
				@Override protected boolean isReverse(int i) { 
					return rotacije.get(i).isClockwise(); 
				}
				@Override protected boolean isComplete(int i) { 
					if(i == rotacije.size()){
						unazad.clear();						
						rotacije.clear();
						//igra = false;
						return true;
					}
					return false; 
				}
			};
			srediAnimatorThread.start();
		}
		else {
			srediAnimatorThread.terminate();
			
		}
	}
	
	private void resi() {
		if (resiAnimatorThread == null || !resiAnimatorThread.isAlive()) {
			Solver solver = new SolverNeoptimizovani(rubikovaKocka.getCopy());
			final List<Rotacija> rotacije = solver.getResenje();
			System.out.println("Resenje ima " + rotacije.size() + " pokreta");
		
			resiAnimatorThread = new RotationAnimatorThread() {
				@Override protected int getDeo(int i) { 
					return rotacije.get(i).getDeo(); 
				}
				@Override protected Osa getOsa(int i) { 
					return rotacije.get(i).getOsa(); 
				}
				@Override protected boolean isReverse(int i) { 
					return rotacije.get(i).isClockwise(); 
				}
				@Override protected boolean isComplete(int i) {					
					return (i == rotacije.size()); 
				}
			};
			resiAnimatorThread.start();
		}
		else {
			resiAnimatorThread.terminate();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				ugaoKamereX -= UGAO_RORIRANJA_KAMERE;
				break;
			case KeyEvent.VK_DOWN:
				ugaoKamereX += UGAO_RORIRANJA_KAMERE;
				break;
			case KeyEvent.VK_LEFT:
				if (e.isShiftDown()) 
					ugaoKamereY += UGAO_RORIRANJA_KAMERE;
				else 
					ugaoKamereY -= UGAO_RORIRANJA_KAMERE;
				break;
			case KeyEvent.VK_RIGHT:
				if (e.isShiftDown()) 
					ugaoKamereZ -= UGAO_RORIRANJA_KAMERE;
				else 
					ugaoKamereZ += UGAO_RORIRANJA_KAMERE;
				break;
			case KeyEvent.VK_Q: 				
				rotirajDeo(Kocka.COLUMN_LEFT, Osa.X, e.isShiftDown()); 				
				unazad.add(new Rotacija(Osa.X, Kocka.COLUMN_LEFT, (e.isShiftDown()) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE));				
				break;				
			case KeyEvent.VK_W:
				rotirajDeo(Kocka.COLUMN_MIDDLE, Osa.X, e.isShiftDown());
				unazad.add(new Rotacija(Osa.X, Kocka.COLUMN_MIDDLE, (e.isShiftDown()) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE));				
				break;
			case KeyEvent.VK_E:
				rotirajDeo(Kocka.COLUMN_RIGHT, Osa.X, e.isShiftDown());
				unazad.add(new Rotacija(Osa.X, Kocka.COLUMN_RIGHT,  (e.isShiftDown()) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE));				
				break;
			case KeyEvent.VK_A:
				rotirajDeo(Kocka.ROW_BOTTOM, Osa.Y, e.isShiftDown()); 
				unazad.add(new Rotacija(Osa.Y, Kocka.ROW_BOTTOM, (e.isShiftDown()) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE));				
				break;
			case KeyEvent.VK_S:
				rotirajDeo(Kocka.ROW_MIDDLE, Osa.Y, e.isShiftDown()); 
				unazad.add(new Rotacija(Osa.Y, Kocka.ROW_MIDDLE, (e.isShiftDown()) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE));				
				break;
			case KeyEvent.VK_D:
				rotirajDeo(Kocka.ROW_TOP, Osa.Y, e.isShiftDown()); 
				unazad.add(new Rotacija(Osa.Y, Kocka.ROW_TOP, (e.isShiftDown()) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE));				
				break;
			case KeyEvent.VK_Z:
				rotirajDeo(Kocka.PLAIN_FRONT, Osa.Z, e.isShiftDown()); 
				unazad.add(new Rotacija(Osa.Z, Kocka.PLAIN_FRONT, (e.isShiftDown()) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE));				
				break;
			case KeyEvent.VK_X:
				rotirajDeo(Kocka.PLAIN_MIDDLE, Osa.Z, e.isShiftDown());
				unazad.add(new Rotacija(Osa.Z, Kocka.PLAIN_MIDDLE, (e.isShiftDown()) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE));				
				break;
			case KeyEvent.VK_C:
				rotirajDeo(Kocka.PLAIN_REAR, Osa.Z, e.isShiftDown());
				unazad.add(new Rotacija(Osa.Z, Kocka.PLAIN_REAR, (e.isShiftDown()) ? Smer.COUNTER_CLOCKWISE : Smer.CLOCKWISE));				
				break;
			case KeyEvent.VK_P:
				razbacaj();
				break;
			case KeyEvent.VK_H:
				sredi();				
				break;						
			case KeyEvent.VK_N:							
				novaIgra();						
				break;
			case KeyEvent.VK_R:
				resi();
				break;
			case KeyEvent.VK_L:
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				//String s = br.readLine();
				try {
					brojRotacija = Integer.parseInt(br.readLine());
				} catch (NumberFormatException e1) {				
					e1.printStackTrace();
				} catch (IOException e1) {					
					e1.printStackTrace();
				}				
				novaIgra();
				break;	
			case KeyEvent.VK_F1:
				JOptionPane.showMessageDialog(canvas, "Uputsvo!\n\n" 
						+" Nova igra - N \n Nova igra sa unosom broja rotacija preko konzole - L \n Resi kocku - R (pozovi solver) \n Vrati unazad poteze - H \n Razbacaj - P\n\n"
						+ "Okretanje X ose kocke: \n 1. deo CW - Q \n 2. deo CW - W \n 3. deo CW - E \n"
						+ " 1. deo CCW - Shift + Q \n 2. deo CCW - Shift + W \n 3. deo CCW - Shift + E \n\n" 
						+ " Okretanje Y ose kocke: \n 1. deo CW - A \n 2. deo CW - S \n 3. deo CW - D \n" 
						+ "1. deo CCW - Shift + A \n 2. deo CCW - Shift + S \n 3. deo CCW - Shift + D  \n\n " 
						+ "Okretanje Z ose kocke: \n 1. deo CW - Z \n 2. deo CW - X \n 3. deo CW - C\n" 
						+ "1. deo CCW - Shift + Z \n 2. deo CCW - Shift + X \n 3. deo CCW - Shift + C\n");
				break;
			case KeyEvent.VK_Y:
				ugaoKamereX = X_UGAO_KAMERE_DEFAULT;
				ugaoKamereY = Y_UGAO_KAMERE_DEFALUT;
				ugaoKamereZ = ZERO_F;				
				break;
		}
	}	
	
	@Override
	public void mouseDragged(MouseEvent e) {
		final int buffer = 2;
		
		if (e.getX() < mouseX-buffer) 
			ugaoKamereY -= UGAO_RORIRANJA_KAMERE;
		else if (e.getX() > mouseX+buffer) 
			ugaoKamereY += UGAO_RORIRANJA_KAMERE;
		
		if (e.getY() < mouseY-buffer) 
			ugaoKamereX -= UGAO_RORIRANJA_KAMERE;
		else if (e.getY() > mouseY+buffer) 
			ugaoKamereX += UGAO_RORIRANJA_KAMERE;
		
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	public void dispose(GLAutoDrawable drawable) { }
	public void keyReleased(KeyEvent e) { }
	public void keyTyped(KeyEvent e) { }
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) { }
	public void mouseWheelMoved(MouseEvent e) { }

}