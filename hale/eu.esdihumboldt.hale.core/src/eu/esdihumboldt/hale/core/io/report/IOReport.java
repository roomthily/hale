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

package eu.esdihumboldt.hale.core.io.report;

import eu.esdihumboldt.hale.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.core.report.Report;

/**
 * Report for I/O tasks
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface IOReport extends Report<IOMessage> {
	
	/**
	 * Get the locatable I/O task target
	 * 
	 * @return the locatable target
	 */
	public Locatable getTarget();

}