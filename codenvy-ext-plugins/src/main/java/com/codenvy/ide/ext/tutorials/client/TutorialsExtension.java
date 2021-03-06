/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.tutorials.client;

import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.icon.Icon;
import com.codenvy.ide.api.icon.IconRegistry;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttree.TreeStructureProviderRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.ext.tutorials.client.action.ShowTutorialGuideAction;
import com.codenvy.ide.ext.tutorials.client.action.UpdateAction;
import com.codenvy.ide.ext.tutorials.client.wizard.ExtensionPagePresenter;
import com.codenvy.ide.ext.tutorials.shared.Constants;
import com.codenvy.ide.extension.maven.client.projecttree.MavenProjectTreeStructureProvider;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN;
import static com.codenvy.ide.api.action.IdeActions.GROUP_WINDOW;

/**
 * Entry point for an extension that adds support to work with tutorial projects.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Codenvy tutorial projects", version = "3.0.0")
public class TutorialsExtension {
    /** Default name of the file that contains tutorial description. */
    public static final String DEFAULT_GUIDE_FILE_NAME = ".guide/guide.html";

    @Inject
    public TutorialsExtension(TutorialsResources resources,
                              TutorialsLocalizationConstant localizationConstants,
                              ActionManager actionManager,
                              ShowTutorialGuideAction showAction,
                              UpdateAction updateAction,
                              ProjectTypeWizardRegistry wizardRegistry,
                              NotificationManager notificationManager,
                              IconRegistry iconRegistry,
                              Provider<ExtensionPagePresenter> extensionPagePresenter,
                              TreeStructureProviderRegistry treeStructureProviderRegistry,
                              MavenProjectTreeStructureProvider mavenProjectTreeStructureProvider) {
        resources.tutorialsCss().ensureInjected();

        // register Icons for samples and codenvy projecttypes
        iconRegistry.registerIcon(new Icon("Samples.samples.category.icon", resources.samplesCategorySamples()));
        iconRegistry.registerIcon(new Icon("Samples - Hello World.samples.category.icon", resources.samplesCategorySamples()));
        iconRegistry.registerIcon(new Icon("Codenvy.samples.category.icon", resources.samplesCategoryCodenvy()));

        // use Maven project tree for 'Codenvy Extension' and 'Tutorial' project types
        treeStructureProviderRegistry.registerProvider(Constants.TUTORIAL_ID, mavenProjectTreeStructureProvider);
        treeStructureProviderRegistry.registerProvider(com.codenvy.ide.Constants.CODENVY_PLUGIN_ID, mavenProjectTreeStructureProvider);

        // register actions
        DefaultActionGroup windowMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_WINDOW);

        actionManager.registerAction(localizationConstants.showTutorialGuideActionId(), showAction);
        windowMenuActionGroup.add(showAction);

        actionManager.registerAction(localizationConstants.updateExtensionActionId(), updateAction);
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN);
        runMenuActionGroup.add(updateAction);

        ProjectWizard wizard = new ProjectWizard(notificationManager);
        wizard.addPage(extensionPagePresenter);
        wizardRegistry.addWizard(Constants.TUTORIAL_ID, wizard);
        wizardRegistry.addWizard(com.codenvy.ide.Constants.CODENVY_PLUGIN_ID, wizard);
    }
}
