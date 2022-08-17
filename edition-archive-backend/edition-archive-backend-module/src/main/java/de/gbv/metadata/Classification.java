package de.gbv.metadata;

public class Classification {

    private String classid;
    private String categid;


    public Classification(String classid, String categid) {
        this.classid = classid;
        this.categid = categid;
    }


    public Classification() {
    }


    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getCategid() {
        return categid;
    }

    public void setCategid(String categid) {
        this.categid = categid;
    }
}
