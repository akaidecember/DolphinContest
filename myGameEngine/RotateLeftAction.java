package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;
import ray.rage.scene.SceneNode;
import ray.rml.Angle;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class RotateLeftAction extends AbstractInputAction{
	
	private MyGame game;
	private Node dolphin;
	
	public RotateLeftAction(Node newNode, MyGame g) {
		
		dolphin = newNode;
		game = g;
		
	}

	@Override
	public void performAction(float arg0, Event arg1) { 
		
		Angle degree = Degreef.createFrom(5.0f);
		dolphin.yaw(degree);
		game.setCameraAzimuthAngleBottom(game.getCameraAzimuthAngleBottom() + 5.0f);
		
	}
	
}
