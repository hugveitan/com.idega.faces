/*
 * Created on 18.5.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.faces.smile;

import java.io.IOException;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import com.idega.core.view.ViewNode;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.StringHandler;

/**
 * @author tryggvil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class WindowViewHandler extends CbpViewHandler {
	
	private static Logger log = Logger.getLogger(WindowViewHandler.class.getName());
	private Class defaultPageClass;
	
	/**
	 * 
	 */
	public WindowViewHandler() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param parentViewHandler
	 */
	public WindowViewHandler(ViewNode windowViewNode) {
		this.defaultPageClass=windowViewNode.getComponentClass();
		ViewHandler parentViewHandler=windowViewNode.getParent().getViewHandler();
		this.setParentViewHandler(parentViewHandler);
	}
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#createView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot createView(FacesContext ctx, String viewId) {


		//UIComponent component = (UIComponent) Class.forName(realClassName).newInstance();
		//Page smilePage getSmilePagesWrapper(obj);
		UIViewRoot ret = new UIViewRoot();
		
		ret.setViewId(viewId);	

		try {
			Class descriptorClazz = getDescriptorClassNameForViewId(viewId);
			if(descriptorClazz == null) { 
				// JSP page....
			} else {
				if(Page.class.isAssignableFrom(descriptorClazz)) {
					Page page = (Page) descriptorClazz.newInstance();
					page.init(ctx,ret);
				} else {
					Page page = new PageWrapper((UIComponent)descriptorClazz.newInstance());
					page.init(ctx,ret);
				}
			}
		} catch(IllegalAccessException e) {
			//throw new SmileException("Please make sure that the default constructor for descriptor class of <" + viewId + "> is public.",e);
			throw new RuntimeException("Please make sure that the default constructor for descriptor class of <" + viewId + "> is public.",e);
		} catch(InstantiationException e) {
			//throw new SmileException("An exception was generated by the default constructor of the descriptor class of <" + viewId + ">.",e);
			throw new RuntimeException("An exception was generated by the default constructor of the descriptor class of <" + viewId + ">.",e);
		} catch(Throwable t) {
			//throw new SmileException("Descriptor Class for '" + viewId + "' threw an exception during initialize() !",t);
			//throw new RuntimeException("Descriptor Class for '" + viewId + "' threw an exception during initialize() !",t);
			Page page;
			try {
				page = new PageWrapper((UIComponent) getDefaultPageClass().newInstance());
				page.init(ctx,ret);
			} catch (InstantiationException e1) {
				log.warning(e1.getMessage());
			} catch (IllegalAccessException e1) {
				log.warning(e1.getMessage());
			} catch (ClassNotFoundException e1) {
				log.warning(e1.getMessage());
			}
		}

		//set the locale
		ret.setLocale(calculateLocale(ctx));

		//set the view on the session
		//ctx.getExternalContext().getSessionMap().put(net.sourceforge.smile.application.CbpStateManagerImpl.SESSION_KEY_CURRENT_VIEW,ret);
		
		return ret;
	}
	
	private Class getDescriptorClassNameForViewId(String viewId) throws ClassNotFoundException{
		String encryptedClassName = null;
		//if(viewId.startsWith("/window")){
		//	encryptedClassName = viewId.substring(11,viewId.length());
		//}
		//else{
			String[] urlArray= StringHandler.breakDownURL(viewId);
			if(urlArray.length<1){
				encryptedClassName = "6975";
			}
			else{
				encryptedClassName = urlArray[0];
			}
			
			//String encryptedClassName=urlArray[1];
		//}
		String realClassName = IWMainApplication.decryptClassName(encryptedClassName);
		return Class.forName(realClassName);
	}
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#renderView(javax.faces.context.FacesContext, javax.faces.component.UIViewRoot)
	 */
	public void renderView(FacesContext ctx, UIViewRoot viewId)
			throws IOException, FacesException {
		//super.cbpRenderView(ctx, viewId);
		super.renderView(ctx,viewId);
	}
	
	public Class getDefaultPageClass() throws ClassNotFoundException{
		//return Class.forName("com.idega.webface.workspace.WorkspaceLoginPage");
		return defaultPageClass;
	}
}
