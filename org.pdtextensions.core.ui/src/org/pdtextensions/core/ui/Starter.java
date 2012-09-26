package org.pdtextensions.core.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.pdtextensions.core.ui.preferences.PreferenceConstants;


public class Starter implements IStartup {

	private IPainter painter;

	private void addListener(IEditorPart part) {
		if (PEXUIPlugin.getDefault().getPreferenceStore()
				.getBoolean(PreferenceConstants.ENABLED)) {
			if (part instanceof AbstractTextEditor) {
				Class<?> editor = part.getClass();
				while (!editor.equals(AbstractTextEditor.class)) {
					editor = editor.getSuperclass();
				}
				try {
					Method method = editor.getDeclaredMethod("getSourceViewer", //$NON-NLS-1$
							(Class[]) null);
					method.setAccessible(true);
					Object viewer = method.invoke(part, (Object[]) null);
					if (viewer instanceof ITextViewerExtension2) {
						painter = new IndentGuidePainter((ITextViewer) viewer);
						((ITextViewerExtension2) viewer).addPainter(painter);
					}
				} catch (SecurityException e) {
					PEXUIPlugin.log(e);
				} catch (NoSuchMethodException e) {
					PEXUIPlugin.log(e);
				} catch (IllegalArgumentException e) {
					PEXUIPlugin.log(e);
				} catch (IllegalAccessException e) {
					PEXUIPlugin.log(e);
				} catch (InvocationTargetException e) {
					PEXUIPlugin.log(e);
				}
			}
		}
	}

	public void earlyStartup() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				if (window != null) {
					IWorkbenchPage page = window.getActivePage();
					if (page != null) {
						IEditorPart part = page.getActiveEditor();
						addListener(part);
					}
					window.getPartService().addPartListener(new PartListener());
				}
			}
		});
	}

	private class PartListener implements IPartListener2 {

		public void partActivated(IWorkbenchPartReference partRef) {
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}

		public void partClosed(IWorkbenchPartReference partRef) {
		}

		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		public void partOpened(IWorkbenchPartReference partRef) {
			IWorkbenchPart part = partRef.getPart(false);
			if (part instanceof IEditorPart) {
				addListener((IEditorPart) part);
			}
		}

		public void partHidden(IWorkbenchPartReference partRef) {
		}

		public void partVisible(IWorkbenchPartReference partRef) {
		}

		public void partInputChanged(IWorkbenchPartReference partRef) {
		}
	}

}
