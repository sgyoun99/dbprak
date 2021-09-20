
package entity;

import java.io.Serializable;

public class Title implements Serializable{
    private String item_id;
    private String title;
    

    public Title() {}
    public Title(String item_id, String title) {
        this.title = title;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }
    public String getItem_id() {
        return this.item_id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }


/*    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!this.getClass().equals(obj.getClass())) return false;
  
        Title obj2 = (Title)obj;
        if((this.title_id == obj2.getTitle_id()) && (this.title.equals(obj2.getTitle()))) {
           return true;
        }
        return false;
     }
     
     public int hashCode() {
        int tmp = 0;
        tmp = ( title_id + title ).hashCode();
        return tmp;
     }*/


    @Override
    public String toString(){
        return this.title;
    }
}
