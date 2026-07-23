package bj.hubcreatif.hubcreatif_backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Interface de base — signatures communes à tous les repositories.
 *
 * @param <T> Nom de l'entité
 */
@NoRepositoryBean
@Transactional(readOnly = true)
public interface BaseRepository<T> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<T> {

    @Query("SELECT u FROM #{#entityName} u WHERE u.code = :code")
    Optional<T> findByCode(@Param("code") String code);

    @Query("SELECT u FROM #{#entityName} u WHERE u.uuid = :uuid")
    Optional<T> findByUuid(@Param("uuid") String uuid);

    @Query("SELECT u FROM #{#entityName} u WHERE upper(u.code) = upper(:code)")
    Optional<T> findByStrictCode(@Param("code") String code);

    @Query("SELECT u FROM #{#entityName} u WHERE lower(u.code) like lower(concat('%', :filter, '%'))")
    Page<T> findByLabelContaining(@Param("filter") String label, Pageable pageable);

    @Query("SELECT u FROM #{#entityName} u WHERE u.code IN :codes")
    Page<T> findByCodeIn(@Param("codes") Collection<String> codes, Pageable pageable);

    @Query("SELECT u.id FROM #{#entityName} u WHERE u.id IN :ids")
    List<Integer> getIds(@Param("ids") List<Integer> ids);
}