package de.tenneb22.test;

import de.tenneb22.core.*;
import de.tenneb22.core.entity.*;
import de.tenneb22.core.entity.terrain.Terrain;
import de.tenneb22.core.lighting.DirectionalLight;
import de.tenneb22.core.lighting.PointLight;
import de.tenneb22.core.lighting.SpotLight;
import de.tenneb22.core.rendering.RenderManager;
import de.tenneb22.core.utils.Consts;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class TestGame implements ILogic {

    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;
    private SceneManager sceneManager;
    private Camera camera;
    Vector3f cameraInc;

    public TestGame() {
        renderer = new RenderManager();
        window = Main.getWindow();
        loader = new ObjectLoader();
        camera = new Camera(new Vector3f(0,0,0), new Vector3f(0,0,0));
        cameraInc = new Vector3f(0,0,0);
        sceneManager = new SceneManager(80);
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        /*Model model = loader.loadOBJModel("/models/cube.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/blue.png")), 1f);
        model.getMaterial().setDisableCulling(true);

        TerrainTexture background = new TerrainTexture(loader.loadTexture("textures/terrain.png"));
        TerrainTexture redTexture = new TerrainTexture(loader.loadTexture("textures/flowers.png"));
        TerrainTexture greenTexture = new TerrainTexture(loader.loadTexture("textures/stone.png"));
        TerrainTexture blueTexture = new TerrainTexture(loader.loadTexture("textures/dirt.png"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("textures/blendMap.png"));

        BlendMapTerrain blendMapTerrain = new BlendMapTerrain(background, redTexture, greenTexture, blueTexture);



        Terrain terrain = new Terrain(new Vector3f(0, 1, -800), loader, new Material(
                new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), 0.1f), blendMapTerrain, blendMap);
        Terrain terrain2 = new Terrain(new Vector3f(-800, 1, -800), loader, new Material(
                new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), 0.1f), blendMapTerrain, blendMap);
        sceneManager.addTerrain(terrain);
        sceneManager.addTerrain(terrain2);

        Random rnd = new Random();
        for(int i = 0; i < 200; i++) {
            float x = rnd.nextFloat() * 800;
            float z = rnd.nextFloat() * -800;
            sceneManager.addEntity(new Entity(model, new Vector3f(x,2,z),
                    new Vector3f(0,0,0), 1));
        }
        sceneManager.addEntity(new Entity(model, new Vector3f(0,2,-5),
                new Vector3f(0,0,0), 1));

        float lightIntensity = 1.0f;
        //point light
        Vector3f lightPosition = new Vector3f(-0.5f,-0.5f,-3.2f);
        Vector3f lightColor = new Vector3f(1,1,1);
        PointLight pointLight = new PointLight(lightColor, lightPosition, lightIntensity, 0, 0, 1);

        //spot light
        Vector3f coneDir = new Vector3f(0,-50,-1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        lightIntensity = 50000f;
        SpotLight spotLight = new SpotLight(new PointLight(lightColor, new Vector3f(1f,50f,-5f),
                lightIntensity, 0,0,0.2f), coneDir, cutoff);

        //spot light
        coneDir = new Vector3f(0,-50,-1);
        cutoff = (float) Math.cos(Math.toRadians(140));
        lightIntensity = 50000f;
        SpotLight spotLight1 = new SpotLight(new PointLight(lightColor, new Vector3f(1f,50f,-5f),
                lightIntensity, 0,0,0.02f), coneDir, cutoff);


        //directional light
        lightIntensity = 1f;
        lightPosition = new Vector3f(-1, 0, 0);
        lightColor = new Vector3f(1,1,1);
        sceneManager.setDirectionalLight(new DirectionalLight(lightColor, lightPosition, lightIntensity));

        sceneManager.setPointLights(new PointLight[]{pointLight});
        sceneManager.setSpotLights(new SpotLight[]{spotLight, spotLight1}); */

        float reflectance = 1f;

        Model mesh = loader.loadOBJModel("/models/cube.obj");
        Texture texture = new Texture(loader.loadTexture("textures/blue.png"));
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);

        Entity cube = new Entity(mesh, new Vector3f(0,0, -2), new Vector3f(0,0,0), 0.5f);
        sceneManager.addEntity(cube);

        // Ambient Light
        sceneManager.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));

        // Point Light
        Vector3f lightPosition = new Vector3f(0,0,1);
        float lightIntensity = 1.0f;
        PointLight pointLight = new PointLight(new Vector3f(1,1,1), lightPosition, lightIntensity);
        PointLight.Attenuation attenuation = new PointLight.Attenuation(0.0f,0.0f,1.0f);
        pointLight.setAttenuation(attenuation);
        sceneManager.setPointLights(new PointLight[]{pointLight});

        // Spot Light
        lightPosition = new Vector3f(0, 0.0f, 10f);
        pointLight = new PointLight(new Vector3f(1,1,1), lightPosition, lightIntensity);
        attenuation = new PointLight.Attenuation(0,0,0.02f);
        pointLight.setAttenuation(attenuation);
        Vector3f coneDir = new Vector3f(0,0,-1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        SpotLight spotLight = new SpotLight(pointLight, coneDir, cutoff);
        sceneManager.setSpotLights(new SpotLight[]{spotLight, new SpotLight(spotLight)});

        // Directional Light
        lightPosition = new Vector3f(-1, 0, 0);
        sceneManager.setDirectionalLight(new DirectionalLight(new Vector3f(1,1,1), lightPosition, lightIntensity));
    }

    @Override
    public void input() {
        cameraInc.set(0,0,0);
        if(window.isKeyPressed(GLFW.GLFW_KEY_W))
            cameraInc.z = -1;
        if(window.isKeyPressed(GLFW.GLFW_KEY_S))
            cameraInc.z = 1;
        if(window.isKeyPressed(GLFW.GLFW_KEY_A))
            cameraInc.x = -1;
        if(window.isKeyPressed(GLFW.GLFW_KEY_D))
            cameraInc.x = 1;

        if(window.isKeyPressed(GLFW.GLFW_KEY_Z))
            cameraInc.y = -1;
        if(window.isKeyPressed(GLFW.GLFW_KEY_X))
            cameraInc.y = 1;

        float lightPos = sceneManager.getSpotLights()[0].getPointLight().getPosition().z;
        if(window.isKeyPressed(GLFW.GLFW_KEY_N)) {
            sceneManager.getSpotLights()[0].getPointLight().getPosition().z = lightPos + 0.1f;
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_M)) {
            sceneManager.getSpotLights()[0].getPointLight().getPosition().z = lightPos - 0.1f;
        }
    }

    @Override
    public void update(MouseInput mouseInput) {
        camera.movePosition(cameraInc.x * Consts.CAMERA_STEP, cameraInc.y * Consts.CAMERA_STEP, cameraInc.z * Consts.CAMERA_STEP);

        if(mouseInput.isRightButtonPress()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
        }

        sceneManager.incSpotAngle(sceneManager.getSpotInc() * 0.05f);

        if(sceneManager.getSpotAngle() > 9600)
            sceneManager.setSpotAngle(-1);
        else if (sceneManager.getSpotAngle() <= -9600)
            sceneManager.setSpotAngle(1);

        double spotAngleRad = Math.toRadians(sceneManager.getSpotAngle());
        Vector3f coneDir = sceneManager.getSpotLights()[0].getConeDirection();
        coneDir.y = (float) Math.sin(spotAngleRad);

        float factor = 1 - (Math.abs(sceneManager.getLightAngle() - 80) / 10.0f);
        sceneManager.getDirectionalLight().setIntensity(factor);
        sceneManager.getDirectionalLight().getColor().y = Math.max(factor, 0.9f);
        sceneManager.getDirectionalLight().getColor().z = Math.max(factor, 0.5f);

        /*
        sceneManager.incLightAngle(1.1f);
        if(sceneManager.getLightAngle() > 90) {
            sceneManager.getDirectionalLight().setIntensity(0);
            if(sceneManager.getLightAngle() >= 360)
                sceneManager.setLightAngle(-90);
        } else if (sceneManager.getLightAngle() <= -80 || sceneManager.getLightAngle() >= 80) {
            float factor = 1 - (Math.abs(sceneManager.getLightAngle()) - 80) / 10.0f;
            sceneManager.getDirectionalLight().setIntensity(factor);
            sceneManager.getDirectionalLight().getColor().y = Math.max(factor, 0.9f);
            sceneManager.getDirectionalLight().getColor().z = Math.max(factor, 0.5f);
        } else {
            sceneManager.getDirectionalLight().setIntensity(1);
            sceneManager.getDirectionalLight().getColor().x = 1;
            sceneManager.getDirectionalLight().getColor().y = 1;
            sceneManager.getDirectionalLight().getColor().z = 1;
        }
        double angRad = Math.toRadians(sceneManager.getLightAngle());
        sceneManager.getDirectionalLight().getDirection().x = (float) Math.sin(angRad);
        sceneManager.getDirectionalLight().getDirection().y = (float) Math.cos(angRad);
         */

        for(Entity entity : sceneManager.getEntities()) {
            renderer.processEntity(entity);
        }

        for(Terrain terrain : sceneManager.getTerrains()) {
            renderer.processTerrain(terrain);
        }
    }

    @Override
    public void render() {
        renderer.render(camera, sceneManager);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
