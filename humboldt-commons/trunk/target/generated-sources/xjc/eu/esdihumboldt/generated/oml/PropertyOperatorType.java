//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.08.16 at 11:40:47 AM MESZ 
//


package eu.esdihumboldt.generated.oml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PropertyOperatorType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PropertyOperatorType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INTERSECTION"/>
 *     &lt;enumeration value="UNION"/>
 *     &lt;enumeration value="UNION_DUPLICATES"/>
 *     &lt;enumeration value="COMPLEMENT"/>
 *     &lt;enumeration value="FIRST"/>
 *     &lt;enumeration value="NEXT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PropertyOperatorType")
@XmlEnum
public enum PropertyOperatorType {

    INTERSECTION,
    UNION,
    UNION_DUPLICATES,
    COMPLEMENT,
    FIRST,
    NEXT;

    public String value() {
        return name();
    }

    public static PropertyOperatorType fromValue(String v) {
        return valueOf(v);
    }

}
