package de.gbv.metadata;

public class Regest {

    public static final String TAG_NAME = "regest";

    private String idno;

    private ClassificationMultivalue deliveryForm;

    private DateRangeText issued;

    // Ausstellungsort
    private String issuedPlace;

    //Aussteller
    private String issuer;

    //Empfänger
    private String recipient;

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

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
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
