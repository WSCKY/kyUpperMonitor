// Copyright 2003 Resplendent Technology Ltd.
// See objectlessons.com for details of the java3d course.

// A merry-go-round or carousel

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.applet.Applet;
import com.sun.j3d.loaders.Scene;

public class MerryGoRound implements Ride, ActionListener, KeyListener {
protected Transform3D viewTransform = new Transform3D();
protected TransformGroup rotTG;
protected static boolean mainRun=false;
protected Transform3D rot = new Transform3D();
private TransformGroup riderPos;
private static Timer timer;
protected int tics=0;
protected boolean riding=false; // used to determe if viewer is riding
protected static boolean onRide=false; // used for testing from main
protected Mover mover;
protected Vector3f viewVector;
protected ViewingPlatform vp = null;
protected TransformGroup viewTG = null;
protected SimpleUniverse universe = null;
protected Transform3D sRot = new Transform3D();
protected Transform3D tRot = new Transform3D();
protected int rowsOfSteeds = 2;
protected int steedsInRow = 8;
private double rotY;
private Transform3D stillTransform = new Transform3D();
protected TransformGroup[] horseGallop
    = new TransformGroup[rowsOfSteeds*steedsInRow];
protected TransformGroup[] horseStraighten
    = new TransformGroup[rowsOfSteeds*steedsInRow];
private boolean justGotOn;
public MerryGoRound(Group group, SimpleUniverse universe) {
       setUp(group, universe);
}
protected void setUp(Group group, SimpleUniverse universe) {
  this.universe = universe;

  // stationary central column
  float height = 2f;
  float radius = 3f;
  float floorHeight = .2f;
  float heightAboveGround = .02f;
  int numberOfDivisions = 12;
  int numberOfSupports = 6;
  float supportRadius = .06f;

  TransformGroup centerPos = Pos.at(0f,height/2,0);
  Cylinder center = new Cylinder(0.1f, height, Cylinder.GENERATE_NORMALS |
      Cylinder.GENERATE_TEXTURE_COORDS, 6,3, Palette.MAGENTA);
  centerPos.addChild(center);
  group.addChild (centerPos);
  // transformation for rotating ride
  rotTG = new TransformGroup();
  rotTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  // bottom platform of ride
  Cylinder bottom = new Cylinder(radius, floorHeight, Cylinder.GENERATE_NORMALS
      | Cylinder.GENERATE_TEXTURE_COORDS, numberOfDivisions,3, Palette.BLUE);
  TransformGroup bottomPos = Pos.at(0f,heightAboveGround + floorHeight/2,0);
  bottomPos.addChild(bottom);
  rotTG.addChild (bottomPos);
  // top canopy of ride
  Cylinder top = new Cylinder(radius, floorHeight, Cylinder.GENERATE_NORMALS
      | Cylinder.GENERATE_TEXTURE_COORDS, numberOfDivisions,3, Palette.YELLOW);
  TransformGroup topPos = Pos.at(0f,height-heightAboveGround+floorHeight/2,0);
  topPos.addChild(top);
  rotTG.addChild (topPos);
  Cone canopy = new Cone(radius, floorHeight*4, Cone.GENERATE_NORMALS |
   Cone.GENERATE_TEXTURE_COORDS,numberOfDivisions,3, Palette.YELLOW);
  TransformGroup canopyPos = Pos.at
      (0f,height-heightAboveGround+5*floorHeight/2,0);
  canopyPos.addChild(canopy);
  rotTG.addChild(canopyPos);
  // some posts to support the canopy
  Transform3D rotateTransform = new  Transform3D();
  for (int i = 0; i < numberOfSupports; i++) {
    TransformGroup postOffset = Pos.at(radius - 2* supportRadius,
        (height + floorHeight + heightAboveGround)/2, 0);
    TransformGroup rotation = new TransformGroup();
    rotateTransform.rotY(i*Math.PI*2/numberOfSupports);
    rotation.setTransform(rotateTransform);
    rotation.addChild(postOffset);
    rotTG.addChild(rotation);
    Cylinder support = new Cylinder(supportRadius, height - heightAboveGround -
       floorHeight, Cylinder.GENERATE_NORMALS
      | Cylinder.GENERATE_TEXTURE_COORDS, 6,3, Palette.ORANGE);
    postOffset.addChild(support);
  }
  // add some steeds
  Applet applet = new Applet();
  Transform3D turn = new Transform3D();
  turn.rotY(Math.PI);
  Transform3D scale = new Transform3D();
  scale.set(.9);
  turn.mul(scale);
  //TransformGroup horseTG = new TransformGroup(turn);
  //GetModel.add("horse.obj", horseTG, applet);
  Scene scene = GetModel.get("horse.obj",  applet);
  BranchGroup horse = scene.getSceneGroup();
  GetModel.showGroup(horse);
  //((Shape3D)horse.getChild(0)).setAppearance(Palette.HORSE);
  Appearance[] horseAppearance = {Palette.RED, Palette.GREEN};

  for (int i = 0; i < rowsOfSteeds; i++) {
      Transform3D steedOffsetTransform = new Transform3D();

      Vector3f vec = new Vector3f(.9f*radius*(i+1)/(rowsOfSteeds),
         0, 0);
      steedOffsetTransform.setTranslation(vec);
    for (int j = 0; j < steedsInRow; j++) {
      int sign = ((j + i) % 2) * 2 - 1;
      TransformGroup steedOffset = new TransformGroup(steedOffsetTransform);
      Vector3f upOrDownVector = new Vector3f(0, height/12*sign, 0);
      Transform3D upOrDownTransform = new Transform3D();
      upOrDownTransform.setTranslation( upOrDownVector );
      TransformGroup upOrDown = new TransformGroup(upOrDownTransform);
      TransformGroup rotation = new TransformGroup();
      rotateTransform.rotY(j*Math.PI*2/steedsInRow);
      rotation.setTransform(rotateTransform);
      horseGallop[i*steedsInRow + j] = new TransformGroup();
      horseGallop[i*steedsInRow + j]
          .setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      horseStraighten[i*steedsInRow + j] = new TransformGroup();
      horseStraighten[i*steedsInRow + j]
          .setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      rotation.addChild(horseGallop[i*steedsInRow + j]);
      horseGallop[i*steedsInRow + j].addChild(steedOffset);
      rotTG.addChild(rotation);
     // Box steed =  new Box(.2f, .1f, .1f, Palette.MAGENTA);
     // steedOffset.addChild(steed);
     //GetModel.add("horse.obj", steedOffset, applet);
     TransformGroup horseTG = new TransformGroup(turn);
     //GetModel.add("horse.obj", horseTG, applet);
     if (i==0 && j==0) {
       Shape3D steed = (Shape3D)horse.getChild(0);
       steed.setAppearance(horseAppearance[(i+j)%2]);
       horseTG.addChild(horse);

     } else {
        Shape3D steed = (Shape3D)(horse.getChild(0).cloneNode(true));
        steed.setAppearance(horseAppearance[(i+j)%2]);
        horseTG.addChild(steed);
     }
     steedOffset.addChild(upOrDown);
     upOrDown.addChild(horseStraighten[i*steedsInRow + j]);
     TransformGroup raiseHorses = Pos.at(0f, heightAboveGround + height/2,0f);

     horseStraighten[i*steedsInRow + j].addChild(raiseHorses);
     raiseHorses.addChild(horseTG);
     Cylinder pole = new Cylinder(0.03f, height - heightAboveGround, Cylinder.GENERATE_NORMALS |
        Cylinder.GENERATE_TEXTURE_COORDS, 6,3, Palette.CYAN);
     TransformGroup poleTrans = Pos.at(0,0,-0.1f);
     poleTrans.addChild(pole);
     raiseHorses.addChild(poleTrans);
     if (i == rowsOfSteeds - 1  && j == 0) {
        riderPos = Pos.at(0f,height/2,0.1f);
        riderPos.setCapability(Group.ALLOW_LOCAL_TO_VWORLD_READ);
        riderPos.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        riderPos.setCapability(Group.ALLOW_CHILDREN_WRITE);
        riderPos.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Transform3D lookForward = new Transform3D();
        lookForward.rotY(Math.PI/7);
        TransformGroup lookForwardTG = new TransformGroup(lookForward);


        lookForwardTG.addChild(riderPos);
        raiseHorses.addChild(lookForwardTG);
     }
    }
  }

  // set up view transformation and add to virtual world
  viewVector = new Vector3f(.0f,1.0f, 10.0f);
  viewTransform.setTranslation(viewVector);
  vp = universe.getViewingPlatform();
  viewTG = vp.getViewPlatformTransform();
  viewTG.setTransform(viewTransform);
  mover = new Mover(viewTransform, viewVector, viewTG, 0);
  group.addChild(rotTG);
}
// implement Ride functions
public void getOn() {
  riding = true;
  vp.detach();
  Virtuland.on = true;
  justGotOn = true;
  riderPos.addChild(vp);
}

public void getOff() {
  riding = false;
  Virtuland.on = false;
  vp.detach();
  ((BranchGroup)(((Locale)(universe.getAllLocales().nextElement())).getAllBranchGraphs().nextElement())).addChild(vp);
}
public boolean riding() {
  return riding;
}
// set up lights sky floor etc. for testing from main
public void setUp(BranchGroup group) {
  // directional and ambient white light
  if (onRide) riding = true;
  new MyLights(group);
  new SkyBackground(group, null);
  Box floor = new Box(40000f,.1f,40000f,new StandardAppearance(Color.green));
  floor.setPickable(false);
  group.addChild(floor);
  group.compile();

   // frame.getContentPane(). addComponentListener(this);

  universe.addBranchGraph(group);
  timer = new Timer(80,this);
  timer.start();
}
// ActionListener and KeyListener interfaces
public void actionPerformed(ActionEvent e ) {
	Mover outsideMover = null;
	try {
		 outsideMover = (Mover) e.getSource();
	} catch (Throwable t) {
	}
  //System.out.println(e.getSource());
  if (outsideMover != null) {
 	 rotY = outsideMover.getFacingAngle();
	 	if (justGotOn) {
	  	outsideMover.setFacingAngle(0);
	  	justGotOn = false;
	  }
  }
    tics++;
    rot.rotY(Math.PI*3.0/4.0 + (Math.PI*tics)/80.0);
    rotTG.setTransform(rot);
    sRot.rotX((Math.PI*tics)/20.0);
    tRot.rotX(-(Math.PI*tics)/20.0);
    for (int i = 0; i < rowsOfSteeds*steedsInRow; i++) {
        horseGallop[i].setTransform(sRot);
        horseStraighten[i].setTransform(tRot);
        //System.out.println("seatRotTG set");
    }

    if (riding) {
//       try {
//          // riderPos.getLocalToVworld(viewTransform);
//          // viewTransform.mul(endArmLevel2);
//       }
//       catch (Throwable error){
//           System.out.println(error);
//       }
//       viewTG.setTransform(viewTransform);
       stillTransform= new Transform3D();
       stillTransform.setTranslation(new Vector3d(0,0,0));
       TransformGroup viewPlatformTransform = vp.getMultiTransformGroup().getTransformGroup(0);

       viewPlatformTransform.setTransform(stillTransform );
      stillTransform.rotY(rotY);
       
      
     
       
            ///ViewPlatform viewPlatform = vp.getViewPlatform();
      // 
       riderPos.setTransform(stillTransform);
    }
  }
public void keyReleased(KeyEvent e){
  mover.keyReleased(e);
  // Invoked when a key has been released.
  }
public void keyTyped(KeyEvent e){
  //System.out.println(e.getKeyChar() +" Key Typed "+e.getKeyCode());
  //Invoked when a key has been typed.
}
public void keyPressed(KeyEvent e) {
  //Invoked when a key has been pressed.
  mover.keyPressed(e);
}
// main function for testing
public static void main( String[] args ) {
  mainRun = true;
 
  // general Set-up
  JFrame frame = new JFrame("Carousel");
  frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent winEvent) {
        System.exit(0);
      }	});
  GraphicsConfiguration config =
            SimpleUniverse.getPreferredConfiguration();
  Canvas3D canvas = new Canvas3D(config);
  canvas.setSize(400, 400);
  frame.getContentPane().add(canvas);
  SimpleUniverse uni = new SimpleUniverse(canvas);
  BranchGroup group = new BranchGroup();
  MerryGoRound ride = new MerryGoRound(group,uni);
  ride.setUp(group);
  canvas.addKeyListener(ride);
  Button go = new Button("Go");
  go.addActionListener(ride);
  go.addKeyListener(ride);
  frame.pack();
  frame.setVisible(true);
  if (args.length > 0) {
		if (args[0].equals("ON"))
			ride.getOn();
	}
}
} // end of class
