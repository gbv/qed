package de.gbv.metadata;

import java.util.ArrayList;
import java.util.List;

public class Regest {

    public static final String TAG_NAME = "regest";

    private String idno;

    private ClassificationMultivalue deliveryForm;

    private DateRangeText issued;

    // Ausstellungsort
    private String issuedPlace;

    //Aussteller
    private PersonLink issuer;

    //Empfänger
    private PersonLink recipient;

    private List<PersonLink> bodyPersons = new ArrayList<>();

    private Authenticity authenticityStatus;

    private String initium;

    private String pontifikatPP;

    private String pontifikatAEP;

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }

    public PersonLink getIssuer() {
        return issuer;
    }

    public void setIssuer(PersonLink issuer) {
        this.issuer = issuer;
    }

    public PersonLink getRecipient() {
        return recipient;
    }

    public void setRecipient(PersonLink recipient) {
        this.recipient = recipient;
    }

    public String getIssuedPlace() {
        return issuedPlace;
    }

    public void setIssuedPlace(String issuedPlace) {
        this.issuedPlace = issuedPlace;
    }
    public String getInitium() {
        return initium;
    }

    public void setInitium(String initium) {
        this.initium = initium;
    }

    public DateRangeText getIssued() {
        return issued;
    }

    public void setIssued(DateRangeText issued) {
        this.issued = issued;
    }

    public ClassificationMultivalue getDeliveryForm() {
        return deliveryForm;
    }

    public void setDeliveryForm(ClassificationMultivalue deliveryForm) {
        this.deliveryForm = deliveryForm;
    }

    public String getPontifikatPP() {
        return pontifikatPP;
    }

    public void setPontifikatPP(String pontifikatPP) {
        this.pontifikatPP = pontifikatPP;
    }

    public String getPontifikatAEP() {
        return pontifikatAEP;
    }

    public void setPontifikatAEP(String pontifikatAEP) {
        this.pontifikatAEP = pontifikatAEP;
    }

    public Authenticity getAuthenticityStatus() {
        return authenticityStatus;
    }

    public void setAuthenticityStatus(Authenticity authenticityStatus) {
        this.authenticityStatus = authenticityStatus;
    }


    public List<PersonLink> getBodyPersons() {
        return bodyPersons;
    }

    public void setBodyPersons(List<PersonLink> bodyPersons) {
        this.bodyPersons = bodyPersons;
    }

    @Override
    public String toString() {
        return "Regest{" +
                "idno='" + idno + '\'' +
                ", deliveryForm=" + deliveryForm +
                ", issued=" + issued +
                ", issuedPlace='" + issuedPlace + '\'' +
                ", issuer='" + issuer + '\'' +
                ", recipient='" + recipient + '\'' +
                ", authenticityStatus=" + authenticityStatus +
                ", initium='" + initium + '\'' +
                ", pontifikatPP='" + pontifikatPP + '\'' +
                ", pontifikatAEP='" + pontifikatAEP + '\'' +
                '}';
    }
}
