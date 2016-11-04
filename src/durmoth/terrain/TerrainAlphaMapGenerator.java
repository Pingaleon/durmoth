/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package durmoth.terrain;

import com.flowpowered.noise.Utils;
import com.flowpowered.noise.module.source.Perlin;
import durmoth.utils.FunctionUtils;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class TerrainAlphaMapGenerator {
    
    private Terrain terr;
    private int size;
    private int xOff, yOff;
    private int seed;
    
    public TerrainAlphaMapGenerator(Terrain t){
        terr = t;
        size = terr.getSize();
        xOff = terr.getxOff();
        yOff = terr.getyOff();
        seed = terr.getSeed();
    }
    
    public BufferedImage generate(){
        BufferedImage image = new BufferedImage(size+1, size+1, BufferedImage.TYPE_INT_RGB);
        Perlin noise = new Perlin();
        noise.setFrequency(0.2f);
        noise.setOctaveCount(2);
        noise.setSeed(seed);
        //double max = -3.0;
        //double min = 3.0;
        int oneCount = 0;
        for(int i = -1; i < size; i++){
            for(int j = -1; j < size; j++){
                float x = (float) xOff*size+i;
                float y = (float) yOff*size+j;
                double v = noise.getValue(x, 0.0f, y);
                //if(v>max) max = v;
                //if(v<min) min = v;
                v = FunctionUtils.map(v, 0.51, 0.97, -0.5, 1.5);
                double red = 0.0;
                if(v<0.25){red = 1.0;}
                else if(v<0.5){red = 1.0-((v-0.25)*4.0);}

                double green = 0.0;
                if(v>0.25&&v<0.75){green=1.0;}
                else if(v<=0.25){green = v*4.0;}
                else{green = 1.0-(v-0.75)*4.0;}

                double blue = 0.0;
                if(v>0.75){blue = 1.0;}
                else if(v>0.5){blue = (v-0.5)*4.0;}
                
                int red_int = (int)(red*255);
                int green_int = (int)(green*255);
                int blue_int = (int)(blue*255);
                
                red_int = Utils.clamp(red_int, 0, 255);
                green_int = Utils.clamp(green_int, 0, 255);
                blue_int = Utils.clamp(blue_int, 0, 255);
                
                //System.out.println("red: "+red_int+" green:"+green_int+" blue: "+blue_int);
                
                Color col = new Color(red_int, green_int, blue_int);
                image.setRGB(i+1, j+1, col.getRGB());
            }
        }
        //System.out.println(oneCount);
        //System.out.println("max: "+max+" min: "+min);
        return image;
    }
    
    public void display(){
        BufferedImage image = this.generate();
        JOptionPane.showMessageDialog(null, null, "Test", JOptionPane.YES_NO_OPTION, new ImageIcon(image.getScaledInstance(size, size, Image.SCALE_DEFAULT)));

    }
    
    public static void main(String[] args){
        Terrain t = new Terrain(512, 0, 0, 1234, null);
        TerrainAlphaMapGenerator thmg = new TerrainAlphaMapGenerator(t);
        thmg.display();
    }
}
