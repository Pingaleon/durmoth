/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package durmoth.terrain;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import durmoth.utils.Vector2int;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author User
 */
public class $TerrainLoaderAppState extends AbstractAppState{
    
    private SimpleApplication app;
    
    private List<Vector2int> currentCoords = new ArrayList<Vector2int>();
    private List<Vector2int> loadedChunks = new ArrayList<Vector2int>();
    private List<Vector2int> toUnload = new ArrayList<Vector2int>();
    private List<Vector2int> toLoad = new ArrayList<Vector2int>();
    private Vector2int workingPos = null;
    private int centerX;
    private int centerY;
    private int c = 0;
    
    
    
    private final float scaleFactor = 1.0458100316200632401264802529605f;

    
    private Vector3f camPos;
    private double x;
    private double y;
    
    private boolean found;
    
    private List<Terrain> terrList = new ArrayList<Terrain>();
    
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    
    private Future<Node> future = null;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
      super.initialize(stateManager, app);
      this.app = (SimpleApplication)app;          // cast to a more specific class               // call custom methods...
   }
    
   @Override
    public void update(float tpf) {
      currentCoords.removeAll(currentCoords);
      camPos = app.getCamera().getLocation();
      centerX = getIntCoord(camPos.x);
      centerY = getIntCoord(camPos.z);
      for(int i = -1; i <= 1; i++){
          for(int j = -1; j <= 1; j++){
              currentCoords.add(new Vector2int(centerX+i, centerY+j));
          }
      }
      for(Vector2int v : currentCoords){
          found = false;
          for(Vector2int e : loadedChunks){
              if(v.equals(e)){
                found = true;
              }
          }
          for(Vector2int f : toLoad){
              if(f.equals(v)){
                found = true;
              }
          }
          try{
            if(workingPos.equals(v)){
                found = true;
            }
          }catch(Exception e){
          }
          if(!found){
            System.out.println(c+" load: "+v.toString());
            toLoad.add(v);
            c++;
          }
      }
      
      for(Vector2int v : loadedChunks){
          found = false;
          for(Vector2int e : currentCoords){
              if(v.equals(e)){
                found = true;
              }
          }
          if(!found){
            toUnload.add(v);
            System.out.println(c+" unload: "+v.toString());
            c++;
          }
      }
      
      boolean c = false;
      
      for(Vector2int v : toUnload){
          unload(v);
          loadedChunks.remove(v);
          c = true;
      }
      toUnload.removeAll(toUnload);
      
      if(workingPos==null&&toLoad.size()>0){     
         workingPos = toLoad.get(0);
         toLoad.remove(workingPos);
         System.out.println("Working pos: "+workingPos+" toLoad: "+toLoad);
         future = executor.submit(generate);
      }
      
      if(future != null){
            //Get the waylist when its done
            if(future.isDone()){
                try{
                app.getRootNode().attachChild(future.get());
                loadedChunks.add(workingPos);
                workingPos = null;
                }catch(Exception e){
                    e.printStackTrace();
                }
                future = null;
            }
            else if(future.isCancelled()){
                //Set future to null. Maybe we succeed next time...
                future = null;
            }
        }
      
      
      if(c){    
          System.out.println(app.getRootNode().getChildren());
          System.out.println(app.getCamera().getLocation());
          System.out.println(" x: "+getIntCoord(app.getCamera().getLocation().x)+" y: "+getIntCoord(app.getCamera().getLocation().z));
      }

      //System.out.println("X: "+centerX+" Y: "+centerY);
    }
   
    public int getIntCoord(double v){
        v = Math.floor(v/128);
        //if(v<=0) v = v - 1.0d;
        return (int) v;
    }
    
    @Deprecated
    public void loadOld(Vector2int v){
        long startTime = System.nanoTime();
        System.out.println("v: "+v);
        Terrain t = new Terrain(128,v.x,v.y,1234,app.getAssetManager());
        t.setXScale(1);
        t.setYScale(1);
        t.generateHeightMap();
        //JOptionPane.showMessageDialog(null, null, "Test", JOptionPane.YES_NO_OPTION, new ImageIcon(t.getHeightMap().getScaledInstance(512, 512, java.awt.Image.SCALE_DEFAULT)));
        t.generateAlphaMap();
        t.generateVegetation();
        Node terr = t.getTerrainQuad();
        terr.setLocalTranslation(0, -130, 0);
        terr.move(0f, 94.2f, 0f);
        long startTime2 = System.nanoTime();
        app.getRootNode().attachChild(terr);  
        long estimatedTime = System.nanoTime() - startTime;
        long estimatedTime2 = System.nanoTime() - startTime2;
        System.out.println("Loading time: "+estimatedTime+" Attaching Time: "+estimatedTime2);
    }
    
    public void load(Vector2int v, Node terr){   
        app.getRootNode().attachChild(terr);  
    }
    
    public void unload(Vector2int v){
        long startTime = System.nanoTime();
        app.getRootNode().detachChildNamed("Terrain_"+v.x+"_"+v.y);
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Unloading time: "+estimatedTime);
    }
    
    public void generateConcurrent(Vector2int v){
        
    }
    
    @Override
    public void finalize() {
      try {
          super.finalize();
      } catch (Throwable ex) {
          Logger.getLogger(TerrainLoaderAppState.class.getName()).log(Level.SEVERE, null, ex);
      }
      executor.shutdown();
    }
    
    private Callable<Node> generate = new Callable<Node>(){
    public Node call() throws Exception {

        //Read or write data from the scene graph -- via the execution queue:
        Vector2int location = app.enqueue(new Callable<Vector2int>() {
            public Vector2int call() throws Exception {
                //we clone the location so we can use the variable safely on our thread
                return app.getStateManager().getState(TerrainLoaderAppState.class).workingPos.clone();
            }
        }).get();
        long startTime = System.nanoTime();
        Terrain t = new Terrain(128,location.x,location.y,1234,app.getAssetManager());
        t.generateHeightMap();
        t.generateAlphaMap();
        t.generateVegetation();
        Node terr = t.getTerrainQuad();
        terr.setLocalTranslation(0, -130, 0);
        terr.move(0f, 94.2f, 0f);
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Loading time: "+estimatedTime);
        return terr;
    }
};
     
}