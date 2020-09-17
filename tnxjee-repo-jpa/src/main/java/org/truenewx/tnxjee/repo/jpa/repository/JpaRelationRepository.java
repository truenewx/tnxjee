package org.truenewx.tnxjee.repo.jpa.repository;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.truenewx.tnxjee.model.entity.relation.Relation;
import org.truenewx.tnxjee.model.entity.relation.RelationKey;
import org.truenewx.tnxjee.repo.RelationRepo;

/**
 * 为JPA关系访问库提供的便捷接口，非必须，业务Repo接口可同时继承{@link JpaRepository}和{@link RelationRepo}达到相同的效果
 *
 * @author jianglei
 */
@NoRepositoryBean
public interface JpaRelationRepository<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends JpaRepository<T, RelationKey<L, R>>, RelationRepo<T, L, R> {

    @Override
    default Optional<T> findById(RelationKey<L, R> id) {
        return findById(id.getLeft(), id.getRight());
    }

}
