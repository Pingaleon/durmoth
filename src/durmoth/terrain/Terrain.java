/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package durmoth.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
  
public class Terrain {
    
    
    private int size;
    private int xOff, yOff;
    private int seed;
    private BufferedImage heightMap;
    private BufferedImage alphaMap;
    private TerrainAssets assetManager;
    private Node terrain;
    private List<VegetationObject> vegList = new ArrayList<VegetationObject>();
    private List<RawVegetationObject> rawVegList = new ArrayList<RawVegetationObject>();
    private float xScale = 1.0f;
    private float yScale = 1.0f;
        
    public Terrain(int tile_size, int xOffset, int  yOffset, int s, TerrainAssets assetMan){
        size = tile_size;
        xOff = xOffset;
        yOff = yOffset;
        seed = s;
        assetManager = assetMan;
    }
    
    public void setXScale(float s){
        xScale = s;
    }
    
    public void setYScale(float s){
        yScale = s;
    }
    
    public float getXScale(){
        return xScale;
    }
    
    public float getYScale(){
        return yScale;
    }
   
    public void setHeightMap(BufferedImage hMap){
        heightMap = hMap;
    }
    
    public void generateHeightMap(){
        TerrainHeightMapGenerator thmg = new TerrainHeightMapGenerator(this);
        this.setHeightMap(thmg.generate());
    }
    
    public BufferedImage getHeightMap(){
        return heightMap;
    }
    
    public void setAlphaMap(BufferedImage aMap){
        alphaMap = aMap;
    }
    
    public void generateAlphaMap(){
        TerrainAlphaMapGenerator tamg = new TerrainAlphaMapGenerator(this);
        this.setAlphaMap(tamg.generate());
    }
    
    public BufferedImage getAlphaMap(){
        return alphaMap;
    }
    
    public void setRawVegetationObjectList(List<RawVegetationObject> vegL){
        rawVegList = vegL;
    }
    
    public void generateVegetation(){
        VegetationGenerator vegGen = new VegetationGenerator(this);
        setRawVegetationObjectList(vegGen.generate());
    }
    
    public List<RawVegetationObject> getRawVegetationObjectList(){
        return rawVegList;
    }
    
    public int getSize(){
        return size;
    }
    
    public int getxOff(){
        return xOff;
    }
    
    public int getyOff(){
        return yOff;
    }
    
    public int getSeed(){
        return seed;
    }
    
    public List<VegetationObject> getVegetationObjectList(){
        return vegList;
    }
    
    public void setVegetationObjectList(List<VegetationObject> vegL){
        vegList = vegL;
    }
    
    public Node getTerrainQuad(){
        Image height = new AWTLoader().load(heightMap, true);
        Image alpha = new AWTLoader().load(alphaMap, true);
        Texture2D alpha_map = new Texture2D(alpha);
        Material terr_mat = assetManager.getMaterial("terrain");
        terr_mat.setTexture("AlphaMap", alpha_map);
        //Material terr_mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        AbstractHeightMap heightmap = null;
        heightmap = new ImageBasedHeightMap(height);
        heightmap.load();
        Node terrain = new Node();
        Node terrainP = new Node();
        Node terrainG = new Node();
        TerrainQuad terrainObject;
        terrainObject = new TerrainQuad("Terrain_"+xOff+"_"+yOff, (size/4)+1 ,size+1, heightmap.getHeightMap());
        terrainObject.scale(xScale, 0.3f, yScale);
        terrainObject.setLocalTranslation(xOff*size*xScale+size*xScale/2, 0f, yOff*size*yScale+size*yScale/2);
        terrainObject.setMaterial(terr_mat);
        //TODO Verbessern
        terrainP.attachChild(terrainObject);
        for(RawVegetationObject o : rawVegList){
            if(o.getType()==VegetationObjectType.GRASS){
                Spatial grass = assetManager.getModel("grass");
                Material mat = assetManager.getMaterial("grass");
                grass.setMaterial(mat);
                grass.setQueueBucket(Bucket.Translucent);
                float x = o.getPosition().x*xScale;
                float z = o.getPosition().y*yScale;
                float y = terrainObject.getHeight(new Vector2f(x,z));
                Vector3f pos = new Vector3f(x, y, z);
                grass.rotate(0f, o.getRotation(), 0f);
                grass.setLocalTranslation(pos);
                if(y>33f&&y<36f){
                terrainG.attachChild(grass);
                vegList.add(new VegetationObject(o.getType(),pos, o.getRotation()));
                }
            }
            if(o.getType()==VegetationObjectType.TREE1){
                Spatial grass = assetManager.getModel("tree1");
                Material wood = assetManager.getMaterial("wood");
                TangentBinormalGenerator.generate(grass);
                grass.setMaterial(wood);
                grass.scale(2.0f);
                float x = o.getPosition().x*xScale;
                float z = o.getPosition().y*yScale;
                float y = terrainObject.getHeight(new Vector2f(x,z));
                Vector3f pos = new Vector3f(x, y, z);
                grass.rotate(0f, o.getRotation(), 0f);
                grass.setLocalTranslation(pos);
                if(y>33f){
                    terrainP.attachChild(grass);
                    vegList.add(new VegetationObject(o.getType(),pos, o.getRotation()));
                }
            }
            if(o.getType()==VegetationObjectType.TREE2){
                Spatial grass = assetManager.getModel("tree3");
                Material wood = assetManager.getMaterial("tree3");
                grass.setMaterial(wood);
                //TangentBinormalGenerator.generate(grass);
                //grass.setMaterial(wood);
                grass.setQueueBucket(Bucket.Translucent);
                grass.scale(2.0f);
                float x = o.getPosition().x*xScale;
                float z = o.getPosition().y*yScale;
                float y = terrainObject.getHeight(new Vector2f(x,z));
                Vector3f pos = new Vector3f(x, y, z);
                grass.rotate(0f, o.getRotation(), 0f);
                grass.setLocalTranslation(pos);
                if(y>33f){
                    terrainP.attachChild(grass);
                    vegList.add(new VegetationObject(o.getType(),pos, o.getRotation()));
                }
            }
            if(o.getType()==VegetationObjectType.LILYPAD){
                Spatial grass = assetManager.getModel("lilypad");
                Material mat = assetManager.getMaterial("lilypad");
                grass.setMaterial(mat);
                grass.setQueueBucket(Bucket.Translucent);
                float x = o.getPosition().x*xScale;
                float z = o.getPosition().y*yScale;
                float y = 35.81f;
                float sy = terrainObject.getHeight(new Vector2f(x,z));
                Vector3f pos = new Vector3f(x, y, z);
                grass.rotate(0f, o.getRotation(), 0f);
                grass.setLocalTranslation(pos);
                if(sy<=34.81f){
                terrainG.attachChild(grass);
                vegList.add(new VegetationObject(o.getType(),pos, o.getRotation()));
                }
            }
            if(o.getType()==VegetationObjectType.SWAMP_FIRE){
                ParticleEmitter fireEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
                Material fireMat = assetManager.getMaterial("fire");
                fireEffect.setMaterial(fireMat);
                fireEffect.setImagesX(2); fireEffect.setImagesY(2); 
                fireEffect.setEndColor( new ColorRGBA(1f, 0f, 0f, 1f) );  
                fireEffect.setStartColor( new ColorRGBA(1f, 1f, 0f, 0.5f) ); 
                fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 4, 0));
                fireEffect.setStartSize(3f);
                fireEffect.setEndSize(1f);
                fireEffect.setGravity(0f,0f,0f);
                fireEffect.setLowLife(0.5f);
                fireEffect.setHighLife(3f);
                fireEffect.getParticleInfluencer().setVelocityVariation(0.3f);
                float x = o.getPosition().x*xScale;
                float z = o.getPosition().y*yScale;
                float y = 34.81f;
                float sy = terrainObject.getHeight(new Vector2f(x,z));
                Vector3f pos = new Vector3f(x, y, z);
                fireEffect.setLocalTranslation(pos);
                fireEffect.setQueueBucket(Bucket.Translucent);
                if(sy<31.81){
                    terrainG.attachChild(fireEffect);
                    vegList.add(new VegetationObject(o.getType(),pos, o.getRotation()));
                    PointLight lamp = new PointLight();
                    lamp.setPosition(fireEffect.getLocalTranslation());
                    lamp.setColor(ColorRGBA.Orange);
                    lamp.setRadius(100);
                    //terrainG.addLight(lamp); 
                }
            }
        }
        terrain.setName("Terrain_"+xOff+"_"+yOff);
        terrain.attachChild(terrainG);
        terrain.attachChild(terrainP);
        System.out.println(terrain);
        System.out.println(terrain.getChildren());
        for(Spatial s : terrain.getChildren()) System.out.println(((Node) s).getChildren());
         /** Save a Node to a .j3o file. */
        String userHome = System.getProperty("user.home");
        BinaryExporter exporter = BinaryExporter.getInstance();
        File file = new File(userHome+"/debug/Terrain_"+xOff+"_"+yOff+".j3o");
        try {
        exporter.save(terrain, file);
        } catch (IOException ex) {
 }
        return terrain;
    }

}