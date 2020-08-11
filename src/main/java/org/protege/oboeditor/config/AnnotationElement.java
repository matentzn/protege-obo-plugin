package org.protege.oboeditor.config;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.ArrayList;
import java.util.List;

public class AnnotationElement {
    private String title = "";
    private final OWLAnnotationProperty annotationProperty;
    private final List<AnnotationElement> subelements = new ArrayList<>();
    private OWLDatatype datatype = OWL2Datatype.valueOf("XSD_STRING").getDatatype(OWLManager.getOWLDataFactory());

    AnnotationElement(OWLAnnotationProperty annotationProperty) {
        this.annotationProperty = annotationProperty;
    }


    public String getTitle() {
        if(title.isEmpty()) {
            return annotationProperty.getIRI().getShortForm();
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addElement(AnnotationElement e) {
        subelements.add(e);
    }

    public OWLAnnotationProperty getAnnotationProperty() {
        return annotationProperty;
    }

    public OWLDatatype getDatatype() {
        return datatype;
    }

    public void setDatatype(OWLDatatype datatype) {
        this.datatype = datatype;
    }
}
