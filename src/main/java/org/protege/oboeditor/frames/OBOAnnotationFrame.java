package org.protege.oboeditor.frames;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.protege.oboeditor.config.AnnotationElement;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.protege.oboeditor.config.Config;

/**
 * @author Nico Matentzoglu
 * semanticly Ltd
 */
public class OBOAnnotationFrame extends AbstractOWLFrame<OWLAnnotationSubject> {


    public OBOAnnotationFrame(OWLEditorKit man) {
        super(man.getModelManager().getOWLOntologyManager());
        Config c = Config.getInstance();

        for(AnnotationElement e:c.getAnnotationElements()) {
            addSection(new OBOAnnotationFrameSection(man, this, e));
        }
        refill();
    }
    
    public OBOAnnotationFrame(OWLEditorKit man, OBOAnnotationFrameSection section) {
    	super(man.getModelManager().getOWLOntologyManager());
    	addSection(section);
    }
}
