package com.example.myflower.repository;

import com.example.myflower.entity.CartItem;
import com.example.myflower.entity.FlowerListing;
import jakarta.persistence.EntityManager;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AuditRepository {
    @Autowired
    private EntityManager entityManager;

    public List<Object[]> getCartHistoryById(Integer cartItemId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.createQuery()
                .forRevisionsOfEntity(CartItem.class, false, true)
                .add(AuditEntity.id().eq(cartItemId))
                .getResultList();
    }
    public Integer getCartHistoryCountByAccountId(Integer accountId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.createQuery()
                .forRevisionsOfEntity(CartItem.class, false, true)
                .add(AuditEntity.property("user_id").eq(accountId))
                .getResultList().size();
    }
    public Integer countCartByTime(Integer flowerId, LocalDateTime startDate, LocalDateTime endDate) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return ((Number) auditReader.createQuery()
                .forRevisionsOfEntity(CartItem.class, false, true)
                .add(AuditEntity.property("flower_id").eq(flowerId))
                .add(AuditEntity.property("createdAt").between(startDate, endDate))
                .getResultList().size()).intValue();
    }

    public Integer countViewByTime(Integer flowerId, LocalDateTime startDate, LocalDateTime endDate) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        // Fetch all revisions of the FlowerListing entity for the given flowerId within the specified time range
        List<Object[]> revisions = auditReader.createQuery()
                .forRevisionsOfEntity(FlowerListing.class, false, true)
                .add(AuditEntity.property("id").eq(flowerId))
                .add(AuditEntity.property("createdAt").between(startDate, endDate))
                .getResultList();

        Integer firstView = null;
        Integer lastView = null;

        for (Object[] revision : revisions) {
            FlowerListing flowerListing = (FlowerListing) revision[0];
            Integer views = flowerListing.getViews();

            if (firstView == null) {
                firstView = views;
            }
            lastView = views;
        }

        if (firstView != null && lastView != null) {
            return lastView - firstView;
        }

        return 0;
    }

}
