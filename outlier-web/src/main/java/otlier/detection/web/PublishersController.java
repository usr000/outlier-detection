package otlier.detection.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/publishers")
public class PublishersController implements ResourceProcessor<RepositoryLinksResource> {

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(ControllerLinkBuilder.linkTo(PublishersController.class).withRel("publishers"));
        return resource;
    }

	@Autowired
	private OutlierRepositoryCustom customRepo;
	
	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<String>> findPublisherNames() {
		return ResponseEntity.ok(customRepo.findDistinctPublishers());
	}
	
}
