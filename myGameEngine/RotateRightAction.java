package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;
import ray.rage.scene.SceneNode;
import ray.rml.Angle;
import ray.rml.Degreef;

public class RotateRightAction extends AbstractInputAction {
	
	private MyGame game;
	private Node dolphin;

	public RotateRightAction(Node newNode, MyGame g) {
		
		dolphin = newNode;
		game = g;
		
	}

	@Override
	public void performAction(float arg0, Event arg1) {
		
		Angle degree = Degreef.createFrom(-5.0f);
		dolphin.yaw(degree);
		game.setCameraAzimuthAngleBottom(game.getCameraAzimuthAngleBottom() - 5.0f);
		
	}
	
}
