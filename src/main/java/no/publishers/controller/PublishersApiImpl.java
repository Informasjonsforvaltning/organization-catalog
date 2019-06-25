package no.publishers.controller;

import io.swagger.annotations.ApiParam;
import no.publishers.generated.model.Publisher;

import no.publishers.graphql.CreatePublisher;
import no.publishers.service.PublisherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class PublishersApiImpl implements no.publishers.generated.api.PublishersApi{
    private static Logger LOGGER = LoggerFactory.getLogger(PublishersApiImpl.class);

    @Autowired
    private PublisherService publisherService;

    @RequestMapping(value="/ping", method=GET, produces={"text/plain"})
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("pong");
    }

    @RequestMapping(value="/ready", method=GET)
    public ResponseEntity getReady() {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value="/publisher/create", method=POST)
    public ResponseEntity<String> createPublisher(HttpServletRequest httpServletRequest, @RequestBody CreatePublisher input) {
        String savedId;

        try {
            savedId = publisherService.createPublisher(input);
        } catch (Exception e) {
            LOGGER.error("createPublisher failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(savedId, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Publisher> getPublisherById(HttpServletRequest httpServletRequest, @ApiParam(value = "id",required=true) @PathVariable("id") String id) {
        ResponseEntity<Publisher> response;

        try {
            response = publisherService.getById(id)
                .map(publisher -> new ResponseEntity<>(publisher, HttpStatus.OK))
                .orElse( new ResponseEntity<>(HttpStatus.NOT_FOUND) );
        } catch (Exception e) {
            LOGGER.error("getPublisherById failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    @Override
    public ResponseEntity<List<Publisher>> getPublishers(HttpServletRequest httpServletRequest, @ApiParam(value = "A query string to match a publisher name") @Valid @RequestParam(value = "name", required = false) String name,@ApiParam(value = "If you want to filter by organizationId") @Valid @RequestParam(value = "organizationId", required = false) String organizationId) {
        List<Publisher> publishers;

        try {
            publishers = publisherService.getPublishers(name, organizationId);
        } catch (Exception e) {
            LOGGER.error("getPublishers failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(publishers, HttpStatus.OK);
    }
}
