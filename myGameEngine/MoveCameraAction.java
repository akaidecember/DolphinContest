package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class MoveCameraAction extends AbstractInputAction {
	
	private MyGame game;

	public MoveCameraAction(MyGame g) {
		
		game = g;
		
	}

	@Override
	public void performAction(float time, Event e) {
		
		if (e.getValue() == 0.25) 
			if (game.getCameraElevationAngleTop() < 60) 
				game.setCameraElevationAngleTop(game.getCameraElevationAngleTop() + 5.0f);

		if (e.getValue() == 0.75) 
			if (game.getCameraElevationAngleTop() > -30) 
				game.setCameraElevationAngleTop(game.getCameraElevationAngleTop() - 5.0f);

		if (e.getValue() == 1.0) 
			game.setCameraAzimuthAngleTop(game.getCameraAzimuthAngleTop() - 5.0f);

		if (e.getValue() == 0.5) 
				game.setCameraAzimuthAngleTop(game.getCameraAzimuthAngleTop() + 5.0f);

	}
		
}
		

