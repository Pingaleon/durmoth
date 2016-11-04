/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package durmoth.unused;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import static durmoth.unused.PerlinNoise.random;

/**
 *
 * @author User
 */
public class PerlinSubNoise {

   // Just a Random class object so I can fill my noise map with random directions.
   public static Random random;

   // Width and Height of the map.
   public int width, height;

   // Random directions of length 1.
   private PerlinNoise.vec2[] values;

   /**
    * Creates a noise map with specified dimensions.
    * @param width of the noise map.
    * @param height of the noise map.
    */
   public PerlinSubNoise(int width, int height, Random rand) {
      random = rand;
      this.width = width;
      this.height = height;

      values = new PerlinNoise.vec2[(width + 1) * (height + 1)]; // Create an array to store random directions.

      for (int y = 0; y < height + 1; y++) {
         for (int x = 0; x < width + 1; x++) {
            int rot = random.nextInt(359); // Random direction.

            // Store random direction of length 1 to our directions array.
            values[x + y * width] = PerlinNoise.Rotation.point(new PerlinNoise.vec2(0, 0), new PerlinNoise.vec2(0, -1), rot);
         }
      }

      // If you're wondering why "width + 1" "height + 1", it is because map looks blurry 
      // at right and bottom edges of the image without it. Try removing it, you will see.
      
   }

   public float noise(float x, float y) {

      // Grid cell coordinates in integer values.
      int gx0 = (int) (Math.floor(x)); // Top-Left
      int gy0 = (int) (Math.floor(y)); // Top-Left
      int gx1 = gx0 + 1; // Down-Right
      int gy1 = gy0 + 1; // Down-Right

      // Random directions.
      PerlinNoise.vec2 g00 = g(gx0, gy0); // Top-Left
      PerlinNoise.vec2 g10 = g(gx1, gy0); // Top-Right
      PerlinNoise.vec2 g11 = g(gx1, gy1); // Down-Right
      PerlinNoise.vec2 g01 = g(gx0, gy1); // Down-Left

      // Subtract grid cells values from the point specified.
      PerlinNoise.vec2 delta00 = new PerlinNoise.vec2(x - gx0, y - gy0); // Top-Left
      PerlinNoise.vec2 delta10 = new PerlinNoise.vec2(x - gx1, y - gy0); // Top-Right
      PerlinNoise.vec2 delta11 = new PerlinNoise.vec2(x - gx1, y - gy1); // Down-Right
      PerlinNoise.vec2 delta01 = new PerlinNoise.vec2(x - gx0, y - gy1); // Down-Left

      // Compute a dot product between random directions and corresponding delta values.
      float s = dot(g00, new PerlinNoise.vec2(delta00.x, delta00.y)); // Top-Left
      float t = dot(g10, new PerlinNoise.vec2(delta10.x, delta10.y)); // Top-Right
      float u = dot(g11, new PerlinNoise.vec2(delta11.x, delta11.y)); // Down-Right
      float v = dot(g01, new PerlinNoise.vec2(delta01.x, delta01.y)); // Down-Left

      // Compute the weights for x and y axis.
      float sx = weigh(delta00.x);
      float sy = weigh(delta00.y);
      
      // Interpolate between values.
      float a = lerp(sy, s, v); // Interpolate Top-Left(s) and Down-Left(v). We can also call this LEFT
      float b = lerp(sy, t, u); // Interpolate Top-Right(t) and Down-Right(u) We can also call this RIGHT
      float h = lerp(sx, a, b); // Interpolate LEFT(a) and RIGHT(b). We can call this height(h)
      
      h *= 4; // Multiply here so adjust contrast.
      
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
   private float dot(PerlinNoise.vec2 v0, PerlinNoise.vec2 v1) {
      return (v0.x * v1.x) + (v0.y * v1.y);
   }

   /**
    * Get the random direction.
    */
   private PerlinNoise.vec2 g(int x, int y) {
      if (x < 0) x = 0;
      if (y < 0) y = 0;
      if (x >= width) x = width;
      if (y >= height) y = height;
      return values[x + y * width];
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
      public static PerlinNoise.vec2 point(PerlinNoise.vec2 pivot, PerlinNoise.vec2 point, float rotation) {
         
         float rot = (float)(1f / 180 * rotation * Math.PI);
         
         float x = point.x - pivot.x;
         float y = point.y - pivot.y;
         
         float newx = (float)(x * Math.cos(rot) - y * Math.sin(rot));
         float newy = (float)(x * Math.sin(rot) + y * Math.cos(rot));
         
         
         newx += pivot.x;
         newy += pivot.y;
         
         return new PerlinNoise.vec2(newx, newy);
      }
               
   }
   
      public void setRandom(Random rand){
       random = rand;
   }
   
}

