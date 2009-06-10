package eu.esdihumboldt.hale.models.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.resolver.CollectionURIResolver;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.xml.SchemaFactory;
import org.geotools.xml.XSISAXHandler;
import org.geotools.xml.schema.ComplexType;
import org.geotools.xml.schema.Element;
import org.geotools.xml.schema.Schema;
import org.geotools.xml.schema.SimpleType;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;

/**
 * GeoTools-based implementation of {@link SchemaService}.
 */
public class SchemaServiceImpl implements SchemaService {
	
	private static Logger _log = Logger.getLogger(SchemaServiceImpl.class);
	
	private static SchemaServiceImpl instance = new SchemaServiceImpl();

	/** FeatureType collection of the source schema */
	Collection<FeatureType> sourceSchema;

	/** FeatureType collection of the target schema */
	Collection<FeatureType> targetSchema;
	
	private Collection<HaleServiceListener> listeners = new HashSet<HaleServiceListener>();
	
	private String sourceNamespace = "";
	private String targetNamespace = "";
	
	private URL sourceLocation = null;
	private URL targetLocation = null;
	
	private SchemaServiceImpl() {
		_log.setLevel(Level.INFO);
	}
	
	public static SchemaService getInstance() {
		return SchemaServiceImpl.instance;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#cleanSourceSchema()
	 */
	public boolean cleanSourceSchema() {
		if (this.sourceSchema != null && this.sourceSchema.size() != 0) {
			this.sourceSchema.clear();
		}
		this.sourceLocation = null;
		this.sourceNamespace = "";
		this.updateListeners();
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#cleanTargetSchema()
	 */
	public boolean cleanTargetSchema() {
		if (this.targetSchema != null && this.targetSchema.size() != 0) {
			this.targetSchema.clear();
		}
		this.targetLocation = null;
		this.targetNamespace = "";
		this.updateListeners();
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#getSourceSchema()
	 */
	public Collection<FeatureType> getSourceSchema() {
		return this.sourceSchema;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#getTargetSchema()
	 */
	public Collection<FeatureType> getTargetSchema() {
		return this.targetSchema;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#loadSourceSchema(java.net.URI)
	 */
	public boolean loadSourceSchema(URI file) {
		this.cleanSourceSchema();
		this.sourceSchema = loadSchema(file);
		try {
			this.sourceLocation = new URL("file://" + file.toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException("The source location " + file.getPath()
					+ "could not be saved: ", e);
		}
		if (this.sourceSchema != null && this.sourceSchema.size() > 0) {
			this.sourceNamespace = this.sourceSchema.iterator().next()
					.getName().getNamespaceURI().toString();
		}
		if (this.sourceSchema != null) {
			this.updateListeners();
			return true;
		} 
		else {
			return false;
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#loadTargetSchema(java.net.URI)
	 */
	public boolean loadTargetSchema(URI file) {
		this.cleanTargetSchema();
		this.targetSchema = loadSchema(file);
		try {
			this.targetLocation = new URL("file://" + file.toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException("The target location " + file.getPath()
					+ "could not be saved: ", e);
		}
		if (this.targetSchema != null && this.targetSchema.size() > 0) {
			this.targetNamespace = this.targetSchema.iterator().next()
					.getName().getNamespaceURI().toString();
		}
		if (targetSchema != null) {
			this.updateListeners();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 *  Copies a file
	 * 
	 * @param in
	 * @param out
	 * @throws Exception
	 */
	  public static void copyFile(File in, File out) throws Exception {
		    FileInputStream fis  = new FileInputStream(in);
		    FileOutputStream fos = new FileOutputStream(out);
		    try {
		        byte[] buf = new byte[1024];
		        int i = 0;
		        while ((i = fis.read(buf)) != -1) {
		            fos.write(buf, 0, i);
		        }
		    } 
		    catch (Exception e) {
		        throw e;
		    }
		    finally {
		        if (fis != null) fis.close();
		        if (fos != null) fos.close();
		    }
		  }

	  public static String getFileContent(File in) throws Exception {
		    FileInputStream fis  = new FileInputStream(in);
		    StringBuffer content = new StringBuffer();
		    try {
		        byte[] buf = new byte[1024];
		        int i = 0;
		        while ((i = fis.read(buf)) != -1) {
		            
		        	content.append(new String(buf, 0, i));
		        }
		    } 
		    catch (Exception e) {
		        throw e;
		    }
		    finally {
		        if (fis != null) fis.close();
		    }
		    return content.toString();
		  }
	  
	  public static void writeFileContent(File out, String content) throws Exception {
		    FileOutputStream fos  = new FileOutputStream(out);
		    try {
		        	fos.write(content.getBytes());
		    } 
		    catch (Exception e) {
		        throw e;
		    }
		    finally {
		        if (fos != null) fos.close();
		    }
		  }	
	
	
	public boolean addListener(HaleServiceListener sl) {
		_log.info("Adding a listener.");
		return this.listeners.add(sl);
	}
	
	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 */
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			_log.info("Updating a listener.");
			hsl.update();
		}
	}

	/**
	 * Method to load a XSD schema file and build a collection of FeatureTypes.
	 * 
	 * @param file
	 *            URI which represents a file
	 * @return Collection FeatureType collection.
	 */
	public Collection<FeatureType> loadSchema(URI file) {
		// use XML Schema to load schema with all its subschema to the memory
		/*InputStream is = null;
		try {
			String path = file.toString();
			is = new FileInputStream(path);
			
		} catch (Throwable e) {
			_log.error("-- path not resolved: " + file);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		XmlSchema prepSchema = null;
		try {
			XmlSchemaCollection schemaCol = new XmlSchemaCollection();
			// Check if the file is located on web
			if (file.getHost() == null) {
				schemaCol.setSchemaResolver(new HumboldtURIResolver());
			    schemaCol.setBaseUri(findBaseUri(file));
			}
			prepSchema = schemaCol.read(new StreamSource(is), null);
			is.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		_log.info("Source schema has " +
					prepSchema.getIncludes().getCount()+ " includes");

		// write schema to memory
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.reset();
		prepSchema.write(System.out);
		prepSchema.write(out);
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		Collection<FeatureType> collection = new ArrayList<org.opengis.feature.type.FeatureType>();
		
		try {
			/*URI targetNamespace = null;
			byte [] inputBytes = out.toByteArray();
			System.out.println(inputBytes.length);
			ByteArrayInputStream inputS = new ByteArrayInputStream(inputBytes);
			Schema schema = SchemaFactory.getInstance(targetNamespace, inputS);*/
			
			XMLReader reader = XMLReaderFactory.createXMLReader();
			XSISAXHandler schemaHandler = new XSISAXHandler(file);
			reader.setContentHandler(schemaHandler);
			reader.parse(new InputSource(new FileInputStream(file.toString())));
			Schema schema = schemaHandler.getSchema();

		// Schema[] imports = schema.getImports();
		// for (Schema s : imports) {
		// _log.debug("Imported URI + Name: " + s.getURI() + " " +
		// s.getTargetNamespace());
		// }

		Collection<SimpleFeatureType> inTypes = new HashSet<SimpleFeatureType>();

		// Build first a list of FeatureTypes
		for (ComplexType type : schema.getComplexTypes()) {
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			// System.out.println("FeatureType: " + type.getName());
			builder.setName(type.getName());
			builder.setNamespaceURI(type.getNamespace());
			builder.setAbstract(type.isAbstract());

			if (type.getParent() != null) {
				//if type not abstract, add its children
				if (type.getChildElements()!=null){
				for (Element element : type.getChildElements()) {
					if (element.getType() !=null) {
						builder.add(element.getName(), element.getType()
								.getClass());
						 
					}

				}
				}
				inTypes.add(builder.buildFeatureType());
			}
		}

		// Build collection of feature type with their parents
		
		for (ComplexType type : schema.getComplexTypes()) {
			
			// Create builder
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			builder.setName(type.getName());
			builder.setNamespaceURI(type.getNamespace());
			builder.setAbstract(type.isAbstract());
			if (type.getParent() instanceof ComplexType) {
				if (type.getParent() != null) {
					for (Element element : type.getChildElements()) {
						if (element.getType() !=null) {
						/*	 System.out.println("\tsimpl0e type element: "
							 + element.getName());*/
							builder.add(element.getName(), element.getType()
									.getClass());
						}

					}

					if (type.getParent().getName()
							.equals("AbstractFeatureType")) {
						builder.setSuperType(null); // FIXME??
					} else {
						for (SimpleFeatureType featureType : inTypes) {
							if (featureType.getName().getLocalPart().equals(
									type.getParent().getName())) {
								builder.setSuperType(featureType);
							}
						}
					}
				}
			}
			collection.add(builder.buildFeatureType());
		}
	} catch (Exception uhe) {
		uhe.printStackTrace();
		_log.error("Imported Schema only available on-line, but "
				+ "cannot be retrieved.", uhe);
	}
		return collection;
	}
	
	private String findBaseUri(URI file) {
		String baseUri = "";
		baseUri = file.toString();
		baseUri = baseUri.substring(0, baseUri.lastIndexOf("/"));
		System.out.println("********* BASE_URI: " + baseUri + "***********");
		return baseUri;
	}

	/**
	 * @see SchemaService#getSourceNameSpace()
	 */
	public String getSourceNameSpace() {
		return this.sourceNamespace;
	}

	/**
	 * @see SchemaService#getSourceURL()
	 */
	public URL getSourceURL() {
		return this.sourceLocation;
	}

	/**
	 * @see SchemaService#getTargetNameSpace()
	 */
	public String getTargetNameSpace() {
		return this.targetNamespace;
	}

	/**
	 * @see SchemaService#getTargetURL()
	 */
	public URL getTargetURL() {
		return this.targetLocation;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#getFeatureTypeByName()
	 */
	@Override
	public FeatureType getFeatureTypeByName(String name) {
		FeatureType result = null;
		// handles cases where a full name was given.
		if (!this.sourceNamespace.equals("") && name.contains(this.sourceNamespace)) {
			for (FeatureType ft : this.sourceSchema) {
				if (ft.getName().getLocalPart().equals(name)) {
					result = ft;
					break;
				}
			}
		}
		else if (!this.targetNamespace.equals("") && name.contains(this.targetNamespace)) {
			for (FeatureType ft : this.targetSchema) {
				if (ft.getName().getLocalPart().equals(name)) {
					result = ft;
					break;
				}
			}
		}
		// handle case where only the local part was given.
		else {
			//String localname = name.substring(name.lastIndexOf("/"));
			Collection<FeatureType> allFTs = new HashSet<FeatureType>();
			allFTs.addAll(this.sourceSchema);
			allFTs.addAll(this.targetSchema);
			for (FeatureType ft : allFTs) {
				if (ft.getName().getLocalPart().equals(name)) {
					result = ft;
					break;
				}
			}
		}
		return result;
	}
}



