package myGameEngine;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import a2.MyGame;
import net.java.games.input.Event;

//Class declaration for MoveBackwardsAction
public class MoveBackwardsAction extends AbstractInputAction {

    private Node dolphin;
    private MyGame myGame;

    public MoveBackwardsAction(Node newNode, MyGame g) {
    	
    	dolphin = newNode;
        myGame = g;
        
    }

    public void performAction(float time, Event event) {
        
        //Console output
        System.out.println("Backwards Action riding the dolphin");
        dolphin.moveForward(-0.15f);

        
    }

}