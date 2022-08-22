package de.gbv.metadata;

import java.util.List;
import java.util.stream.Collectors;

public class ClassificationMultivalue {

    private String classid;

    private List<String> categids;

    public ClassificationMultivalue(String classid, List<String> categids) {
        this.classid = classid;
        this.categids = categids;
    }

    public ClassificationMultivalue(){

    }

    public String getClassid() {
        return classid;
    }

    public List<String> getCategids() {
        return categids;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public void setCategids(List<String> categids) {
        this.categids = categids;
    }


    @Override
    public String toString() {
        return "ClassificationMultivalue{" +
                "classid='" + classid + '\'' +
                ", categids=" + String.join(",", categids) +
                '}';
    }
}
