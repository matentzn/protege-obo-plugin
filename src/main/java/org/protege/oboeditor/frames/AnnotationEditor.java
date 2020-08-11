package org.protege.oboeditor.frames;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.OWLLiteralParser;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLAutoCompleter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.protege.oboeditor.config.AnnotationElement;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

public class AnnotationEditor extends JPanel implements OWLObjectEditor<OWLLiteral> {
    private static final long serialVersionUID = 3199534896795886986L;
    private JTextArea annotationContent;
    private JComboBox langComboBox;
    private JLabel langLabel = new JLabel("Lang");
    private JComboBox datatypeComboBox;
    private OWLDataFactory dataFactory;
    private String lastLanguage;
    private OWLDatatype lastDatatype;
    private AnnotationElement annotationElement;

    public AnnotationEditor(OWLEditorKit owlEditorKit, AnnotationElement annotationElement) {
        this.annotationElement = annotationElement;
        this.dataFactory = owlEditorKit.getModelManager().getOWLDataFactory();
        this.annotationContent = new JTextArea(8, 40);
        this.annotationContent.setWrapStyleWord(true);
        this.annotationContent.setLineWrap(true);
        UIHelper uiHelper = new UIHelper(owlEditorKit);
        this.langComboBox = uiHelper.getLanguageSelector();
        this.datatypeComboBox = uiHelper.getDatatypeSelector();
        System.out.println("TEST TEST");
        this.datatypeComboBox.getModel().setSelectedItem(annotationElement.getDatatype());
        this.datatypeComboBox.addActionListener((e) -> {
            OWLDatatype owlDatatype = this.getSelectedDatatype();
            boolean b = owlDatatype == null;
            this.toggleLanguage(b);
        });
        removeNonSelectableDatatypes(this.datatypeComboBox);

        this.datatypeComboBox.getModel().setSelectedItem(annotationElement.getDatatype());
        System.out.println(this.datatypeComboBox.getSelectedIndex());
        System.out.println(this.datatypeComboBox.getSelectedItem());
        this.setupAutoCompleter(owlEditorKit);
        this.layoutComponents();
    }

    private static void removeNonSelectableDatatypes(JComboBox datatypeComboBox) {
        for(int i = 0; i < datatypeComboBox.getItemCount(); ++i) {
            OWLDatatype datatype = (OWLDatatype)datatypeComboBox.getItemAt(i);
            if (datatype != null) {
                if (datatype.isRDFPlainLiteral()) {
                    datatypeComboBox.removeItemAt(i);
                } else if (datatype.isBuiltIn() && datatype.getBuiltInDatatype().equals(OWL2Datatype.OWL_REAL)) {
                    datatypeComboBox.removeItemAt(i);
                }
            }
        }

    }

    private void toggleLanguage(boolean b) {
        this.langLabel.setEnabled(b);
        this.langComboBox.setEnabled(b);
    }

    public boolean canEdit(Object object) {
        return object instanceof OWLLiteral;
    }

    public boolean isPreferred(Object object) {
        return object instanceof OWLLiteral;
    }

    public JComponent getEditorComponent() {
        return this;
    }

    public OWLLiteral getEditedObject() {
        this.lastDatatype = null;
        this.lastLanguage = null;
        String value = this.annotationContent.getText().trim();
        if (this.isDatatypeSelected()) {
            this.lastDatatype = this.getSelectedDatatype();
            return this.dataFactory.getOWLLiteral(value, this.getSelectedDatatype());
        } else if (this.isLangSelected()) {
            this.lastLanguage = this.getSelectedLang();
            return this.dataFactory.getOWLLiteral(value, this.getSelectedLang());
        } else {
            OWLLiteralParser parser = new OWLLiteralParser(this.dataFactory);
            return parser.parseLiteral(value);
        }
    }

    public Set<OWLLiteral> getEditedObjects() {
        return Collections.singleton(this.getEditedObject());
    }

    public boolean setEditedObject(OWLLiteral constant) {
        this.clear();
        if (constant != null) {
            this.annotationContent.setText(constant.getLiteral());
            if (!constant.isRDFPlainLiteral()) {
                this.datatypeComboBox.setSelectedItem(constant.getDatatype());
            } else {
                this.langComboBox.setSelectedItem(constant.getLang());
            }
        }

        return true;
    }

    public boolean isMultiEditSupported() {
        return false;
    }

    public String getEditorTypeName() {
        return "Literal";
    }

    public void clear() {
        this.annotationContent.setText("");
        this.datatypeComboBox.setSelectedItem(this.lastDatatype);
        this.langComboBox.setSelectedItem(this.lastLanguage);
    }

    private boolean isLangSelected() {
        return this.langComboBox.getSelectedItem() != null && !this.langComboBox.getSelectedItem().equals("");
    }

    private boolean isDatatypeSelected() {
        return this.datatypeComboBox.getSelectedItem() != null;
    }

    private String getSelectedLang() {
        return (String)this.langComboBox.getSelectedItem();
    }

    private OWLDatatype getSelectedDatatype() {
        return (OWLDatatype)this.datatypeComboBox.getSelectedItem();
    }

    private void setupAutoCompleter(OWLEditorKit owlEditorKit) {
        new OWLAutoCompleter(owlEditorKit, this.annotationContent, new OWLExpressionChecker() {
            public void check(String text) throws OWLExpressionParserException {
                throw new OWLExpressionParserException(text, 0, text.length(), true, true, true, true, true, true, new HashSet());
            }

            public Object createObject(String text) throws OWLExpressionParserException {
                return null;
            }
        });
    }

    private void layoutComponents() {
        this.setLayout(new GridBagLayout());
        this.add(new JScrollPane(this.annotationContent), new GridBagConstraints(1, 1, 5, 1, 100.0D, 100.0D, 18, 1, new Insets(7, 7, 7, 7), 0, 0));
        this.add(new JLabel("Value"), new GridBagConstraints(1, 0, 5, 1, 0.0D, 0.0D, 18, 0, new Insets(7, 7, 0, 7), 0, 0));
        this.add(new JLabel("Type"), new GridBagConstraints(1, 3, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 7, 0, 7), 0, 0));
        this.add(this.datatypeComboBox, new GridBagConstraints(2, 3, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(5, 5, 5, 5), 40, 0));
        this.add(this.langLabel, new GridBagConstraints(3, 3, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 20, 0, 0), 0, 0));
        this.langLabel.setEnabled(true);
        this.add(this.langComboBox, new GridBagConstraints(4, 3, 1, 1, 100.0D, 0.0D, 17, 0, new Insets(5, 5, 5, 5), 40, 0));
    }

    public void dispose() {
    }

    public void setHandler(OWLObjectEditorHandler<OWLLiteral> owlLiteralOWLObjectEditorHandler) {
    }

    public OWLObjectEditorHandler<OWLLiteral> getHandler() {
        return null;
    }
}
