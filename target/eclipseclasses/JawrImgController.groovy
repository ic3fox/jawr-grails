import net.jawr.web.servlet.JawrImageRequestHandler;

/**
 * Jawr controller for images requests. 
 * It will delegate in the corresponding requestHandler to attend requests. 
 */
class JawrImgController {
	def defaultAction = "doGet"
	JawrImageRequestHandler requestHandler;
	
	def doGet = {
		
		if(null == requestHandler)
			requestHandler = servletContext.getAttribute("ImgJawrRequestHandler");
		
			// In grails the request is always internally forwarded. This takes account for that. 
			String path = request['javax.servlet.forward.servlet_path'];			
			if(grailsApplication.config.jawr.js.mapping){
				path = path.replaceFirst(grailsApplication.config.jawr.js.mapping, '');
				
			}
			
		render "";	
		requestHandler.processRequest(path,request, response );
		
		return null;
	}
	
	
}