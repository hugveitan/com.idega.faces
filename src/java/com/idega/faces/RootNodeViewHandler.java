/*
 * Created on 18.5.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.faces;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.faces.componentbased.CbpViewHandler;
import com.idega.presentation.IWContext;

/**
 * @author tryggvil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class RootNodeViewHandler extends CbpViewHandler{// extends CbpViewHandler {
	
	private static Logger log = Logger.getLogger(RootNodeViewHandler.class.getName());
	private ViewHandler parentViewHandler;
	
	/**
	 * 
	 */
	//public RootViewHandler() {
	//	super();
	//}
	/**
	 * @param getParentViewHandler()
	 */
	public RootNodeViewHandler(ViewHandler parentViewHandler) {
		//super(parentViewHandler);
		setParentViewHandler(parentViewHandler);
	}

	protected ViewHandler getParentViewHandler(){
		return parentViewHandler;
	}
	
	protected void setParentViewHandler(ViewHandler viewHandler){
		this.parentViewHandler=viewHandler;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#calculateLocale(javax.faces.context.FacesContext)
	 */
	public Locale calculateLocale(FacesContext arg0) {
		IWContext iwc = IWContext.getIWContext(arg0);
		return iwc.getCurrentLocale();
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#calculateRenderKitId(javax.faces.context.FacesContext)
	 */
	public String calculateRenderKitId(FacesContext arg0) {
		return getParentViewHandler().calculateRenderKitId(arg0);
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#createView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot createView(FacesContext ctx, String viewId) {
		//return getParentViewHandler().createView(arg0, arg1);
		ViewNode node = getViewManager(ctx).getViewNodeForContext(ctx);
		if(node!=null){
			if(node.isComponentBased()){
				UIComponent comp = node.createComponent(ctx);
				UIViewRoot root = null;
				if(comp instanceof UIViewRoot){
					root = (UIViewRoot)comp;
				}
				else{
					root = new UIViewRoot();
					root.setViewId(viewId);
					root.getChildren().add(comp);
				}
				//set the locale
				root.setLocale(calculateLocale(ctx));
				return root;
			}
		}

		if(getParentViewHandler()!=null){
			return getParentViewHandler().createView(ctx,viewId);
		}
		else{
			//return createView(ctx,vewId);
			throw new RuntimeException ("No parent ViewHandler");
		}
	}
	
	protected ViewManager getViewManager(FacesContext context){
		return ViewManager.getInstance(context);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#getActionURL(javax.faces.context.FacesContext, java.lang.String)
	 */
	public String getActionURL(FacesContext ctx, String arg1) {
		ViewNode node = getViewManager(ctx).getViewNodeForContext(ctx);
		if(node!=null){
			//if(node.isComponentBased()){
				String url = node.getURIWithContextPath();
				return url;
			//}
		}
		String url = getParentViewHandler().getActionURL(ctx, arg1);
		return url;
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#getResourceURL(javax.faces.context.FacesContext, java.lang.String)
	 */
	public String getResourceURL(FacesContext arg0, String arg1) {
		return getParentViewHandler().getResourceURL(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#renderView(javax.faces.context.FacesContext, javax.faces.component.UIViewRoot)
	 */
	public void renderView(FacesContext ctx, UIViewRoot viewRoot) throws IOException, FacesException {
		//getParentViewHandler().renderView(arg0, arg1);
		super.renderView(ctx,viewRoot);
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#restoreView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot restoreView(FacesContext arg0, String arg1) {
		return getParentViewHandler().restoreView(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#writeState(javax.faces.context.FacesContext)
	 */
	public void writeState(FacesContext arg0) throws IOException {
		getParentViewHandler().writeState(arg0);
	}
}
