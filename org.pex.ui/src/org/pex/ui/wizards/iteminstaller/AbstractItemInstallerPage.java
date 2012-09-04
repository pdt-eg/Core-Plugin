package org.pex.ui.wizards.iteminstaller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.WorkbenchJob;
import org.pex.core.model.InstallableItem;

public abstract class AbstractItemInstallerPage extends WizardPage implements
		ItemInstaller {

	protected Text filterText;
	protected Composite body;
	protected ScrolledComposite bodyScrolledComposite;
	protected Color colorWhite;

	protected List<? extends InstallableItem> items;

	/** the previous filter text. */
	protected String previousFilterText = ""; //$NON-NLS-1$

	/** the filter pattern. */
	protected Pattern filterPattern;

	protected WorkbenchJob refreshJob;

	protected Color colorDisabled;

	protected Font h2Font;

	protected Font h1Font;

	protected AbstractDescriptorItemUi lastSelected;

	protected static final String COLOR_WHITE = "white"; //$NON-NLS-1$

	protected static final String COLOR_DARK_GRAY = "DarkGray"; //$NON-NLS-1$

	protected static final String COLOR_CATEGORY_GRADIENT_START = "category.gradient.start"; //$NON-NLS-1$

	protected static final String COLOR_CATEGORY_GRADIENT_END = "category.gradient.end"; //$NON-NLS-1$ 

	protected final List<Resource> disposables = new ArrayList<Resource>();

	protected Color colorCategoryGradientStart;

	protected Color colorCategoryGradientEnd;

	protected Cursor handCursor;
	protected Composite categoryChildrenContainer;
	
	protected Composite scrolledContentsComposite;

	protected AbstractItemInstallerPage(String pageName) {
		super(pageName);
	}
	
	protected abstract void createRefreshJob();
	
	public abstract boolean modifySelection(AbstractDescriptorItemUi item, boolean selected);

	public abstract Button getItemButton(Composite checkboxContainer);
	
	public void createControl(Composite parent) {

		initializeDialogUnits(parent);
		createRefreshJob();

		final Composite container = new Composite(parent, SWT.NULL);

		container.setLayout(new GridLayout(1, false));

		// mostly taken from
		// http://dev.eclipse.org/viewcvs/viewvc.cgi/trunk/org.eclipse.team.svn.ui/src/org/eclipse/team/svn/ui/discovery/wizards/ConnectorDiscoveryWizardMainPage.java?view=co&root=Technology_SUBVERSIVE
		{ // header

			createHeader(container);
			body = new Composite(container, SWT.NULL);
			GridDataFactory.fillDefaults().grab(true, true)
					.hint(SWT.DEFAULT, 480).applyTo(body);

			setControl(container);
			createBodyContents();
		}
	}
	
	protected void createHeader(Composite container) {
		
		final Composite header = new Composite(container, SWT.NULL);
		GridLayoutFactory.fillDefaults().applyTo(header);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(header);

		Composite filterContainer = new Composite(header, SWT.NULL);
		GridDataFactory.fillDefaults().grab(true, false)
				.applyTo(filterContainer);

		int numColumns = 2; // 1 for label, 2 for text filter
		GridLayoutFactory.fillDefaults().numColumns(numColumns)
				.applyTo(filterContainer);
		final Label label = new Label(filterContainer, SWT.NULL);
		label.setText("Find package");

		final Composite textFilterContainer = new Composite(
				filterContainer, SWT.NULL);
		GridDataFactory.fillDefaults().grab(true, false)
				.applyTo(textFilterContainer);
		GridLayoutFactory.fillDefaults().numColumns(2)
				.applyTo(textFilterContainer);

		this.filterText = new Text(textFilterContainer, SWT.SINGLE
				| SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);

		GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
			.applyTo(this.filterText);
		
		this.filterText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				filterTextChanged();
			}
		});

		this.filterText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == SWT.ICON_CANCEL) {
					clearFilterText();
				}
			}

			protected void clearFilterText() {
				// TODO Auto-generated method stub

			}
		});
	}

	protected void filterTextChanged() {
		
		if (refreshJob != null) {
			refreshJob.cancel();
			refreshJob.schedule(200L);
		}
	}

	/**
	 * Creates the body content (the archetype list).
	 */
	protected void createBodyContents() {
		// remove any existing contents
		for (Control child : body.getChildren()) {
			child.dispose();
		}
		
		clearDisposables();
		initializeCursors();
		initializeImages();
		initializeFonts();
		initializeColors();

		GridLayoutFactory.fillDefaults().applyTo(this.body);

		this.bodyScrolledComposite = new ScrolledComposite(body,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		configureLook(this.bodyScrolledComposite, colorWhite);
		GridDataFactory.fillDefaults().grab(true, true)
				.applyTo(this.bodyScrolledComposite);

		scrolledContentsComposite = new Composite(
				this.bodyScrolledComposite, SWT.NONE);
		configureLook(scrolledContentsComposite, colorWhite);
		scrolledContentsComposite.setRedraw(false);
		try {
			createPackageContents(scrolledContentsComposite);
		} finally {
			scrolledContentsComposite.layout(true);
			scrolledContentsComposite.setRedraw(true);
		}
		Point size = scrolledContentsComposite.computeSize(
				body.getSize().x, SWT.DEFAULT, true);
		scrolledContentsComposite.setSize(size);

		bodyScrolledComposite.setExpandHorizontal(true);
		bodyScrolledComposite.setMinWidth(100);
		bodyScrolledComposite.setExpandVertical(true);
		bodyScrolledComposite.setMinHeight(1);

		this.bodyScrolledComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				// XXX small offset in case list has a scroll bar
				Point sz = scrolledContentsComposite.computeSize(
						AbstractItemInstallerPage.this.body.getSize().x - 20,
						SWT.DEFAULT, true);
				scrolledContentsComposite.setSize(sz);
				AbstractItemInstallerPage.this.bodyScrolledComposite
						.setMinHeight(sz.y);
			}
		});

		this.bodyScrolledComposite.setContent(scrolledContentsComposite);

		Dialog.applyDialogFont(body);
		// we've changed it so it needs to know
		body.layout(true);
		body.redraw();
		
	}
	
	protected AbstractDescriptorItemUi getItemUI(InstallableItem item, Composite container, Color background) {
		return new AbstractDescriptorItemUi(this, item, container, background);
	}
		
	protected void createPackageContents(Composite container) {
		
		final Color background = container.getBackground();
		GridLayoutFactory.fillDefaults().numColumns(2).spacing(0, 0)
				.applyTo(container);

		int numChildren = 0;
		categoryChildrenContainer = new Composite(container,
				SWT.NULL);
		configureLook(categoryChildrenContainer, background);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false)
				.applyTo(categoryChildrenContainer);
		GridLayoutFactory.fillDefaults().spacing(0, 0)
				.applyTo(categoryChildrenContainer);

		// FIXME: initialize properly
		if (items == null) {
			items = new ArrayList<InstallableItem>();
		}

		for (final InstallableItem item : items) {

			if (++numChildren > 1) {
				// a separator between connector descriptors
				Composite border = new Composite(categoryChildrenContainer,
						SWT.NULL);
				GridDataFactory.fillDefaults().grab(true, false)
						.hint(SWT.DEFAULT, 1).applyTo(border);
				GridLayoutFactory.fillDefaults().applyTo(border);
				border.addPaintListener(new ConnectorBorderPaintListener());
			}

			AbstractDescriptorItemUi itemUI = getItemUI(item, categoryChildrenContainer, background);
			itemUI.updateAvailability();
		}

		// last one gets a border
		Composite border = new Composite(categoryChildrenContainer, SWT.NULL);
		GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 1)
				.applyTo(border);
		GridLayoutFactory.fillDefaults().applyTo(border);
		border.addPaintListener(new ConnectorBorderPaintListener());

		container.layout(true);
		container.redraw();

	}

	protected void initializeColors() {

		if (this.colorWhite == null) {
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			if (!colorRegistry.hasValueFor(COLOR_WHITE)) {
				colorRegistry.put(COLOR_WHITE, new RGB(255, 255, 255));
			}
			this.colorWhite = colorRegistry.get(COLOR_WHITE);
		}
		if (this.colorDisabled == null) {
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			if (!colorRegistry.hasValueFor(COLOR_DARK_GRAY)) {
				colorRegistry.put(COLOR_DARK_GRAY, new RGB(0x69, 0x69, 0x69));
			}
			this.colorDisabled = colorRegistry.get(COLOR_DARK_GRAY);
		}

		if (this.colorCategoryGradientStart == null) {
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			if (!colorRegistry.hasValueFor(COLOR_CATEGORY_GRADIENT_START)) {
				colorRegistry.put(COLOR_CATEGORY_GRADIENT_START, new RGB(240,
						240, 240));
			}
			this.colorCategoryGradientStart = colorRegistry
					.get(COLOR_CATEGORY_GRADIENT_START);
		}

		if (this.colorCategoryGradientEnd == null) {
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			if (!colorRegistry.hasValueFor(COLOR_CATEGORY_GRADIENT_END)) {
				colorRegistry.put(COLOR_CATEGORY_GRADIENT_END, new RGB(220,
						220, 220));
			}
			this.colorCategoryGradientEnd = colorRegistry
					.get(COLOR_CATEGORY_GRADIENT_END);
		}

	}

	protected void initializeFonts() {
		// create a level-2 heading font
		if (this.h2Font == null) {
			Font baseFont = JFaceResources.getDialogFont();
			FontData[] fontData = baseFont.getFontData();
			for (FontData data : fontData) {
				data.setStyle(data.getStyle() | SWT.BOLD);
				data.height = data.height * 1.25f;
			}
			this.h2Font = new Font(Display.getCurrent(), fontData);
			this.disposables.add(this.h2Font);
		}
		// create a level-1 heading font
		if (this.h1Font == null) {
			Font baseFont = JFaceResources.getDialogFont();
			FontData[] fontData = baseFont.getFontData();
			for (FontData data : fontData) {
				data.setStyle(data.getStyle() | SWT.BOLD);
				data.height = data.height * 1.35f;
			}
			this.h1Font = new Font(Display.getCurrent(), fontData);
			this.disposables.add(this.h1Font);
		}
	}

	protected void initializeImages() {
		// TODO Auto-generated method stub
	}

	protected void initializeCursors() {
		if (this.handCursor == null) {
			this.handCursor = new Cursor(getShell().getDisplay(),
					SWT.CURSOR_HAND);
			this.disposables.add(this.handCursor);
		}
	}

	protected void clearDisposables() {
		
		disposables.clear();
		h1Font = null;
		h2Font = null;
		// infoImage = null;
		handCursor = null;
		colorCategoryGradientStart = null;
		colorCategoryGradientEnd = null;
	}

	public void configureLook(Control control, Color background) {
		control.setBackground(background);
	}

	protected Pattern createPattern(String filter) {
		if (filter == null || filter.length() == 0) {
			return null;
		}
		final String regex = filter
				.replace("\\", "\\\\").replace("?", ".").replace("*", ".*?"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		return Pattern
				.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	}

	public class ConnectorBorderPaintListener implements PaintListener {
		@Override
		public void paintControl(PaintEvent e) {
			Composite composite = (Composite) e.widget;
			Rectangle bounds = composite.getBounds();
			GC gc = e.gc;
			gc.setLineStyle(SWT.LINE_DOT);
			gc.drawLine(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y);
		}
	}

	@Override
	public Font getH2Font() {
		return h2Font;
	}

	@Override
	public Color getDisabledColor() {
		return colorDisabled;
	}

	public void itemGainedFocus(
			AbstractDescriptorItemUi abstractDescriptorItemUi) {
		bodyScrolledComposite.showControl(abstractDescriptorItemUi
				.getConnectorContainer());
	}
}
