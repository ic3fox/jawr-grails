import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.jawr.web.resource.ImageResourcesHandler;
import net.jawr.web.resource.bundle.renderer.BundleRendererContext;
import net.jawr.web.resource.bundle.renderer.JavascriptHTMLBundleLinkRenderer;
import net.jawr.web.resource.bundle.renderer.CSSHTMLBundleLinkRenderer;
import net.jawr.web.resource.bundle.renderer.image.ImgRenderer;
import net.jawr.web.resource.bundle.handler.ResourceBundlesHandler;
import net.jawr.web.servlet.RendererRequestUtils;
import net.jawr.web.taglib.ImageTagUtils;
import net.jawr.web.JawrConstant;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Jawr tag library, uses Jawr Renderer object to delegate content generation. 
 */
class JawrTagLib {
	static namespace = "jawr"
	
	/**
	 * script Tag: generates javascript script tags. 
	 * Attributes: 
	 * src (required): Set the source of the resource or bundle to retrieve. 
	 * useRandomParam (optional, default true): Set wether random param will be added in development mode to generated urls. 
	 */
	def script = { attrs ->
		boolean useRandomParam = null == attrs['useRandomParam'] ? true : attrs['useRandomParam'];
		
		def renderer = new JavascriptHTMLBundleLinkRenderer(servletContext[JawrConstant.JS_CONTEXT_ATTRIBUTE],useRandomParam);
		
		// set the debug override
		RendererRequestUtils.setRequestDebuggable(request,renderer.getBundler().getConfig());
		
		def ctx = RendererRequestUtils.getBundleRendererContext(request, renderer);
		renderer.renderBundleLinks(attrs['src'], ctx, out);
			
	}
	
	/**
	 * style Tag: generates CSS link tags
	 * Attributes: 
	 * src (required): Set the source of the resource or bundle to retrieve. 
	 * media(optional, default 'screen'):Set the media attribute to use in the link tag. 
	 * useRandomParam (optional, default true): Set wether random param will be added in development mode to generated urls. 
	 * alternate (optional, default false): Sets the link as alternate stysheet. 
 	 * displayAlternate (optional, default false): Displays all the variant CSS bundle of the CSS bundle defined in src. 
 	 * title (optional, default ''): Sets the title of the link. 
	 */ 
	def style = { attrs ->
		boolean useRandomParam = attrs['useRandomParam'] == null ? true : attrs['useRandomParam'];
		boolean alternate = attrs['alternate'] == null ? false : attrs['alternate'];
		boolean displayAlternate = attrs['displayAlternate'] == null ? false : attrs['displayAlternate'];
		
		def renderer = new CSSHTMLBundleLinkRenderer(servletContext[JawrConstant.CSS_CONTEXT_ATTRIBUTE],useRandomParam, attrs['media'], alternate, displayAlternate, attrs['title']);
		
		// set the debug override
		RendererRequestUtils.setRequestDebuggable(request,renderer.getBundler().getConfig());
		def ctx = RendererRequestUtils.getBundleRendererContext(request, renderer);
		
		renderer.renderBundleLinks(attrs['src'], ctx, out);
	}
	
	/**
	 * image Tag: generates HTML input image
	 * Main Attributes: 
	 * src (required): Set the source of the image resource to retrieve. 
	 */ 
	def imagePath = { attrs ->
		
		boolean base64 = attrs['base64'] == null ? false : attrs['base64'];
		out << getImgSrcToRender(attrs['src'], base64);
	}

	/**
	 * image Tag: generates HTML input image
	 * Main Attributes: 
	 * src (required): Set the source of the image resource to retrieve. 
	 * base64 (optional, default false): Set flag indicating if we want to generate base64 encoded image or not. 
	 */ 
	def image = { attrs ->
		
		def renderer = new ImgRenderer(false);
		boolean base64 = attrs['base64'] == null ? false : attrs['base64'];
		renderer.renderImage(getImgSrcToRender(attrs['src'], base64),  attrs, out);
	}

	/**
	 * img Tag: generates HTML image
	 * Main Attributes: 
	 * src (required): Set the source of the image resource to retrieve. 
	 */ 
	def img = { attrs ->
		
		def renderer = new ImgRenderer(true);
		boolean base64 = attrs['base64'] == null ? false : true;
		renderer.renderImage(getImgSrcToRender(attrs['src'], base64),  attrs, out);
	}
	
	/**
	 * Returns the image source to render
	 * @return the image source to render
	 * @throws JspException if a JSP exception occurs.
	 */
	def getImgSrcToRender = { src, base64 ->
		
		ImageResourcesHandler imgRsHandler = (ImageResourcesHandler) servletContext.getAttribute(JawrConstant.IMG_CONTEXT_ATTRIBUTE);
		if (imgRsHandler == null){
			throw new JspException(
					"You are using a Jawr image tag while the Jawr Image servlet has not been initialized. Initialization of Jawr Image servlet either failed or never occurred.");
		}
		
		return ImageTagUtils.getImageUrl(src, base64, imgRsHandler, request, response);
	}
	
}