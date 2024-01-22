package Potato;

import static org.lwjgl.glfw.GLFW.GLFW_CONNECTED;
import static org.lwjgl.glfw.GLFW.GLFW_DISCONNECTED;

public class ControllerListener {
    private int hat;
    private int gamepadBtn;
    private static ControllerListener instance;

    // TODO, Finish this later
    private ControllerListener() {

    }

    public static ControllerListener get() {
        if (ControllerListener.instance == null) {
            ControllerListener.instance = new ControllerListener();
        }
        return ControllerListener.instance;
    }

    public static void JoystickCallback(int jid, int event) {
        if (event == GLFW_CONNECTED) {

        } else if (event == GLFW_DISCONNECTED) {

        }
    }
}
