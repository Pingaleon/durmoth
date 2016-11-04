/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package durmoth.utils;

/**
 *
 * @author User
 */
public class Vector2int implements Cloneable{
    public int x;
    public int y;
    public Vector2int(int _x, int _y){
       x = _x;
       y = _y;
    }
    public boolean equals(Vector2int b){
        return x==b.x&&y==b.y;
    }
    @Override
    public String toString(){
        return "( x: "+x+" y: "+y+" )";
    }
    
    @Override
    public Vector2int clone(){
        return new Vector2int(x,y);
    }
}
