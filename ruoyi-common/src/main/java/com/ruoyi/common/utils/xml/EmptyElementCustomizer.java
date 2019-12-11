/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.utils.xml;

import static java.util.Objects.nonNull;
import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

/**
 *
 * @author wmao
 */
public class EmptyElementCustomizer implements DescriptorCustomizer {

    @Override
    public void customize(ClassDescriptor descriptor) throws Exception {
        if (nonNull(descriptor)) {
            descriptor.getMappings().forEach((mapping) -> {
                if (mapping.isAbstractDirectMapping()) {
                    XMLDirectMapping xmlDirectMapping = (XMLDirectMapping) mapping;
                    xmlDirectMapping.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
                    xmlDirectMapping.getNullPolicy().setNullRepresentedByEmptyNode(true);
                }
            });
        }
    }

}
