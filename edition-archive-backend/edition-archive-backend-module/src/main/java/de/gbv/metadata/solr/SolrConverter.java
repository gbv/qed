package de.gbv.metadata.solr;

import de.gbv.metadata.solr.ObjectSolrInputDocumentConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SolrConverter {
  Class<? extends ObjectSolrInputDocumentConverter> converter();
}
