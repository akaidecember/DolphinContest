package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import a2.MyGame;
import net.java.games.input.Event;

//Class declaration for MoveForwardsAction 
public class MoveForwardsAction extends AbstractInputAction {

    private Node dolphin;
    private MyGame myGame;

    public MoveForwardsAction(Node newNode, MyGame g) {
    	
        dolphin = newNode;
        myGame = g;
        
    }

    public void performAction(float time, Event event) {
       	
        System.out.println("Forward Action riding the dolphin");
        dolphin.moveForward(0.15f);

        
    }
    
}