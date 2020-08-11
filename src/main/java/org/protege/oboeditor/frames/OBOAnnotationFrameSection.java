package org.protege.oboeditor.frames;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.oboeditor.config.AnnotationElement;
import org.semanticweb.owlapi.model.*;

import java.util.*;

/**
 * @author Simon Jupp
 * Functional Genomics Group EMBL-EBI
 */
public class OBOAnnotationFrameSection extends AbstractOWLFrameSection<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation> {

    private String LABEL;
    private final AnnotationElement annotationElement;

    private static OWLAnnotationSectionRowComparator comparator;

    final OWLAnnotationProperty property;


    public OBOAnnotationFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLAnnotationSubject> frame, AnnotationElement annotationElement) {
        super(editorKit, annotationElement.getTitle(), "Entity annotation", frame);
        this.annotationElement = annotationElement;
        this.LABEL = annotationElement.getTitle();
        this.property = annotationElement.getAnnotationProperty();
        comparator = new OWLAnnotationSectionRowComparator(editorKit.getModelManager());
    }


    public OBOAnnotationFrameSection createFullSection() {
    	return new OBOAnnotationFrameSection(getOWLEditorKit(), getFrame(), annotationElement);
    }

    @Override
    protected void refill(OWLOntology ontology) {
        final boolean hidden = getOWLEditorKit().getWorkspace().isHiddenAnnotationURI(property.getIRI().toURI());
        final OWLAnnotationSubject annotationSubject = getRootObject();

        Set<OWLAnnotationProperty> filterProperty = new HashSet<>();
        filterProperty.add(property);
        
        
        if (hidden) {
            setLabel(LABEL + " (some annotations are hidden)");
        }
        else {
        	Collection<OWLAnnotationAssertionAxiom> relevantAxioms = filterAxioms(ontology.getAnnotationAssertionAxioms(annotationSubject));
        	renderAxioms(relevantAxioms, ontology, annotationSubject);
            setLabel(LABEL);
        }

    }
    
    private Collection<OWLAnnotationAssertionAxiom> filterAxioms(Collection<OWLAnnotationAssertionAxiom> axioms) {
    	if (axioms == null || axioms.isEmpty()) {
			return Collections.emptySet();
		}
    	List<OWLAnnotationAssertionAxiom> filtered = new ArrayList<OWLAnnotationAssertionAxiom>();
    	for(OWLAnnotationAssertionAxiom ax : axioms) {
    		if (property.equals(ax.getProperty())) {
    			filtered.add(ax);
    		}
    	}
    	return filtered;
    }
    
    private void renderAxioms(Collection<OWLAnnotationAssertionAxiom> axioms, OWLOntology ontology, OWLAnnotationSubject annotationSubject) {
        if (axioms != null && !axioms.isEmpty()) {
            addRow(new OBOAnnotationsFrameSectionSummaryRow(getOWLEditorKit(), this, ontology, annotationSubject, axioms, false));
        }
    }

    @Override
    protected void clear() {

    }

    @Override
    public Comparator<OWLFrameSectionRow<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation>> getRowComparator() {
        return comparator;
    }


    @Override
    protected OWLAnnotationAssertionAxiom createAxiom(OWLAnnotation object) {
        return getOWLDataFactory().getOWLAnnotationAssertionAxiom(getRootObject(), object);
    }

    @Override
    public OWLObjectEditor<OWLAnnotation> getObjectEditor() {
        if (!getOWLEditorKit().getModelManager().getActiveOntology().getAnnotationPropertiesInSignature().contains(property)) {
            OWLModelManager man = getOWLEditorKit().getModelManager();
            OWLAxiom ax = man.getOWLDataFactory().getOWLDeclarationAxiom(property);
            man.applyChange(new AddAxiom(getOWLEditorKit().getModelManager().getActiveOntology(), ax));
        }
        return new OBOAnnotationEditor(getOWLEditorKit(), annotationElement);
    }

    private static class OWLAnnotationSectionRowComparator implements Comparator<OWLFrameSectionRow<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation>> {

        private Comparator<OWLObject> owlObjectComparator;

        public OWLAnnotationSectionRowComparator(OWLModelManager owlModelManager) {
            owlObjectComparator = owlModelManager.getOWLObjectComparator();
        }

        public int compare(OWLFrameSectionRow<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation> o1,
                           OWLFrameSectionRow<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation> o2) {
        	if (o1 == o2) {
				return 0;
			}
        	if (o1 == null) {
				return 1;
			}
        	if (o2 == null) {
        		return -1;
        	}
            return owlObjectComparator.compare(o1.getAxiom(), o2.getAxiom());
        }
    }

    public void visit(OWLAnnotationAssertionAxiom axiom) {
        final OWLAnnotationSubject root = getRootObject();
        if (axiom.getSubject().equals(root)){
            reset();
        }
    }

    public boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLAnnotation)) {
                return false;
            }
        }
        return true;
    }

    public boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLAnnotation) {
                OWLAnnotation annot = (OWLAnnotation)obj;
                OWLAxiom ax = getOWLDataFactory().getOWLAnnotationAssertionAxiom(getRootObject(), annot);
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }
            else {
                return false;
            }
        }
        getOWLModelManager().applyChanges(changes);
        return true;
    }

	@Override
	public boolean canAdd() {
		boolean defaultCanAdd = super.canAdd();
		return defaultCanAdd;
	}

	@Override
	public List<OWLAnnotationAssertionAxiom> getAxioms() {
		List<OWLAnnotationAssertionAxiom> axioms = new ArrayList<OWLAnnotationAssertionAxiom>();
		List<OWLFrameSectionRow<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation>> rows = getRows();
		for (OWLFrameSectionRow<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation> row : rows) {
			if (row instanceof OBOAnnotationsFrameSectionSummaryRow) {
				axioms.addAll(((OBOAnnotationsFrameSectionSummaryRow) row).getAxioms());
			}
			else {
				axioms.add(row.getAxiom());
			}
		}
		return axioms;
	}
    
}
