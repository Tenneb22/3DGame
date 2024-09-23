package de.tenneb22.test;

import de.tenneb22.core.WindowManager;
import org.lwjgl.Version;

public class Launcher {
    public static void main(String[] args) {
        System.out.println(Version.getVersion());
        WindowManager window = new WindowManager("3D Game", 1600, 900, false);
        window.init();

        while (!window.windowShouldClose()) {
            window.update();
        }
        
        window.cleanup();
    }
}