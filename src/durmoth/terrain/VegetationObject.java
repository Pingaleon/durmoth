/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package durmoth.terrain;

import com.jme3.math.Vector3f;

/**
 *
 * @author User
 */
public class VegetationObject {
    
    private VegetationObjectType type;
    private Vector3f pos;
    private float rotation;
    
    public VegetationObject(VegetationObjectType t,Vector3f p, float rot){
        type = t;
        pos = p;
        rotation = rot;
    }
    
    public void setType(VegetationObjectType t){
        type = t;
    }
    
    public void setPosition(Vector3f p){
        pos = p;
    }
    
    public void setRotation(float r){
        rotation = r;
    }
    
    public VegetationObjectType getType(){
        return type;
    }
    
    public Vector3f getPosition(){
        return pos;
    }
    
    public float getRotation(){
        return rotation;
    }
    
}
