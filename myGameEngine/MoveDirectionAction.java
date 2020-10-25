package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;

public class MoveDirectionAction extends AbstractInputAction {
	
	private Node dolphin;
	private MyGame game;

	public MoveDirectionAction(Node node, MyGame g) {
		
		dolphin = node;
		game = g;
		
	}

	@Override
	public void performAction(float time, Event e) {
		
		// move left
		if (e.getValue() > 0.7f) 
			dolphin.moveLeft(0.05f);
		
		// move right
		if (e.getValue() < -0.7f) 
			dolphin.moveRight(0.05f);

	}

}
