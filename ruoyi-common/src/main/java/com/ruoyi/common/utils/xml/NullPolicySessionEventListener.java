package com.ruoyi.common.utils.xml;


import java.util.List;
import static java.util.Objects.nonNull;
import java.util.Vector;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author wmao
 */
public class NullPolicySessionEventListener extends SessionEventAdapter {

    @Override
    public void preLogin(SessionEvent event) {
        Project project = event.getSession().getProject();
        if (nonNull(project)) {
            List<ClassDescriptor> descriptors = project.getOrderedDescriptors();
            if (isNotEmpty(descriptors)) {
                descriptors.forEach((descriptor) -> {
                    if (nonNull(descriptor)) {
                        Vector<DatabaseMapping> mappings = descriptor.getMappings();
                        if (isNotEmpty(mappings)) {
                            mappings.forEach((mapping) -> {
                                if (mapping.isAbstractDirectMapping()) {
                                    XMLDirectMapping xmlDirectMapping = (XMLDirectMapping) mapping;
                                    xmlDirectMapping.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
                                    xmlDirectMapping.getNullPolicy().setNullRepresentedByEmptyNode(true);
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}
