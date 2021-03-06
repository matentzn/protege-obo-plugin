package org.protege.oboeditor.views;

import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.protege.oboeditor.config.Config;
import org.protege.oboeditor.frames.OBOAnnotationFrame;
import org.protege.oboeditor.frames.OBOAnnotationFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import java.awt.*;

/**
 * @author Simon Jupp
 * @date 14/03/2014
 * Functional Genomics Group EMBL-EBI
 */
public class OBOAnnotationViewComponent extends AbstractOWLClassViewComponent {

    /**
     *
     */
    private static final long serialVersionUID = -3036939007124710864L;
    private OBOAnnotationFrameList<OWLAnnotationSubject> list;


    public void disposeView() {
        list.dispose();
    }


    @Override
    public void initialiseClassView() throws Exception {
        Config.getInstance().prepareConfig("https://raw.githubusercontent.com/matentzn/protege-obo-plugin/master/src/main/resources/phenoconfig.yml");
        list = new OBOAnnotationFrameList<OWLAnnotationSubject> (getOWLEditorKit(), new OBOAnnotationFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
    }

    @Override
    protected OWLClass updateView(OWLClass selectedClass) {
        list.setRootObject(selectedClass == null ? null : selectedClass.getIRI());
        return selectedClass;
    }
}
