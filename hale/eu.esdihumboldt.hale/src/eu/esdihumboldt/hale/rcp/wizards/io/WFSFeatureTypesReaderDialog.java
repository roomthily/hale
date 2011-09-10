/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.io;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.DataStore;
import org.geotools.data.wfs.WFSDataStore;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.Messages;

/**
 * The {@link WFSFeatureTypesReaderDialog} enables a user to select a WFS from
 * which to read capabilities and will confirm communication by displaying
 * {@link FeatureType} names that are found.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class WFSFeatureTypesReaderDialog 
	extends Dialog {
	
	private final static Logger _log = Logger.getLogger(WFSFeatureTypesReaderDialog.class);

	URL url_result;
	
	/**
	 * Constructor
	 * 
	 * @param parent the parent shell
	 * @param style the dialog style
	 */
	public WFSFeatureTypesReaderDialog(Shell parent, int style) {
		super(parent, style);
	}
	
	/**
	 * Constructor
	 * 
	 * @param parent the parent shell
	 * @param title the dialog title
	 */
	public WFSFeatureTypesReaderDialog(Shell parent, String title) {
		super(parent, 0);
		this.setText(title);
	}

	/**
	 * @see org.eclipse.swt.widgets.Dialog
	 * @return any Object.
	 */
	public URL open () {
		Shell parent = super.getParent();
		Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(500, 400);
		shell.setLayout(new GridLayout());
		shell.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
		shell.setText(super.getText());
		
		this.createControls(shell);
		
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		_log.debug("returning result."); //$NON-NLS-1$
		
		return url_result;
	}

	private void createControls(final Shell shell) {
		_log.debug("Creating Controls"); //$NON-NLS-1$
		
		// Create Fields for URL entry.
		final Composite c = new Composite(shell, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL |
				GridData.GRAB_VERTICAL | GridData.FILL_VERTICAL));
		final Group urlDefinitionArea = new Group(c, SWT.NONE);
		urlDefinitionArea.setText(Messages.WFSFeatureTypesReaderDialog_UrlEnterText);
		urlDefinitionArea.setLayoutData( new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 2;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		urlDefinitionArea.setLayout(fileSelectionLayout);
		
		// Host + Port
		final Label hostPortLabel = new Label(urlDefinitionArea, SWT.NONE);
		hostPortLabel.setText(Messages.WFSFeatureTypesReaderDialog_HostPortLabelText);
		hostPortLabel.setToolTipText(Messages.WFSFeatureTypesReaderDialog_HostPortTooltip1);
		final Text hostPortText = new Text (urlDefinitionArea, SWT.BORDER | SWT.SINGLE);
		hostPortText.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		hostPortText.setText(
				"http://staging-esdi-humboldt.igd.fraunhofer.de:8080/" + //$NON-NLS-1$
				"geoserver/ows?service=WFS&request=GetCapabilities"); //$NON-NLS-1$
		/*hostPortText.setText(
				"http://car2.esrin.esa.int:8080/" +
				"geoserver/ows?service=WFS&request=GetCapabilities");*/
		hostPortText.setEditable(true);
		hostPortText.addListener (SWT.FocusOut, new Listener () {
			public void handleEvent (Event e) {
				String string = hostPortText.getText();
				try {
					new URL(string);
				}
				catch (Exception ex) {
					_log.warn("Exception occured in parsing " + string  //$NON-NLS-1$
							+ " to URL: " + ex.getMessage()); //$NON-NLS-1$
					e.doit = false;
					return;
				}
			}
		});
		
		// Protocol Version & Type
		/*final Label protocolVersionLabel = new Label(urlDefinitionArea, SWT.NONE);
		protocolVersionLabel.setText("WFS Version/Protocol:");
		protocolVersionLabel.setToolTipText("Select one of the offered combinations of service version and protocol.");
		final Combo combo = new Combo (urlDefinitionArea, SWT.READ_ONLY);
		combo.setItems (new String [] {"1.1.0, HTTP GET", "1.0.0 XML POST", "1.1.0 XML POST"});
		combo.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));*/

		// Validation Group
		final Group urlValidationArea = new Group(c, SWT.NONE);
		urlValidationArea.setText(Messages.WFSFeatureTypesReaderDialog_ValidateText);
		urlValidationArea.setLayoutData( new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL 
				| GridData.GRAB_VERTICAL | GridData.FILL_VERTICAL));
		GridLayout urlValidationLayout = new GridLayout();
		urlValidationLayout.numColumns = 1;
		urlValidationLayout.makeColumnsEqualWidth = false;
		urlValidationArea.setLayout(urlValidationLayout);
		
		Composite urlValidationStatusArea = new Composite(urlValidationArea, SWT.NONE);
		urlValidationStatusArea.setLayoutData( new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		GridLayout urlValidationStatusLayout = new GridLayout();
		urlValidationStatusLayout.numColumns = 2;
		urlValidationStatusArea.setLayout(urlValidationStatusLayout);
		
		Button testUrl = new Button(urlValidationStatusArea, SWT.PUSH);
		testUrl.setText(Messages.WFSFeatureTypesReaderDialog_TestUrlText);
		final Label currentStatusLabel = new Label(urlValidationStatusArea, SWT.NONE);
		currentStatusLabel.setText(Messages.WFSFeatureTypesReaderDialog_StatusText);
		currentStatusLabel.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		currentStatusLabel.setAlignment(SWT.RIGHT);
		
		final FeatureTypeSelection selection = new FeatureTypeSelection(urlValidationArea);
		selection.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		/*XXX final Text testResultText = new Text(urlValidationArea, 
				SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		testResultText.setLayoutData(new GridData(GridData.FILL_BOTH));
		testResultText.setEditable(false);*/
		
		// Cancel/Finish buttons
		Composite buttons = new Composite(c, SWT.BOTTOM);
		buttons.setLayoutData( new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		GridLayout buttonsLayout = new GridLayout();
		buttonsLayout.numColumns = 3;
		buttonsLayout.makeColumnsEqualWidth = false;
		buttons.setLayout(buttonsLayout);
		
		final Button finish = new Button(buttons, SWT.NONE);
		finish.setAlignment(SWT.RIGHT);
		finish.setText("   Use this WFS    "); //$NON-NLS-1$
		finish.setEnabled(false);
		finish.setSize(100, 24);
		finish.addListener(SWT.Selection, new Listener () {
			public void handleEvent(Event event) {
				if (finish.isEnabled()) {
					_log.debug("saving result: " + hostPortText.getText()); //$NON-NLS-1$
					try {
						//url_result = new URL(hostPortText.getText());
						String capabilities = hostPortText.getText();
						
						// build DescribeFeatureType URL
						DataStore data = GetCapabilititiesRetriever.getDataStore(capabilities);
						
						// collect type names
						StringBuffer typeNames = new StringBuffer();
						String firstType = null;
						boolean first = true;
						for (FeatureType type : selection.getSelection()) { //data.getTypeNames()) {
							String typeName = type.getName().getLocalPart();
							
							if (first) {
								first = false;
								firstType = typeName;
							}
							else {
								typeNames.append(',');
							}
							typeNames.append(typeName);
						}
						
						// get the URL
						//XXX replaced by code below - url_result = ((WFSDataStore) data).getDescribeFeatureTypeURL(typeNames.toString());
						//XXX we have to trick because the geotools implementation of the WFS 1.1.0 protocol is limited to one feature type
						//TODO better solution
						if (firstType != null) {
							String temp = ((WFSDataStore) data).getDescribeFeatureTypeURL(firstType).toString();
							String repl = URLEncoder.encode(firstType);
							if (temp.indexOf(repl) < 0) {
								repl = firstType;
							}
							temp = temp.replaceAll(repl, URLEncoder.encode(typeNames.toString()));
							url_result = new URL(temp);
						}
						
						_log.info("DescribeFeatureType URL: " + url_result.toString()); //$NON-NLS-1$
					} catch (MalformedURLException e) {
						_log.error("An error occured when parsing the " + //$NON-NLS-1$
								"selected host to a URL:", e); //$NON-NLS-1$
					} catch (IOException e) {
						_log.error("Error connecting to the WFS service", e); //$NON-NLS-1$
					}
				}
				finish.getParent().getParent().getShell().dispose();
			}
		});
		
		Button cancel = new Button(buttons, SWT.NONE);
		cancel.setAlignment(SWT.RIGHT);
		cancel.setText(Messages.WFSFeatureTypesReaderDialog_CancelText);
		cancel.setSize(100, 24);
		cancel.addListener(SWT.Selection, new Listener () {
			public void handleEvent(Event event) {
				finish.getParent().getParent().getShell().dispose();
			}
		});
		
		// add complex Listeners
		testUrl.addListener(SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				URL url = null;
				try {
					url = new URL(hostPortText.getText());
				} catch (Exception e1) {
					currentStatusLabel.setText(Messages.WFSFeatureTypesReaderDialog_ValidationFailedText);
					//XXX testResultText.setText("Capabilities URL could not " +
					//		"be built: " + e1.getMessage());
					finish.setEnabled(false);
				}
				if (url != null) {
					final String capabilitiesURL = url.toString();
					final Display display = Display.getCurrent();
					
					ProgressMonitorDialog progress = new ProgressMonitorDialog(display.getActiveShell());
					progress.setCancelable(true);
					try {
						progress.run(true, true, new IRunnableWithProgress() {

							@Override
							public void run(IProgressMonitor monitor)
									throws InvocationTargetException,
									InterruptedException {
								try {
									final List<FeatureType> types = GetCapabilititiesRetriever
												.readFeatureTypes(capabilitiesURL, monitor);
									
									display.asyncExec(new Runnable() {

										@Override
										public void run() {
											selection.setFeatureTypes(types);
											
											currentStatusLabel.setText(MessageFormat.format(Messages.WFSFeatureTypesReaderDialog_ValdationOKText, types.size()));
											finish.setEnabled(true);
										}
										
									});
									
								} catch (final IOException e1) {
									display.asyncExec(new Runnable() {

										@Override
										public void run() {
											currentStatusLabel.setText(Messages.WFSFeatureTypesReaderDialog_ValidationFailedText);
											
											selection.setFeatureTypes(null);
											
											//XXX testResultText.setText("Capabilities document " +
											//		"could not be read: " + e1.getMessage());
											finish.setEnabled(false);
											_log.warn(e1.getMessage());
											e1.printStackTrace();
										}
										
									});
								}
							}
							
						});
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		// finish drawing
		urlDefinitionArea.moveAbove(null);
	}

}