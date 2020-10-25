package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import a2.MyGame;
import net.java.games.input.Event;

//Class declaration for MoveLeftAction
public class MoveLeftAction extends AbstractInputAction {

    private Node dolphin;
    private MyGame myGame;

    public MoveLeftAction(Node newNode, MyGame g) {
    	
        dolphin = newNode;
        myGame = g;
        
    }

	public void performAction(float time, Event e) {
		
		//Console output
		System.out.println("Left Action riding the dolphin");
		dolphin.moveLeft(0.15f);
			
	}
    
}