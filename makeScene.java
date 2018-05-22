import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

public class makeScene implements ChangeListener, ActionListener, GLEventListener {
    
    float diameter;
    ArrayList<GLObject> objects;
    static String firstObjFile = "house.obj";
    static String secondObjFile = "tree.obj";
    static String fourthObjFile = "airboat.obj";
    static String thirdObjFile = "tattooine.obj";
    static String fifthObjFile="uh60.obj";
    GLObject object, object2;
    float xCam;
    float yCam;
    float zCam;
    int X = 0;
    int Y  = 0;
    int Z = 0;
    float cx, cy, cz;
    GL2 gl;
    GLU glu;
    GLUT glut;
    int ww = 800, wh = 800;
    static JFrame frame;
    JButton startButton, pauseButton, resumeButton,resetButton;
    JSlider zSlider, ySlider, xSlider;
    private JSlider ambientSlider, diffuseSlider, specularSlider;
    private JRadioButton light0Button, light1Button;

    boolean light0On = true;
    boolean light1On = true;

    private float lightAmbient[] = { .3f, .3f, .3f, 1f };
    private float lightDiffuse[] = { .7f, .7f, .7f, .7f };
    private float lightSpecular[] = { 1f, 1f, 1f, 1f };
    private float light0Position[] = { 0f, 0f, 1f, 0f };
    private float light1Position[] = { 0f, 1f, 0f, 0f };
    private float torusAmbient[] = { 1, 1, 1, 1 };
    private float torusDiffuse[] = {0.9f, 0, 0};
    private float materialSpecular[] = {0.9f, 0.9f, 0.9f};
    private static final String[] tList = { "walls.png", "cloth018.gif", "marchgr.png", "wt.png","marb015.jpg" };
    private int[] texName = new int[tList.length + 1];
    private int tIndex = 0;
    
    // for animation
    int anim;
    int orienVar=0;
	boolean startAnimation=false;
	boolean pauseAnimation=false;
	double time = 0.0;
    private double time2=0.0;
	JButton quitButton;
    private int timespan = 8;  // 8 seconds
    private float currPos0[] = {100,0,-16};
    private float endPos0[] = {200,0,-16};
    private float startPos0[] = {100,0,-16};
    private float currPos1[] = {100,0,-16};
    private float endPos1[] = {200,0,-16};
    private float startPos1[] = {100,0,-16};
    private float currPos2[] = {70,0,-16};
    private float endPos2[] = {200,0,-16};
    private float startPos2[] = {70,0,-16};
    private float currPos3[] = {70,0,-16};
    private float endPos3[] = {200,0,-16};
    private float startPos3[] = {70,0,-16};
    
    public void init(GLAutoDrawable drawable) {
	gl = drawable.getGL().getGL2();
	glu = new GLU();
	glut = new GLUT();
	gl.glEnable(GL2.GL_DEPTH_TEST);
	gl.glClearColor(0, 0, 0, 0);
	gl.glShadeModel(GL2.GL_SMOOTH);
	gl.glEnable(GL2.GL_LIGHTING);
	gl.glEnable(GL2.GL_LIGHT0);
	gl.glLightfv(GL2.GL_LIGHT0,  GL2.GL_POSITION, light0Position, 0);
	gl.glEnable(GL2.GL_LIGHT1);
	gl.glLightfv(GL2.GL_LIGHT1,  GL2.GL_POSITION, light1Position, 0);

	gl.glShadeModel(GL2.GL_FLAT);
	gl.glEnable(GL2.GL_TEXTURE_2D);
	gl.glEnable(GL2.GL_DEPTH_TEST);
	gl.glGenTextures(tList.length + 1, texName, 0);

	gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);
	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
	//make textures
	TexImage img = null;
	for (int k = 0; k < tList.length; k++) {
	    String fname = tList[k];

	    try {
		// call the TexImage constructor
		img = new TexImage(new File(fname));
	    } catch (IOException e) {
		System.out.println("error " + e.getMessage() + " opening file " + fname);
		continue;
	    }

	    byte[] pixels = img.getPixels();
	    gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[k + 1]);
	    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
	    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
	    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
	    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, img.getWidth(), img.getHeight(), 0, GL2.GL_RGBA,
		    GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(pixels));
	}
   }

    public void display(GLAutoDrawable drawable) {

	gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
	gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[tIndex]);
	if ((startAnimation==true) && (pauseAnimation==false)){
		update();
	}
	render(drawable);
	    }

    public void update(){

    	if(time<2.0){  // advance the timer
    	    time += 1.0/(30*timespan);
    	}
    	if(time<=2.0){// object 0 position
    	    currPos0[0] = (float)(startPos0[0] + time*(endPos0[0] - startPos0[0]));
    	    currPos0[1] = (float)(startPos0[1] + time*(endPos0[1] - startPos0[1]));
    	    currPos0[2] = (float)(startPos0[2] + time*(endPos0[2] - startPos0[2]));
    	}
    	else {
    	    currPos0[0] = endPos0[0];
    	    currPos0[1] = endPos0[1];
    	    currPos0[2] = endPos0[2];
    	}
    	if(time<=1.0){// object 1 position
    	    currPos1[0] = startPos1[0];
    	    currPos1[1] = startPos1[1];
    	    currPos1[2] = startPos1[2];
    	}
    	else {
    	    currPos1[0] = (float)(startPos1[0] + (time-1)*(endPos1[0] - startPos1[0]));
    	    currPos1[1] = (float)(startPos1[1] + (time-1)*(endPos1[1] - startPos1[1]));
    	    currPos1[2] = (float)(startPos1[2] + (time-1)*(endPos1[2] - startPos1[2]));
    	}
    	if(time<=1.0){// object 2 position
    	    currPos2[0] = startPos2[0];
    	    currPos2[1] = startPos2[1];
    	    currPos2[2] = startPos2[2];
    	}
    	else {
    	    currPos2[0] = (float)(startPos2[0] + (time-1)*(endPos2[0] - startPos2[0]));
    	    currPos2[1] = (float)(startPos2[1] + (time-1)*(endPos2[1] - startPos2[1]));
    	    currPos2[2] = (float)(startPos2[2] + (time-1)*(endPos2[2] - startPos2[2]));
    	}
    	if(time2<2.0){  // advance the timer
    	    time2 += 1.0/(30*timespan);
    	}
    	if(time2<=2.0){// object 0 position
    	    currPos3[0] = (float)(startPos3[0] + time*(endPos3[0] - startPos3[0]));
    	    currPos3[1] = (float)(startPos3[1] + time*(endPos3[1] - startPos3[1]));
    	    currPos3[2] = (float)(startPos3[2] + time*(endPos3[2] - startPos3[2]));
    	    orienVar +=1;
    	}
    }	

    public void render(GLAutoDrawable drawable) {
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		float distance = (float) (diameter / Math.tan(30));
		glu.gluLookAt(0, 0, distance - zCam, 0, 0, 0, 0, 1, 0);
		gl.glTranslatef(cx, cy, cz);
		gl.glRotatef(yCam, 0, 1, 0);
		gl.glRotatef(xCam, 1, 0, 0);
		gl.glTranslatef(-cx, -cy, -cz);
	
		gl.glEnable(GL2.GL_NORMALIZE);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
    	
		// specular material is always white
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_SPECULAR);
		gl.glColor3f(0, 1, 0);
    	
		// subsequent glColor calls are for ambient and diffuse
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
	
		//2d texture
		gl.glEnable(GL2.GL_TEXTURE_2D);
	
		// turn the lights on or off
		if (light0On)
		    gl.glEnable(GL2.GL_LIGHT0);
		else
		    gl.glDisable(GL2.GL_LIGHT0);
		if (light1On)
		    gl.glEnable(GL2.GL_LIGHT1);
		else
		    gl.glDisable(GL2.GL_LIGHT1);
	
		for (int light = GL2.GL_LIGHT0; light <= GL2.GL_LIGHT1; light++) {
		    gl.glLightfv(light, GL2.GL_AMBIENT, lightAmbient, 0);
		    gl.glLightfv(light, GL2.GL_DIFFUSE, lightDiffuse, 0);
		    gl.glLightfv(light, GL2.GL_SPECULAR, lightSpecular, 0);
		}

		//set the board
		gl.glPushMatrix();
		gl.glBegin(GL2.GL_QUADS);
		float boardVal = (float) 0.5 + (yMin() / 2);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, torusAmbient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, torusDiffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, materialSpecular, 0);
		for (int x = (int) distance / 10; x < -distance / 10; ++x) {
		    for (int z = (int) distance / 10; z < -distance / 10; ++z) {		
			if (((x + z) % 2) != 0) {
			    gl.glColor3f(0.5f,0.5f,0.5f);
			} else
			    gl.glColor3f(1, 1, 1);
			gl.glVertex3f(x * 50, boardVal, z * 50);
			gl.glVertex3f((x + 1) * 50, boardVal, z * 50);
			gl.glVertex3f((x + 1) * 50, boardVal, (z + 1) * 50);
			gl.glVertex3f(x * 50, boardVal, (z + 1) * 50);
		    }
		}
		gl.glEnd();
		gl.glPopMatrix();
	
	//rendering obj files
	gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[2]);
	renderSecondObjFile(-cx+120, -cy+12, -cz+20, drawable, objects.get(1));
	renderSecondObjFile(-cx+100, -cy+12, -cz-20, drawable, objects.get(1));
	renderSecondObjFile(-cx+110, -cy+12, -cz-30, drawable, objects.get(1));
	renderSecondObjFile(-cx+118, -cy+12, -cz-25, drawable, objects.get(1));
	renderSecondObjFile(-cx +120, -cy+12, -cz+10, drawable, objects.get(1));
	renderSecondObjFile(-cx +125, -cy+12, -cz+15, drawable, objects.get(1));
	renderSecondObjFile(-cx +170, -cy+12, -cz+15, drawable, objects.get(1));
	renderSecondObjFile(-cx +180, -cy+12, -cz+19, drawable, objects.get(1));
	renderSecondObjFile(-cx +176, -cy+12, -cz+10, drawable, objects.get(1));
	renderSecondObjFile(-cx +180, -cy+12, -cz+9, drawable, objects.get(1));
	renderSecondObjFile(-cx +180, -cy+12, -cz-20, drawable, objects.get(1));
	renderSecondObjFile(-cx +178, -cy+12, -cz+18, drawable, objects.get(1));
	renderSecondObjFile(-cx +179, -cy+12, -cz+12, drawable, objects.get(1));
	gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[1]); 
	gl.glRotatef(180, 0, 1, 0);
	renderFirstObjFile(-cx+150, -cy-98, -cz-16, drawable, objects.get(0)); 
	gl.glRotatef(180, 0, 1, 0);
	gl.glRotatef(180, 0, 1, 0);
	gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[4]);
	gl.glRotatef(180, 0, 1, 0);
	renderThirdObjFile(-cx+40, -cy+1000, -cz+15, drawable, objects.get(3));
	gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[3]);
	gl.glRotatef(180, 0, 1, 0);
	renderFourthObjFile(-cx+currPos0[0], -cy+currPos0[1], -cz+currPos0[2], drawable, objects.get(2));
	gl.glRotatef(180, 0, 1, 0);
	renderFourthObjFile(-cx+currPos1[0], -cy+currPos1[1], -cz+currPos1[2], drawable, objects.get(2));
	renderFourthObjFile(-cx+currPos2[0], -cy+currPos2[1], -cz+currPos2[2], drawable, objects.get(2));
	renderFourthObjFile(-cx+currPos3[0], -cy+currPos3[1], -cz+currPos3[2], drawable, objects.get(2));
	gl.glRotatef(210,0,1,0);
	renderFourthObjFile(-cx+currPos3[0], -cy+currPos3[1], -cz+currPos3[2], drawable, objects.get(2));
	//gl.glRotatef(210,0,1,0);
	gl.glRotatef(orienVar,0,1,0);
	renderFourthObjFile(-cx+currPos3[0], -cy+currPos3[1], -cz+currPos3[2], drawable, objects.get(2));
	//renderFifthObjFile(-cx+currPos3[0], -cy+currPos3[1], -cz+currPos3[2], drawable, objects.get(4));
	gl.glDisable(GL2.GL_TEXTURE_2D);
	gl.glFlush();
    }



    static public void main(String[] args) throws FileNotFoundException, IOException {
	new makeScene();

    }

    public void makeObject(String filePath) throws FileNotFoundException, IOException {

	this.object = new GLObject(filePath);
	objects.add(object);

    }

    makeScene() throws FileNotFoundException, IOException {

	objects = new ArrayList<GLObject>();
	makeObject(firstObjFile);
	makeObject(secondObjFile);
	makeObject(fourthObjFile);
	makeObject(thirdObjFile);
	makeObject(fifthObjFile);

	frame = new JFrame("viewing object");
	GLProfile glp = GLProfile.getDefault();
	GLCapabilities caps = new GLCapabilities(glp);
	GLCanvas canvas = new GLCanvas(caps);
    
    frame.add(canvas);                                                    
    frame.setSize(ww+200,wh+200);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel cameraMotion=new JLabel("Sliders for the Camera Motion");
    
	xSlider = new JSlider(-150, 150);
	xSlider.setValue((int)(X));
	xSlider.setMajorTickSpacing(60);
	xSlider.setPaintTicks(true);
	xSlider.setPaintLabels(true);
	xSlider.addChangeListener(this);
	JLabel xSliderName = new JLabel("left-right:");
	
	ySlider = new JSlider(-150, 150);
	ySlider.setValue((int)(Y));
	ySlider.setMajorTickSpacing(60);
	ySlider.setPaintTicks(true);
	ySlider.setPaintLabels(true);
	ySlider.addChangeListener(this);
	JLabel ySliderName = new JLabel("up-down:");

	zSlider = new JSlider(-1100,0);
	zSlider.setValue((int)(Z-550));
	zSlider.setPaintTicks(true);
	zSlider.setPaintLabels(true);
	zSlider.addChangeListener(this);
	zCam = (float) (zSlider.getValue());
	JLabel zSliderName = new JLabel("in-out:");
	    
    JPanel north = new JPanel( new GridLayout(1, 1));   
    JPanel next = new JPanel();
    next.add(cameraMotion);
    next.add(xSliderName);
    next.add(ySlider);
    next.add(ySliderName);
    next.add(xSlider);
    next.add(zSliderName);
    next.add(zSlider);

    north.add(next);
    frame.add(north,  BorderLayout.NORTH);
    
    frame.setVisible(true);
    frame.addWindowListener(new WindowAdapter()
    {
    	@Override
    	public void windowClosing(WindowEvent arg0){
    		System.exit(0);
    		}
    });
    frame.setVisible(true);

	JFrame sliderFrame = new JFrame("Modify Attributes");
	sliderFrame.setLayout(new GridLayout(6,1));
	sliderFrame.setSize(400, 300);
	sliderFrame.setAlwaysOnTop(true);
	sliderFrame.setLocation(1000, 50);
	
	JPanel row0 = new JPanel(new FlowLayout(FlowLayout.LEADING));
	light0Button = new JRadioButton("Light 0",true);
	light0Button.addActionListener(this);
	row0.add(light0Button);
	light1Button = new JRadioButton("Light 1",true);
	light1Button.addActionListener(this);
	row0.add(light1Button);
	sliderFrame.add(row0);
	
	JPanel row1 = new JPanel(new BorderLayout());
	ambientSlider = newSlider(row1, 0, 100, 25, "Ambient");
	ambientSlider.setValue(50);
	sliderFrame.add(row1);
	
	JPanel row2 = new JPanel(new BorderLayout());
	diffuseSlider = newSlider(row2, 0, 100, 25, "Diffuse");
	diffuseSlider.setValue(0);
	sliderFrame.add(row2);
	
	JPanel row3 = new JPanel(new BorderLayout());
	specularSlider = newSlider(row3, 0, 100, 25, "Specular");
	specularSlider.setValue(0);
	sliderFrame.add(row3);

    sliderFrame.setVisible(true);

	
	JPanel row6 = new JPanel(new FlowLayout(FlowLayout.LEADING));
    startButton = new JButton( "Start");
    startButton.addActionListener(this);
	row6.add(startButton);
    pauseButton = new JButton( "Pause");
    pauseButton.addActionListener(this);
	row6.add(pauseButton);
    resumeButton = new JButton( "Resume");
    resumeButton.addActionListener(this);
	row6.add(resumeButton);
    resetButton = new JButton( "Reset");
    resetButton.addActionListener(this);
	row6.add(resetButton);
	quitButton=new JButton("Quit");
	quitButton.addActionListener(this);
	row6.add(quitButton);

	sliderFrame.add(row6); 

    
    canvas.addGLEventListener(this);

    FPSAnimator animator = new FPSAnimator(canvas, 60);
    animator.start();

    
   
}

    @Override
    public void stateChanged(ChangeEvent e) {
	if (e.getSource() == zSlider) {
	    zCam = (float) (zSlider.getValue());
	} else if (e.getSource() == xSlider) {
	    xCam = (float) xSlider.getValue();
	} else if (e.getSource() == ySlider) {
	    yCam = (float) ySlider.getValue();
	}
	if (e.getSource() == ambientSlider) {
	    lightAmbient[0]=lightAmbient[1]=lightAmbient[2]=
		ambientSlider.getValue()/100.0f;
	}
	if (e.getSource() == diffuseSlider) {
	    lightDiffuse[0]=lightDiffuse[1]=lightDiffuse[2]=
		diffuseSlider.getValue()/100.0f;
	}
	if (e.getSource() == specularSlider) {
	    lightSpecular[0]=lightSpecular[1]=lightSpecular[2]=
		specularSlider.getValue()/100.0f;
	}
    }

    @Override
    public void actionPerformed(ActionEvent event) {
	if (event.getSource() == light0Button)
	    light0On = !light0On;
	else if (event.getSource() == light1Button) {
	    light1On = !light1On;
	}
	else if (event.getSource() == quitButton) {
	    System.exit(0);
	}
	else if (event.getSource() == startButton) {
	    startAnimation=true;
		anim=1;
	}
	else if (event.getSource() == pauseButton) {
	    //startAnimation=false;	    
		pauseAnimation=true;
	    anim=0;
	}
	else if (event.getSource() == resetButton) {
		orienVar=0;
	    time=0;
	    time2=0;
	}
	else if (event.getSource() == resumeButton) {
	    anim=2;
	    //startAnimation=true;
	    pauseAnimation=false;
	}
    }
    private JSlider newSlider(JPanel parent, int min, int max, int step, String label) {
    	JSlider slider = new JSlider(min, max);
   	slider.setMajorTickSpacing(step);
   	slider.setPaintTicks(true);
   	slider.setPaintLabels(true); 
   	slider.addChangeListener(this);
   	JLabel name = new JLabel(label);
   	parent.add(name, BorderLayout.WEST); 
   	parent.add(slider, BorderLayout.CENTER);
   	return slider;
    }

    public void dispose(GLAutoDrawable arg0) {
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	ww = width;
	wh = height;
	cx = (xMax() + xMin()) / 2f;
	cy = (yMax() + yMin()) / 2f;
	cz = (zMax() + zMin()) / 2f;
	diameter = 195;
	gl.glEnable(GL2.GL_DEPTH_TEST);
	gl.glViewport(0, 0, ww, wh);
	gl.glMatrixMode(GL2.GL_PROJECTION);
	gl.glLoadIdentity();
	glu.gluPerspective(60f, ww / (float) wh, (float) (diameter / 0.5773f)-diameter, (float) (diameter / 0.5773f) + 2 * diameter);
	gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    public void renderFirstObjFile(float x, float y, float z, GLAutoDrawable drawable, GLObject o) {
	gl.glPushMatrix();
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, torusAmbient, 0);
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, torusDiffuse, 0);
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, materialSpecular, 0);
	gl.glScalef(6f,3f,6f);
	gl.glTranslatef(x, y + 75, z);
	gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	gl.glColor3f(1, 1, 1);
	o.render(drawable, GL2.GL_POLYGON);
	gl.glPopMatrix();
    }

    public void renderSecondObjFile(float x, float y, float z, GLAutoDrawable drawable, GLObject o) {
	gl.glPushMatrix();
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, torusAmbient, 0);
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, torusDiffuse, 0);
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, materialSpecular, 0);
	gl.glRotatef(180, 0, 1, 0);
	gl.glScalef(4f, 3f, 4f);
	gl.glTranslatef(x, y - 35, z);
	gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	gl.glColor3f(1, 1, 1);
	o.render(drawable, GL2.GL_POLYGON);
	gl.glPopMatrix();
    }

    //moon
    public void renderThirdObjFile(float x, float y, float z, GLAutoDrawable drawable, GLObject o) {
	gl.glPushMatrix();
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, torusAmbient, 0);
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, torusDiffuse, 0);
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, materialSpecular, 0);
	gl.glScalef(.1f,.1f,.1f);
	gl.glTranslatef(x + 50, y + 145, z);
	gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	gl.glColor3f(1, 1, 1);
	o.render(drawable, GL2.GL_POLYGON);
	gl.glPopMatrix();
    }
    
    public void renderFourthObjFile(float x, float y, float z, GLAutoDrawable drawable, GLObject o) {
	gl.glPushMatrix();
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, torusAmbient, 0);
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, torusDiffuse, 0);
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, materialSpecular, 0);
	gl.glScalef(4f,2f,4f);
	gl.glTranslatef(x, y + 75, z);
	gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	gl.glColor3f(1, 1, 1);
	o.render(drawable, GL2.GL_POLYGON);
	gl.glPopMatrix();
    }
    
    public void renderFifthObjFile(float x, float y, float z, GLAutoDrawable drawable, GLObject o) {
	gl.glPushMatrix();
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, torusAmbient, 0);
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, torusDiffuse, 0);
	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, materialSpecular, 0);
	gl.glScalef(4f,2f,4f);
	gl.glTranslatef(x, y+75 , z);
	gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	gl.glColor3f(1, 1, 1);
	o.render(drawable, GL2.GL_POLYGON);
	gl.glPopMatrix();
    }
    
    public float xMax() {

	ArrayList<Float> max = new ArrayList<Float>();
	for (GLObject o : objects)
	    max.add(o.getMaxX());
	return Collections.max(max);

    }

    public float xMin() {

	ArrayList<Float> min = new ArrayList<Float>();
	for (GLObject o : objects)
	    min.add(o.getMinX());
	return Collections.min(min);

    }

    public float yMax() {

	ArrayList<Float> max = new ArrayList<Float>();
	for (GLObject o : objects)
	    max.add(o.getMaxY());
	return Collections.max(max);

    }

    public float yMin() {

	ArrayList<Float> min = new ArrayList<Float>();
	for (GLObject o : objects)
	    min.add(o.getMinY());
	return Collections.min(min);

    }

    public float zMax() {

	ArrayList<Float> max = new ArrayList<Float>();
	for (GLObject o : objects)
	    max.add(o.getMaxZ());
	return Collections.max(max);

    }

    public float zMin() {

	ArrayList<Float> min = new ArrayList<Float>();
	for (GLObject o : objects)
	    min.add(o.getMinZ());
	return Collections.min(min);

    }
}