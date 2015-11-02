package coe.com.c0r0vans.GameObjects;

/**
 * @author Shadilan
 */
public class SelectedObject {
    private static SelectedObject instance;
    private GameObject executer;
    public GameObject getExecuter(){
        return executer;
    }
    public void setExecuter(GameObject executer){
        this.executer=executer;
    }
    private GameObject target;
    public GameObject getTarget(){
        return target;
    }
    public void setTarget(GameObject target){
        this.target=target;
    }
    public static SelectedObject getInstance(){
        if (instance==null){
            instance=new SelectedObject();
        }
        return instance;
    }

}
