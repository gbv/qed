package de.gbv.metadata;

public class Regest {

    public static final String TAG_NAME = "regest";

    private String idno;



    private DateRangeText issued;

    // Ausstellungsort
    private String issuedPlace;

    //Aussteller
    private String issuer;

    //Empfänger
    private String recipient;

    private Authenticity authenticityStatus;

    private String initium;


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

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getIssuedPlace() {
        return issuedPlace;
    }

    public void setIssuedPlace(String issuedPlace) {
        this.issuedPlace = issuedPlace;
    }


    public Authenticity getAuthenticityStatus() {
        return authenticityStatus;
    }

    public void setAuthenticityStatus(Authenticity authenticityStatus) {
        this.authenticityStatus = authenticityStatus;
    }

    public void setInitium(String initium) {
        this.initium = initium;
    }

    public String getInitium() {
        return initium;
    }

    public void setIssued(DateRangeText issued) {
        this.issued = issued;
    }

    public DateRangeText getIssued() {
        return issued;
    }

    @Override
    public String toString() {
        return "Regest{" +
                "idno='" + idno + '\'' +
                ", issued=" + issued +
                ", issuedPlace='" + issuedPlace + '\'' +
                ", issuer='" + issuer + '\'' +
                ", recipient='" + recipient + '\'' +
                ", authenticityStatus=" + authenticityStatus +
                ", initium='" + initium + '\'' +
                '}';
    }
}
