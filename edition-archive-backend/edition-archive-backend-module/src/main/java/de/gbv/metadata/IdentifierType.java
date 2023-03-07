package de.gbv.metadata;

import java.util.Objects;

public class IdentifierType {

    private String type;
    private String identifier;

    public IdentifierType(String type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IdentifierType that = (IdentifierType) o;
    return getType().equals(that.getType()) && getIdentifier().equals(that.getIdentifier());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getType(), getIdentifier());
  }

  @Override
    public String toString() {
        return "IdentifierType{" +
                "type='" + type + '\'' +
                ", identifier='" + identifier + '\'' +
                '}';
    }
}
