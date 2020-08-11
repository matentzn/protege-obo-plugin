package org.protege.oboeditor.config;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class Config {
    private static Logger logger = LoggerFactory.getLogger(Config.class);
    private static Config config = null;
    private final static OWLDataFactory df = OWLManager.getOWLDataFactory();
    private final List<AnnotationElement> annotationElements = new ArrayList<>();

    private Config() {
    }

    static void resetConfig() {
        config = null;
    }

    public static Config getInstance() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public void prepareConfig(String url) {
        Map<String, Object> configs;
        try {
            URL url_ = new URL(url);
            Yaml yaml = new Yaml();
            InputStream inputStream = url_.openStream();
            configs = yaml.load(inputStream);
        } catch (Exception e) {
            return;
        }

        if (configs.containsKey("obo_curate")) {
            Object o_curate = configs.get("obo_curate");
            if (o_curate instanceof HashMap) {
                @SuppressWarnings("unchecked")
                HashMap<String, Object> curate = (HashMap<String, Object>) o_curate;
                if (curate.containsKey("elements")) {
                    Object o_elements = curate.get("elements");
                    if (o_elements instanceof ArrayList) {
                        @SuppressWarnings("unchecked")
                        ArrayList<Map<String, Object>> elements = (ArrayList<Map<String, Object>>) o_elements;
                        for (Map<String, Object> element : elements) {
                            if (element.containsKey("property")) {
                                OWLAnnotationProperty ap = df.getOWLAnnotationProperty(IRI.create((String)element.get("property")));
                                AnnotationElement e = new AnnotationElement(ap);
                                annotationElements.add(e);
                                if(element.containsKey("title")) {
                                    e.setTitle((String)element.get("title"));
                                }
                                if(element.containsKey("datatype")) {
                                    e.setDatatype(OWL2Datatype.valueOf((String)element.get("datatype")).getDatatype(df));
                                }
                                if(element.containsKey("annotations")) {
                                    Object o_annotations = element.get("annotations");
                                    if (o_annotations instanceof ArrayList) {
                                        @SuppressWarnings("unchecked")
                                        ArrayList<HashMap<String, Object>> annotations = (ArrayList<HashMap<String, Object>>) o_annotations;
                                        for(HashMap<String, Object> annotation:annotations) {
                                            if(annotation.containsKey("property")) {
                                                OWLAnnotationProperty apa = df.getOWLAnnotationProperty(IRI.create((String)annotation.get("property")));
                                                AnnotationElement ea = new AnnotationElement(apa);
                                                e.addElement(ea);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

            }

        }
        logger.info("Number of annotation elements successfully loaded: "+annotationElements.size());
    }

    public List<AnnotationElement> getAnnotationElements() {
        return annotationElements;
    }
}
