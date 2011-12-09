/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.corefunctions;


import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.Feature;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.rdf.About;
import eu.esdihumboldt.commons.tools.FeatureInspector;
import eu.esdihumboldt.specification.cst.AbstractCstFunction;
import eu.esdihumboldt.specification.cst.CstFunction;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;

/**
 * CST Function to set default
 * attribute target values.
 *
 * @author Ulrich Schaeffler, Anna Pitaev, Simon Templer
 * @partner 14 / TUM, 04 / Logica, 01 / Fraunhofer IGD
 * @version $Id$ 
 */
public class ConstantValueFunction extends AbstractCstFunction {
	
	/**
	 * Parameter name for the default value
	 */
	public static final String DEFAULT_VALUE_PARAMETER_NAME = "defaultValue"; //$NON-NLS-1$
	
	private String defaultValue = null;
	private Property targetProperty = null;

	/**
	 * @see CstFunction#configure(ICell)
	 */
	public boolean configure(ICell cell) {
		for (IParameter ip : cell.getEntity2().getTransformation().getParameters()) {
			if (ip.getName().equals(ConstantValueFunction.DEFAULT_VALUE_PARAMETER_NAME)) {
				this.defaultValue = ip.getValue();
			}
		}
		this.targetProperty = (Property) cell.getEntity2();
		return true;
	}
	
	/**
	 * @see CstFunction#getParameters()
	 */
	@Override
	public Cell getParameters() {
		Cell parameterCell = new Cell();	
		Property entity2 = new Property(new About("")); //$NON-NLS-1$
		
		// Setting of type condition for entity2
		List <String> entity2Types = new ArrayList <String>();
		entity2Types.add("java.lang.String"); //$NON-NLS-1$
		entity2Types.add("java.lang.Number"); //$NON-NLS-1$
		entity2Types.add("java.lang.Boolean"); //$NON-NLS-1$
		entity2Types.add("java.util.Date");		 //$NON-NLS-1$
		entity2.setTypeCondition(entity2Types);
		
				
		Transformation t = new Transformation();
		List<IParameter> params = new ArrayList<IParameter>(); 
		Parameter p = new Parameter(ConstantValueFunction.DEFAULT_VALUE_PARAMETER_NAME,""); //$NON-NLS-1$
		params.add(p);
		t.setParameters(params);
		entity2.setTransformation(t);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

	/**
	 * @see CstFunction#transform(Feature, Feature)
	 */
	@Override
	public Feature transform(Feature source, Feature target) {
		String value = defaultValue;
		/*
		 * FIXME as the value given is always a string (because cell parameters 
		 * can only be strings) there must be some conversion mechanism e.g.
		 * for dates and numbers
		 */
		
		org.opengis.feature.Property pd = FeatureInspector.getProperty(target, targetProperty.getAbout(), true);
		
		if (pd != null && pd.getType().getBinding().equals(String.class)){
			value = value.toString();
		}
		
		FeatureInspector.setPropertyValue(target, targetProperty.getAbout(), value);
		
		return target;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}


}