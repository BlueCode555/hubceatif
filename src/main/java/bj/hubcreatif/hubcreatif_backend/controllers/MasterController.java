package bj.hubcreatif.hubcreatif_backend.controllers;

import bj.hubcreatif.hubcreatif_backend.annotations.CreateResource;
import bj.hubcreatif.hubcreatif_backend.annotations.UpdateResource;
import bj.hubcreatif.hubcreatif_backend.dto.request.FormRequest;
import bj.hubcreatif.hubcreatif_backend.dto.request.IdsRequest;
import bj.hubcreatif.hubcreatif_backend.entities.Timestamps;
import bj.hubcreatif.hubcreatif_backend.services.AbstractBaseService;
import bj.hubcreatif.hubcreatif_backend.specs.FilterCriteria;
import bj.hubcreatif.hubcreatif_backend.specs.PaginationCriteria;
import bj.hubcreatif.hubcreatif_backend.utils.MessageConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static bj.hubcreatif.hubcreatif_backend.dto.response.ApiResponse.apiSuccess;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

public abstract class MasterController<E extends Timestamps, R, F extends FormRequest> {
    @Autowired
    protected HttpServletRequest request;

    protected abstract AbstractBaseService<E, R> service();

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Validated(CreateResource.class) F form) {
        return sendCreateResponse(doCreate(form));
    }

    @PostMapping(value = "/create-with-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createResourceWithFile(@Validated(CreateResource.class) F form) {
        return sendCreateResponse(doCreateWithFile(form));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid  @PathVariable("id") Integer id,
                                    @RequestBody @Validated(UpdateResource.class) F form) {
        R response = doUpdate(id, form);
        return sendUpdateResponse(response, updateMessage(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@Valid  @PathVariable("id") Integer id) {
        return sendDeleteResponse(doDelete(IdsRequest.of(id)));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> massDelete(@RequestBody IdsRequest ids) {
        return sendDeleteResponse(service().delete(ids));
    }

    @RequestMapping(value = "/find", method = {GET,  POST})
    public ResponseEntity<?> find(@RequestParam Map<String, Object> findRequest) {
        return sendResponse(doFind(findRequest), MessageConstants.DATA_RETRIEVED);
    }

    @GetMapping
    public ResponseEntity<?> list(PaginationCriteria criteria) {
        return sendResponse(service().searchByTerm(criteria));
    }



































    @PostMapping("/filter")
    public ResponseEntity<?> filter(PaginationCriteria criteria, @RequestBody List<FilterCriteria> filters) {
        return sendResponse(service().applyFilters(filters, criteria), MessageConstants.DATA_FILTERED);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<?> findOne(@PathVariable("identifier") @Valid @UUID String identifier) {
        try {
            return sendResponse(get((int) Long.parseLong(identifier)), String.format(MessageConstants.ENTRY_FOUND, identifier));
        } catch (NumberFormatException e) {
            return sendResponse(get(identifier), String.format(MessageConstants.ENTRY_FOUND, identifier));
        }
    }

    protected R get(Integer id) {
        return service().toResponse(id);
    }

    protected R get(String code) {
        return service().toResponse(code);
    }

    protected R doCreate(F form) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    protected R doCreateWithFile(F form) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    protected R doUpdate(Integer id, F form) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    protected boolean doDelete(IdsRequest ids) {
        throw new UnsupportedOperationException("Method not implemented");
    }


    protected Object doFind(Map<String, Object> findRequest) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    // Utility Methods for Response Handling
    private <X> ResponseEntity<?> sendCreateResponse(X data) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiSuccess(MessageConstants.ENTRY_CREATED, data, path()));
    }

    private <X> ResponseEntity<?> sendUpdateResponse(X data, String message) {
        return ResponseEntity.accepted().body(apiSuccess(message, data, path()));
    }

    protected <X> ResponseEntity<?> sendResponse(X data, String message) {
        return ResponseEntity.ok(apiSuccess(message, data, path()));
    }

    protected  <X> ResponseEntity<?> sendResponse(X data) {
        return sendResponse(data, MessageConstants.DATA_RETRIEVED);
    }

    private ResponseEntity<?> sendDeleteResponse(boolean deleted) {
        return sendResponse(deleted, MessageConstants.ENTRY_REMOVED);
    }

    private String updateMessage(Object data) {
        return "Entry #" + data + " updated successfully";
    }

    protected String path() {
        return request.getRequestURI();
    }
}
