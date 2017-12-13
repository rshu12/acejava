package org.sapient.ace.studentrecord.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.sapient.ace.studentrecord.service.UploadServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UploadController {
	
	private final UploadServiceImpl uploadServiceImpl;
	
	@Inject
	public UploadController(final UploadServiceImpl uploadServiceImpl) {
		this.uploadServiceImpl = uploadServiceImpl;
	}
	
	
	@GetMapping
	public String index() {
		return "upload";
	}
	
	 @PostMapping("/upload") 
	    public String singleFileUpload(@RequestParam("file") MultipartFile file,
	                                   RedirectAttributes redirectAttributes) throws Exception {

	        if (file.isEmpty()) {
	            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
	            return "redirect:uploadStatus";
	        }

					uploadServiceImpl.uploadOperation(file);
					redirectAttributes.addFlashAttribute("message",
							"You successfully uploaded '" + file.getOriginalFilename() + "'");


	        return "redirect:/uploadStatus";
	    }

	    @GetMapping("/uploadStatus")
	    public String uploadStatus() {
	        return "uploadStatus";
	    }
	    
	    @ExceptionHandler(Exception.class)
		  public ModelAndView handleError(HttpServletRequest req, Exception ex) {
//		    logger.error("Request: " + req.getRequestURL() + " raised " + ex);

		    ModelAndView mav = new ModelAndView();
		    mav.addObject("exception", ex);
		    mav.addObject("url", req.getRequestURL());
		    mav.setViewName("errorPage");
		    return mav;
		  }

}
