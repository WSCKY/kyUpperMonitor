// Copyright 2003 Resplendent Technology Ltd.
// See objectlessons.com for details of the java3d course.

// The  Spinner Class is for a ride that can be called Spider
// or Bermuda Triangle it has a central spinning section
// which splits into more sections that also spin

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Sphere;
import javax.media.j3d.*;
import javax.vecmath.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import com.sun.j3d.utils.universe.ViewingPlatform;

//package park;

public class Spinner implements Ride, ActionListener, KeyListener {

private Transform3D viewTransform = new Transform3D();
private TransformGroup rotTG;
private TransformGroup[] rotTG2;
private Transform3D rot = new Transform3D();
private Transform3D rot2 = new Transform3D();
private TransformGroup riderPos = new TransformGroup();
//private TransformGroup rotTG2;
private static Timer timer;
private int tics=0;
private int numArms = 4;
private SimpleUniverse universe = null;
private Transform3D endArmLevel2;
private Transform3D turn = new Transform3D();
private Vector3f viewVector;
private ViewingPlatform vp = null;
private TransformGroup viewTG = null;
private Mover mover;
private boolean riding=false;
private static boolean onRide=false;

private float armLength = 1.2f;
private float armAngle = (float)(-2*Math.PI)/14;

protected static boolean mainRun=false;

public void getOn() {
  riding = true;
  Virtuland.on = true;
}
public void getOff() {
  riding = false;
  Virtuland.on = false;
}
public boolean riding() {
  return riding;
}

public Spinner(Group group, SimpleUniverse univer
