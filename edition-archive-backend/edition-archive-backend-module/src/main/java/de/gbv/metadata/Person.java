package de.gbv.metadata;

import java.util.ArrayList;
import java.util.List;

public class Person {


    private String displayName;

    private List<String> firstName = new ArrayList<>();

    private String lastName;

    private List<IdentifierType> identifier = new ArrayList<>();

    private List<String> role = new ArrayList<>();

    private List<String> affiliation = new ArrayList<>();

    public Person() {
    }

    public List<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(List<String> firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<IdentifierType> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierType> identifier) {
        this.identifier = identifier;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

    public List<String> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<String> affiliation) {
        this.affiliation = affiliation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "displayName='" + displayName + '\'' +
                ", firstName=" + firstName +
                ", lastName='" + lastName + '\'' +
                ", identifier=" + identifier +
                ", role=" + role +
                ", affiliation=" + affiliation +
                '}';
    }
}
