package org.protege.oboeditor.frames;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLAnnotationPropertyHierarchyProvider;
import org.protege.editor.owl.ui.editor.*;
import org.protege.editor.owl.ui.selector.OWLAnnotationPropertySelectorPanel;
import org.protege.oboeditor.config.AnnotationElement;
import org.protege.oboeditor.panel.DatabaseCrossReferencePanel;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Jupp
 * Functional Genomics Group EMBL-EBI
 */
public class OBOAnnotationEditor extends AbstractOWLObjectEditor<OWLAnnotation> implements VerifiedInputEditor {
    protected final OWLEditorKit owlEditorKit;

    private final AnnotationElement annotationElement;

    private JTabbedPane tabbedPane;

    private JPanel mainPanel;

    private OWLAnnotationPropertySelectorPanel annotationPropertySelector;

    private List<OWLObjectEditor<? extends OWLAnnotationValue>> editors;

    private OWLAnnotationProperty lastSelectedProperty;

    private List<InputVerificationStatusChangedListener> verificationListeners = new ArrayList<InputVerificationStatusChangedListener>();

    private boolean status = false;

    private ChangeListener changeListener = new ChangeListener(){
        public void stateChanged(ChangeEvent event) {
            verify();
        }
    };

    private InputVerificationStatusChangedListener mergedVerificationListener = new InputVerificationStatusChangedListener() {

        public void verifiedStatusChanged(final boolean newState) {
            for (InputVerificationStatusChangedListener listener : verificationListeners) {
                listener.verifiedStatusChanged(newState);
            }
        }
    };

    public OBOAnnotationEditor(OWLEditorKit owlEditorKit, AnnotationElement element) {
        this.owlEditorKit = owlEditorKit;
//        tabbedPane = new JTabbedPane();
        mainPanel = new VerifiedInputJPanel();
        mainPanel.setLayout(new BorderLayout());
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//        mainPanel.add(splitPane);

        annotationElement = element;
        annotationPropertySelector = createAnnotationPropertySelector();
        annotationPropertySelector.setSelection(element.getAnnotationProperty());
//        JPanel listHolder = new JPanel(new BorderLayout());

//        listHolder.add(annotationPropertySelector);
//        listHolder.setPreferredSize(new Dimension(200, 300));

//        splitPane.setLeftComponent(listHolder);
//        splitPane.setRightComponent(tabbedPane);
//        splitPane.setBorder(null);
//        loadEditors();
        final AnnotationEditor constantEditor = new AnnotationEditor(owlEditorKit,element);
        List<OWLObjectEditor<? extends OWLAnnotationValue>> result = new ArrayList<>();
        result.add(constantEditor);
        editors = result;
        initialiseLastSelectedProperty(element.getAnnotationProperty());

        annotationPropertySelector.addSelectionListener(new ChangeListener(){
            public void stateChanged(ChangeEvent event) {
                verify();
            }
        });

        mainPanel.add(constantEditor);

//        tabbedPane.addChangeListener(changeListener);
    }

    protected final void initialiseLastSelectedProperty(OWLAnnotationProperty prop) {
        assert lastSelectedProperty == prop;
//            lastSelectedProperty = getDefaultAnnotationProperty();
    }
    protected OWLAnnotationProperty getDefaultAnnotationProperty() {
        final OWLModelManager mngr = owlEditorKit.getOWLModelManager();
        return mngr.getOWLDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
    }

    protected OWLAnnotationPropertySelectorPanel createAnnotationPropertySelector() {
        final OWLModelManager mngr = owlEditorKit.getOWLModelManager();
        final OWLAnnotationPropertyHierarchyProvider hp =
                mngr.getOWLHierarchyManager().getOWLAnnotationPropertyHierarchyProvider();
        return new OWLAnnotationPropertySelectorPanel(owlEditorKit, true, hp);
    }


    private void loadEditors() {
        editors = createEditors();
        assert !editors.isEmpty();
        for (OWLObjectEditor<? extends OWLAnnotationValue> editor : editors) {
            tabbedPane.add(editor.getEditorTypeName(), editor.getEditorComponent());
        }
        tabbedPane.setSelectedIndex(0);
    }


    protected List<OWLObjectEditor<? extends OWLAnnotationValue>> createEditors() {
        final IRIFromEntityEditor iriEditor = new IRIFromEntityEditor(owlEditorKit);
        iriEditor.addSelectionListener(changeListener);

        final AnnotationEditor constantEditor = new AnnotationEditor(owlEditorKit,annotationElement);
        // @@TODO add change listener

        final OWLAnonymousIndividualAnnotationValueEditor anonIndividualEditor = new OWLAnonymousIndividualAnnotationValueEditor(owlEditorKit);
        // @@TODO add change listener

        final IRITextEditor textEditor = new IRITextEditor(owlEditorKit);
        textEditor.addStatusChangedListener(mergedVerificationListener);

        List<OWLObjectEditor<? extends OWLAnnotationValue>> result = new ArrayList<OWLObjectEditor<? extends OWLAnnotationValue>>();
        result.add(constantEditor);
        result.add(iriEditor);
        result.add(textEditor);
        result.add(anonIndividualEditor);
        return result;
    }


    protected OWLObjectEditor<? extends OWLAnnotationValue> getSelectedEditor() {
        return editors.get(0);
    }


    public boolean setEditedObject(OWLAnnotation annotation) {
        int tabIndex = -1;
        if (annotation != null) {
            annotationPropertySelector.setSelection(annotation.getProperty());
            for (int i = 0; i < editors.size(); i++) {
                OWLObjectEditor editor = editors.get(i);
                // because we don't know the type of the editor we need to test
                if (editor.canEdit(annotation.getValue())) {
                    editor.setEditedObject(annotation.getValue());
                    if (tabIndex == -1) {
                        tabIndex = i;
                    }
                }
                else {
                    editor.setEditedObject(null);
                }
            }
        }
        else {
            annotationPropertySelector.setSelection(lastSelectedProperty);
            for (OWLObjectEditor<? extends OWLAnnotationValue> editor : editors) {
                editor.setEditedObject(null);
            }
        }
//        tabbedPane.setSelectedIndex(tabIndex == -1 ? 0 : tabIndex);
        return true;
    }


    public OWLAnnotation getAnnotation() {
        OWLAnnotationProperty property = annotationPropertySelector.getSelectedObject();
        if (property != null){
            lastSelectedProperty = property;

            OWLDataFactory dataFactory = owlEditorKit.getModelManager().getOWLDataFactory();

            OWLAnnotationValue obj = getSelectedEditor().getEditedObject();

            if (obj != null) {
                return dataFactory.getOWLAnnotation(property, obj);
            }
        }
        return null;
    }


    public String getEditorTypeName() {
        return "OWL Annotation";
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLAnnotation;
    }


    public JComponent getEditorComponent() {
        return mainPanel;
    }


    public JComponent getInlineEditorComponent() {
        return getEditorComponent();
    }


    /**
     * Gets the object that has been edited.
     * @return The edited object
     */
    public OWLAnnotation getEditedObject() {
        return getAnnotation();
    }


    public void dispose() {
        annotationPropertySelector.dispose();
        for (OWLObjectEditor<? extends OWLAnnotationValue> editor : editors) {
            editor.dispose();
        }
    }


    private void verify() {
        if (status != isValid()){
            status = isValid();
            for (InputVerificationStatusChangedListener l : verificationListeners){
                l.verifiedStatusChanged(status);
            }
        }
    }


    private boolean isValid() {
        return annotationPropertySelector.getSelectedObject() != null && getSelectedEditor().getEditedObject() != null;
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        verificationListeners.add(listener);
        listener.verifiedStatusChanged(isValid());
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        verificationListeners.remove(listener);
    }

    private class VerifiedInputJPanel extends JPanel implements VerifiedInputEditor {
        private static final long serialVersionUID = -653287162287844213L;

        public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
            OBOAnnotationEditor.this.addStatusChangedListener(listener);
        }

        public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
            OBOAnnotationEditor.this.removeStatusChangedListener(listener);
        }

    }

    protected final OWLAnnotationProperty getLastSelectedProperty() {
        return lastSelectedProperty;
    }

    protected final OWLAnnotationPropertySelectorPanel getAnnotationPropertySelector() {
        return annotationPropertySelector;
    }
}
