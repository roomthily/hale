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

package eu.esdihumboldt.hale.io.gml.reader.internal.instance;

import eu.esdihumboldt.hale.schema.model.PropertyDefinition;

/**
 * Represents a property at the end of a group path
 * @author Simon Templer
 */
public class GroupProperty {
	
	private final PropertyDefinition property;
	
	private final GroupPath path;

	/**
	 * Create a group property
	 * 
	 * @param property the property definition
	 * @param path the group path
	 */
	public GroupProperty(PropertyDefinition property, GroupPath path) {
		super();
		this.property = property;
		this.path = path;
	}

	/**
	 * @return the property
	 */
	public PropertyDefinition getProperty() {
		return property;
	}

	/**
	 * @return the path
	 */
	public GroupPath getPath() {
		return path;
	}

}