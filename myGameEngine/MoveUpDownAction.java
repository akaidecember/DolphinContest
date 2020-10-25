package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;

public class MoveUpDownAction extends AbstractInputAction {
	
	private Node dolphin;
	private MyGame game;

	public MoveUpDownAction(Node n, MyGame g) {
		
		dolphin = n;
		game = g;
		
	}

	@Override
	public void performAction(float time, Event e) {
		
		// move forward
		if (e.getValue() < -0.7f) 
			dolphin.moveForward(0.05f);
			
		// move backward
		if (e.getValue() > 0.7f) 
			dolphin.moveBackward(0.05f);
		
	}
	
}
