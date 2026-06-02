package game_main;

import processing.core.PApplet;

public class GameMain extends PApplet {
	
	final static public int GAME_FRAME = 60;
	
	public static enum Gamestate 
	{
		TITLE, START, TUTORIAL, BATTLE, END, EXIT
    }
	public static Gamestate currentState = Gamestate.TITLE;

	@Override
    public void settings() 
	{
        // Set the window to fullscreen mode
        fullScreen();
		
        // Alternatively, use size(1920, 1080) if you want a specific window size
        // size(1920, 1080);
		
		noSmooth();
    }

	@Override
    public void setup() 
	{
        // Lock the frame rate to 60 FPS
        frameRate(GAME_FRAME);		
    }

	@Override
    public void draw()
	{
        // Clear the screen with a black background every frame
        background(0);
        
        // Game logic and rendering will be added here later
		switch(currentState)
		{
			case TITLE:
				
				break;
			case START:
				
				break;
			case TUTORIAL:
				
				break;
			case BATTLE:
				
				break;
			case END:
				
				break;
			case EXIT:
				exit();
				break;
		}
    }
	
	/**
	 * @param args the command line arguments
	 */
    public static void main(String[] args) {
        PApplet.main("game_main.GameMain");
    }
}