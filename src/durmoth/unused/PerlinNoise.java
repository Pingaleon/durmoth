/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package durmoth.unused;

import com.jme3.math.Vector3f;
import java.awt.Color;
import java.awt.Image;
import java.util.List;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
 

public class PerlinNoise {

   // Just a Random class object so I can fill my noise map with random directions.
   public static Random random;

   // Width and Height of the map.
   public int width, height;
   
   public int xOff, yOff;

   // Random directions of length 1.
   private vec2[] values;
      
   private long seed2;
   /**
    * Creates a noise map with specified dimensions.
    * @param width of the noise map.
    * @param height of the noise map.
    */
   public PerlinNoise(int width, int height, int seed, int xOffset, int yOffset) {
      super();
      random = new Random();
      random.setSeed(seed);
      seed2 = random.nextLong();
      this.width = width;
      this.height = height;
      
      this.xOff = xOffset;
      this.yOff = yOffset;

      values = new vec2[(width + 1) * (height + 1)]; // Create an array to store random directions.

      if(height == 8){
          System.out.println("Passt das ? Size: "+height+"*"+width+" xOff : "+xOff+" yOff : "+yOff);
      }
      for (int y = 0; y < height + 1; y++) {
         for (int x = 0; x < width + 1; x++) {
            random.setSeed(seed2+((xOff*width)+x)*((yOff*height)+y));
            int rot = random.nextInt(359); // Random direction.

            // Store random direction of length 1 to our directions array.
            values[x + y * width] = Rotation.point(new vec2(0, 0), new vec2(0, -1), rot);
         }
      }

      // If you're wondering why "width + 1" "height + 1", it is because map looks blurry 
      // at right and bottom edges of the image without it. Try removing it, you will see.
      
   }

   public float noise(float x, float y) {
       
      //x = x+(xOff*width);
      //y = y+(yOff*height);

      // Grid cell coordinates in integer values.
      int gx0 = (int) (Math.floor(x)); // Top-Left
      int gy0 = (int) (Math.floor(y)); // Top-Left
      int gx1 = gx0 + 1; // Down-Right
      int gy1 = gy0 + 1; // Down-Right

      // Random directions.
      vec2 g00 = g(gx0, gy0); // Top-Left
      vec2 g10 = g(gx1, gy0); // Top-Right
      vec2 g11 = g(gx1, gy1); // Down-Right
      vec2 g01 = g(gx0, gy1); // Down-Left

      // Subtract grid cells values from the point specified.
      vec2 delta00 = new vec2(x - gx0, y - gy0); // Top-Left
      vec2 delta10 = new vec2(x - gx1, y - gy0); // Top-Right
      vec2 delta11 = new vec2(x - gx1, y - gy1); // Down-Right
      vec2 delta01 = new vec2(x - gx0, y - gy1); // Down-Left

      // Compute a dot product between random directions and corresponding delta values.
      float s = dot(g00, new vec2(delta00.x, delta00.y)); // Top-Left
      float t = dot(g10, new vec2(delta10.x, delta10.y)); // Top-Right
      float u = dot(g11, new vec2(delta11.x, delta11.y)); // Down-Right
      float v = dot(g01, new vec2(delta01.x, delta01.y)); // Down-Left

      // Compute the weights for x and y axis.
      float sx = weigh(delta00.x);
      float sy = weigh(delta00.y);
      
      // Interpolate between values.
      float a = lerp(sy, s, v); // Interpolate Top-Left(s) and Down-Left(v). We can also call this LEFT
      float b = lerp(sy, t, u); // Interpolate Top-Right(t) and Down-Right(u) We can also call this RIGHT
      float h = lerp(sx, a, b); // Interpolate LEFT(a) and RIGHT(b). We can call this height(h)
      
      h *= 1; // Multiply here so adjust contrast.
      
      // Make sure it is -1 to 1. If you don't change contrast, you don't have to do this.
      if(h > 1) h = 1;
      if(h < -1) h = -1;
      
      return h;
   }

   /**
    * Computes a weight using S-curve function f(x) = 3 * (x * x) - 2 * (x * x * x).
    * @param x NOT as in axis, but as a variable.
    */
   private float weigh(float x) {
      return 3 * (x * x) - 2 * (x * x * x);
   }
   
   /**
    * Interpolate between 2 values, using weight.
    */
   private float lerp(float weight, float a, float b) {
      float result = a + weight * (b - a);
      return result;
   }
   
   /**
    * Compute a dot product.
    * Example: dot product between (a, b) and (c, d) is:
    * a * c + b * d
    */
   private float dot(vec2 v0, vec2 v1) {
      return (v0.x * v1.x) + (v0.y * v1.y);
   }

   /**
    * Get the random direction.
    */
   private vec2 g(int b_x, int b_y) {
      int x = b_x;//-(xOff*width);
      int y = b_y;//-(yOff*width);
      if (x < 0) x = 0;
      if (y < 0) y = 0;
      if (x >= width) x = width;
      if (y >= height) y = height;
      return values[x + y * width];
   }

   public static void main(String args[]) {

      while (true) { // Hope you can click stop application from your IDE. :P

         PerlinNoise n1 = new PerlinNoise(4096,4096,1,0,0);
         n1.display();

         

      }

   }

   private void display() {

      random = new Random();
       
      long time0 = System.nanoTime(); // Take a time stamp.
      
      BufferedImage image = getImage();

      long time1 = System.nanoTime(); // Take another time stamp.
      
      String time = "It took: " + (time1 - time0) / 1000000 + "MS to generate the image."; // Make a string which says how long it took to generate the image.
      
      JOptionPane.showMessageDialog(null, null, "Perlin Noise | " + time, JOptionPane.YES_NO_OPTION, new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_DEFAULT)));
      
   }
   
   public BufferedImage getImage() {

      int size1 = 8; // Perlin's noise map.
      PerlinNoise noise1 = new PerlinNoise(size1, size1, 1, xOff, yOff);
      
      int size2 = 45;
      PerlinSubNoise noise2 = new PerlinSubNoise(size2, size2 , random);
      
      int width = 512; // Width of the finished image.
      int height = 512; // Height of the finished image.

      BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // Image to store pixel data in.

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            
            float xx1 = (float) x / width * size1; // Where does the point lie in the noise space according to image space. 
            float yy1 = (float) y / height * size1; // Where does the point lie in the noise space according to image space. 
            
            float xx2 = (float) x / width * size2; // Where does the point lie in the noise space according to image space. 
            float yy2 = (float) y / height * size2; // Where does the point lie in the noise space according to image space. 
            
            float n = (float) noise1.noise(xx1, yy1); // Noise values from Perlin's noise.
            float p = (float) noise2.noise(xx2, yy2); //Noise value from the finer Noise.
            
            p = p / 40;
            
            
            //System.out.println("n :"+n+" p: "+p);
            
            if(n > 0.13f){  // Noise cutoff at values near 0 for realistic swamp landscape.
                n =  n - 0.13f;
            } else if(n > -0.13f){
                n = 0.0f;
            }else{
                n = n + 0.13f;
            }
            
            if(n > 0){
                if(p>n) n = p;
            } else{
                n = n+p;
            }
            
            n = n - 0.04f;
            
            /*if(n < 0.05f && n > 0.03f){
                n = 0.03f;
            }*/
            
            if(n>1) n = 1;
            if(n<-1)n = -1;
                      
            int col = (int) ((n + 1) * 255 / 2f); // Since noise value returned is -1 to 1, we make it so that -1 is black, and 1 is white.
            
            Color color = new Color(col, col, col); // java.AWT color to get RGB from.
            image.setRGB(x, y, color.getRGB()); // set XY image value to our generated color.
            
         }
      }
      return image;
      
   }
   
   public static class vec2 {

      public float x, y;

      /**
       * Just holds some float values.
       * @param x
       * @param y
       */
      public vec2(float x, float y) {
         this.x = x;
         this.y = y;
      }
      
      public int getX() {
         return (int) x;
      }
      public int getY() {
         return (int) y;
      }
      
   }
   
   public static class Rotation {

      /** 
       * Rotates specified point around pivot.
       * @param pivot to rotate around.
       * @param point to rotate around pivot.
       * @param rotation - how many degrees to rotate.
       * @return a new point, which was created by rotating given point around pivot by some degrees.
       */
      public static vec2 point(vec2 pivot, vec2 point, float rotation) {
         
         float rot = (float)(1f / 180 * rotation * Math.PI);
         
         float x = point.x - pivot.x;
         float y = point.y - pivot.y;
         
         float newx = (float)(x * Math.cos(rot) - y * Math.sin(rot));
         float newy = (float)(x * Math.sin(rot) + y * Math.cos(rot));
         
         
         newx += pivot.x;
         newy += pivot.y;
         
         return new vec2(newx, newy);
      }
               
   }
   
   public void setRandom(Random rand){
       random = rand;
   }
   
   
}
