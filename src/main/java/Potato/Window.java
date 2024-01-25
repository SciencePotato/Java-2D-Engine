package Potato;

import Util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private final int width, height;
    private final String title;
    private long glfwWindow;
    private static Window window = null;
    private static Scene currentScene = null;
    public float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;

    private Window() {
        this.width = 800;
        this.height = 600;
        this.title = "Temporary title";
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:        // Test scene
                break;
        }

        if (currentScene != null) currentScene.init();
    }
    public void run() {
        System.out.println("Initialization" + Version.getVersion());

        init();
        loop();

        // Free Memory & Erro
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Error Callback
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to Initialize GLFW Window");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // Create window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Window creation Failure");
        }

        // Attach Listener to GLFW
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable V-sync
        glfwSwapInterval(1);
        glfwShowWindow(glfwWindow);

        // LWJGL detects context, make OpenGL bindings
        GL.createCapabilities();

        Window.changeScene(0);
    }

    public void loop() {
        float startTime = Time.getTime(), endTime, deltaTime = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow))  {
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            // Check if first scene has executed or not
            if (deltaTime >= 0) currentScene.update(deltaTime);

            glfwSwapBuffers(glfwWindow);

            // Time calculation | After frame ends
            endTime = Time.getTime();
            deltaTime = endTime - startTime;
            startTime = Time.getTime();
        }
    }
}
