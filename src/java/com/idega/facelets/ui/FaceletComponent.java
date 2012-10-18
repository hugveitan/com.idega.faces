package com.idega.facelets.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.component.UniqueIdVendor;

import com.idega.presentation.IWBaseComponent;
import com.idega.servlet.filter.IWBundleResourceFilter;
import com.idega.util.CoreConstants;
import com.idega.util.expression.ELUtil;
import org.apache.myfaces.view.facelets.Facelet;
import org.apache.myfaces.view.facelets.FaceletFactory;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/02/14 21:29:08 $ by $Author: civilis $
 *
 *@deprecated This should not be used for new components since this violates JSF spec by building component tree for the component in render_view phase.  Since JSF 2.0 one can use composite components.
 */
public class FaceletComponent extends IWBaseComponent implements UniqueIdVendor {
	
	private String faceletURI;
	
	private static final String faceletFactoryFactoryBeanId = "faceletFactoryFactory";
	public static final String COMPONENT_TYPE = "FaceletComponent";
	
	
	public FaceletComponent() { }
	
	public FaceletComponent(String faceletURI) {
		setFaceletURI(faceletURI);
	}
	
	@Override
	protected void initializeComponent(FacesContext context) {

		super.initializeComponent(context);
		
		String resourceURI = getFaceletURI();
		
		if(resourceURI == null || CoreConstants.EMPTY.equals(resourceURI))
			return;
		
		IWBundleResourceFilter.checkCopyOfResourceToWebapp(context, resourceURI);
	}
	
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		super.encodeBegin(context);
		
		try {
			String resourceURI = getFaceletURI();
			
			if(resourceURI == null || CoreConstants.EMPTY.equals(resourceURI))
				return;
			
			List<UIParameter> paramList = new ArrayList<UIParameter>();
			for(UIComponent c : getChildren()){
				if(c instanceof UIParameter){
					paramList.add((UIParameter) c);
				}
			}

			FaceletFactory faceletFactory = ((FaceletFactoryFactory)getBeanInstance(faceletFactoryFactoryBeanId)).createFaceletFactory(paramList);
			
			Facelet facelet;
			
			synchronized (faceletFactory) {
//			TODO: perhaps facelet factory is thread safe?
				facelet = faceletFactory.getFacelet(resourceURI);
			}
			
			facelet.apply(context, this);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object saveState(FacesContext ctx) {
		
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = faceletURI;
		return values;
	}
	
	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		faceletURI = (String) values[1];
	}

	public String getFaceletURI() {
		if (faceletURI == null)	    {
			ValueExpression vb = getValueExpression("faceletURI");
			if (vb != null)			{
			    return  (String) vb.getValue(getFacesContext().getELContext());
			}
			return null;
	    } else if(ELUtil.isExpression(faceletURI)){
			FacesContext fContext = FacesContext.getCurrentInstance();
	    	ValueExpression valExpr = ELUtil.createValueExpression(fContext, faceletURI, String.class);
			return (String) valExpr.getValue(fContext.getELContext());
	    } else {
	    	return faceletURI;
	    }
	}

	public void setFaceletURI(String faceletURI) {
		if(this.faceletURI != null)
			throw new UnsupportedOperationException("Facelet URI already set. Create new component. URI: "+this.faceletURI);
		
		this.faceletURI = faceletURI;
	}
	
	
    enum PropertyKeys
    {
         uniqueIdCounter
    }
    

    public static final String UNIQUE_ID_PREFIX = "iw_id";
    
	 /**
     * 
     * {@inheritDoc}
     * 
     * @since 2.0
     */
    public String createUniqueId(FacesContext context, String seed)
    {
        StringBuilder bld = _getSharedStringBuilder(context);
        
        Map<Object, Object> attributes = context.getAttributes();

        Long uniqueIdCounter = (Long) attributes.get(PropertyKeys.uniqueIdCounter);
        uniqueIdCounter = (uniqueIdCounter == null) ? 0 : uniqueIdCounter;
        attributes.put(PropertyKeys.uniqueIdCounter, (uniqueIdCounter+1L));
        // Generate an identifier for a component. The identifier will be prefixed with
        // UNIQUE_ID_PREFIX, and will be unique within this UIViewRoot.
        if(seed==null)
        {
            return bld.append(UNIQUE_ID_PREFIX).append(uniqueIdCounter).toString();    
        }
        // Optionally, a unique seed value can be supplied by component creators which
        // should be included in the generated unique id.
        else
        {
            return bld.append(UNIQUE_ID_PREFIX).append(seed).toString();
        }
    }
    
    /**
     * <p>
     * This gets a single FacesContext-local shared stringbuilder instance, each time you call
     * _getSharedStringBuilder it sets the length of the stringBuilder instance to 0.
     * </p><p>
     * This allows you to use the same StringBuilder instance over and over.
     * You must call toString on the instance before calling _getSharedStringBuilder again.
     * </p>
     * Example that works
     * <pre><code>
     * StringBuilder sb1 = _getSharedStringBuilder();
     * sb1.append(a).append(b);
     * String c = sb1.toString();
     *
     * StringBuilder sb2 = _getSharedStringBuilder();
     * sb2.append(b).append(a);
     * String d = sb2.toString();
     * </code></pre>
     * <br><br>
     * Example that doesn't work, you must call toString on sb1 before
     * calling _getSharedStringBuilder again.
     * <pre><code>
     * StringBuilder sb1 = _getSharedStringBuilder();
     * StringBuilder sb2 = _getSharedStringBuilder();
     *
     * sb1.append(a).append(b);
     * String c = sb1.toString();
     *
     * sb2.append(b).append(a);
     * String d = sb2.toString();
     * </code></pre>
     *
     */
    static StringBuilder _getSharedStringBuilder()
    {
        return _getSharedStringBuilder(FacesContext.getCurrentInstance());
    }

    private static final String _STRING_BUILDER_KEY = "javax.faces.component.UIComponentBase.SHARED_STRING_BUILDER"+"_IW_INSTANCE";
    
    // TODO checkstyle complains; does this have to lead with __ ?
    static StringBuilder _getSharedStringBuilder(FacesContext facesContext)
    {
        Map<Object, Object> attributes = facesContext.getAttributes();

        StringBuilder sb = (StringBuilder) attributes.get(_STRING_BUILDER_KEY);

        if (sb == null)
        {
            sb = new StringBuilder();
            attributes.put(_STRING_BUILDER_KEY, sb);
        }
        else
        {

            // clear out the stringBuilder by setting the length to 0
            sb.setLength(0);
        }

        return sb;
    }
}