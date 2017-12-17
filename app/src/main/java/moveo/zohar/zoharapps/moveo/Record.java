package moveo.zohar.zoharapps.moveo;

import io.realm.RealmObject;

public class Record extends RealmObject {

    private int id;
    private String action;

   public Record(){

   }

    public  void setId(int id) {
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public String getAction(){
        return action;
    }

    public  void setAction(String action) {
        this.action = action;
    }
}
