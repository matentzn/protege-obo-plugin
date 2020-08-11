package org.protege.oboeditor.config;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import java.util.ArrayList;
import java.util.List;

public class AnnotationElement {
    private String title = "";
    private final OWLAnnotationProperty annotationProperty;
    private final List<AnnotationElement> subelements = new ArrayList<>();

    AnnotationElement(OWLAnnotationProperty annotationProperty) {
        this.annotationProperty = annotationProperty;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addElement(AnnotationElement e) {
        subelements.add(e);
    }

}
