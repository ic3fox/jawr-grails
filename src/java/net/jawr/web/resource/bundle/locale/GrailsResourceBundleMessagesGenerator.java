/**
 * Copyright 2008-2013 Jordi Hern�ndez Sell�s, Ibrahim Chaehoi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package net.jawr.web.resource.bundle.locale;

import java.io.Reader;
import java.util.List;

import javax.servlet.ServletContext;

import net.jawr.web.JawrGrailsConstant;
import net.jawr.web.config.JawrConfig;
import net.jawr.web.resource.bundle.generator.ConfigurationAwareResourceGenerator;
import net.jawr.web.resource.bundle.generator.GeneratorContext;
import net.jawr.web.resource.bundle.generator.resolver.ResourceGeneratorResolver;
import net.jawr.web.resource.bundle.generator.resolver.ResourceGeneratorResolverFactory;
import net.jawr.web.resource.bundle.locale.message.GrailsMessageBundleScriptCreator;
import net.jawr.web.resource.bundle.locale.message.MessageBundleScriptCreator;

import org.apache.log4j.Logger;

/**
 * A generator that creates a script from message bundles. The generated script
 * can be used to reference the message literals easily from javascript.
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 * 
 */
public class GrailsResourceBundleMessagesGenerator extends
		ResourceBundleMessagesGenerator implements
		ConfigurationAwareResourceGenerator {

	/** The logger */
	private static final Logger LOGGER = Logger
			.getLogger(GrailsResourceBundleMessagesGenerator.class);

	/** The resource path prefix for grails i18n messages */
	private static final String GRAILS_APP_I18N_RESOURCE_PREFIX = "grails-app.i18n.";

	/** The generator prefix */
	private String generatorPrefix = "g_messages";

	/** The servlet context */
	private ServletContext servletContext;

	/** The flag indicating if we are in a grails context */
	private boolean grailsContext;

	/** The flag indicating if we are in a grails war is deployed */
	private boolean grailsWarDeployed;

	/** The resolver */
	private ResourceGeneratorResolver resolver;

	/**
	 * Constructor
	 */
	public GrailsResourceBundleMessagesGenerator() {

		resolver = ResourceGeneratorResolverFactory
				.createPrefixResolver(generatorPrefix);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.generator.BaseResourceGenerator#getPathMatcher
	 * ()
	 */
	public ResourceGeneratorResolver getResolver() {

		return resolver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.generator.ConfigurationAwareResourceGenerator
	 * #setConfig(net.jawr.web.config.JawrConfig)
	 */
	public void setConfig(JawrConfig config) {
		servletContext = config.getContext();
		grailsContext = servletContext
				.getAttribute(JawrGrailsConstant.GRAILS_WAR_DEPLOYED) != null;
		if (grailsContext) {
			grailsWarDeployed = ((Boolean) servletContext
					.getAttribute(JawrGrailsConstant.GRAILS_WAR_DEPLOYED))
					.booleanValue();
			LOGGER.info("Grails war deployed");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.generator.ResourceGenerator#createResource
	 * (java.lang.String, java.nio.charset.Charset)
	 */
	public Reader createResource(GeneratorContext context) {
		MessageBundleScriptCreator creator = new GrailsMessageBundleScriptCreator(
				context);
		return creator.createScript(context.getCharset());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.handler.LocaleAwareResourceReader#getAvailableLocales
	 * (java.lang.String)
	 */
	public List<String> getAvailableLocales(String resource) {

		List<String> availableLocales = null;
		if (grailsContext && grailsWarDeployed
				&& resource.startsWith(GRAILS_APP_I18N_RESOURCE_PREFIX)) {
			availableLocales = GrailsLocaleUtils
					.getAvailableLocaleSuffixesForBundle(resource,
							servletContext);
		} else {
			availableLocales = GrailsLocaleUtils
					.getAvailableLocaleSuffixesForBundle(resource);
		}
		return availableLocales;
	}

}
