/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package durmoth.terrain;

import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class VegetationGenerator {
    
    private Terrain terr;
    private int size;
    private int xOff, yOff;
    private int seed;
    private BufferedImage heightMap;
    
    public VegetationGenerator(Terrain t){
        terr = t;
        size = terr.getSize();
        xOff = terr.getxOff();
        yOff = terr.getyOff();
        seed = terr.getSeed();
        heightMap = terr.getHeightMap();
    }
    
    public List<RawVegetationObject> generate(){
        List list = new ArrayList<RawVegetationObject>();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                float x = (float) xOff*size+i;
                float y = (float) yOff*size+j;
                double v = Noise.valueCoherentNoise3D(x, 0.0, y, seed, NoiseQuality.STANDARD);
                int hVal = heightMap.getRGB(i, j);
                Color c = new Color(hVal);
                int hPre = c.getRed();
                double h = ((double)hPre)/255.0;
                //System.out.println(hPre+" "+h);
                if(v>0.655&&v<0.657){
                        Vector2f finalPos = getFinalPos(new Vector3f(x,y,0));
                        float rand = (float)Noise.valueCoherentNoise3D(x, -2.0, y, seed, NoiseQuality.STANDARD);
                        float rot = (float)Noise.valueCoherentNoise3D(x, 3.0, y, seed, NoiseQuality.STANDARD);
                        boolean b = (rand>0.5)?true:false;
                        RawVegetationObject vegObj = new RawVegetationObject(b?VegetationObjectType.TREE1:VegetationObjectType.TREE2, finalPos, rot*360);
                        list.add(vegObj);
                }
                if(v>0.54&&v<0.56){
                        Vector2f finalPos = getFinalPos(new Vector3f(x,y,0)); 
                        float rot = (float)Noise.valueCoherentNoise3D(x, 3.0, y, seed, NoiseQuality.STANDARD);
                        RawVegetationObject vegObj = new RawVegetationObject(VegetationObjectType.GRASS, finalPos, rot*360);
                        list.add(vegObj);
                }
                if(v>0.59&&v<0.59007){
                        Vector2f finalPos = getFinalPos(new Vector3f(x,y,0)); 
                        float rot = (float)Noise.valueCoherentNoise3D(x, 3.0, y, seed, NoiseQuality.STANDARD);
                        RawVegetationObject vegObj = new RawVegetationObject(VegetationObjectType.SWAMP_FIRE, finalPos, rot*360);
                        list.add(vegObj);
                }
                if(v>0.595&&v<0.597){
                        Vector2f finalPos = getFinalPos(new Vector3f(x,y,0)); 
                        float rot = (float)Noise.valueCoherentNoise3D(x, 3.0, y, seed, NoiseQuality.STANDARD);
                        RawVegetationObject vegObj = new RawVegetationObject(VegetationObjectType.LILYPAD, finalPos, rot*360);
                        list.add(vegObj);
                }
            }
        }
        return list;
    }
    
    
    public static void main(String[] args){
        Terrain t = new Terrain(512, 0, 0, 1234, null);
        TerrainHeightMapGenerator thmg = new TerrainHeightMapGenerator(t);
        thmg.display();
        t.setHeightMap(thmg.generate());
        VegetationGenerator veggieGen = new VegetationGenerator(t);
        List<RawVegetationObject> list = veggieGen.generate();
        int trees = 0;
        int grasses = 0;
        int fires = 0;
        int lilypads = 0;
        for(RawVegetationObject vo : list){
            System.out.println("Type: "+vo.getType()+" Pos: "+vo.getPosition()+" Rot: "+vo.getRotation());
            if(vo.getType()==VegetationObjectType.GRASS) grasses++;
            if(vo.getType()==VegetationObjectType.TREE1) trees++;
            if(vo.getType()==VegetationObjectType.TREE2) trees++;
            if(vo.getType()==VegetationObjectType.SWAMP_FIRE) fires++;
            if(vo.getType()==VegetationObjectType.LILYPAD) lilypads++;
        }
        System.out.println("Lenght: "+list.size());
        System.out.println("Grass count: "+grasses+" Trees count: "+trees+" Fires count: "+fires+" Lilypads count: "+lilypads);
    }

    private Vector2f getFinalPos(Vector3f v) {
                        /*float r = (float)Noise.valueCoherentNoise3D(v.x, 1.0, v.y, seed, NoiseQuality.STANDARD);
                        float d = (float)Noise.valueCoherentNoise3D(v.x, 2.0, v.y, seed, NoiseQuality.STANDARD);
                        float rot = (float)Noise.valueCoherentNoise3D(v.x, 3.0, v.y, seed, NoiseQuality.STANDARD);*/
                        Vector3f pos = new Vector3f(v.x,v.y, 0);
                        /*Vector3f addVector = new Vector3f(1, 0, 0);
                        Quaternion roll = new Quaternion();
                        roll.fromAngleAxis(r*FastMath.TWO_PI, Vector3f.UNIT_Z);
                        addVector = roll.mult(pos);
                        addVector.normalizeLocal();
                        addVector = addVector.mult(d);
                        
                        pos = pos.add(addVector);
                        
                        //System.out.println(addVector+" "+d+" "+roll);*/
                        
                        return new Vector2f(pos.x, pos.y);
    }
    
}
