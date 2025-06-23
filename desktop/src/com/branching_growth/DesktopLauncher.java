package com.branching_growth;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;


public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("libgdx-opengl");
		config.setWindowedMode(2048, 1280);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4); // Enable 4x anti-aliasing

		new Lwjgl3Application(new BranchSimulation(), config);

	}
}