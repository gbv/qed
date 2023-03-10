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
    private EntityLink issuedPlace;

    //Aussteller
    private List<EntityLink> issuer = new ArrayList<>();

    //Empfänger
    private List<EntityLink> recipient = new ArrayList<>();

    private List<EntityLink> bodyPersons = new ArrayList<>();

    private List<EntityLink> bodyOrganizations = new ArrayList<>();
    private Authenticity authenticityStatus;

    private List<String> initium = new ArrayList<>();

    private String ueberlieferungsform;

    public String getUeberlieferungsform() {
        return ueberlieferungsform;
    }

    public void setUeberlieferungsform(String ueberlieferungsform) {
        this.ueberlieferungsform = ueberlieferungsform;
    }

    private EntityLink pontifikatPP;

    private EntityLink pontifikatAEP;
    private final List<EntityLink> bodyPlaces = new ArrayList<>();

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }

    public List<EntityLink> getIssuers() {
        return issuer;
    }


    public List<EntityLink> getRecipients() {
        return recipient;
    }


    public EntityLink getIssuedPlace() {
        return issuedPlace;
    }

    public void setIssuedPlace(EntityLink issuedPlace) {
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

    public EntityLink getPontifikatPP() {
        return pontifikatPP;
    }

    public void setPontifikatPP(EntityLink pontifikatPP) {
        this.pontifikatPP = pontifikatPP;
    }

    public EntityLink getPontifikatAEP() {
        return pontifikatAEP;
    }

    public void setPontifikatAEP(EntityLink pontifikatAEP) {
        this.pontifikatAEP = pontifikatAEP;
    }

    public Authenticity getAuthenticityStatus() {
        return authenticityStatus;
    }

    public void setAuthenticityStatus(Authenticity authenticityStatus) {
        this.authenticityStatus = authenticityStatus;
    }

    public List<EntityLink> getBodyPersons() {
        return bodyPersons;
    }

    public List<EntityLink> getBodyPlaces() {
        return bodyPlaces;
    }


    public List<EntityLink> getBodyOrganizations() {
        return bodyOrganizations;
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
