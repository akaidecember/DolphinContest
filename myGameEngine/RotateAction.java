package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Angle;
import ray.rml.Degreef;

public class RotateAction extends AbstractInputAction {
	
	private MyGame game;

	public RotateAction(MyGame g) {
		
		game = g;
		
	}
	
	@Override
	public void performAction(float arg0, Event e) {
		
		SceneNode dolphin = game.getEngine().getSceneManager().getSceneNode("myDolphinTopNode");
		
		if (e.getValue() < -0.7) {
			
			Angle degree = Degreef.createFrom(5.0f);
			dolphin.yaw(degree);
			game.setCameraAzimuthAngleTop(game.getCameraAzimuthAngleTop() + 5.0f);
			
		}

		if (e.getValue() > 0.7) {
			
			Angle degree = Degreef.createFrom(-5.0f);
			dolphin.yaw(degree);
			game.setCameraAzimuthAngleTop(game.getCameraAzimuthAngleTop() - 5.0f);
			
		}
		
	}

}
