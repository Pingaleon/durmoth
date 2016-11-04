/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package durmoth.terrain;

import com.jme3.math.Vector2f;

/**
 *
 * @author User
 */
public class RawVegetationObject {
    
    private VegetationObjectType type;
    private Vector2f pos;
    private float rotation;
    
    public RawVegetationObject(VegetationObjectType t,Vector2f p, float rot){
        type = t;
        pos = p;
        rotation = rot;
    }
    
    public void setType(VegetationObjectType t){
        type = t;
    }
    
    public void setPosition(Vector2f p){
        pos = p;
    }
    
    public void setRotation(float r){
        rotation = r;
    }
    
    public VegetationObjectType getType(){
        return type;
    }
    
    public Vector2f getPosition(){
        return pos;
    }
    
    public float getRotation(){
        return rotation;
    }
    
}
