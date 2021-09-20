
package entity;

public class Label{
    private String label;

    public Label() {}
    public Label(String label) {
        this.label = label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString(){
        return this.label;
    }
}
