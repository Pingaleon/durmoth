package durmoth;

import durmoth.unused.PerlinAlphaNoise;
import durmoth.unused.PerlinNoise;
import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.post.filters.TranslucentBucketFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.LodCalculator;
import com.jme3.terrain.geomipmap.lodcalc.SimpleLodThreshold;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.water.SimpleWaterProcessor;
import com.jme3.water.WaterFilter;
import durmoth.terrain.Terrain;
import durmoth.terrain.TerrainAssets;
import durmoth.terrain.TerrainLoaderAppState;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * test
 * @author normenhansen
 */
public class Test extends SimpleApplication {
    
    private Node terrNode;
    private TerrainQuad terrain;
    Material mat_terrain;
    private FogFilter fog;
    private SpotLight spot;
    private FilterPostProcessor fpp;
    private WaterFilter waterF;
    private Node terr;
    private Vector3f lightDir = new Vector3f(-4.9f, -1.3f, 5.9f); // same as light source
    private float initialWaterHeight = 20f; // choose a value for your scene
    private TerrainLoaderAppState terrAppState = new TerrainLoaderAppState();
    private List<Spatial> test = new ArrayList<Spatial>();
    public TerrainAssets terrAss;
    
    private Random rand = new Random(1234);

    public static void main(String[] args) {
        Test app = new Test();
        AppSettings cfg = new AppSettings(true);
        cfg.setRenderer(AppSettings.LWJGL_OPENGL_ANY);
        cfg.setFrameRate(60); // set to less than or equal screen refresh rate
        cfg.setVSync(false);   // prevents page tearing
        cfg.setFrequency(60); // set to screen refresh rate
        cfg.setResolution(1024, 768);   
        cfg.setFullscreen(false); 
        cfg.setSamples(0);    // anti-aliasing
        cfg.setTitle("Swamp");
        app.setSettings(cfg);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //Spatial sky = assetManager.loadModel("Scenes/skybox.j3o");
        //rootNode.attachChild(sky);
        viewPort.setBackgroundColor(new ColorRGBA(0.1f,0.1f,0.1f,1.0f));
        flyCam.setMoveSpeed(300);
        initLight();
        //initTerrain();
        initTerr();
        initSimpleWater();
        //initShaderFog();
    }
    
    public void initLight(){
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0.0f,-1f,-1f));
        sun.setColor(ColorRGBA.White.mult(1.1f));
        spot = new SpotLight();
        spot.setSpotRange(200f);                           // distance
        spot.setSpotInnerAngle(40f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(60f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.White.mult(1.3f));         // light color
        spot.setPosition(cam.getLocation());               // shine from camera loc
        spot.setDirection(cam.getDirection());             // shine forward from camera loc
        rootNode.addLight(spot);
        //rootNode.addLight(sun);
    }
    
    public void initTerrain(){
        terrNode = new Node();
        mat_terrain = new Material(assetManager,
            "Common/MatDefs/Terrain/TerrainLighting.j3md"); 
        PerlinAlphaNoise alphaNoise = new PerlinAlphaNoise(512, 512, rand);
        Image alpha = new AWTLoader().load(alphaNoise.getImage(), false);
        Texture2D alpha_map = new Texture2D(alpha);
        mat_terrain.setTexture("AlphaMap", alpha_map);
        Texture grass = assetManager.loadTexture(
            "Textures/splat/grass_splat.png");
        grass.setWrap(WrapMode.MirroredRepeat);
        mat_terrain.setTexture("DiffuseMap", grass);
        mat_terrain.setFloat("DiffuseMap_0_scale", 128f);
        Texture dirt = assetManager.loadTexture(
            "Textures/splat/dirt_splat.png");
        dirt.setWrap(WrapMode.MirroredRepeat);
        mat_terrain.setTexture("DiffuseMap_1", dirt);
        mat_terrain.setFloat("DiffuseMap_1_scale", 128f);
        Texture path = assetManager.loadTexture(
            "Textures/splat/path_splat.png");
        path.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("DiffuseMap_2", path);
        mat_terrain.setFloat("DiffuseMap_2_scale", 128f);
        AbstractHeightMap heightmap = null;
        PerlinNoise noise = new PerlinNoise(512, 512, 1234, 0, 0);
        Image image = new AWTLoader().load(noise.getImage(), false);
        heightmap = new ImageBasedHeightMap(image);
        heightmap.load();
        
        int patchSize = 65;
        terrain = new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap());
        
        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(2f, 1f, 2f);
        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        terrain.addControl(control);
        terrNode.attachChild(terrain);
        for(int i = 0; i < rand.nextInt(400)+100; i++){
            Spatial tree = assetManager.loadModel("Models/tree/unused/baum1.j3o");
            /*Material mat = new Material(assetManager, 
            "MatDefs/LightingFog.j3md");
            mat.setTexture("DiffuseMap", assetManager.loadTexture(
            "/Models/grass.png"));
            mat.setColor("fogColor", new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f));
            mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            tree.setMaterial(mat);
            tree.setQueueBucket(Bucket.Translucent);*/
            tree.setLocalScale(4.0f);
            Node terrainNode = (Node) terrain;
            float x = (float) rand.nextFloat()*1024-512;
            float z = (float) rand.nextFloat()*1024-512;
            tree.rotate(0f, rand.nextFloat()*360f, 0f);
            tree.setLocalTranslation(x, terrain.getHeight(new Vector2f(x,z))-100f, z);
            if(!(terrain.getHeight(new Vector2f(x,z))<120f))
                terrNode.attachChild(tree);
        }
        for(int i = 0; i< rand.nextInt(30)+1; i++){
                /** Uses Texture from jme3-test-data library! */
             ParticleEmitter fireEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
            Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
            fireMat.setTexture("Texture", assetManager.loadTexture("Textures/plants/grass.png"));
            fireEffect.setMaterial(fireMat);
            fireEffect.setImagesX(2); fireEffect.setImagesY(2); // 2x2 texture animation
            fireEffect.setEndColor( new ColorRGBA(1f, 0f, 0f, 1f) );   // red
            fireEffect.setStartColor( new ColorRGBA(1f, 1f, 0f, 0.5f) ); // yellow
            fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 4, 0));
            fireEffect.setStartSize(3f);
            fireEffect.setEndSize(1f);
            fireEffect.setGravity(0f,0f,0f);
            fireEffect.setLowLife(0.5f);
            fireEffect.setHighLife(3f);
            fireEffect.getParticleInfluencer().setVelocityVariation(0.3f);
            Node terrainNode = (Node) terrain;
            float x = (float) rand.nextFloat()*1024-512;
            float z = (float) rand.nextFloat()*1024-512;
            fireEffect.setLocalTranslation(x, 22, z);
            if((terrain.getHeight(new Vector2f(x,z))<100f)){
                terrNode.attachChild(fireEffect);
                PointLight lamp = new PointLight();
                lamp.setPosition(fireEffect.getLocalTranslation());
                lamp.setColor(ColorRGBA.Orange);
                lamp.setRadius(100);
                rootNode.addLight(lamp); 
            }
        }
        Node terrainNode = (Node) terrain;
        rootNode.attachChild(terrNode);

    }

    public void initTerr(){
        System.out.println("Initializing terrain");
        stateManager.attach(terrAppState);  
        terrAss = new TerrainAssets(this.assetManager);
        terrAss.addModel("grass", assetManager.loadModel("Models/plant/grass.j3o"));
        terrAss.addModel("lilypad", assetManager.loadModel("Models/plant/lilypad.j3o"));
        terrAss.addModel("tree1", assetManager.loadModel("Models/tree/tree1.j3o"));
        terrAss.addModel("tree3", assetManager.loadModel("Models/raw/tree3.j3o"));
        terrAss.addMaterial("terrain", assetManager.loadMaterial("Materials/Terrain.j3m"));
        terrAss.addMaterial("grass", assetManager.loadMaterial("Materials/HighGrass.j3m"));
        terrAss.addMaterial("lilypad", assetManager.loadMaterial("Materials/Lilypad.j3m"));
        terrAss.addMaterial("wood", assetManager.loadMaterial("Materials/Wood.j3m"));
        terrAss.addMaterial("tree3", assetManager.loadMaterial("Materials/Tree3.j3m"));
        terrAss.addMaterial("fire", assetManager.loadMaterial("Materials/Fire.j3m"));
    }
    
    public void initSimpleWater(){
        Spatial water = assetManager.loadModel("Models/water/waterplane.j3o");
        water.scale(100);
        water.setLocalTranslation(new Vector3f(0,0f,0));
        Material mat1 = assetManager.loadMaterial("Materials/Water.j3m");
        /*Material mat1 = new Material(assetManager, 
                "Common/MatDefs/Light/Lighting.j3md");
        Texture waterTex = assetManager.loadTexture("Textures/water/water2.png");
        waterTex.setWrap(WrapMode.MirroredRepeat);
        //mat1.setBoolean("UseMaterialColors",true);
        mat1.setTexture("DiffuseMap", waterTex);
        mat1.setColor("Specular", ColorRGBA.Brown);
        mat1.setFloat("Shininess", 100f);
        mat1.setBoolean("UseAlpha", true);
        mat1.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);*/
        water.setMaterial(mat1);
        water.setQueueBucket(Bucket.Transparent);
        rootNode.attachChild(water);

    }
    
    public void initMediumWater(){
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setReflectionScene(rootNode);

        // we set the water plane
        Vector3f waterLocation=new Vector3f(0,-30f,0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
        viewPort.addProcessor(waterProcessor);

        // we set wave properties
        //waterProcessor.setRenderSize(1,1);
        waterProcessor.setWaterDepth(1);         // transparency of water
        waterProcessor.setDistortionScale(0.01f); // strength of waves
        waterProcessor.setWaveSpeed(0.05f);  // speed of waves

        // we define the wave size by setting the size of the texture coordinates
        Quad quad = new Quad(1000, 1000);
        quad.scaleTextureCoordinates(new Vector2f(2f,2f));

        // we create the water geometry from the quad
        Geometry water=new Geometry("water", quad);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setLocalTranslation(-250, -5.1f, 250);
        water.setShadowMode(RenderQueue.ShadowMode.Receive);
        water.setMaterial(waterProcessor.getMaterial());
        rootNode.attachChild(water);
    }
    
    public void initShaderWater(){
        fpp = new FilterPostProcessor(assetManager);
        waterF = new WaterFilter(rootNode, lightDir);
        waterF.setWaterHeight(initialWaterHeight);
        waterF.setWaterColor(ColorRGBA.Brown.mult(2.0f));
        waterF.setUseRipples(false);
        waterF.setDeepWaterColor(ColorRGBA.Brown);
        waterF.setUseFoam(false);
        waterF.setUseHQShoreline(false);
        waterF.setCenter(new Vector3f(0, -50, 0));
        waterF.setRadius(1000.0f);
        waterF.setSpeed(1.0f);
        waterF.setUseCaustics(false);
        waterF.setUseSpecular(false);
        waterF.setUseRipples(false);
        waterF.setUseRefraction(false);
        waterF.setReflectionMapSize(128);
        fpp.addFilter(waterF);
        viewPort.addProcessor(fpp);
    }
    
    public void initShaderFog(){
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        fog=new FogFilter();
        fog.setFogColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f));
        fog.setFogDistance(200);
        fog.setFogDensity(1.0f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
        TranslucentBucketFilter  tbf = new TranslucentBucketFilter();
         fpp.addFilter(tbf);
    }

    @Override
    public void simpleUpdate(float tpf) {
       spot.setPosition(cam.getLocation());               // shine from camera loc
        spot.setDirection(cam.getDirection()); 
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    @Override
    public void update(){
        try{
            super.update();
        }catch(NullPointerException e){
            JOptionPane.showMessageDialog(null, e.getStackTrace(), "NullPointerException", 0);
        }
    }
}
