/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package durmoth.terrain;

import com.flowpowered.noise.module.source.Perlin;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import durmoth.utils.FunctionUtils;

/**
 *
 * @author User
 */
public class TerrainHeightMapGenerator {
    
    private Terrain terr;
    private int size;
    private int xOff, yOff;
    private int seed;
    
    private final double cutoff = 0.3;
    private final double hillyness = 0.15;
    private final double hillScale = 1.0;
    
    public TerrainHeightMapGenerator(Terrain t){
        terr = t;
        size = terr.getSize();
        xOff = terr.getxOff();
        yOff = terr.getyOff();
        seed = terr.getSeed();
        System.out.println("GEN x: "+xOff+" y: "+yOff);
    }
    
    public BufferedImage generate(){
        BufferedImage image = new BufferedImage(size+1, size+1, BufferedImage.TYPE_INT_RGB);
        Perlin mainTerrain = new Perlin();
        mainTerrain.setFrequency(0.01f);
        mainTerrain.setOctaveCount(4);
        mainTerrain.setSeed(seed);
        Perlin finerTerrain = new Perlin();
        finerTerrain.setFrequency(0.03f);
        finerTerrain.setOctaveCount(4);
        finerTerrain.setSeed(seed);
        double max = -3.0;
        double min = 3.0;
        for(int i = -1; i < size; i++){
            for(int j = -1; j < size; j++){
               float x = (float) xOff*size+i;
               float y = (float) yOff*size+j;
               //System.out.println("Loc"+x+" "+y);
               double e1 = mainTerrain.getValue(x, 0.0f, y);
               double e2 = finerTerrain.getValue(x, 1.0f, y);
               //if(e1>max) max = e1;
               //if(e1<min) min = e1;
               double v1 = FunctionUtils.map(e1, 0.73, 1.15, 0.0, 1.0);
               double v2 = FunctionUtils.map(e2, 0.73, 1.15, -hillyness, hillyness);
               if(v2>0.03){
                   v2 = 0.01+((v2-0.01)/4);
                   if(v2<0.03) v2 = 0.03;
               }
               if(v1<-0.03) v2 = -0.01-((v2+0.01)*2);
               if(v1<cutoff) v1 = v1+0.2;
               else if(v1>1-cutoff) v1 = v1-0.2;
               else v1 = 0.5;
               if(v1<=0.5) v1 = v1+v2;
               else{
                   if(0.5+v2>v1) v1 = 0.5+v2; 
                   
               }
               int col = (int) ((v1) * 255);
               Color color = new Color(col, col, col);
               image.setRGB(i+1, j+1, color.getRGB());
            }
        }
        //System.out.println("max: "+max+" min: "+min);
        return image;
    }
    
    public void display(){
        BufferedImage image = this.generate();
        JOptionPane.showMessageDialog(null, null, "Test", JOptionPane.YES_NO_OPTION, new ImageIcon(image.getScaledInstance(size, size, Image.SCALE_DEFAULT)));

    }
    
    public static void main(String[] args){
        Terrain t = new Terrain(512, 0, 0, 1234, null);
        TerrainHeightMapGenerator thmg = new TerrainHeightMapGenerator(t);
        thmg.display();
    }
    
}
