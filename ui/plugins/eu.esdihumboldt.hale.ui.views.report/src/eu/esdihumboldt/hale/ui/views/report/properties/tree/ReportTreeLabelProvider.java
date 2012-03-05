/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.report.properties.tree;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.dialogs.FilteredTree;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.ui.views.report.properties.ReportDetails;

/**
 * LabelProvider for {@link FilteredTree} in {@link ReportDetails}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object obj) {
		if (obj instanceof ReportGroupInfo) {
			return String.format("Info (%d)", ((ReportGroupInfo) obj).size());
		} else if (obj instanceof ReportGroupWarning) {
			return String.format("Warning (%d)", ((ReportGroupWarning) obj).size());
		} else if (obj instanceof ReportGroupError) {
			return String.format("Error (%d)", ((ReportGroupError) obj).size());
		} else if (obj instanceof Message) {
			return ((Message) obj).getMessage();
		}
		
		return obj.toString();
	}
}