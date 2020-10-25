/*
 * This is the MyGame class for the Dolphin Contest mini-game. This class contains the main function and all the other game related setups that are 
 * necessary for the operation of the game. This file also contains the instances for the objects that are present in the game world. 
 * The base of this game is the previous Assign. #1 called Dolphin Explorer.
 * 
 * Assignment #2 for CSC 165
 * Professor Scott Gordon
 * CSUS
 * 
 * @author Anshul Kumar Shandilya
 * 
 * */

package a2;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import ray.rage.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.scene.controllers.*;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;

import ray.rage.rendersystem.states.*;
import ray.rage.asset.texture.*;
import ray.input.*;
import ray.input.action.*;

import ray.rage.rendersystem.shader.*;
import ray.rage.util.*;
import myGameEngine.*;
import net.java.games.input.Controller;

//Class declaration for MyGame
public class MyGame extends VariableFrameRateGame {

	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	String elapsTimeStr, planetsVisitedStrTop, planetsVisitedStrBottom, dispStrTop, dispStrBottom, energyLeftTop, energyLeftBottom, gameOverStr, keyboardName, gamepadName;
    int elapsTimeSec, planetsVisitedTop = 0, planetsVisitedBottom = 0, energyTop = 100, energyBottom = 100, counterTop = 0, counterBottom = 0;
    
    //Private variables for the class MyGame
    private InputManager im;
    private Action moveForwardAction, moveBackwardAction, moveLeftAction, moveRightAction, moveCameraAction, moveDirectionAction, moveUpDownAction, rotateAction, rotateDolphinBottomLeftAction, rotateDolphinBottomRightAction;
    public Camera cameraTop, cameraBottom;
    public SceneNode dolphinTopNode, dolphinBottomNode;
    private boolean visitPlanetATop = false, visitPlanetBTop = false, visitPlanetCTop = false, depletedTop = false, freezeTop = false, gamepadInserted = true;
    private boolean visitPlanetABottom = false, visitPlanetBBottom = false, visitPlanetCBottom = false, depletedBottom = false, freezeBottom = false;
    private boolean availablePlanetA = true, availablePlanetB = true, availablePlanetC = true;
    private SceneNode planetANode, planetBNode, planetCNode, cameraTopNode, cameraBottomNode, pyramidNode, gameWorldObjectsNode, manualObjectsNode, playerNode, planetNode;
    private Camera3pController controllerTop, controllerBottom;																		//Idea taken from csc 133
    
    //Random Function for randomly positioning the planets
    Random rand = new Random();

    //Constructor for the class MyGame
    public MyGame() {
    	
        super();
        
    }

    //Main function for the program/game
    public static void main(String[] args) {
    	
        Game game = new MyGame();
        
        try {
        	
            game.startup();
            game.run();
            
        } catch (Exception e) {
        	
            e.printStackTrace(System.err);
            
        } finally {
        	
            game.shutdown();
            game.exit();
            
        }
        
    }
    
	@Override
	protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
		
		rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
		
	}
	
	@Override
	protected void setupWindowViewports(RenderWindow rw) {
		
		rw.addKeyListener(this);
		Viewport topViewport = rw.getViewport(0);
		topViewport.setDimensions(0.51f, 0.01f, 0.99f, 0.49f); 
		topViewport.setClearColor(new Color(0.0f, 0.0f, 0.0f));
		Viewport bottomViewport = rw.createViewport(0.01f, 0.01f, 0.99f, 0.49f);
		bottomViewport.setClearColor(new Color(0.5f, 0.5f, 0.5f));
		
	}

    @Override
    protected void setupCameras(SceneManager sm, RenderWindow rw) {
    	
    	SceneNode rootNode = sm.getRootSceneNode();
    	
    	//For the top player
    	cameraTop = sm.createCamera("MainTopCamera", Projection.PERSPECTIVE);
    	rw.getViewport(0).setCamera(cameraTop);
    	cameraTopNode = rootNode.createChildSceneNode("MainCameraTopNode");
    	cameraTopNode.attachObject(cameraTop);
    	cameraTop.setMode('n');
    	cameraTop.getFrustum().setFarClipDistance(1000.0f);
    	
    	//For the bottom player
    	cameraBottom = sm.createCamera("MainBottomCamera", Projection.PERSPECTIVE);
    	rw.getViewport(1).setCamera(cameraBottom);
    	cameraBottomNode = rootNode.createChildSceneNode("MainCameraBottomNode");
    	cameraBottomNode.attachObject(cameraBottom);
    	cameraBottom.setMode('n');
    	cameraBottom.getFrustum().setFarClipDistance(1000.0f);
    	
    }
	
    @Override
    protected void setupScene(Engine eng, SceneManager sm) throws IOException {
    	
    	im = new GenericInputManager();																				//Initializing the input manager
    	
    	//Setting the strings for the keyboard and gamepad
    	keyboardName = im.getKeyboardName();
    	gamepadName = im.getFirstGamepadName();

        gameWorldObjectsNode = sm.getRootSceneNode().createChildSceneNode("GameWorldObjectsNode");			        //Initializing the gameWorldObjects Scene Node
        manualObjectsNode = gameWorldObjectsNode.createChildSceneNode("ManualObjectsNode");							//Initializing the manualObjects scene node (phew, found my error !!!)
        
        //Make two triangles for the floor/ground of the game------------------------------------------------------------------------------------------------------
        //----Triangle 1 of 2----
        ManualObject triangle1 = sm.createManualObject("triangle1");	
        SceneNode triangle1Node = manualObjectsNode.createChildSceneNode("Triangle1Node");		
        triangle1Node.scale(1000.0f, 0.05f, 1000.0f);
        triangle1Node.moveDown(2.0f);
        ManualObjectSection triangle1Sec = triangle1.createManualSection("triangle1Sec");
        triangle1.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        
        //Setting the coordinates
		float[] vertices1 = new float[] { 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // front top
				1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 1.0f, 						// front bottom
				0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 						// right top
				0.0f, 0.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 						// right bottom
				0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 						// left top
				0.0f, 0.0f, -1.0f, 0.0f, -1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 						// left bottom
				0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 							// top
				0.0f, -1.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 1.0f 						// bottom
		};

		float[] texture1 = new float[] { 0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 					// front top
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 											// front bottom
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 											// right top
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 											// right bottom
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 											// left top
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 											// left bottom
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 											// top
				1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f 												// bottom

		}; 										

		float[] normals1 = new float[] { 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 	// front top
				0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 							// front bottom
				0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 						// right top
				0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 						// right bottom
				-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 						// left top
				-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 						// left bottom
				0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 							// top
				0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f 						// bottom

		};

		int[] indices1 = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 };
		
		FloatBuffer verticalBuffer1 = BufferUtil.directFloatBuffer(vertices1);
		FloatBuffer normalBuffer1 = BufferUtil.directFloatBuffer(normals1);
		FloatBuffer textureBuffer1 = BufferUtil.directFloatBuffer(texture1);
		IntBuffer indicesBuffer1 = BufferUtil.directIntBuffer(indices1);
		triangle1Sec.setVertexBuffer(verticalBuffer1);
		triangle1Sec.setNormalsBuffer(normalBuffer1);
		triangle1Sec.setTextureCoordsBuffer(textureBuffer1);
		triangle1Sec.setIndexBuffer(indicesBuffer1);
		Texture textureGround1 = eng.getTextureManager().getAssetByPath("red.jpeg");				//Texture file for the ground
		TextureState textureGroundState1 = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		textureGroundState1.setTexture(textureGround1);
		FrontFaceState faceState1 = (FrontFaceState)sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
		triangle1.setDataSource(DataSource.INDEX_BUFFER);
		triangle1.setRenderState(textureGroundState1);
		triangle1.setRenderState(faceState1);
		triangle1Node.attachObject(triangle1);
		
		//----Triangle 2 of 2----
        ManualObject triangle2 = sm.createManualObject("triangle2");
        SceneNode triangle2Node = manualObjectsNode.createChildSceneNode("Triangle2Node");
        triangle2Node.yaw(Degreef.createFrom(180.0f));
        triangle2Node.scale(1000.0f, 0.05f, 1000.0f);
        triangle2Node.moveDown(2.0f);
        ManualObjectSection triangle2Sec = triangle1.createManualSection("triangle2Sec");
        triangle2.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        
        //Setting the coordinates
		float[] vertices2 = new float[] { 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // front top
				1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 1.0f, 						// front bottom
				0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 						// right top
				0.0f, 0.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 						// right bottom
				0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 						// left top
				0.0f, 0.0f, -1.0f, 0.0f, -1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 						// left bottom
				0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 							// top
				0.0f, -1.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 1.0f 						// bottom
		};

		float[] texture2 = new float[] { 0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 					// front top
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 											// front bottom
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 											// right top
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 											// right bottom
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 											// left top
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 											// left bottom
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 											// top
				1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f }; 											// bottom

		float[] normals2 = new float[] { 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 	// front top
				0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 							// front bottom
				0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 						// right top
				0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 						// right bottom
				-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 						// left top
				-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 						// left bottom
				0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 							// top
				0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f 						// bottom

		};

		int[] indices2 = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 };
		
		FloatBuffer verticalBuffer2 = BufferUtil.directFloatBuffer(vertices2);
		FloatBuffer normalBuffer2 = BufferUtil.directFloatBuffer(normals2);
		FloatBuffer textureBuffer2 = BufferUtil.directFloatBuffer(texture2);
		IntBuffer indicesBuffer2 = BufferUtil.directIntBuffer(indices2);
		triangle2Sec.setVertexBuffer(verticalBuffer2);
		triangle2Sec.setNormalsBuffer(normalBuffer2);
		triangle2Sec.setTextureCoordsBuffer(textureBuffer2);
		triangle2Sec.setIndexBuffer(indicesBuffer2);
		Texture textureGround2 = eng.getTextureManager().getAssetByPath("red.jpeg");				//Texture file for the ground
		TextureState textureGroundState2 = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		textureGroundState2.setTexture(textureGround2);
		FrontFaceState faceState2 = (FrontFaceState)sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
		triangle2.setDataSource(DataSource.INDEX_BUFFER);
		triangle2.setRenderState(textureGroundState2);
		triangle2.setRenderState(faceState2);
		triangle2Node.attachObject(triangle2);		
		//---------------------------------------------------------------------------------------------------------------------------------------------------
		
		//Creating the player node to add in the game, upgrade from last only entity approach
		playerNode = gameWorldObjectsNode.createChildSceneNode("PlayerNode");
		
        //Creating a dolphin for the top player
        Entity dolphinTopEntity = sm.createEntity("myDolphinTop", "dolphinHighPoly.obj");
        dolphinTopEntity.setPrimitive(Primitive.TRIANGLES);
        dolphinTopNode = playerNode.createChildSceneNode(dolphinTopEntity.getName() + "Node");
        dolphinTopNode.attachObject(dolphinTopEntity);
        
        //Creating a dolphin for the bottom player
        Entity dolphinBottomEntity = sm.createEntity("myDolphinBottom", "dolphinHighPoly.obj");
        dolphinBottomEntity.setPrimitive(Primitive.TRIANGLES);
        dolphinBottomNode = playerNode.createChildSceneNode(dolphinBottomEntity.getName() + "Node");
        dolphinBottomNode.attachObject(dolphinBottomEntity);
        
        //Setting up the orbit controllers for the dolphin
        controllerTop = new Camera3pController(cameraTop, cameraTopNode, dolphinTopNode, gamepadName, im);													//For the top dolphin				
        controllerBottom = new Camera3pController(cameraBottom, cameraBottomNode, dolphinBottomNode, keyboardName, im);										//For the bottom dolphin
        
        dolphinTopNode.yaw(Degreef.createFrom(180.0f));
        dolphinBottomNode.yaw(Degreef.createFrom(180.0f));
        
        //Setting up the planets-----------------------------------------------------------------------------------------------------------------------------
        Entity planetA = sm.createEntity("PlanetA", "sphere.obj");
        planetA.setPrimitive(Primitive.TRIANGLES);
        Entity planetB = sm.createEntity("PlanetB", "sphere.obj");
        planetB.setPrimitive(Primitive.TRIANGLES);
        Entity planetC = sm.createEntity("PlanetC", "sphere.obj");
        planetC.setPrimitive(Primitive.TRIANGLES);
        
        //WHY DO I KEEP FORGETTING TO INITIALIZE THE FREAKING NODES!!!!!!!!!!!!!!!!!
        planetNode = gameWorldObjectsNode.createChildSceneNode("PlanetNode");
        
        //Attaching Planets to Node Object, assigning positions, and scaling
        //----For planet A----
        planetANode = planetNode.createChildSceneNode("PlanetANode");
        planetANode.attachObject(planetA);
        planetANode.setLocalPosition(20 + new Random().nextFloat() * (5), 0.0f, 10 + new Random().nextFloat() * 10);
        planetANode.setLocalScale(3.0f, 3.0f, 3.0f);
        
        //----For planet B----
        planetBNode = planetNode.createChildSceneNode("PlanetBNode");
        planetBNode.attachObject(planetB);
        planetBNode.setLocalPosition(5 + new Random().nextFloat() * (15), 0.0f, -20 + new Random().nextFloat() * 5);
        planetBNode.setLocalScale(3.0f, 3.0f, 3.0f);        
        
        //----For planet C----
        planetCNode = planetNode.createChildSceneNode("PlanetCNode");
        planetCNode.attachObject(planetC);
        planetCNode.setLocalPosition(-5 + new Random().nextFloat() * (15), 0.0f, 20 + new Random().nextFloat() * 10);
        planetCNode.setLocalScale(3.0f, 3.0f, 3.0f);
        
        //Now setting the textures for the planets
        TextureManager tm = eng.getTextureManager();
        Texture planetBTexture = tm.getAssetByPath("earth-day.jpeg");
        RenderSystem rs = sm.getRenderSystem();
        TextureState state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
        state.setTexture(planetBTexture);
        planetB.setRenderState(state);

        tm = eng.getTextureManager();
        Texture planetCTexture = tm.getAssetByPath("moon.jpeg");
        rs = sm.getRenderSystem();
        state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
        state.setTexture(planetCTexture);
        planetC.setRenderState(state);
        
        tm = eng.getTextureManager();
        Texture planetATexture = tm.getAssetByPath("blue.jpeg");
        rs = sm.getRenderSystem();
        state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
        state.setTexture(planetATexture);
        planetA.setRenderState(state);
        
        //Cause Planets Objects to Rotate
        RotationController rcPlanetA = new RotationController(Vector3f.createUnitVectorY(), .0f);
        rcPlanetA.addNode(planetANode);
        RotationController rcPlanetB = new RotationController(Vector3f.createUnitVectorX(), .0f);
        rcPlanetB.addNode(planetBNode);
        RotationController rcPlanetC = new RotationController(Vector3f.createUnitVectorY(), .0f);
        rcPlanetC.addNode(planetCNode);
        sm.addController(rcPlanetA);
        sm.addController(rcPlanetB);
        sm.addController(rcPlanetC);
        //---------------------------------------------------------------------------------------------------------------------------------------------------
        
        //Creating pyramid as a home base to refill energy
        ManualObject pyramid = makePyramid(eng, sm);
        pyramidNode = sm.getRootSceneNode().createChildSceneNode("PyrNode");
        pyramidNode.scale(0.75f, 0.75f, 0.75f);
        pyramidNode.setLocalPosition(10 + new Random().nextFloat() * (8), 0.0f, 20 + new Random().nextFloat() * 25);
        pyramidNode.attachObject(pyramid);
    
        // Set up Lights
        sm.getAmbientLight().setIntensity(new Color(.3f, .3f, .3f));
		Light plight = sm.createLight("testLamp1", Light.Type.POINT);
		plight.setAmbient(new Color(.1f, .1f, .1f));
        plight.setDiffuse(new Color(.7f, .7f, .7f));
		plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        plight.setRange(5f);
        
		SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode("plightNode");
        plightNode.attachObject(plight);
      
        StretchController sc = new StretchController();
        sc.addNode(dolphinTopNode);
        sc.addNode(dolphinBottomNode);
        
        sm.addController(sc);
        
        setupInputs(sm);																								//Calling the function to setup the inputs

    }

    //Function to make the pyramid
    private ManualObject makePyramid(Engine eng, SceneManager sm) throws IOException {

        ManualObject pyramid = sm.createManualObject("Pyramid");
        ManualObjectSection pyrSec = pyramid.createManualSection("PyramidSection");
        pyramid.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        float[] vertices = new float[]
            { -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 											//front
            1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 											//right
            1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 											//back
            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 											//left
            -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 										//LF
            1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f 											//RR
            };
        float[] texcoords = new float[]
            { 0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
            };
        float[] normals = new float[]
            { 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f
            };

        int[] indices = new int[] { 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};
        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
        FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
        pyrSec.setVertexBuffer(vertBuf);
        pyrSec.setTextureCoordsBuffer(texBuf);
        pyrSec.setNormalsBuffer(normBuf);
        pyrSec.setIndexBuffer(indexBuf);
        Texture tex = eng.getTextureManager().getAssetByPath("chain-fence.jpeg");
        TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        texState.setTexture(tex);
        FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
        pyramid.setDataSource(DataSource.INDEX_BUFFER);
        pyramid.setRenderState(texState);
        pyramid.setRenderState(faceState);
        
        return pyramid;
            
    }

	//Function to setup inputs for various actions
    protected void setupInputs(SceneManager sm){ 

    	ArrayList<Controller> controllers = im.getControllers();						//Get the list of all the input devices available
    	
    	//Initialization action for the top dolphin
    	moveCameraAction = new MoveCameraAction(this);
    	moveDirectionAction = new MoveDirectionAction(dolphinTopNode, this);
    	moveUpDownAction = new MoveUpDownAction(dolphinTopNode, this);
    	rotateAction = new RotateAction(this);
    	
    	//Initialization action for the bottom dolphin
        moveForwardAction = new MoveForwardsAction(dolphinBottomNode, this);						//camera forward
        moveBackwardAction = new MoveBackwardsAction(dolphinBottomNode, this);						//camera backward
        moveLeftAction = new MoveLeftAction(dolphinBottomNode, this);								//camera left
        moveRightAction = new MoveRightAction(dolphinBottomNode, this);								//camera right
        rotateDolphinBottomLeftAction = new RotateLeftAction(dolphinBottomNode, this);				//Rotate the dolphin left
        rotateDolphinBottomRightAction = new RotateRightAction(dolphinBottomNode, this);			//Rotate the dolphin right

       //Error checking to check if the controllers are connected or not (ensuring the game does not crash)
        for (Controller c : controllers) {
        	
        	//If the controller type is keyboard, then use the keyboard controls, otherwise use the gamepad controls
            if (c.getType() == Controller.Type.KEYBOARD)
                keyboardControls(c);													//Call the keyboard Control function to handle the keyboard inputs
            if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK)
                gamepadControls(c);														//Call the gamepad input to control the XB1 inputs
            
        }
        
    }
    
    //Function to handle the gamepad controlls
    void gamepadControls(Controller gpName) {
    	
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.POV, moveCameraAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);  
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.X, moveDirectionAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);  
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y, moveUpDownAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RX, rotateAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

    }

    //Function to handle the keyboard controls
    void keyboardControls(Controller kbName) {
   
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, moveLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, moveRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, moveForwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, moveBackwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.Q, rotateDolphinBottomLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.E, rotateDolphinBottomRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        
    }

    @Override
    protected void update(Engine engine) {
    	
    	if(depletedTop == true || depletedBottom == true) {
    		
    		if(depletedTop == true) {
    		
	    		System.out.println("Energy exhausted for player 1, freezing game!");
	    		
	    		//measures for a case where energy is exhausted
		    	rs = (GL4RenderSystem) engine.getRenderSystem();
				rs.setHUD2("Game Over!............. You ran out of energy............Player 2 won!", 12, engine.getRenderSystem().getCanvas().getHeight()/2 + 12);
		    		
		    	//Freeze the game once out of energy
		    	while(!freezeTop) {
		    			
		    		freezeTop = freezeBottom = true;
		    		im.update(elapsTime);
		    		
		    	}
		    	
    		}
    		if(depletedBottom == true) {
    			
	    		System.out.println("Energy exhausted for player 2, freezing game!");
	    		
	    		//measures for a case where energy is exhausted
		    	rs = (GL4RenderSystem) engine.getRenderSystem();
		    	rs.setHUD("Game Over!............. You ran out of energy............Player 1 won!");
		    		
		    	//Freeze the game once out of energy
		    	while(!freezeBottom) {
		    			
		    		freezeBottom = freezeTop = true;
		    		im.update(elapsTime);
		    		
		    	}
		    	
    		}
    	
    	}
    	else {
    		
    		//Error checking for the gamepad
    		if(freezeBottom == true && gamepadInserted == false) {
    			
    			System.out.println("Game paused since controller not inserted!!!");													//Console output
    			
	    		//measures for a case where energy is exhausted
		    	rs = (GL4RenderSystem) engine.getRenderSystem();
		    	rs.setHUD("Please connect a game controller to play for player 1");
		    		
		    	//Freeze the game until gamepad inserted
		    	while(!freezeBottom) {
		    			
		    		if(im.getFirstGamepadName() == null)
		    			freezeBottom = true;
		    		else {
		    			
		    			gamepadInserted = true;																						//If gamepad inserted, 
		    			break;
		    			
		    		}
		    		im.update(elapsTime);
		    		
		    	}
    			
    		}
    		
			//Build and set HUD
			rs = (GL4RenderSystem) engine.getRenderSystem();
			elapsTime += engine.getElapsedTimeMillis();
			elapsTimeSec = Math.round(elapsTime/1000.0f);
			elapsTimeStr = Integer.toString(elapsTimeSec);
			
			//Energy mechanism
			counterTop++;
			counterBottom++;
			
			//For the top player
			if(counterTop > 100) {
				
				energyTop--;
				counterTop = 0;
				if(energyTop <= 0)
					depletedTop = true;
				
			}
			
			//For the bottom player
			if(counterBottom > 100) {
				
				energyBottom--;
				counterBottom = 0;
				if(energyBottom <= 0)
					depletedBottom = true;
				
			}
			
			//For top player
			energyLeftTop = Integer.toString(energyTop);
			planetsVisitedStrTop = Integer.toString(planetsVisitedTop);
			dispStrTop = "Time Elapsed: " + elapsTimeStr + "             Score : " + planetsVisitedStrTop + "             Energy Left: " + energyLeftTop;
			rs.setHUD2(dispStrTop, 12, engine.getRenderSystem().getCanvas().getHeight()/2 + 12);
	        
			//For Bottom player
			energyLeftBottom = Integer.toString(energyBottom);
			planetsVisitedStrBottom = Integer.toString(planetsVisitedBottom);
			dispStrBottom = "Time Elapsed: " + elapsTimeStr + "             Score : " + planetsVisitedStrBottom + "             Energy Left: " + energyLeftBottom;
	        rs.setHUD(dispStrBottom, 12, 20);
	        
	       
	        checkForCollisionTop();																				//Check for collision for top player
	        checkForCollisionBottom();																			//Check for collision for bottom player
	        checkRefuelCollision();																				//Check for refuel collision
	        
	        im.update(elapsTime);
	        controllerTop.updateCameraPosition();
	        controllerBottom.updateCameraPosition();
	        
	        
	        
    	}
    	
    }

    //Function to check if the player has collided with the home base to be able to refuel
	private void checkRefuelCollision() {

		//For top player
		if(this.collideRefuel(dolphinTopNode, pyramidNode)) {
				
			System.out.println("Player 1 regained Energy from homebase");											//Console output
			energyTop = 100;

		}
		
		//For bottom player
		if(this.collideRefuel(dolphinBottomNode, pyramidNode)) {
			
			System.out.println("Player 2 regained Energy from homebase");											//Console output
			energyBottom = 100;

		}
		
	}

	//Function similar to the collide function, but with different values
	private boolean collideRefuel(SceneNode node1, SceneNode node2) {
		
		Vector3 node1Pos = node1.getLocalPosition();
		if((Math.abs(node1Pos.x() - node2.getLocalPosition().x()) < 1.0f) && (Math.abs(node1Pos.y() - node2.getLocalPosition().y()) < 1.0f) && (Math.abs(node1Pos.z() - node2.getLocalPosition().z()) < 1.0f)) {
			
			//Console output
			System.out.println("Refueled");
			return true;
			
		}
		else
			return false;

	}

	//This method checks for collision between the top player and the top planets
    public void checkForCollisionTop(){

    		
		if(visitPlanetATop == false && availablePlanetA == true) {
				
			if(this.collide(dolphinTopNode, planetANode)) {
					
				System.out.println("Planet A visited");
				visitPlanetATop = true;
				availablePlanetA = false;
				incrementCounter(1, 1);
				startRotate(planetANode);

				
			}
			
		}
		if(visitPlanetBTop == false && availablePlanetB == true) {
	
			if(this.collide(dolphinTopNode, planetBNode)) {
				
				System.out.println("Planet B visited");
				visitPlanetBTop = true;
				availablePlanetB = false;
				incrementCounter(1, 1);
				startRotate(planetBNode);

				
			}
			
		}
		if(visitPlanetCTop == false && availablePlanetC == true) {
		
			if(this.collide(dolphinTopNode, planetCNode)) {
			
				System.out.println("Planet C visited");
				visitPlanetCTop = true;
				availablePlanetC = false;
				incrementCounter(1, 1);
				startRotate(planetCNode);

				
			}
		
		}
			
    }
    
	//This method checks for collision between the bottom player and the bottom planets
    public void checkForCollisionBottom(){

    		
		if(visitPlanetABottom == false && availablePlanetA == true) {
				
			if(this.collide(dolphinBottomNode, planetANode)) {
					
				System.out.println("Planet A visited bottom");
				visitPlanetABottom = true;
				availablePlanetA = false;
				incrementCounter(1, 2);
				startRotate(planetANode);

				
			}
			
		}
		if(visitPlanetBBottom == false && availablePlanetB == true) {
	
			if(this.collide(dolphinBottomNode, planetBNode)) {
				
				System.out.println("Planet B visited bottom");
				visitPlanetBBottom = true;
				availablePlanetB = false;
				incrementCounter(1, 2);
				startRotate(planetBNode);

				
			}
			
		}
		if(visitPlanetCBottom == false && availablePlanetC == true) {
		
			if(this.collide(dolphinBottomNode, planetCNode)) {
			
				System.out.println("Planet C visited bottom");
				visitPlanetCBottom = true;
				availablePlanetC = false;
				incrementCounter(1, 2);
				startRotate(planetCNode);
				
			}
		
		}
			
    }

    //Function to start the rotation of the visited planets
    private void startRotate(SceneNode planetNode) {
		// TODO Auto-generated method stub
    	
    	WobbleController rc = new WobbleController();
    	rc.addNode(planetNode);
    	this.getEngine().getSceneManager().addController(rc);
		
	}

	//This function will do the necessary checks for collision detection
	private boolean collide(SceneNode node1, SceneNode node2) {
		
		Vector3 node1Pos = node1.getLocalPosition();
		if((Math.abs(node1Pos.x() - node2.getLocalPosition().x()) < 7.0f) && (Math.abs(node1Pos.y() - node2.getLocalPosition().y()) < 7.0f) && (Math.abs(node1Pos.z() - node2.getLocalPosition().z()) < 7.0f)) {

			System.out.println("Collision occured");												//Console output
			return true;
			
		}
		else 
			return false;

	}

	//This function will return a random set of values within a range
    float getRandomPosition(float min, float max) {
    	
        return (min + rand.nextFloat() * (max - min));
        
    }
    
    //This function will randomly select the positions for the planets based on the getRandomPosition() function
    void planetRandomPosition(SceneNode node, float[] positions) {
    	
        node.setLocalPosition(getRandomPosition(positions[0], positions[1]), getRandomPosition(positions[2], positions[3]), getRandomPosition(positions[4], positions[5]));

    }

    //This function will increment the counter planetsVisited when called (1 for top and 2 for bottom player)
    public void incrementCounter(int num, int player) {
    	
    	if(player == 1)
    		planetsVisitedTop += num;
        
    	if(player == 2)
    		planetsVisitedBottom += num;
    	
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
    	
        Entity dolphin = getEngine().getSceneManager().getEntity("myDolphinBottom");
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_L:
                dolphin.setPrimitive(Primitive.LINES);
                break;
            case KeyEvent.VK_T:
                dolphin.setPrimitive(Primitive.TRIANGLES);
                break;
            case KeyEvent.VK_P:
                dolphin.setPrimitive(Primitive.POINTS);
                break;
        }
        super.keyPressed(e);
    }
    
	//Getter and setter functions
    
    //----Top dolphin----
	public void setCameraElevationAngleTop(float newAngle) {
		
		this.controllerTop.setCameraElevationAngle(newAngle);
		
	}

	public float getCameraElevationAngleTop() {
		
		return this.controllerTop.getCameraElevationAngle();
		
	}

	public void setRadiusTop(float r) {
		
		this.controllerTop.setRadius(r);
		
	}

	public float getRadiusTop() {
		
		return this.controllerTop.getRadius();
		
	}

	public void setCameraAzimuthAngleTop(float newAngle) {
		
		this.controllerTop.setAzimuth(newAngle);
		
	}

	public float getCameraAzimuthAngleTop() {
		
		return this.controllerTop.getAzimuth();
		
	}

	//----Bottom dolphin----
	public void setCameraElevationAngleBottom(float newAngle) {
		
		this.controllerBottom.setCameraElevationAngle(newAngle);
		
	}

	public float getCameraElevationAngleBottom() {
		
		return this.controllerBottom.getCameraElevationAngle();
		
	}

	public void setRadiusBottom(float r) {
		
		this.controllerBottom.setRadius(r);
		
		
	}

	public float getRadiusBottom() {
		return this.controllerBottom.getRadius();
	}

	public void setCameraAzimuthAngleBottom(float newAngle) {
		
		this.controllerBottom.setAzimuth(newAngle);
		
	}

	public float getCameraAzimuthAngleBottom() {
		
		return this.controllerBottom.getAzimuth();
		
	}

}



