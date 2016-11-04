/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package durmoth.utils;

/**
 *
 * @author User
 */
public class FunctionUtils {
    
    public static double map(double x, double in_min, double in_max, double out_min, double out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
    
}
