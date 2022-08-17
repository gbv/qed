package de.gbv.metadata;

public class Authenticity {
    boolean fake; // †
    boolean lost; // *
    boolean certainly; // ?

    public Authenticity() {
    }

    public boolean isFake() {
        return fake;
    }

    public void setFake(boolean fake) {
        this.fake = fake;
    }

    public boolean isLost() {
        return lost;
    }

    public void setLost(boolean lost) {
        this.lost = lost;
    }

    public boolean isCertainly() {
        return certainly;
    }

    public void setCertainly(boolean certainly) {
        this.certainly = certainly;
    }

    @Override
    public String toString() {
        return "Authenticity{" +
                "fake=" + fake +
                ", lost=" + lost +
                ", certainly=" + certainly +
                '}';
    }
}
