/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.internal.validator;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.e4.core.di.extensions.Preference;
import org.pdtextensions.semanticanalysis.IValidatorFactory;
import org.pdtextensions.semanticanalysis.IValidatorManager;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.model.validators.Category;
import org.pdtextensions.semanticanalysis.model.validators.Validator;
import org.pdtextensions.semanticanalysis.model.validators.ValidatorsFactory;
import org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl;

@Singleton
final public class Manager implements IValidatorManager {
	public static String DEFAULT_CATEGORY_ID = "org.pdtextensions.semanticanalysis.defaultCategory"; //$NON-NLS-1$
	
	final private Map<String, Category> categories = new HashMap<String, Category>();
	final private Map<String, Validator> validators = new HashMap<String, Validator>();
	
	@Inject
	@Preference(nodePath=PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID)
	private IEclipsePreferences preferences;
	
	@PostConstruct
	public void initialize(IExtensionRegistry registry) {
		ValidatorsPackageImpl.init();
		final IConfigurationElement[] config = registry.getConfigurationElementsFor(Extension_ID);
		final ValidatorsFactory factory = ValidatorsFactory.eINSTANCE;
		// build categories
		for(final IConfigurationElement el : config) {
			if (el.getName().equals("category")) { //$NON-NLS-1$
				final Category category = factory.createCategory();
				category.setId(el.getAttribute("id")); //$NON-NLS-1$
				category.setLabel(el.getAttribute("label")); //$NON-NLS-1$
				category.setDescription(el.getAttribute("description")); //$NON-NLS-1$
				
				categories.put(category.getId(), category);
			}
		}

		// build validators
		for (final IConfigurationElement el : config) {
			if (el.getName().equals("validator")) { //$NON-NLS-1$
				final Validator validator = factory.createValidator();
				String categoryId = el.getAttribute("categoryId"); //$NON-NLS-1$
				if (categoryId == null) {
					categoryId = DEFAULT_CATEGORY_ID;
				}
				validator.setId(el.getAttribute("id")); //$NON-NLS-1$
				if (!categories.containsKey(categoryId)) {
					PEXAnalysisPlugin.error(String.format("Validator %s has reference to a non-existent category %s", validator.getId(), categoryId)); //$NON-NLS-1$
					categoryId = DEFAULT_CATEGORY_ID;
				}
				
				validator.setCategory(categories.get(categoryId));
				validator.getCategory().getValidators().add(validator);
				validator.setLabel(el.getAttribute("label")); //$NON-NLS-1$
				validator.setDescription(el.getAttribute("description")); //$NON-NLS-1$
				validator.setDefaultSeverity(ProblemSeverity.valueOf(preferences.get(validator.getId(), el.getAttribute("defaultSeverity")).toUpperCase())); //$NON-NLS-1$
				
				try {
					IValidatorFactory exec = (IValidatorFactory) el.createExecutableExtension("class");  //$NON-NLS-1$
					
					validator.setValidatorFactory(exec);
					validators.put(validator.getId(), validator);
				} catch (CoreException e) {
					PEXAnalysisPlugin.error(String.format("Error during %s extension initialization.", validator.getId())); //$NON-NLS-1$
				}
			}
		}
	}

	@Override
	public boolean isEnabled(IScriptProject scriptProject) {
		return true;
	}

	@Override
	public Validator[] getValidators() {
		return validators.values().toArray(new Validator[validators.size()]);
	}
	
	@Override
	public Validator[] getValidators(IScriptProject scriptProject) {
		return validators.values().toArray(new Validator[validators.size()]);
	}

	@Override
	public Category[] getCategories() {
		return categories.values().toArray(new Category[categories.size()]);
	}

	@Override
	public Category getCategory(String id) {
		return categories.get(id);
	}

	@Override
	public Validator getValidator(String id) {
		return validators.get(id);
	}
}
