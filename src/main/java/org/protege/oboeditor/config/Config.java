package org.protege.oboeditor.config;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

class Config {
    private String configlocation = "https://raw.githubusercontent.com/matentzn/protege-obo-plugin/master/src/main/resources/phenoconfig.yml";

    private static Config config = null;
    private final OWLDataFactory df = OWLManager.getOWLDataFactory();
    private final List<AnnotationElement> annotationElements = new ArrayList<>();

    private Config() {
    }

    static void resetConfig() {
        config = null;
    }

    static Config getInstance() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    void prepareConfig(String url) throws IOException {
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
                    Object o_elements = configs.get("elements");
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
                                if(element.containsKey("annotations")) {
                                    Object o_annotations = element.get("annotations");
                                    if (o_annotations instanceof ArrayList) {
                                        @SuppressWarnings("unchecked")
                                        ArrayList<HashMap<String, Object>> annotations = (ArrayList<HashMap<String, Object>>) o_annotations;
                                        for(HashMap<String, Object> annotation:annotations) {
                                            if(annotation.containsKey("property")) {
                                                OWLAnnotationProperty apa = df.getOWLAnnotationProperty(IRI.create((String)annotation.get("property")));
                                                AnnotationElement ea = new AnnotationElement(ap);
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
    }

}
