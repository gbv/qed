package de.gbv.metadata.model;

import de.gbv.metadata.Authenticity;
import de.gbv.metadata.ClassificationMultivalue;
import de.gbv.metadata.DateRangeText;
import de.gbv.metadata.solr.Regest2Solr;
import de.gbv.metadata.solr.SolrConverter;

import java.util.ArrayList;
import java.util.List;

@SolrConverter(converter = Regest2Solr.class)
public class Regest {

    public static final String TAG_NAME = "regest";
    private String idno;

    private ClassificationMultivalue deliveryForm;

    private DateRangeText issued;

    // Ausstellungsort
    private PlaceLink issuedPlace;

    //Aussteller
    private PersonLink issuer;

    //Empfänger
    private PersonLink recipient;

    private List<PersonLink> bodyPersons = new ArrayList<>();

    private Authenticity authenticityStatus;

    private List<String> initium = new ArrayList<>();

    private String ueberlieferungsform;

  public String getUeberlieferungsform() {
    return ueberlieferungsform;
  }

  public void setUeberlieferungsform(String ueberlieferungsform) {
    this.ueberlieferungsform = ueberlieferungsform;
  }

  private PersonLink pontifikatPP;

    private PersonLink pontifikatAEP;
    private final List<PlaceLink> bodyPlaces = new ArrayList<>();

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

    public PlaceLink getIssuedPlace() {
        return issuedPlace;
    }

    public void setIssuedPlace(PlaceLink issuedPlace) {
        this.issuedPlace = issuedPlace;
    }

    public List<String> getInitium() {
        return initium;
    }

    public void setInitium(List<String> initium) {
        this.initium = initium;
    }

    public DateRangeText getIssued() {
        return issued;
    }

    public void setIssued(DateRangeText issued) {
        this.issued = issued;
    }

    public PersonLink getPontifikatPP() {
        return pontifikatPP;
    }

    public void setPontifikatPP(PersonLink pontifikatPP) {
        this.pontifikatPP = pontifikatPP;
    }

    public PersonLink getPontifikatAEP() {
        return pontifikatAEP;
    }

    public void setPontifikatAEP(PersonLink pontifikatAEP) {
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

    public List<PlaceLink> getBodyPlaces() {
        return bodyPlaces;
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
            ", ueberlieferungsform='" + ueberlieferungsform + '\'' +
            '}';
    }
}
