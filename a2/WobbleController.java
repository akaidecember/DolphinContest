package a2;

import ray.rage.scene.Node;
import ray.rage.scene.controllers.AbstractController;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class WobbleController extends AbstractController {
	
	//Variables for the class rotation controller
	private float scale = 0.03f;
	private float cycle = 200.0f; 
	private float totalTime = 0.0f;
	private float direction = 1.0f;

	@Override
	protected void updateImpl(float elapsedTimeMillis) {
		
		totalTime += elapsedTimeMillis;
		float scaleAmt = 1.0f + direction * scale;
		
		if (totalTime > cycle) {
			
			direction = -direction;
			totalTime = 0.0f;
			
		}
		
		for (Node n : super.controlledNodesList) {
			
			Vector3 curScale = n.getLocalScale();
			curScale = Vector3f.createFrom(curScale.x() * scaleAmt, curScale.y(), curScale.z());
			n.setLocalScale(curScale);
			
		}
		
	}
	
}
