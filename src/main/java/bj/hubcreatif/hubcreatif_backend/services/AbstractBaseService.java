package bj.hubcreatif.hubcreatif_backend.services;

import bj.hubcreatif.hubcreatif_backend.dto.request.IdsRequest;
import bj.hubcreatif.hubcreatif_backend.dto.response.MetaResponse;
import bj.hubcreatif.hubcreatif_backend.dto.response.PageResponse;
import bj.hubcreatif.hubcreatif_backend.exception.EntityNotFoundException;
import bj.hubcreatif.hubcreatif_backend.mappers.Mapper;
import bj.hubcreatif.hubcreatif_backend.repositories.BaseRepository;
import bj.hubcreatif.hubcreatif_backend.specs.FilterCriteria;
import bj.hubcreatif.hubcreatif_backend.specs.FilterSpecification;
import bj.hubcreatif.hubcreatif_backend.specs.PaginationCriteria;
import bj.hubcreatif.hubcreatif_backend.utils.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public abstract class AbstractBaseService<E, R> {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractBaseService.class);
    protected final Class<E> entity;
    private final String domain;
    protected Page<E> paginated;

    public AbstractBaseService(Class<E> klass) {
        entity = klass;
        domain = klass.getSimpleName();
    }

    public String getDomain() {
        return domain;
    }

    protected abstract BaseRepository<E> repository();

    protected Mapper<E, R> mapper() {
        return null;
    }

    /**
     * Retourne le UUID Keycloak (claim "sub") de l'utilisateur connecté.
     * Retourne null si aucune session active (batch, scheduler).
     *
     * Anciennement Integer — migré en String cohérent avec AppUtil.connectedUserKeycloakUuid().
     */
    public String user() {
        return AppUtil.connectedUserKeycloakUuid();
    }

    public void update(E entity) {
        repository().saveAndFlush(entity);
    }

    public E create(E entity) {
        return repository().saveAndFlush(entity);
    }

    @Transactional
    public boolean delete(IdsRequest request) {
        repository().deleteAllById(request.ids());
        return true;
    }

    private List<Integer> getIds(List<Integer> ids) {
        return repository().getIds(ids);
    }

    @Transactional
    public boolean delete(Integer id) {
        repository().deleteById(id);
        return true;
    }

    public E find(Object id) {
        int iid = Integer.parseInt(id.toString());
        return repository().findById(iid).orElseThrow(() -> throwNotFound(id));
    }

    public E findById(Integer id) {
        return find(id);
    }

    public E findByCode(String code) {
        return repository().findByCode(code).orElseThrow(() -> throwNotFound(code));
    }

    public R toResponse(Integer id) {
        return mapper().toResponse(findById(id));
    }

    public R toResponse(String code) {
        return mapper().toResponse(findByCode(code));
    }

    public PageResponse<?> findAll() {
        return findAll(PaginationCriteria.of());
    }

    @SuppressWarnings("unused")
    public PageResponse<?> customPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<E> list = repository().findAll();

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), list.size());

        paginated = list.isEmpty()
                ? new PageImpl<>(list, pageable, 0)
                : new PageImpl<>(list.subList(start, end), pageable, list.size());

        return paginateResponse();
    }

    public PageResponse<?> findAll(PaginationCriteria criteria) {
        paginated = repository().findAll(criteria.pageable());
        return paginateResponse();
    }

    public PageResponse<?> searchByTerm(PaginationCriteria criteria) {
        String filter = criteria.filter() == null ? "" : criteria.filter();
        paginated = repository().findByLabelContaining(filter, criteria.pageable());
        return paginateResponse();
    }

    public Specification<E> applySpecification(List<FilterCriteria> filters) {
        return new FilterSpecification<E>().applyFilters(filters);
    }

    public PageResponse<?> applyFilters(List<FilterCriteria> filters, PaginationCriteria criteria) {
        paginated = repository().findAll(applySpecification(filters), criteria.pageable());
        return paginateResponse();
    }

    private EntityNotFoundException throwNotFound(Object data) {
        return new EntityNotFoundException(domain, data.toString());
    }

    protected PageResponse<?> paginateResponse() {
        if (mapper() == null) {
            logger.error("THE MAPPER FOR THIS SERVICE IS NOT CONFIGURED.");
            return new PageResponse<R>(null, MetaResponse.of());
        }
        List<R> operationResponse = paginated.getContent()
                .stream()
                .map(mapper()::toResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(operationResponse, MetaResponse.ofPage(paginated));
    }

    protected PageResponse<?> paginateResponse(Function<E, ?> mapperFunction) {
        List<?> operationResponse = paginated.getContent()
                .stream()
                .map(mapperFunction)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new PageResponse<>(operationResponse, MetaResponse.ofPage(paginated));
    }

    public Object dataForForm() {
        return null;
    }

    public Page<E> getPlainEntities(PaginationCriteria criteria) {
        return repository().findAll(criteria.pageable());
    }
}