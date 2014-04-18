package org.protege.oboeditor.frames;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.protege.oboeditor.util.OBOVocabulary;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLDataFactory;

/**
 * @author Simon Jupp
 * @date 14/03/2014
 * Functional Genomics Group EMBL-EBI
 */
public class OBOAnnotationFrame extends AbstractOWLFrame<OWLAnnotationSubject> {


    public OBOAnnotationFrame(OWLEditorKit man) {
        super(man.getModelManager().getOWLOntologyManager());
        
        final OWLDataFactory factory = man.getModelManager().getOWLDataFactory();
        
        addSection(new OBOAnnotationFrameSection(man, this, "Identifier",
                factory.getOWLAnnotationProperty(OBOVocabulary.OBO_ID.getIRI()), 1, false));
        addSection(new OBOAnnotationFrameSection(man, this, "Alternate Identifier",
                factory.getOWLAnnotationProperty(OBOVocabulary.OBO_ALTERNATE_ID.getIRI()), -1, false));
        
		addSection(new OBOAnnotationFrameSection(man, this, "Definition",
                factory.getOWLAnnotationProperty(OBOVocabulary.DEFINITION.getIRI()), 1, true));
        addSection(new OBOAnnotationFrameSection(man, this, "Exact synonym",
                factory.getOWLAnnotationProperty(OBOVocabulary.HAS_EXACT_SYNONYM.getIRI()), -1, true));
        addSection(new OBOAnnotationFrameSection(man, this, "Related synonym",
                factory.getOWLAnnotationProperty(OBOVocabulary.HAS_RELATED_SYNONYM.getIRI()), -1, true));
        addSection(new OBOAnnotationFrameSection(man, this, "Broad synonym",
                factory.getOWLAnnotationProperty(OBOVocabulary.HAS_BROAD_SYNONYM.getIRI()), -1, true));
        addSection(new OBOAnnotationFrameSection(man, this, "Narrow synonym",
                factory.getOWLAnnotationProperty(OBOVocabulary.HAS_NARROW_SYNONYM.getIRI()), -1, true));
        addSection(new OBOAnnotationFrameSection(man, this, "Comment",
                factory.getRDFSComment(), 1, false));
        addSection(new OBOAnnotationFrameSection(man, this, "Subset",
        		factory.getOWLAnnotationProperty(OBOVocabulary.OBO_SUBSET.getIRI()), -1, true));
        addSection(new OBOAnnotationFrameSection(man, this, "Database Cross References",
        		factory.getOWLAnnotationProperty(OBOVocabulary.OBO_DBXREF.getIRI()), -1, false));
        refill();
    }
}
