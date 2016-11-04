/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package durmoth.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import java.util.HashMap;

public class TerrainAssets {
    
    public AssetManager am;
    
    public HashMap<String,Spatial> models = new HashMap<String,Spatial>();
    public HashMap<String,Material> materials = new HashMap<String,Material>();
    
    public TerrainAssets(AssetManager a){
        am = a;
    }
    
    public TerrainAssets(AssetManager a, HashMap<String,Spatial> m1, HashMap<String,Material> m2){
        am = a;
        models = m1;
        materials = m2;
    }
    
    public void addMaterial(String key, Material mat){
        materials.put(key, mat);
    }
    
    public Material getMaterial(String key){
        return materials.get(key).clone();
    }
    
    public void removeMaterial(String key){
        materials.remove(key);
    }
    
    public void addModel(String key, Spatial mod){
        models.put(key, mod);
    }
    
    public Spatial getModel(String key){
        return models.get(key).clone();
    }
    
    public void removeModel(String key){
        models.remove(key);
    }
    
    public TerrainAssets clone(){
        return new TerrainAssets(null, models, materials);
    }
}
