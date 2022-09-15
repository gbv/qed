package de.gbv.metadata.solr;

import de.gbv.metadata.model.Person;
import org.apache.solr.common.SolrInputDocument;
import org.mycore.datamodel.metadata.MCRObject;

public class Person2Solr extends BasicSolrInputDocumentConverter<Person> {
  @Override
  public SolrInputDocument convertToDocument(MCRObject obj, Person person) {
    SolrInputDocument base = super.convertToDocument(obj, person);

    base.setField("displayName", person.getDisplayName());

    person.getIdentifier().forEach(idenitifer -> {
      base.addField("identifier." + idenitifer.getType(), idenitifer.getIdentifier());
    });

    return base;
  }
}
