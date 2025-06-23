package com.branching_growth;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;

public class BranchSimulation extends ApplicationAdapter {
    private Pixmap pixmap;
    private Texture texture;
    private SpriteBatch batch;
    private Branch[] branchList;
    private int width, height;
    private boolean clearScreen = false;

    // Shader program variables
    private ShaderProgram shader;
    private float startTime;
    private int u_timeLocation;
    private int u_pixelDataLocation;

    // New uniform locations
    private int u_glowRadiusLocation;
    private int u_minGlowIntensityLocation;
    private int u_maxGlowIntensityLocation;
    private int u_glowFrequencyLocation;
    private int u_weightExponentLocation;
    private int u_lineBrightnessMultiplierLocation;

    // Executor service for concurrency
    private ExecutorService executor;

    // Task list for rendering branches
    private List<Runnable> tasks;

    // FPS variables
    private long frameCount;
    private float elapsedTime;
    private static final float FPS_UPDATE_INTERVAL = 1f; // Update FPS every second
    private BitmapFont font; // For displaying FPS

    @Override
    public void create() {
        startTime = System.nanoTime() / 1e9f; // Convert to seconds
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        texture = new Texture(pixmap);
        batch = new SpriteBatch();
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // Thread pool

        // Initialize task list
        tasks = new ArrayList<>();

        setupGraphics();

        // Load shaders
        shader = loadShader("assets/shaders/vertex_shader.glsl", "assets/shaders/fragment_shader.glsl");

        // Get uniform locations
        u_timeLocation = shader.getUniformLocation("u_time");
        u_pixelDataLocation = shader.getUniformLocation("u_pixelData");
        u_glowRadiusLocation = shader.getUniformLocation("u_glowRadius");
        u_minGlowIntensityLocation = shader.getUniformLocation("u_minGlowIntensity");
        u_maxGlowIntensityLocation = shader.getUniformLocation("u_maxGlowIntensity");
        u_glowFrequencyLocation = shader.getUniformLocation("u_glowFrequency");
        u_weightExponentLocation = shader.getUniformLocation("u_weightExponent");
        u_lineBrightnessMultiplierLocation = shader.getUniformLocation("u_lineBrightnessMultiplier");

        // Initialize FPS variables
        frameCount = 0;
        elapsedTime = 0f;

        // Initialize font for FPS display
        font = new BitmapFont(); // Ensure you have a .fnt file for custom font if needed
        initialize();
    }

    private void setupGraphics() {
        Gdx.gl.glViewport(0, 0, width, height);
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, width, height));
    }

    private ShaderProgram loadShader(String vertexPath, String fragmentPath) {
        ShaderProgram shader = new ShaderProgram(Gdx.files.internal(vertexPath), Gdx.files.internal(fragmentPath));
        if (!shader.isCompiled()) {
            Gdx.app.error("Shader", "Shader compilation failed: " + shader.getLog());
        }
        return shader;
    }

    private void initialize() {
        branchList = new Branch[Constants.BRANCH_COUNT];
        for (int i = 0; i < branchList.length; i++) {
            branchList[i] = new Branch(randomPos(), width, height);
        }
        clearScreen = false;
    }

    private Position randomPos() {
        return new Position((int) (Math.random() * width), (int) (Math.random() * height));
    }

    @Override
    public void render() {
        clearScreen();
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Clear the tasks list for reuse
        tasks.clear();

        // Use executor service to update branches concurrently
        boolean allStopped = true;

        for (Branch branch : branchList) {
            if (branch != null && "RUNNING".equals(branch.getState())) {
                tasks.add(() -> branch.drawMove(pixmap, deltaTime));
                if (branch.getAge() < branch.getLifeTime()) {
                    allStopped = false;
                }
            }
        }

        // Submit tasks to the executor
        tasks.forEach(executor::submit);

        if (allStopped) {
            initialize();
            clearScreen = true;
        }

        texture.draw(pixmap, 0, 0);
        renderBatch();

        // Update and render FPS
        updateFPS(deltaTime);
        renderFPS();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        if (clearScreen) {
            pixmap.setColor(0, 0, 0, 1);
            pixmap.fill();
            clearScreen = false;
        }
    }

    private void renderBatch() {
        batch.begin();
        shader.begin();

        float currentDeltaTime = Gdx.graphics.getDeltaTime(); // Store delta time for reuse
        shader.setUniformf(u_timeLocation, currentDeltaTime);
        shader.setUniformi(u_pixelDataLocation, 0);
        shader.setUniformf("u_screenSize", width, height);
        shader.setUniformf("iTime", (System.nanoTime() / 1e9f) - startTime);

        // Set values for new uniforms from Constants
        shader.setUniformf(u_glowRadiusLocation, Constants.GLOW_RADIUS);
        shader.setUniformf(u_minGlowIntensityLocation, Constants.MIN_GLOW_INTENSITY);
        shader.setUniformf(u_maxGlowIntensityLocation, Constants.MAX_GLOW_INTENSITY);
        shader.setUniformf(u_glowFrequencyLocation, Constants.GLOW_FREQUENCY);
        shader.setUniformf(u_weightExponentLocation, Constants.WEIGHT_EXPONENT);
        shader.setUniformf(u_lineBrightnessMultiplierLocation, Constants.LINE_BRIGHTNESS_MULTIPLIER);

        batch.draw(texture, 0 - this.width / 2, 0 - this.height / 2); // Draw texture without adjustments
        shader.end();
        batch.end();
    }

    private void updateFPS(float deltaTime) {
        frameCount++;
        elapsedTime += deltaTime;

        if (elapsedTime >= FPS_UPDATE_INTERVAL) {
            System.out.println("FPS: " + frameCount); // Optional for terminal output
            elapsedTime = 0f;
            frameCount = 0;
        }
    }

    private void renderFPS() {
        batch.begin();
        font.draw(batch, "FPS: " + (1 / Gdx.graphics.getDeltaTime()), 10, height - 10); // Display FPS
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        setupGraphics();
    }

    @Override
    public void dispose() {
        executor.shutdown();
        pixmap.dispose();
        texture.dispose();
        batch.dispose();
        shader.dispose();
        font.dispose();
    }
}
