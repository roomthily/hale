/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : CST
 * 	 
 * Classname    : eu.esdihumboldt.cst.transformer/CstServiceFactory.java 
 * 
 * Author       :  Bernd Schneiders, Logica
 * 
 * Created on   : Aug 26, 2009 -- 11:50:35 AM
 *
 */
package eu.esdihumboldt.cst.transformer.service;

import eu.esdihumboldt.cst.transformer.CstService;
import eu.esdihumboldt.cst.transformer.service.impl.CstServiceImpl;

/**
 * Factory to get an instance of an {@link CstService}.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class CstServiceFactory {

	/** Reference to a transformation service */
	private static CstServiceImpl service = null;
	private static ToleranceLevel tl = ToleranceLevel.lenient;
	
	/**
	 * Returns an instance of an {@link CstService}.
	 * @return CstService
	 */
	public static CstService getInstance() {
		if (service == null) {
			service = new CstServiceImpl(tl);
		}
		return service;
	}
	
	/**
	 * 
	 * @param tl the {@link ToleranceLevel} to use for this CST.
	 */
	public static void setToleranceLevel(ToleranceLevel tl) {
		CstServiceFactory.tl = tl;
		service = new CstServiceImpl(tl);
	}
	
	/**
	 * @param activateLineage true if Lineage should be created and attached to 
	 * the transformed features.
	 */
	public static void setLineageMode(boolean activateLineage) {
		service.enableLineageCreation(activateLineage);
	}
	
	/**
	 * This enumeration provides values for specifying the execution mode of the
	 * CSTService.
	 */
	public enum ToleranceLevel {
		/** strict execution mode; execution is stopped when any function
	        execution fails */
		strict, 
		/** exceptions thrown by functions are caught, execution is continued and
	        the cst will always try to return a (partial) result */
		lenient
		
	}
}