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

package eu.esdihumboldt.cst.functions.geometric;

import java.util.List;
import java.util.Map;

import org.springframework.core.convert.ConversionException;

import com.google.common.collect.ListMultimap;
import com.iabcinc.jmep.XExpression;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.buffer.BufferBuilder;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

import eu.esdihumboldt.cst.functions.numeric.MathematicalExpression;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.convert.ConversionUtil;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder;
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;

/**
 * Network expansion function.
 * 
 * @author Simon Templer
 */
public class NetworkExpansion extends
		AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements NetworkExpansionFunction {

	private static int CAP_STYLE = BufferParameters.CAP_ROUND;

	/**
	 * @see AbstractSingleTargetPropertyTransformation#evaluate(String,
	 *      TransformationEngine, ListMultimap, String,
	 *      PropertyEntityDefinition, Map, TransformationLog)
	 */
	@Override
	protected Object evaluate(String transformationIdentifier,
			TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException, NoResultException {
		// get the buffer width parameter
		String bufferWidthString = getParameterChecked(PARAMETER_BUFFER_WIDTH);
		
		double bufferWidth;
		try {
			// try simple number
			bufferWidth = Double.parseDouble(bufferWidthString);
		} catch (NumberFormatException e) {
			// evaluate as math expression
			List<PropertyValue> vars = variables.get(ENTITY_VARIABLE);
			try {
				Object result = MathematicalExpression.evaluateExpression(bufferWidthString, vars);
				bufferWidth = ConversionUtil.getAs(result, Double.class);
			} catch (XExpression e1) {
				throw new TransformationException("Failed to evaluate buffer width expression.", e1);
			} catch (ConversionException e2) {
				throw new TransformationException("Failed to convert buffer width expression result to double.", e2);
			}
		}

		// get input geometry
		PropertyValue input = variables.get(null).get(0);
		Object inputValue = input.getValue();
		if (inputValue instanceof Instance) {
			inputValue = ((Instance) inputValue).getValue();
		}

		// find contained geometries
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);
		GeometryFinder geoFind = new GeometryFinder(null);
		traverser.traverse(inputValue, geoFind);

		List<GeometryProperty<?>> geometries = geoFind.getGeometries();
		
		GeometryProperty<?> old_geometry = null;
		if (!geometries.isEmpty()) {
			old_geometry = geometries.get(0);
			
			if (geometries.size() > 1) {
				log.warn(log.createMessage(
						"Multiple geometries found, but network expansion is only done on the first.",
						null));
			}
		}

		if (old_geometry != null) {
			Geometry new_geometry = null;
			BufferParameters bufferParameters = new BufferParameters();
			bufferParameters.setEndCapStyle(CAP_STYLE);
			BufferBuilder bb = new BufferBuilder(new BufferParameters());
			new_geometry = bb.buffer(old_geometry.getGeometry(), bufferWidth);

			// try to yield a result compatible to the target
			TypeDefinition targetType = resultProperty.getDefinition()
					.getPropertyType();
			// TODO check element type?
			Class<?> binding = targetType.getConstraint(Binding.class)
					.getBinding();
			if (Geometry.class.isAssignableFrom(binding)
					&& binding.isAssignableFrom(new_geometry.getClass())) {
				return new_geometry;
			}
			return new DefaultGeometryProperty<Geometry>(
					old_geometry.getCRSDefinition(), new_geometry);
		}
		
		throw new TransformationException(
				"Geometry for network expansion could not be retrieved.");
	}

}